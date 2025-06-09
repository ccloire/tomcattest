<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>404 - 页面不存在 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f5f5f5;
        }
        .error-page {
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;
        }
        .error-page .error-code {
            font-size: 120px;
            color: #33cabb;
            font-weight: 300;
        }
        .error-page .error-title {
            font-size: 32px;
            color: #333;
            margin-bottom: 30px;
        }
        .error-page .error-desc {
            color: #666;
            margin-bottom: 30px;
        }
    </style>
</head>

<body>
<div class="error-page">
    <div class="container">
        <div class="error-code">404</div>
        <div class="error-title">页面不存在</div>
        <div class="error-desc">抱歉，您访问的页面不存在或已被删除。</div>
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">返回首页</a>
        <a href="javascript:history.back();" class="btn btn-default">返回上一页</a>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
</body>
</html>