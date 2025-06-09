<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Course" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>成绩统计 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/assets/js/chart.min.js"></script>
    <style>
        .chart-container {
            position: relative;
            height: 300px;
            margin-bottom: 20px;
        }
        .content-wrapper {
            padding: 20px;
        }
    </style>
</head>
<body>
<%
    String userRole = (String)session.getAttribute("role");
    if (userRole == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    Map<String, Object> statistics = (Map<String, Object>)request.getAttribute("statistics");
    Course course = (Course)request.getAttribute("course");
    String statisticsJson = (String)request.getAttribute("statisticsJson");
    String selectedCourseId = (String)request.getAttribute("courseId");
    List<Course> courses = (List<Course>)request.getAttribute("courses");
%>

<div class="content-wrapper">
    <div class="container">
        <h1>成绩统计</h1>

        <!-- 课程选择表单 -->
        <form action="${pageContext.request.contextPath}/score/statistics" method="get" class="mb-4">
            <div class="row">
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="courseId">选择课程</label>
                        <select class="form-control" id="courseId" name="courseId">
                            <option value="">请选择课程</option>
                            <%
                                if (courses != null && !courses.isEmpty()) {
                                    for (Course c : courses) {
                                        boolean isSelected = selectedCourseId != null && selectedCourseId.equals(c.getCourseId());
                            %>
                            <option value="<%= c.getCourseId() %>" <%= isSelected ? "selected" : "" %>><%= c.getCourseName() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="form-group">
                        <label>&nbsp;</label>
                        <button type="submit" class="btn btn-primary btn-block">查看统计</button>
                    </div>
                </div>
            </div>
        </form>

        <div id="statsContent" <% if (statisticsJson != null) { %>data-stats='<%= statisticsJson %>'<% } %>>
            <% if (statistics != null && course != null) { %>
            <div class="alert alert-success">
                <h4><%= course.getCourseName() %> 成绩统计信息</h4>
            </div>

            <div class="row" id="statsCards">
                <!-- 这里的卡片将由JavaScript添加 -->
            </div>

            <div class="row">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header">
                            <h5>分数分布</h5>
                        </div>
                        <div class="card-body">
                            <div class="chart-container">
                                <canvas id="scoreDistributionChart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header">
                            <h5>等级分布</h5>
                        </div>
                        <div class="card-body">
                            <div class="chart-container">
                                <canvas id="gradeDistributionChart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                // 使用后端传来的JSON字符串初始化JavaScript变量
                var statsData = JSON.parse(document.getElementById('statsContent').getAttribute('data-stats'));

                // 创建统计卡片
                var statsCardsHTML = '';

                // 总人数卡片
                statsCardsHTML += '<div class="col-md-3">';
                statsCardsHTML += '<div class="card bg-primary text-white">';
                statsCardsHTML += '<div class="card-body">';
                statsCardsHTML += '<h5 class="card-title">总人数</h5>';
                statsCardsHTML += '<h3>' + statsData.totalStudents + '</h3>';
                statsCardsHTML += '</div></div></div>';

                // 平均分卡片
                statsCardsHTML += '<div class="col-md-3">';
                statsCardsHTML += '<div class="card bg-success text-white">';
                statsCardsHTML += '<div class="card-body">';
                statsCardsHTML += '<h5 class="card-title">平均分</h5>';
                statsCardsHTML += '<h3>' + statsData.averageScore.toFixed(2) + '</h3>';
                statsCardsHTML += '</div></div></div>';

                // 最高分卡片
                statsCardsHTML += '<div class="col-md-3">';
                statsCardsHTML += '<div class="card bg-warning text-white">';
                statsCardsHTML += '<div class="card-body">';
                statsCardsHTML += '<h5 class="card-title">最高分</h5>';
                statsCardsHTML += '<h3>' + statsData.maxScore.toFixed(2) + '</h3>';
                statsCardsHTML += '</div></div></div>';

                // 最低分卡片
                statsCardsHTML += '<div class="col-md-3">';
                statsCardsHTML += '<div class="card bg-danger text-white">';
                statsCardsHTML += '<div class="card-body">';
                statsCardsHTML += '<h5 class="card-title">最低分</h5>';
                statsCardsHTML += '<h3>' + statsData.minScore.toFixed(2) + '</h3>';
                statsCardsHTML += '</div></div></div>';

                // 添加到页面
                document.getElementById('statsCards').innerHTML = statsCardsHTML;

                // 分数分布图表
                var scoreCtx = document.getElementById('scoreDistributionChart').getContext('2d');
                var scoreChart = new Chart(scoreCtx, {
                    type: 'bar',
                    data: {
                        labels: ['0-59', '60-69', '70-79', '80-89', '90-100'],
                        datasets: [{
                            label: '学生人数',
                            data: statsData.scoreRanges,
                            backgroundColor: [
                                'rgba(255, 99, 132, 0.5)',
                                'rgba(255, 159, 64, 0.5)',
                                'rgba(255, 205, 86, 0.5)',
                                'rgba(75, 192, 192, 0.5)',
                                'rgba(54, 162, 235, 0.5)'
                            ],
                            borderColor: [
                                'rgb(255, 99, 132)',
                                'rgb(255, 159, 64)',
                                'rgb(255, 205, 86)',
                                'rgb(75, 192, 192)',
                                'rgb(54, 162, 235)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    stepSize: 1
                                }
                            }
                        }
                    }
                });

                // 等级分布图表
                var gradeCtx = document.getElementById('gradeDistributionChart').getContext('2d');
                var gradeChart = new Chart(gradeCtx, {
                    type: 'pie',
                    data: {
                        labels: ['A (优秀)', 'B (良好)', 'C (中等)', 'D (及格)', 'F (不及格)'],
                        datasets: [{
                            data: statsData.grades,
                            backgroundColor: [
                                'rgba(54, 162, 235, 0.8)',
                                'rgba(75, 192, 192, 0.8)',
                                'rgba(255, 205, 86, 0.8)',
                                'rgba(255, 159, 64, 0.8)',
                                'rgba(255, 99, 132, 0.8)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false
                    }
                });
            </script>
            <% } else if (selectedCourseId != null && !selectedCourseId.isEmpty()) { %>
            <div class="alert alert-warning">
                <h4>没有找到该课程的成绩数据</h4>
            </div>
            <% } %>
        </div>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
</body>
</html> 