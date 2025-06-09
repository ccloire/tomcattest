<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>登录 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <style>
        body {
            background-image: url('${pageContext.request.contextPath}/assets/images/p1.png');
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            position: relative;
        }

        body::before {
            content: "";
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.4);
            z-index: -1;
        }

        .lyear-wrapper {
            position: relative;
        }

        .lyear-login {
            display: flex !important;
            min-height: 100vh;
            align-items: center !important;
            justify-content: center !important;
        }

        .lyear-login:after{
            content: '';
            min-height: inherit;
            font-size: 0;
        }

        .login-center {
            background: rgba(255, 255, 255, 0.9);
            min-width: 29.25rem;
            padding: 2.14286em 3.57143em;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            margin: 2.85714em;
            animation: fadeIn 1s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .login-header {
            margin-bottom: 1.5rem !important;
        }

        .login-header h1 {
            color: #5e4caf;
            font-weight: 600;
        }

        .login-center .has-feedback.feedback-left .form-control {
            padding-left: 38px;
            padding-right: 12px;
            border-radius: 6px;
            border: 1px solid #e0e0e0;
            transition: all 0.3s ease;
        }

        .login-center .has-feedback.feedback-left .form-control:focus {
            border-color: #7266ba;
            box-shadow: 0 0 8px rgba(114, 102, 186, 0.4);
        }

        .login-center .has-feedback.feedback-left .form-control-feedback {
            left: 0;
            right: auto;
            width: 38px;
            height: 38px;
            line-height: 38px;
            z-index: 4;
            color: #dcdcdc;
        }

        .login-center .has-feedback.feedback-left.row .form-control-feedback {
            left: 15px;
        }

        .btn-block {
            padding: 10px;
            font-size: 16px;
            font-weight: 500;
            border-radius: 6px;
            background-color: #7266ba;
            border-color: #7266ba;
            transition: all 0.3s ease;
        }

        .btn-block:hover {
            background-color: #5e4caf;
            border-color: #5e4caf;
            transform: translateY(-2px);
            box-shadow: 0 4px 10px rgba(114, 102, 186, 0.3);
        }

        .error-message {
            color: #dc3545;
            margin-top: 10px;
            display: none;
        }
    </style>
</head>

<body>
<div class="row lyear-wrapper" style="background-image: url(${pageContext.request.contextPath}/assets/images/p1.png); background-size: cover;">
    <div class="lyear-login">
        <div class="login-center">
            <div class="login-header text-center">
                <h3>学生学业管理系统</h3>
            </div>
            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group has-feedback feedback-left">
                    <input type="text" placeholder="请输入您的用户名" class="form-control" name="username" id="username" />
                    <span class="mdi mdi-account form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group has-feedback feedback-left">
                    <input type="password" placeholder="请输入密码" class="form-control" id="password" name="password" />
                    <span class="mdi mdi-lock form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group has-feedback feedback-left row">
                    <div class="col-xs-7">
                        <input type="text" name="captcha" id="captcha" class="form-control" placeholder="验证码">
                        <span class="mdi mdi-check-all form-control-feedback" aria-hidden="true"></span>
                    </div>
                    <div class="col-xs-5">
                        <img src="${pageContext.request.contextPath}/captcha" class="pull-right" style="cursor: pointer;" onclick="this.src=this.src+'?d='+Math.random();" title="点击刷新" alt="captcha">
                    </div>
                </div>
                <div class="form-group" style="text-align: center">
                    <input type="radio" checked name="usertype" value="admin">管理员
                    <input type="radio" name="usertype" value="teacher">教师
                    <input type="radio" name="usertype" value="student">学生
                </div>
                <div class="form-group">
                    <button class="btn btn-block btn-primary" type="button" onclick="login()">立即登录</button>
                </div>
                <div class="error-message" id="errorMsg"></div>
            </form>
            <hr>
            <footer class="col-sm-12 text-center">
                <p class="m-b-0">学生学业管理系统</p>
            </footer>
        </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript">
    function login() {
        let username = $("#username").val()
        let password = $("#password").val()
        let captcha = $("#captcha").val()
        let usertype = $("input[name=usertype]:checked").val()

        // 清除之前的错误信息
        $("#errorMsg").hide().html("");

        // 显示加载中
        let loginBtn = $("button.btn-primary");
        let originalText = loginBtn.text();
        loginBtn.text("登录中...").prop("disabled", true);

        $.ajax({
            type:"post",
            url:"${pageContext.request.contextPath}/login",
            dataType:'json',
            data:{username, password,usertype,captcha},
            success:function (data) {
                if(data.success){
                    location.href = '${pageContext.request.contextPath}/index.jsp'
                }else {
                    $("#errorMsg").html(data.message).show();
                    loginBtn.text(originalText).prop("disabled", false);
                    // 刷新验证码
                    $("img[alt='captcha']").attr("src", "${pageContext.request.contextPath}/captcha?d=" + Math.random());
                }
            },
            error:function (xhr, status, error) {
                console.error("AJAX错误:", status, error);
                console.log("响应文本:", xhr.responseText);
                $("#errorMsg").html("请求服务器错误！<br>状态: " + status + "<br>错误: " + error + "<br>请检查服务器日志获取详细信息").show();
                loginBtn.text(originalText).prop("disabled", false);
                // 刷新验证码
                $("img[alt='captcha']").attr("src", "${pageContext.request.contextPath}/captcha?d=" + Math.random());
            }
        })
    }
</script>
</body>
</html>
