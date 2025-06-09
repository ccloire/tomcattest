<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Certificate" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>证书详情 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>

<body>
<%
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Certificate certificate = (Certificate) request.getAttribute("certificate");
    if (certificate == null) {
        response.sendError(404, "证书不存在");
        return;
    }
%>

<div class="lyear-layout-web">
    <div class="lyear-layout-container">
        <jsp:include page="../_aside_header.jsp" />

        <main class="lyear-layout-content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="card">
                            <div class="card-header">
                                <h4>证书详情</h4>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <h5>基本信息</h5>
                                        <table class="table table-bordered">
                                            <tr>
                                                <th width="30%">学号</th>
                                                <td><%= certificate.getSno() %></td>
                                            </tr>
                                            <tr>
                                                <th>学生姓名</th>
                                                <td><%= certificate.getStudentName() != null ? certificate.getStudentName() : "未知" %></td>
                                            </tr>
                                            <tr>
                                                <th>证书类型</th>
                                                <td><%= certificate.getCertType() %></td>
                                            </tr>
                                            <tr>
                                                <th>证书编号</th>
                                                <td><%= certificate.getCertNumber() != null ? certificate.getCertNumber() : "无" %></td>
                                            </tr>
                                            <tr>
                                                <th>分数/成绩</th>
                                                <td><%= certificate.getScore() != null ? certificate.getScore() : "无" %></td>
                                            </tr>
                                            <tr>
                                                <th>颁发日期</th>
                                                <td><%= certificate.getIssueDate() != null ? sdf.format(certificate.getIssueDate()) : "未知" %></td>
                                            </tr>
                                            <tr>
                                                <th>有效期至</th>
                                                <td><%= certificate.getValidUntil() != null ? sdf.format(certificate.getValidUntil()) : "长期有效" %></td>
                                            </tr>
                                        </table>
                                    </div>
                                    <div class="col-md-6">
                                        <h5>验证信息</h5>
                                        <table class="table table-bordered">
                                            <tr>
                                                <th width="30%">验证状态</th>
                                                <td>
                                                    <% if ("已验证".equals(certificate.getVerifyStatus())) { %>
                                                    <span class="label label-success">已验证</span>
                                                    <% } else if ("验证失败".equals(certificate.getVerifyStatus())) { %>
                                                    <span class="label label-danger">验证失败</span>
                                                    <% } else { %>
                                                    <span class="label label-warning">未验证</span>
                                                    <% } %>
                                                </td>
                                            </tr>
                                            <tr>
                                                <th>上传时间</th>
                                                <td><%= certificate.getUploadTime() != null ? sdf.format(certificate.getUploadTime()) : "未知" %></td>
                                            </tr>
                                            <tr>
                                                <th>验证时间</th>
                                                <td><%= certificate.getVerifyTime() != null ? sdf.format(certificate.getVerifyTime()) : "未验证" %></td>
                                            </tr>
                                            <tr>
                                                <th>验证备注</th>
                                                <td><%= certificate.getVerifyRemark() != null ? certificate.getVerifyRemark() : "无" %></td>
                                            </tr>
                                        </table>
                                    </div>
                                </div>
                                
                                <% if (certificate.getScanPath() != null && !certificate.getScanPath().isEmpty()) { %>
                                <div class="row">
                                    <div class="col-md-12">
                                        <h5>证书扫描件</h5>
                                        <div class="well">
                                            <a href="${pageContext.request.contextPath}/<%= certificate.getScanPath() %>" target="_blank" class="btn btn-primary">查看原图</a>
                                            <div class="mt-3">
                                                <img src="${pageContext.request.contextPath}/<%= certificate.getScanPath() %>" class="img-responsive" style="max-width: 100%; max-height: 500px;">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <% } %>
                                
                                <div class="row">
                                    <div class="col-md-12 mt-3">
                                        <a href="${pageContext.request.contextPath}/certificate" class="btn btn-default">返回列表</a>
                                        <a href="${pageContext.request.contextPath}/certificate/edit?id=<%= certificate.getId() %>" class="btn btn-primary">编辑证书</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/perfect-scrollbar.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>
</body>
</html> 