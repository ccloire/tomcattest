<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>服务器内部错误</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .error-container {
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #d9534f;
        }
        .error-details {
            margin-top: 20px;
            border-top: 1px solid #eee;
            padding-top: 10px;
        }
        pre {
            background-color: #f9f9f9;
            padding: 10px;
            border-radius: 3px;
            overflow: auto;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <h1>服务器内部错误</h1>
        <p>系统处理您的请求时遇到了问题。管理员已经收到错误报告。</p>
        
        <div class="error-details">
            <h2>可能的解决方案:</h2>
            <ol>
                <li>刷新页面重试</li>
                <li>清除浏览器缓存后重试</li>
                <li>稍后再试</li>
                <li>如果问题持续，请联系系统管理员</li>
            </ol>
            
            <h2>错误详情:</h2>
            <% if (exception != null) { %>
                <pre><%= exception.toString() %></pre>
            <% } else { %>
                <p>未捕获到异常详情</p>
            <% } %>
        </div>
        
        <p><a href="<%= request.getContextPath() %>/login.jsp">返回登录页</a></p>
    </div>
</body>
</html> 