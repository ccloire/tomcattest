<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>修改密码 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/custom-bg.css" rel="stylesheet">
</head>

<body>
<%
    String userRole = (String)session.getAttribute("role");
    if (userRole == null || !"student".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>

<div class="lyear-layout-web">
    <div class="lyear-layout-container">
        <jsp:include page="../_aside_header.jsp" />

        <!--页面主要内容-->
        <main class="lyear-layout-content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="card">
                            <div class="card-header">
                                <h4>修改密码</h4>
                            </div>
                            <div class="card-body">
                                <form id="passwordForm" class="form-horizontal">
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">原密码</label>
                                        <div class="col-md-7">
                                            <input type="password" class="form-control" name="oldPassword" id="oldPassword" placeholder="请输入原密码">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">新密码</label>
                                        <div class="col-md-7">
                                            <input type="password" class="form-control" name="newPassword" id="newPassword" placeholder="请输入新密码">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">确认新密码</label>
                                        <div class="col-md-7">
                                            <input type="password" class="form-control" name="confirmPassword" id="confirmPassword" placeholder="请再次输入新密码">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-md-9 col-md-offset-3">
                                            <button type="button" class="btn btn-primary" id="saveBtn">修改密码</button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <!--End 页面主要内容-->
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/perfect-scrollbar.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
        $('#saveBtn').click(function() {
            var oldPassword = $('#oldPassword').val();
            var newPassword = $('#newPassword').val();
            var confirmPassword = $('#confirmPassword').val();

            if (!oldPassword) {
                alert('请输入原密码');
                return;
            }
            if (!newPassword) {
                alert('请输入新密码');
                return;
            }
            if (!confirmPassword) {
                alert('请确认新密码');
                return;
            }
            if (newPassword !== confirmPassword) {
                alert('两次输入的新密码不一致');
                return;
            }

            $.ajax({
                url: '${pageContext.request.contextPath}/student/updatePassword',
                type: 'post',
                data: {
                    oldPassword: oldPassword,
                    newPassword: newPassword,
                    confirmPassword: confirmPassword
                },
                dataType: 'json',
                success: function(data) {
                    if (data.success) {
                        alert('密码修改成功！');
                        $('#passwordForm')[0].reset();
                    } else {
                        alert('修改失败: ' + data.message);
                    }
                },
                error: function() {
                    alert('服务器错误，请稍后再试');
                }
            });
        });
    });
</script>
</body>
</html> 