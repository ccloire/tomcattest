<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Course" %>
<%@ page import="com.hello.utils.vo.PagerVO" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>课程管理 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/custom-bg.css" rel="stylesheet">
    <style>
        .search-bar {
            margin-bottom: 20px;
        }
    </style>
</head>

<body>
<%
    String userRole = (String)session.getAttribute("role");
    if (userRole == null || !"admin".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
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
                                <h4>课程管理</h4>
                            </div>
                            <div class="card-body">
                                <div class="search-bar">
                                    <form action="${pageContext.request.contextPath}/course" method="get">
                                        <div class="row">
                                            <div class="col-md-3">
                                                <div class="form-group">
                                                    <label for="courseName">课程名称</label>
                                                    <input type="text" class="form-control" id="courseName" name="courseName" placeholder="请输入课程名称关键字" value="${courseName}">
                                                </div>
                                            </div>
                                            <div class="col-md-3">
                                                <div class="form-group">
                                                    <label for="courseType">课程类型</label>
                                                    <select class="form-control" id="courseType" name="courseType">
                                                        <option value="">全部</option>
                                                        <option value="必修" ${courseType == '必修' ? 'selected' : ''}>必修</option>
                                                        <option value="选修" ${courseType == '选修' ? 'selected' : ''}>选修</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="col-md-3">
                                                <div class="form-group">
                                                    <label for="department">所属院系</label>
                                                    <input type="text" class="form-control" id="department" name="department" placeholder="请输入院系名称" value="${department}">
                                                </div>
                                            </div>
                                            <div class="col-md-3">
                                                <div class="form-group">
                                                    <label>&nbsp;</label>
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <button type="submit" class="btn btn-primary btn-block">搜索</button>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <a href="${pageContext.request.contextPath}/course/edit" class="btn btn-success btn-block">添加课程</a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </form>
                                </div>

                                <div class="table-responsive">
                                    <table class="table table-bordered">
                                        <thead>
                                        <tr>
                                            <th>课程编号</th>
                                            <th>课程名称</th>
                                            <th>课程类型</th>
                                            <th>学分</th>
                                            <th>授课教师</th>
                                            <th>所属院系</th>
                                            <th>学期</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            try {
                                                PagerVO<Course> pager = (PagerVO<Course>)request.getAttribute("pager");
                                                // 正常的数据展示
                                                if (pager != null && pager.getList() != null && !pager.getList().isEmpty()) {
                                                    for (Course course : pager.getList()) {
                                        %>
                                        <tr>
                                            <td><%= course.getCourseId() %></td>
                                            <td><%= course.getCourseName() != null ? course.getCourseName() : "" %></td>
                                            <td><%= course.getCourseType() != null ? course.getCourseType() : "" %></td>
                                            <td><%= course.getCredit() != null ? course.getCredit() : "" %></td>
                                            <td><%= course.getTeacherName() != null ? course.getTeacherName() : "" %></td>
                                            <td><%= course.getDepartment() != null ? course.getDepartment() : "" %></td>
                                            <td><%= course.getSemester() != null ? course.getSemester() : "" %></td>
                                            <td>
                                                <div class="btn-group">
                                                    <a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/course/edit?courseId=<%= course.getCourseId() %>" title="编辑" data-toggle="tooltip"><i class="mdi mdi-pencil"></i></a>
                                                    <a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/course/detail?courseId=<%= course.getCourseId() %>" title="详情" data-toggle="tooltip"><i class="mdi mdi-eye"></i></a>
                                                    <a class="btn btn-xs btn-default delete-btn" href="javascript:void(0);" data-id="<%= course.getCourseId() %>" title="删除" data-toggle="tooltip"><i class="mdi mdi-window-close"></i></a>
                                                </div>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        } else {
                                        %>
                                        <tr>
                                            <td colspan="8" class="text-center">没有找到匹配的课程记录</td>
                                        </tr>
                                        <%
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        %>
                                        <tr>
                                            <td colspan="8" class="text-center">加载数据时出错: <%= e.getMessage() %></td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- 分页 -->
                                <jsp:include page="../_pager.jsp">
                                    <jsp:param name="pageUrl" value="${pageContext.request.contextPath}/course" />
                                </jsp:include>
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
<script type="text/javascript">
    $(document).ready(function() {
        // 删除按钮点击事件
        $('.delete-btn').on('click', function() {
            var courseId = $(this).data('id');
            if (confirm('确定要删除课程编号为 ' + courseId + ' 的课程吗？')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/course/delete',
                    type: 'post',
                    data: {courseId: courseId},
                    dataType: 'json',
                    success: function(data) {
                        if (data.success) {
                            alert('删除成功');
                            location.reload();
                        } else {
                            alert('删除失败: ' + data.message);
                        }
                    },
                    error: function() {
                        alert('操作失败，请稍后重试');
                    }
                });
            }
        });
    });
</script>
</body>
</html> 