<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Course" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>课程详情 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>

<body>
<%
    String userRole = (String)session.getAttribute("role");
    if (userRole == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Course course = (Course)request.getAttribute("course");
    if (course == null) {
        response.sendRedirect(request.getContextPath() + "/course");
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
                                <h4>课程详情</h4>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-8">
                                        <div class="panel panel-default">
                                            <div class="panel-heading">
                                                <h3 class="panel-title">基本信息</h3>
                                            </div>
                                            <div class="panel-body">
                                                <table class="table table-bordered">
                                                    <tr>
                                                        <th width="150">课程编号</th>
                                                        <td><%= course.getCourseId() != null ? course.getCourseId() : "" %></td>
                                                    </tr>
                                                    <tr>
                                                        <th>课程名称</th>
                                                        <td><%= course.getCourseName() != null ? course.getCourseName() : "" %></td>
                                                    </tr>
                                                    <tr>
                                                        <th>课程类型</th>
                                                        <td><%= course.getCourseType() != null ? course.getCourseType() : "" %></td>
                                                    </tr>
                                                    <tr>
                                                        <th>学分</th>
                                                        <td><%= course.getCredit() != null ? course.getCredit() : "" %></td>
                                                    </tr>
                                                    <tr>
                                                        <th>授课教师</th>
                                                        <td><%= course.getTeacherName() != null ? course.getTeacherName() : (course.getTno() != null ? course.getTno() : "") %></td>
                                                    </tr>
                                                    <tr>
                                                        <th>所属院系</th>
                                                        <td><%= course.getDepartment() != null ? course.getDepartment() : "" %></td>
                                                    </tr>
                                                    <tr>
                                                        <th>学期</th>
                                                        <td><%= course.getSemester() != null ? course.getSemester() : "" %></td>
                                                    </tr>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="panel panel-default">
                                            <div class="panel-heading">
                                                <h3 class="panel-title">课程描述</h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="well">
                                                    <%= course.getDescription() != null ? course.getDescription().replace("\n", "<br>") : "暂无课程描述" %>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <a href="${pageContext.request.contextPath}/course" class="btn btn-default">返回课程列表</a>
                                    <% if ("admin".equals(userRole)) { %>
                                        <a href="${pageContext.request.contextPath}/course/edit?courseId=<%= course.getCourseId() %>" class="btn btn-primary">编辑课程</a>
                                    <% } %>
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