package com.hello.servlet;

import com.hello.utils.ApiResult;
import com.hello.utils.WebSocketServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.text.SimpleDateFormat;

/**
 * 处理实时通知的Servlet，替代WebSocket实现
 */
// 注解已移至web.xml配置
public class NotificationServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        if (path.equals("/notification/connect")) {
            connect(req, resp);
        } else if (path.equals("/notification/poll")) {
            poll(req, resp);
        } else if (path.equals("/notification/send")) {
            send(req, resp);
        } else if (path.equals("/notification/broadcast")) {
            broadcast(req, resp);
        } else if (path.equals("/notification/disconnect")) {
            disconnect(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * 用户连接/上线
     */
    private void connect(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = getUserId(req);
        if (userId == null) {
            resp.getWriter().print(ApiResult.json(false, "用户未登录"));
            return;
        }

        WebSocketServer.userConnect(userId);
        resp.getWriter().print(ApiResult.json(true, "连接成功"));
    }

    /**
     * 获取消息（轮询）
     */
    private void poll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = getUserId(req);
        if (userId == null) {
            resp.getWriter().print(ApiResult.json(false, "用户未登录"));
            return;
        }

        // 更新用户活动状态
        WebSocketServer.userActivity(userId);

        // 获取未读消息
        List<WebSocketServer.Message> messages = WebSocketServer.getAndClearMessages(userId);

        // 设置响应类型为JSON
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // 手动构建JSON响应
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"success\":true,\"messages\":[");

        for (int i = 0; i < messages.size(); i++) {
            WebSocketServer.Message message = messages.get(i);

            if (i > 0) {
                jsonBuilder.append(",");
            }

            jsonBuilder.append("{")
                    .append("\"content\":\"").append(escapeJson(message.getContent())).append("\",")
                    .append("\"time\":").append(message.getSendTime().getTime())
                    .append("}");
        }

        jsonBuilder.append("]}");
        out.print(jsonBuilder.toString());
    }

    /**
     * 转义JSON字符串中的特殊字符
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 发送消息给特定用户
     */
    private void send(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role) && !"teacher".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        String targetUserId = req.getParameter("targetUserId");
        String message = req.getParameter("message");

        if (targetUserId == null || targetUserId.isEmpty()) {
            resp.getWriter().print(ApiResult.json(false, "目标用户ID不能为空"));
            return;
        }

        if (message == null || message.isEmpty()) {
            resp.getWriter().print(ApiResult.json(false, "消息内容不能为空"));
            return;
        }

        boolean result = WebSocketServer.sendMessage(targetUserId, message);
        if (result) {
            resp.getWriter().print(ApiResult.json(true, "消息已发送"));
        } else {
            resp.getWriter().print(ApiResult.json(false, "消息发送失败，用户可能不在线"));
        }
    }

    /**
     * 广播消息给所有用户
     */
    private void broadcast(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        String message = req.getParameter("message");
        if (message == null || message.isEmpty()) {
            resp.getWriter().print(ApiResult.json(false, "消息内容不能为空"));
            return;
        }

        int count = WebSocketServer.broadcastMessage(message);
        resp.getWriter().print(ApiResult.json(true, "消息已广播给" + count + "个用户"));
    }

    /**
     * 用户断开连接/下线
     */
    private void disconnect(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = getUserId(req);
        if (userId == null) {
            resp.getWriter().print(ApiResult.json(false, "用户未登录"));
            return;
        }

        WebSocketServer.userDisconnect(userId);
        resp.getWriter().print(ApiResult.json(true, "已断开连接"));
    }

    /**
     * 获取当前登录用户的ID
     */
    private String getUserId(HttpServletRequest req) {
        String role = (String) req.getSession().getAttribute("role");
        if (role == null) {
            return null;
        }

        Object user = req.getSession().getAttribute("user");
        if (user == null) {
            return null;
        }

        try {
            if ("admin".equals(role)) {
                java.lang.reflect.Method method = user.getClass().getMethod("getUsername");
                return (String) method.invoke(user);
            } else if ("teacher".equals(role)) {
                java.lang.reflect.Method method = user.getClass().getMethod("getTno");
                return (String) method.invoke(user);
            } else if ("student".equals(role)) {
                java.lang.reflect.Method method = user.getClass().getMethod("getSno");
                return (String) method.invoke(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
} 