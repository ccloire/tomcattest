<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Score" %>
<%@ page import="com.hello.entity.Course" %>
<%@ page import="com.hello.utils.vo.PagerVO" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>成绩管理 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
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
                                <h4>成绩管理</h4>
                            </div>
                            <div class="card-body">
                                <div class="search-bar">
                                    <form action="${pageContext.request.contextPath}/score" method="get">
                                        <div class="row">
                                            <div class="col-md-2">
                                                <div class="form-group">
                                                    <label for="sno">学号</label>
                                                    <input type="text" class="form-control" id="sno" name="sno" placeholder="请输入学号" value="${sno}">
                                                </div>
                                            </div>
                                            <div class="col-md-2">
                                                <div class="form-group">
                                                    <label for="studentName">学生姓名</label>
                                                    <input type="text" class="form-control" id="studentName" name="studentName" placeholder="请输入姓名" value="${studentName}">
                                                </div>
                                            </div>
                                            <div class="col-md-3">
                                                <div class="form-group">
                                                    <label for="courseId">课程</label>
                                                    <select class="form-control" id="courseId" name="courseId">
                                                        <option value="">所有课程</option>
                                                        <% 
                                                            List<Course> courses = (List<Course>)request.getAttribute("courses");
                                                            String selectedCourseId = (String)request.getAttribute("courseId");
                                                            if (courses != null && !courses.isEmpty()) {
                                                                for (Course course : courses) {
                                                        %>
                                                        <option value="<%= course.getCourseId() %>" <%= (selectedCourseId != null && selectedCourseId.equals(course.getCourseId())) ? "selected" : "" %>><%= course.getCourseName() %></option>
                                                        <%
                                                                }
                                                            }
                                                        %>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="col-md-2">
                                                <div class="form-group">
                                                    <label for="schoolYear">学年</label>
                                                    <input type="text" class="form-control" id="schoolYear" name="schoolYear" placeholder="例：2023-2024" value="${schoolYear}">
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
                                                            <a href="${pageContext.request.contextPath}/score/edit" class="btn btn-success btn-block">录入成绩</a>
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
                                            <th>学号</th>
                                            <th>学生姓名</th>
                                            <th>课程编号</th>
                                            <th>课程名称</th>
                                            <th>平时成绩</th>
                                            <th>期中成绩</th>
                                            <th>期末成绩</th>
                                            <th>总分</th>
                                            <th>等级</th>
                                            <th>学年</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            try {
                                                PagerVO<Score> pager = (PagerVO<Score>)request.getAttribute("pager");
                                                if (pager != null && pager.getList() != null && !pager.getList().isEmpty()) {
                                                    for (Score score : pager.getList()) {
                                        %>
                                        <tr>
                                            <td><%= score.getSno() %></td>
                                            <td><%= score.getStudentName() != null ? score.getStudentName() : "" %></td>
                                            <td><%= score.getCourseId() %></td>
                                            <td><%= score.getCourseName() != null ? score.getCourseName() : "" %></td>
                                            <td><%= score.getDailyScore() != null ? score.getDailyScore() : "" %></td>
                                            <td><%= score.getMidtermScore() != null ? score.getMidtermScore() : "" %></td>
                                            <td><%= score.getFinalScore() != null ? score.getFinalScore() : "" %></td>
                                            <td><%= score.getTotalScore() != null ? score.getTotalScore() : "" %></td>
                                            <td><%= score.getGrade() != null ? score.getGrade() : "" %></td>
                                            <td><%= score.getSchoolYear() != null ? score.getSchoolYear() : "" %></td>
                                            <td>
                                                <div class="btn-group">
                                                    <a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/score/edit?id=<%= score.getId() %>" title="编辑" data-toggle="tooltip"><i class="mdi mdi-pencil"></i></a>
                                                    <a class="btn btn-xs btn-default delete-btn" href="javascript:void(0);" data-id="<%= score.getId() %>" title="删除" data-toggle="tooltip"><i class="mdi mdi-window-close"></i></a>
                                                </div>
                                            </td>
                                        </tr>
                                        <%
                                                    }
                                                } else {
                                        %>
                                        <tr>
                                            <td colspan="11" class="text-center">没有找到匹配的成绩记录</td>
                                        </tr>
                                        <%
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                        %>
                                        <tr>
                                            <td colspan="11" class="text-center">加载数据时出错</td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- 分页 -->
                                <jsp:include page="../_pager.jsp">
                                    <jsp:param name="pageUrl" value="${pageContext.request.contextPath}/score" />
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
            var id = $(this).data('id');
            if (confirm('确定要删除此成绩记录吗？')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/score/delete',
                    type: 'post',
                    data: {id: id},
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