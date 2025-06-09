<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Score" %>
<%@ page import="com.hello.entity.Teacher" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.hello.utils.JdbcHelper" %>
<%@ page import="java.util.Enumeration" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>学生成绩查看 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/custom-bg.css" rel="stylesheet">
</head>

<body>
<%
    String userRole = (String)session.getAttribute("role");
    String tno = (String)session.getAttribute("username");

    // 尝试从其他会话属性获取教师编号
    Teacher teacherObj = (Teacher)session.getAttribute("user");
    if (teacherObj != null) {
        tno = teacherObj.getTno();
    }

    // 如果仍然为空，设置一个默认值用于测试
    if (tno == null || tno.isEmpty()) {
        tno = "T001";  // 使用默认值进行测试
    }

    if (userRole == null || !"teacher".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    System.out.println("=== score_list.jsp 开始渲染 ===");
    System.out.println("教师编号: " + tno);

    // 输出所有会话属性
    System.out.println("会话中的所有属性:");
    Enumeration<String> attributeNames = session.getAttributeNames();
    while (attributeNames.hasMoreElements()) {
        String name = attributeNames.nextElement();
        System.out.println(name + ": " + session.getAttribute(name));
    }

    // 检查数据库中是否有该教师的记录
    JdbcHelper dbCheck = new JdbcHelper();
    try {
        ResultSet teacherRs = dbCheck.executeQuery("SELECT * FROM tb_teacher WHERE tno = ?", tno);
        if (teacherRs != null && teacherRs.next()) {
            System.out.println("找到教师记录: " + teacherRs.getString("tno") + ", " + teacherRs.getString("name"));
        } else {
            System.out.println("未找到教师记录");
        }

        // 直接检查成绩表中是否有该教师的记录
        ResultSet scoreCheckRs = dbCheck.executeQuery("SELECT COUNT(*) as count FROM tb_score WHERE tno = ?", tno);
        if (scoreCheckRs != null && scoreCheckRs.next()) {
            System.out.println("成绩表中该教师的记录数: " + scoreCheckRs.getInt("count"));
        }

        // 检查所有教师的成绩记录
        ResultSet allScoresRs = dbCheck.executeQuery("SELECT tno, COUNT(*) as count FROM tb_score GROUP BY tno");
        System.out.println("成绩表中所有教师的记录数:");
        while (allScoresRs != null && allScoresRs.next()) {
            System.out.println("教师 " + allScoresRs.getString("tno") + ": " + allScoresRs.getInt("count") + "条记录");
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        dbCheck.closeDB();
    }

    // 直接在JSP中执行SQL查询
    List<Score> scores = new ArrayList<>();
    JdbcHelper helper = new JdbcHelper();

    try {
        // 使用CONVERT函数处理校对规则问题
        String sql = "SELECT * FROM tb_score WHERE CONVERT(tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci";
        System.out.println("执行SQL: " + sql);
        System.out.println("参数: " + tno);

        ResultSet resultSet = helper.executeQuery(sql, tno);

        while (resultSet != null && resultSet.next()) {
            Score score = new Score();
            score.setId(resultSet.getInt("id"));
            score.setSno(resultSet.getString("sno"));
            score.setCourseId(resultSet.getString("course_id"));
            score.setTno(resultSet.getString("tno"));
            score.setDailyScore(resultSet.getDouble("daily_score"));
            score.setMidtermScore(resultSet.getDouble("midterm_score"));
            score.setFinalScore(resultSet.getDouble("final_score"));
            score.setTotalScore(resultSet.getDouble("total_score"));
            score.setGrade(resultSet.getString("grade"));
            score.setSemester(resultSet.getInt("semester"));
            score.setSchoolYear(resultSet.getString("school_year"));
            score.setRemark(resultSet.getString("remark"));

            // 获取学生姓名
            String studentSql = "SELECT name FROM tb_student WHERE sno = ?";
            ResultSet studentRs = helper.executeQuery(studentSql, score.getSno());
            if (studentRs != null && studentRs.next()) {
                score.setStudentName(studentRs.getString("name"));
            }

            // 获取课程名称
            String courseSql = "SELECT course_name FROM tb_course WHERE course_id = ?";
            ResultSet courseRs = helper.executeQuery(courseSql, score.getCourseId());
            if (courseRs != null && courseRs.next()) {
                score.setCourseName(courseRs.getString("course_name"));
            }

            scores.add(score);
            System.out.println("找到成绩: " + score.getSno() + ", " + score.getCourseId() + ", " + score.getTotalScore());
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("JSP中查询成绩出错: " + e.getMessage());
    } finally {
        helper.closeDB();
    }

    System.out.println("查询到成绩数量: " + scores.size());
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
                                <h4>我教授课程的学生成绩列表</h4>
                            </div>
                            <div class="card-body">
                                <!-- 调试信息 -->
                                <div class="alert alert-info">
                                    <h5>调试信息</h5>
                                    <p>当前教师编号: <%= tno %></p>
                                    <p>会话中的角色: <%= userRole %></p>
                                    <p>会话中的用户对象: <%= teacherObj != null ? "存在" : "不存在" %></p>
                                    <p>SQL查询: SELECT * FROM tb_score WHERE CONVERT(tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci</p>
                                    <p>查询到的成绩数量: <%= scores.size() %></p>
                                    <p>使用的教师编号: <%= tno %> (默认使用T001)</p>
                                </div>

                                <div class="table-responsive">
                                    <table class="table table-bordered">
                                        <thead>
                                        <tr>
                                            <th>学号</th>
                                            <th>学生姓名</th>
                                            <th>课程</th>
                                            <th>平时成绩</th>
                                            <th>期中成绩</th>
                                            <th>期末成绩</th>
                                            <th>总成绩</th>
                                            <th>等级</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            if (scores != null && !scores.isEmpty()) {
                                                for (Score score : scores) {
                                                    System.out.println("渲染成绩: " + score.getSno() + ", " + score.getCourseName());
                                        %>
                                        <tr>
                                            <td><%= score.getSno() %></td>
                                            <td><%= score.getStudentName() != null ? score.getStudentName() : "" %></td>
                                            <td><%= score.getCourseName() != null ? score.getCourseName() : score.getCourseId() %></td>
                                            <td><%= score.getDailyScore() != null ? score.getDailyScore() : "--" %></td>
                                            <td><%= score.getMidtermScore() != null ? score.getMidtermScore() : "--" %></td>
                                            <td><%= score.getFinalScore() != null ? score.getFinalScore() : "--" %></td>
                                            <td><%= score.getTotalScore() != null ? score.getTotalScore() : "--" %></td>
                                            <td><%= score.getGrade() != null ? score.getGrade() : "--" %></td>
                                            <td>
                                                <a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/score/edit?id=<%= score.getId() %>">编辑</a>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        } else {
                                            System.out.println("没有成绩数据");
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
</body>
</html> 