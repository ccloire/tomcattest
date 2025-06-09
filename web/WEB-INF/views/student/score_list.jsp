<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hello.entity.Score" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>我的成绩 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
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
                                <h4>我的成绩</h4>
                            </div>
                            <div class="card-body">
                                <!-- 测试工具链接 -->
                                <div class="mb-3">
                                    <a href="${pageContext.request.contextPath}/student_test.jsp" class="btn btn-info" target="_blank">打开测试工具</a>
                                </div>

                                <!-- 成绩汇总信息 -->
                                <div class="row mb-4">
                                    <div class="col-md-6">
                                        <div class="card bg-primary text-white">
                                            <div class="card-body">
                                                <h5>综合平均分</h5>
                                                <h3>
                                                    <%
                                                        Double averageScore = (Double)request.getAttribute("averageScore");
                                                        if (averageScore != null) {
                                                            out.print(String.format("%.2f", averageScore));
                                                        } else {
                                                            out.print("暂无数据");
                                                        }
                                                    %>
                                                </h3>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="card bg-success text-white">
                                            <div class="card-body">
                                                <h5>已获得学分</h5>
                                                <h3>
                                                    <%
                                                        Integer totalCredits = (Integer)request.getAttribute("totalCredits");
                                                        if (totalCredits != null) {
                                                            out.print(totalCredits);
                                                        } else {
                                                            out.print("暂无数据");
                                                        }
                                                    %>
                                                </h3>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- 成绩列表 -->
                                <div class="table-responsive">
                                    <table class="table table-bordered table-striped">
                                        <thead>
                                        <tr>
                                            <th>课程代码</th>
                                            <th>课程名称</th>
                                            <th>学分</th>
                                            <th>学期</th>
                                            <th>学年</th>
                                            <th>平时成绩</th>
                                            <th>期末成绩</th>
                                            <th>总成绩</th>
                                            <th>等级</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            List<Score> scores = (List<Score>)request.getAttribute("scores");
                                            if (scores != null && !scores.isEmpty()) {
                                                for (Score score : scores) {
                                        %>
                                        <tr>
                                            <td><%= score.getCourseId() %></td>
                                            <td><%= score.getCourseName() %></td>
                                            <td>--</td>
                                            <td><%= score.getSemester() %></td>
                                            <td><%= score.getSchoolYear() %></td>
                                            <td><%= score.getDailyScore() %></td>
                                            <td><%= score.getFinalScore() %></td>
                                            <td><%= score.getTotalScore() %></td>
                                            <td>
                                                    <span class="<%= getGradeClass(score.getGrade()) %>">
                                                        <%= score.getGrade() %>
                                                    </span>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        } else {
                                        %>
                                        <tr>
                                            <td colspan="9" class="text-center">暂无成绩数据</td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                        </tbody>
                                    </table>
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

<%!
    // 根据等级返回对应的CSS类
    private String getGradeClass(String grade) {
        if (grade == null) return "";

        switch (grade) {
            case "A+":
            case "A":
                return "badge badge-success";
            case "B+":
            case "B":
                return "badge badge-info";
            case "C+":
            case "C":
                return "badge badge-primary";
            case "D":
                return "badge badge-warning";
            case "F":
                return "badge badge-danger";
            default:
                return "badge badge-secondary";
        }
    }
%>
</body>
</html> 