<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Course" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>成绩统计 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/assets/js/chart.min.js"></script>
    <style>
        .chart-container {
            position: relative;
            height: 300px;
            margin-bottom: 20px;
        }
        
        .statistics-card {
            margin-bottom: 20px;
        }
        
        .grade-distribution {
            margin-top: 20px;
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
                                <h4>成绩统计</h4>
                            </div>
                            <div class="card-body">
                                <!-- 课程选择表单 -->
                                <form action="${pageContext.request.contextPath}/score/statistics" method="get" class="mb-4">
                                    <div class="row">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="courseId">选择课程</label>
                                                <select class="form-control" id="courseId" name="courseId">
                                                    <option value="">请选择课程</option>
                                                    <% 
                                                        List<Course> courses = (List<Course>)request.getAttribute("courses");
                                                        String selectedCourseId = (String)request.getParameter("courseId");
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
                                                <label>&nbsp;</label>
                                                <button type="submit" class="btn btn-primary btn-block">查看统计</button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                                
                                <% 
                                    Map<String, Object> statistics = (Map<String, Object>)request.getAttribute("statistics");
                                    if (statistics != null) {
                                        Course course = (Course)request.getAttribute("course");
                                %>
                                <!-- 统计信息 -->
                                <div class="alert alert-success">
                                    <h4><%= course.getCourseName() %> 成绩统计信息</h4>
                                </div>
                                
                                <div class="row">
                                    <div class="col-md-3">
                                        <div class="card bg-primary text-white statistics-card">
                                            <div class="card-body">
                                                <h5 class="card-title">总人数</h5>
                                                <h3><%= statistics.get("totalStudents") %></h3>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <div class="card bg-success text-white statistics-card">
                                            <div class="card-body">
                                                <h5 class="card-title">平均分</h5>
                                                <h3><%= String.format("%.2f", statistics.get("averageScore")) %></h3>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <div class="card bg-warning text-white statistics-card">
                                            <div class="card-body">
                                                <h5 class="card-title">最高分</h5>
                                                <h3><%= String.format("%.2f", statistics.get("maxScore")) %></h3>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <div class="card bg-danger text-white statistics-card">
                                            <div class="card-body">
                                                <h5 class="card-title">最低分</h5>
                                                <h3><%= String.format("%.2f", statistics.get("minScore")) %></h3>
                                            </div>
                                        </div>
                                    </div>
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
                                    // 分数分布图表
                                    var scoreCtx = document.getElementById('scoreDistributionChart').getContext('2d');
                                    var scoreChart = new Chart(scoreCtx, {
                                        type: 'bar',
                                        data: {
                                            labels: ['0-59', '60-69', '70-79', '80-89', '90-100'],
                                            datasets: [{
                                                label: '学生人数',
                                                data: [
                                                    <%= statistics.get("scoreRange0_59") %>,
                                                    <%= statistics.get("scoreRange60_69") %>,
                                                    <%= statistics.get("scoreRange70_79") %>,
                                                    <%= statistics.get("scoreRange80_89") %>,
                                                    <%= statistics.get("scoreRange90_100") %>
                                                ],
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
                                                data: [
                                                    <%= statistics.get("gradeA") %>,
                                                    <%= statistics.get("gradeB") %>,
                                                    <%= statistics.get("gradeC") %>,
                                                    <%= statistics.get("gradeD") %>,
                                                    <%= statistics.get("gradeF") %>
                                                ],
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