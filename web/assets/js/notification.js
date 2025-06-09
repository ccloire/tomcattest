/**
 * 通知系统客户端 - 使用HTTP轮询代替WebSocket
 */
const NotificationSystem = (function () {
    // 是否已连接
    let connected = false;
    // 轮询间隔(毫秒)
    const POLL_INTERVAL = 5000;
    // 轮询计时器
    let pollTimer = null;
    // 通知回调函数
    let notificationCallback = null;
    // 连接状态回调函数
    let connectionStatusCallback = null;

    /**
     * 初始化通知系统
     * @param {Function} onNotification 收到通知时的回调函数
     * @param {Function} onConnectionStatus 连接状态变化时的回调函数
     */
    function init(onNotification, onConnectionStatus) {
        // 保存回调函数
        notificationCallback = onNotification;
        connectionStatusCallback = onConnectionStatus;
        
        // 页面加载时自动连接
        connect();
        
        // 页面关闭时断开连接
        window.addEventListener('beforeunload', disconnect);
    }

    /**
     * 连接到通知服务器
     */
    function connect() {
        if (connected) {
            return;
        }
        
        $.ajax({
            url: contextPath + '/notification/connect',
            type: 'POST',
            dataType: 'json',
            success: function (response) {
                if (response.success) {
                    connected = true;
                    
                    // 开始轮询
                    startPolling();
                    
                    // 调用状态回调
                    if (connectionStatusCallback) {
                        connectionStatusCallback(true);
                    }
                } else {
                    console.error('连接失败: ' + response.message);
                }
            },
            error: function (xhr, status, error) {
                console.error('连接错误: ' + error);
            }
        });
    }

    /**
     * 断开通知服务器连接
     */
    function disconnect() {
        if (!connected) {
            return;
        }
        
        // 停止轮询
        stopPolling();
        
        $.ajax({
            url: contextPath + '/notification/disconnect',
            type: 'POST',
            dataType: 'json',
            success: function (response) {
                connected = false;
                
                // 调用状态回调
                if (connectionStatusCallback) {
                    connectionStatusCallback(false);
                }
            },
            error: function (xhr, status, error) {
                console.error('断开连接错误: ' + error);
            }
        });
    }

    /**
     * 开始轮询获取消息
     */
    function startPolling() {
        if (pollTimer) {
            clearInterval(pollTimer);
        }
        
        // 立即执行一次轮询
        pollMessages();
        
        // 设置定期轮询
        pollTimer = setInterval(pollMessages, POLL_INTERVAL);
    }

    /**
     * 停止轮询
     */
    function stopPolling() {
        if (pollTimer) {
            clearInterval(pollTimer);
            pollTimer = null;
        }
    }

    /**
     * 轮询获取消息
     */
    function pollMessages() {
        if (!connected) {
            return;
        }
        
        $.ajax({
            url: contextPath + '/notification/poll',
            type: 'GET',
            dataType: 'json',
            success: function (response) {
                if (response.success && response.messages && response.messages.length > 0) {
                    // 有新消息，调用回调函数
                    if (notificationCallback) {
                        response.messages.forEach(function (msg) {
                            notificationCallback(msg.content, new Date(msg.time));
                        });
                    }
                }
            },
            error: function (xhr, status, error) {
                console.error('轮询错误: ' + error);
                
                // 如果返回401或403，说明会话已过期，停止轮询
                if (xhr.status === 401 || xhr.status === 403) {
                    disconnect();
                }
            }
        });
    }

    /**
     * 发送消息给指定用户（仅管理员或教师权限）
     * @param {String} targetUserId 目标用户ID
     * @param {String} message 消息内容
     * @param {Function} callback 回调函数
     */
    function sendMessage(targetUserId, message, callback) {
        $.ajax({
            url: contextPath + '/notification/send',
            type: 'POST',
            dataType: 'json',
            data: {
                targetUserId: targetUserId,
                message: message
            },
            success: function (response) {
                if (callback) {
                    callback(response.success, response.message);
                }
            },
            error: function (xhr, status, error) {
                if (callback) {
                    callback(false, error);
                }
            }
        });
    }

    /**
     * 广播消息给所有用户（仅管理员权限）
     * @param {String} message 消息内容
     * @param {Function} callback 回调函数
     */
    function broadcastMessage(message, callback) {
        $.ajax({
            url: contextPath + '/notification/broadcast',
            type: 'POST',
            dataType: 'json',
            data: {
                message: message
            },
            success: function (response) {
                if (callback) {
                    callback(response.success, response.message);
                }
            },
            error: function (xhr, status, error) {
                if (callback) {
                    callback(false, error);
                }
            }
        });
    }

    // 公开API
    return {
        init: init,
        connect: connect,
        disconnect: disconnect,
        sendMessage: sendMessage,
        broadcastMessage: broadcastMessage
    };
})();

// 示例使用方法：
// $(document).ready(function() {
//     // 假设在全局定义了contextPath变量，例如
//     // var contextPath = '${pageContext.request.contextPath}';
//     
//     // 初始化通知系统
//     NotificationSystem.init(
//         // 通知回调
//         function(message, time) {
//             // 显示通知
//             alert('新通知: ' + message);
//         },
//         // 连接状态回调
//         function(isConnected) {
//             console.log('通知系统连接状态: ' + (isConnected ? '已连接' : '已断开'));
//         }
//     );
// }); 