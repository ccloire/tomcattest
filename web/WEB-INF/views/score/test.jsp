<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>测试页面</title>
</head>
<body>
    <h1>这是一个JSP测试页面</h1>
    <p>当前时间: <%= new Date() %></p>
    
    <% 
        String name = request.getParameter("name");
        if (name != null && !name.isEmpty()) {
    %>
        <p>欢迎, <%= name %>!</p>
    <% 
        } else {
    %>
        <p>欢迎访问!</p>
    <% 
        }
    %>
    
    <a href="${pageContext.request.contextPath}/score">返回成绩管理</a>
</body>
</html> 