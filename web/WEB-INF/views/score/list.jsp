<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Score" %>
<%@ page import="com.hello.entity.Course" %>
<%@ page import="com.hello.utils.vo.PagerVO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.hello.utils.JdbcHelper" %>
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
    String tno = (String)session.getAttribute("username");
    if (userRole == null || (!"admin".equals(userRole) && !"teacher".equals(userRole))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    boolean isAdmin = "admin".equals(userRole);

    // 获取请求参数
    String sno = request.getParameter("sno");
    String studentName = request.getParameter("studentName");
    String courseId = request.getParameter("courseId");
    String courseName = request.getParameter("courseName");
    String semester = request.getParameter("semester");
    String schoolYear = request.getParameter("schoolYear");

    System.out.println("=== score/list.jsp 开始渲染 ===");
    System.out.println("用户角色: " + userRole);
    System.out.println("用户名: " + tno);

    // 直接在JSP中执行SQL查询
    List<Score> scoreList = new ArrayList<>();
    JdbcHelper helper = new JdbcHelper();

    try {
        // 构建查询条件
        StringBuilder whereBuilder = new StringBuilder(" WHERE 1=1 ");

        // 如果是教师角色，只能查看自己教授课程的成绩
        if ("teacher".equals(userRole)) {
            whereBuilder.append(" AND tno = ? ");
        }

        if (sno != null && !sno.isEmpty()) {
            whereBuilder.append(" AND sno LIKE ? ");
        }

        if (courseId != null && !courseId.isEmpty()) {
            whereBuilder.append(" AND course_id = ? ");
        }

        if (semester != null && !semester.isEmpty()) {
            try {
                int semesterInt = Integer.parseInt(semester);
                whereBuilder.append(" AND semester = ? ");
            } catch (NumberFormatException e) {
                // 无效的学期格式，忽略
            }
        }

        if (schoolYear != null && !schoolYear.isEmpty()) {
            whereBuilder.append(" AND school_year LIKE ? ");
        }

        String sql = "SELECT * FROM tb_score" + whereBuilder.toString() + " ORDER BY course_id, sno";
        System.out.println("执行SQL: " + sql);

        // 准备参数
        List<Object> params = new ArrayList<>();
        if ("teacher".equals(userRole)) {
            params.add(tno);
        }

        if (sno != null && !sno.isEmpty()) {
            params.add("%" + sno + "%");
        }

        if (courseId != null && !courseId.isEmpty()) {
            params.add(courseId);
        }

        if (semester != null && !semester.isEmpty()) {
            try {
                int semesterInt = Integer.parseInt(semester);
                params.add(semesterInt);
            } catch (NumberFormatException e) {
                // 无效的学期格式，忽略
            }
        }

        if (schoolYear != null && !schoolYear.isEmpty()) {
            params.add("%" + schoolYear + "%");
        }

        ResultSet resultSet = helper.executeQuery(sql, params.toArray());

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

            scoreList.add(score);
            System.out.println("找到成绩: " + score.getSno() + ", " + score.getCourseId() + ", " + score.getTotalScore());
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("JSP中查询成绩出错: " + e.getMessage());
    }

    System.out.println("查询到成绩数量: " + scoreList.size());

    // 获取所有课程，用于筛选
    List<Course> courses = new ArrayList<>();
    try {
        String courseWhere = "";
        if ("teacher".equals(userRole)) {
            courseWhere = " WHERE tno = ?";
            ResultSet courseRs = helper.executeQuery("SELECT * FROM tb_course" + courseWhere, tno);
            while (courseRs != null && courseRs.next()) {
                Course course = new Course();
                course.setCourseId(courseRs.getString("course_id"));
                course.setCourseName(courseRs.getString("course_name"));
                courses.add(course);
            }
        } else {
            ResultSet courseRs = helper.executeQuery("SELECT * FROM tb_course");
            while (courseRs != null && courseRs.next()) {
                Course course = new Course();
                course.setCourseId(courseRs.getString("course_id"));
                course.setCourseName(courseRs.getString("course_name"));
                courses.add(course);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        helper.closeDB();
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
                                <h4><% if ("teacher".equals(userRole)) { %>我教授课程的学生成绩列表<% } else { %>成绩管理<% } %></h4>
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
                                                            if (courses != null && !courses.isEmpty()) {
                                                                for (Course course : courses) {
                                                        %>
                                                        <option value="<%= course.getCourseId() %>" <%= (courseId != null && courseId.equals(course.getCourseId())) ? "selected" : "" %>><%= course.getCourseName() %></option>
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
                                            <th>课程</th>
                                            <th>平时成绩</th>
                                            <th>期中成绩</th>
                                            <th>期末成绩</th>
                                            <th>总分</th>
                                            <th>等级</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            try {
                                                if (scoreList != null && !scoreList.isEmpty()) {
                                                    for (Score score : scoreList) {
                                        %>
                                        <tr>
                                            <td><%= score.getSno() %></td>
                                            <td><%= score.getStudentName() != null ? score.getStudentName() : "" %></td>
                                            <td><%= score.getCourseName() != null ? score.getCourseName() : score.getCourseId() %></td>
                                            <td><%= score.getDailyScore() != null ? score.getDailyScore() : "" %></td>
                                            <td><%= score.getMidtermScore() != null ? score.getMidtermScore() : "" %></td>
                                            <td><%= score.getFinalScore() != null ? score.getFinalScore() : "" %></td>
                                            <td><%= score.getTotalScore() != null ? score.getTotalScore() : "" %></td>
                                            <td><%= score.getGrade() != null ? score.getGrade() : "" %></td>
                                            <td>
                                                <div class="btn-group">
                                                    <a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/score/edit?id=<%= score.getId() %>" title="编辑" data-toggle="tooltip"><i class="mdi mdi-pencil"></i></a>
                                                    <% if (isAdmin) { %>
                                                    <a class="btn btn-xs btn-default delete-btn" href="javascript:void(0);" data-id="<%= score.getId() %>" title="删除" data-toggle="tooltip"><i class="mdi mdi-window-close"></i></a>
                                                    <% } %>
                                                </div>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        } else {
                                        %>
                                        <tr>
                                            <td colspan="9" class="text-center">暂无相关数据</td>
                                        </tr>
                                        <%
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        %>
                                        <tr>
                                            <td colspan="9" class="text-center">加载数据时出错</td>
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