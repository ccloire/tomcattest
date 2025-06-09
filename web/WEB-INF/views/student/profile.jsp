<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Student" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>个人资料 - 学生学业管理系统</title>
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

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Student student = (Student) request.getAttribute("student");
    if (student == null) {
        response.sendRedirect(request.getContextPath() + "/logout");
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
                                <h4>个人资料</h4>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-3 text-center">
                                        <img src="${pageContext.request.contextPath}/assets/images/default-avatar.jpg" alt="头像" class="img-circle img-thumbnail">
                                    </div>
                                    <div class="col-md-9">
                                        <form id="profileForm" class="form-horizontal">
                                            <div class="form-group">
                                                <label class="col-md-3 control-label">学号</label>
                                                <div class="col-md-9">
                                                    <input type="text" class="form-control" readonly value="<%= student.getSno() %>">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-md-3 control-label">姓名</label>
                                                <div class="col-md-9">
                                                    <input type="text" class="form-control" readonly value="<%= student.getName() %>">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-md-3 control-label">性别</label>
                                                <div class="col-md-9">
                                                    <input type="text" class="form-control" readonly value="<%= student.getGender() != null ? student.getGender() : "未设置" %>">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-md-3 control-label">年龄</label>
                                                <div class="col-md-9">
                                                    <input type="text" class="form-control" readonly value="<%= student.getAge() != null ? student.getAge() : "未设置" %>">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-md-3 control-label">入学日期</label>
                                                <div class="col-md-9">
                                                    <input type="text" class="form-control" readonly value="<%= student.getEnterdate() != null ? sdf.format(student.getEnterdate()) : "未设置" %>">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-md-3 control-label">班级</label>
                                                <div class="col-md-9">
                                                    <input type="text" class="form-control" readonly value="<%= student.getClazzno() != null ? student.getClazzno() : "未分配" %>">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-md-3 control-label">联系电话</label>
                                                <div class="col-md-9">
                                                    <input type="text" class="form-control" name="tele" id="tele" value="<%= student.getTele() != null ? student.getTele() : "" %>">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-md-3 control-label">联系地址</label>
                                                <div class="col-md-9">
                                                    <input type="text" class="form-control" name="address" id="address" value="<%= student.getAddress() != null ? student.getAddress() : "" %>">
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="col-md-9 col-md-offset-3">
                                                    <button type="button" class="btn btn-primary" id="saveBtn">保存修改</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
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
            var tele = $('#tele').val();
            var address = $('#address').val();

            $.ajax({
                url: '${pageContext.request.contextPath}/student/updateProfile',
                type: 'post',
                data: {
                    tele: tele,
                    address: address
                },
                dataType: 'json',
                success: function(data) {
                    if (data.success) {
                        alert('个人资料更新成功！');
                        // 可以选择刷新页面
                        // location.reload();
                    } else {
                        alert('更新失败: ' + data.message);
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