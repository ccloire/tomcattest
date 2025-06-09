<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>上传证书 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/bootstrap-datepicker3.min.css" rel="stylesheet">
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
        <jsp:include page="/nonjstl_aside_header.jsp" />

        <!--页面主要内容-->
        <main class="lyear-layout-content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="card">
                            <div class="card-header">
                                <h4>上传证书</h4>
                            </div>
                            <div class="card-body">
                                <% if (request.getAttribute("error") != null) { %>
                                <div class="alert alert-danger">
                                    <strong>错误：</strong> <%= request.getAttribute("error") %>
                                </div>
                                <% } %>

                                <div class="upload-instruction">
                                    <div class="instruction-icon">
                                        <i class="mdi mdi-certificate"></i>
                                    </div>
                                    <h5>证书上传指南</h5>
                                    <p>请填写您的证书信息，所有带 <span class="text-danger">*</span> 的字段为必填项。</p>
                                </div>

                                <form action="${pageContext.request.contextPath}/certificate/doUpload" method="post" class="row">
                                    <div class="form-group col-md-12">
                                        <label for="certName">证书名称 <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="certName" name="certName" placeholder="请输入证书名称" required>
                                        <small class="form-text text-muted">例如：大学英语四级证书 (CET-4)、计算机等级证书等</small>
                                    </div>
                                    <div class="form-group col-md-6">
                                        <label for="certNo">证书编号 <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="certNo" name="certNo" placeholder="请输入证书编号" required>
                                        <small class="form-text text-muted">请输入证书上的唯一编号</small>
                                    </div>
                                    <div class="form-group col-md-6">
                                        <label for="obtainDate">获得日期 <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control js-datepicker" id="obtainDate" name="obtainDate" placeholder="请选择获得日期" data-date-format="yyyy-mm-dd" required>
                                        <small class="form-text text-muted">证书颁发的日期</small>
                                    </div>
                                    <div class="form-group col-md-6">
                                        <label for="expireDate">有效期至</label>
                                        <input type="text" class="form-control js-datepicker" id="expireDate" name="expireDate" placeholder="请选择有效期至(可选)" data-date-format="yyyy-mm-dd">
                                        <small class="form-text text-muted">如证书有效期，请填写截止日期；如为永久有效，可不填</small>
                                    </div>
                                    <div class="form-group col-md-12">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="mdi mdi-upload"></i> 上传证书
                                        </button>
                                        <button type="button" class="btn btn-default" onclick="history.back()">
                                            <i class="mdi mdi-arrow-left"></i> 返回
                                        </button>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
        $('.js-datepicker').datepicker({
            language: 'zh-CN',
            autoclose: true,
            todayHighlight: true,
            orientation: 'bottom'
        });
    });
</script>

<style>
    /* 美化上传页面 */
    .upload-instruction {
        text-align: center;
        margin-bottom: 30px;
        padding: 20px;
        background-color: rgba(114, 102, 186, 0.05);
        border-radius: 10px;
    }

    .instruction-icon {
        font-size: 48px;
        color: #7266ba;
        margin-bottom: 15px;
    }

    .upload-instruction h5 {
        color: #5e4caf;
        font-weight: 600;
        margin-bottom: 10px;
    }

    .upload-instruction p {
        color: #666;
        margin-bottom: 0;
    }

    .form-group {
        margin-bottom: 25px;
    }

    .form-control {
        border-radius: 6px;
        border: 1px solid #e0e0e0;
        padding: 10px 15px;
        transition: all 0.3s ease;
    }

    .form-control:focus {
        border-color: #7266ba;
        box-shadow: 0 0 8px rgba(114, 102, 186, 0.4);
    }

    .form-text {
        margin-top: 5px;
        font-size: 12px;
    }

    .btn {
        border-radius: 6px;
        padding: 10px 20px;
        transition: all 0.3s ease;
    }

    .btn-primary {
        background-color: #7266ba;
        border-color: #7266ba;
    }

    .btn-primary:hover {
        background-color: #5e4caf;
        border-color: #5e4caf;
        transform: translateY(-2px);
        box-shadow: 0 4px 10px rgba(114, 102, 186, 0.3);
    }

    .btn i {
        margin-right: 5px;
    }
</style>
</body>
</html> 