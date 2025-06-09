package com.hello.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * 消息通知服务器，使用普通HTTP轮询代替WebSocket
 * 这个实现不依赖于WebSocket库，避免兼容性问题
 */
public class WebSocketServer {
    // 使用线程安全的Map存储所有未读消息
    private static final Map<String, List<Message>> messageQueue = new ConcurrentHashMap<>();
    // 记录在线用户的最后活动时间
    private static final Map<String, Long> userLastActive = new ConcurrentHashMap<>();
    // 过期时间：30分钟
    private static final long EXPIRE_TIME = 30 * 60 * 1000;

    /**
     * 消息类，存储消息内容和发送时间
     */
    public static class Message {
        private String content;
        private Date sendTime;

        public Message(String content) {
            this.content = content;
            this.sendTime = new Date();
        }

        public String getContent() {
            return content;
        }

        public Date getSendTime() {
            return sendTime;
        }
    }

    /**
     * 用户上线，记录用户活动时间
     * @param userId 用户ID
     */
    public static void userConnect(String userId) {
        if (userId != null && !userId.isEmpty()) {
            userLastActive.put(userId, System.currentTimeMillis());
            System.out.println("用户上线: " + userId + "，当前在线数: " + getOnlineCount());
        }
    }

    /**
     * 用户活动，更新最后活动时间
     * @param userId 用户ID
     */
    public static void userActivity(String userId) {
        if (userId != null && !userId.isEmpty()) {
            userLastActive.put(userId, System.currentTimeMillis());
        }
    }

    /**
     * 用户下线
     * @param userId 用户ID
     */
    public static void userDisconnect(String userId) {
        if (userId != null) {
            userLastActive.remove(userId);
            System.out.println("用户下线: " + userId + "，当前在线数: " + getOnlineCount());
        }
    }

    /**
     * 向指定用户发送消息
     * @param userId 用户ID
     * @param message 消息内容
     */
    public static boolean sendMessage(String userId, String message) {
        if (userId == null || message == null || message.isEmpty()) {
            return false;
        }

        // 初始化用户的消息队列（如果不存在）
        messageQueue.putIfAbsent(userId, new ArrayList<>());

        // 添加消息到队列
        messageQueue.get(userId).add(new Message(message));
        System.out.println("已向用户 " + userId + " 发送消息: " + message);

        return true;
    }

    /**
     * 向所有在线用户广播消息
     * @param message 消息内容
     * @return 成功发送的用户数
     */
    public static int broadcastMessage(String message) {
        if (message == null || message.isEmpty()) {
            return 0;
        }

        int count = 0;
        // 清理过期用户
        cleanExpiredUsers();

        // 发送给所有在线用户
        for (String userId : userLastActive.keySet()) {
            if (sendMessage(userId, message)) {
                count++;
            }
        }

        System.out.println("广播消息: " + message + "，已发送给 " + count + " 个用户");
        return count;
    }

    /**
     * 获取并清除用户的未读消息
     * @param userId 用户ID
     * @return 未读消息列表
     */
    public static List<Message> getAndClearMessages(String userId) {
        if (userId == null || userId.isEmpty()) {
            return new ArrayList<>();
        }

        // 更新用户活动时间
        userActivity(userId);

        // 获取消息
        List<Message> messages = messageQueue.get(userId);
        if (messages == null) {
            return new ArrayList<>();
        }

        // 清除已读消息
        List<Message> result = new ArrayList<>(messages);
        messages.clear();

        return result;
    }

    /**
     * 获取当前在线用户数
     * @return 在线用户数
     */
    public static int getOnlineCount() {
        cleanExpiredUsers();
        return userLastActive.size();
    }

    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return 是否在线
     */
    public static boolean isUserOnline(String userId) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }

        Long lastActive = userLastActive.get(userId);
        if (lastActive == null) {
            return false;
        }

        // 检查是否超过过期时间
        return System.currentTimeMillis() - lastActive < EXPIRE_TIME;
    }

    /**
     * 清理过期用户
     */
    private static void cleanExpiredUsers() {
        long currentTime = System.currentTimeMillis();
        List<String> expiredUsers = new ArrayList<>();

        for (Map.Entry<String, Long> entry : userLastActive.entrySet()) {
            if (currentTime - entry.getValue() > EXPIRE_TIME) {
                expiredUsers.add(entry.getKey());
            }
        }

        for (String userId : expiredUsers) {
            userLastActive.remove(userId);
            messageQueue.remove(userId);
            System.out.println("移除过期用户: " + userId);
        }
    }
}