<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Score" %>
<%@ page import="com.hello.entity.Student" %>
<%@ page import="com.hello.entity.Course" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>编辑成绩 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>

<body>
<%
    String userRole = (String)session.getAttribute("role");
    if (userRole == null || (!"admin".equals(userRole) && !"teacher".equals(userRole))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Score score = (Score)request.getAttribute("score");
    boolean isEdit = (score != null && score.getId() != null);
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
                                <h4><%= isEdit ? "编辑" : "录入" %>成绩</h4>
                            </div>
                            <div class="card-body">
                                <form id="score-form" action="${pageContext.request.contextPath}/score/save" method="post">
                                    <% if (isEdit) { %>
                                    <input type="hidden" name="id" value="<%= score.getId() %>">
                                    <% } %>
                                    
                                    <div class="form-group">
                                        <label for="sno">学生</label>
                                        <select class="form-control" id="sno" name="sno" <%= isEdit ? "readonly" : "" %>>
                                            <option value="">请选择学生</option>
                                            <%
                                                List<Student> students = (List<Student>)request.getAttribute("students");
                                                if (students != null && !students.isEmpty()) {
                                                    for (Student student : students) {
                                            %>
                                            <option value="<%= student.getSno() %>" <%= isEdit && student.getSno().equals(score.getSno()) ? "selected" : "" %>><%= student.getName() %> (<%= student.getSno() %>)</option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="courseId">课程</label>
                                        <select class="form-control" id="courseId" name="courseId" <%= isEdit ? "readonly" : "" %>>
                                            <option value="">请选择课程</option>
                                            <%
                                                List<Course> courses = (List<Course>)request.getAttribute("courses");
                                                if (courses != null && !courses.isEmpty()) {
                                                    for (Course course : courses) {
                                            %>
                                            <option value="<%= course.getCourseId() %>" <%= isEdit && course.getCourseId().equals(score.getCourseId()) ? "selected" : "" %>><%= course.getCourseName() %> (<%= course.getCourseId() %>)</option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="tno">授课教师工号</label>
                                        <input type="text" class="form-control" id="tno" name="tno" value="<%= isEdit && score.getTno() != null ? score.getTno() : "" %>">
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="dailyScore">平时成绩 (占比: 30%)</label>
                                        <input type="number" class="form-control" id="dailyScore" name="dailyScore" value="<%= isEdit && score.getDailyScore() != null ? score.getDailyScore() : "" %>" min="0" max="100" step="0.1">
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="midtermScore">期中成绩 (占比: 20%)</label>
                                        <input type="number" class="form-control" id="midtermScore" name="midtermScore" value="<%= isEdit && score.getMidtermScore() != null ? score.getMidtermScore() : "" %>" min="0" max="100" step="0.1">
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="finalScore">期末成绩 (占比: 50%)</label>
                                        <input type="number" class="form-control" id="finalScore" name="finalScore" value="<%= isEdit && score.getFinalScore() != null ? score.getFinalScore() : "" %>" min="0" max="100" step="0.1">
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="semester">学期</label>
                                        <select class="form-control" id="semester" name="semester">
                                            <option value="1" <%= isEdit && score.getSemester() != null && score.getSemester() == 1 ? "selected" : "" %>>第1学期</option>
                                            <option value="2" <%= isEdit && score.getSemester() != null && score.getSemester() == 2 ? "selected" : "" %>>第2学期</option>
                                            <option value="3" <%= isEdit && score.getSemester() != null && score.getSemester() == 3 ? "selected" : "" %>>第3学期</option>
                                            <option value="4" <%= isEdit && score.getSemester() != null && score.getSemester() == 4 ? "selected" : "" %>>第4学期</option>
                                            <option value="5" <%= isEdit && score.getSemester() != null && score.getSemester() == 5 ? "selected" : "" %>>第5学期</option>
                                            <option value="6" <%= isEdit && score.getSemester() != null && score.getSemester() == 6 ? "selected" : "" %>>第6学期</option>
                                            <option value="7" <%= isEdit && score.getSemester() != null && score.getSemester() == 7 ? "selected" : "" %>>第7学期</option>
                                            <option value="8" <%= isEdit && score.getSemester() != null && score.getSemester() == 8 ? "selected" : "" %>>第8学期</option>
                                        </select>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="schoolYear">学年</label>
                                        <input type="text" class="form-control" id="schoolYear" name="schoolYear" placeholder="例：2023-2024" value="<%= isEdit && score.getSchoolYear() != null ? score.getSchoolYear() : "" %>">
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="remark">备注</label>
                                        <textarea class="form-control" id="remark" name="remark" rows="3"><%= isEdit && score.getRemark() != null ? score.getRemark() : "" %></textarea>
                                    </div>
                                    
                                    <div class="form-group">
                                        <button type="submit" class="btn btn-primary">保存</button>
                                        <a href="${pageContext.request.contextPath}/score" class="btn btn-default">返回</a>
                                    </div>
                                </form>
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
        // 表单提交处理
        $('#score-form').on('submit', function(e) {
            e.preventDefault();
            
            // 表单验证
            if (!$('#sno').val()) {
                alert('请选择学生');
                return false;
            }
            
            if (!$('#courseId').val()) {
                alert('请选择课程');
                return false;
            }
            
            // 提交表单
            $.ajax({
                url: $(this).attr('action'),
                type: 'post',
                data: $(this).serialize(),
                dataType: 'json',
                success: function(data) {
                    if (data.success) {
                        alert('保存成功');
                        window.location.href = '${pageContext.request.contextPath}/score';
                    } else {
                        alert('保存失败: ' + data.message);
                    }
                },
                error: function() {
                    alert('操作失败，请稍后重试');
                }
            });
        });
    });
</script>
</body>
</html> 