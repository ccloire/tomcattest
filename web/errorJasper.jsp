<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP解析错误</title>
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
        <h1>JSP文件解析错误</h1>
        <p>系统在处理JSP页面时遇到了问题，这通常是由于标签库问题或JSP语法错误导致的。</p>
        
        <div class="error-details">
            <h2>可能的解决方案:</h2>
            <ol>
                <li>检查JSTL标签库是否正确配置</li>
                <li>确保web.xml中的标签库路径正确</li>
                <li>检查JSP文件中的语法是否有误</li>
                <li>确保所有引用的类和方法存在</li>
            </ol>
            
            <h2>错误详情:</h2>
            <% if (exception != null) { %>
                <pre><%= exception.toString() %></pre>
                <pre>
                <% exception.printStackTrace(new java.io.PrintWriter(out)); %>
                </pre>
            <% } else { %>
                <p>未捕获到异常详情</p>
            <% } %>
        </div>
        
        <p><a href="<%= request.getContextPath() %>/login.jsp">返回登录页</a></p>
    </div>
</body>
</html> 