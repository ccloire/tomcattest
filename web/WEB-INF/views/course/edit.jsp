<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Course" %>
<%@ page import="com.hello.entity.Teacher" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>编辑课程 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>

<body>
<%
    String userRole = (String)session.getAttribute("role");
    if (userRole == null || !"admin".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Course course = (Course)request.getAttribute("course");
    boolean isEdit = (course != null && course.getCourseId() != null);
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
                                <h4><%= isEdit ? "编辑" : "添加" %>课程</h4>
                            </div>
                            <div class="card-body">
                                <form id="course-form" action="${pageContext.request.contextPath}/course/save" method="post">
                                    <div class="form-group">
                                        <label for="courseId">课程编号</label>
                                        <input type="text" class="form-control" id="courseId" name="courseId" value="<%= isEdit ? course.getCourseId() : "" %>" <%= isEdit ? "readonly" : "" %> required>
                                    </div>
                                    <div class="form-group">
                                        <label for="courseName">课程名称</label>
                                        <input type="text" class="form-control" id="courseName" name="courseName" value="<%= isEdit && course.getCourseName() != null ? course.getCourseName() : "" %>" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="courseType">课程类型</label>
                                        <select class="form-control" id="courseType" name="courseType">
                                            <option value="必修" <%= isEdit && "必修".equals(course.getCourseType()) ? "selected" : "" %>>必修</option>
                                            <option value="选修" <%= isEdit && "选修".equals(course.getCourseType()) ? "selected" : "" %>>选修</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="credit">学分</label>
                                        <input type="number" class="form-control" id="credit" name="credit" value="<%= isEdit && course.getCredit() != null ? course.getCredit() : "" %>" min="0" step="0.5">
                                    </div>
                                    <div class="form-group">
                                        <label for="tno">授课教师</label>
                                        <select class="form-control" id="tno" name="tno">
                                            <option value="">请选择教师</option>
                                            <%
                                                List<Teacher> teachers = (List<Teacher>)request.getAttribute("teachers");
                                                if (teachers != null && !teachers.isEmpty()) {
                                                    for (Teacher teacher : teachers) {
                                            %>
                                            <option value="<%= teacher.getTno() %>" <%= isEdit && teacher.getTno().equals(course.getTno()) ? "selected" : "" %>><%= teacher.getName() %> (<%= teacher.getTno() %>)</option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="department">所属院系</label>
                                        <input type="text" class="form-control" id="department" name="department" value="<%= isEdit && course.getDepartment() != null ? course.getDepartment() : "" %>">
                                    </div>
                                    <div class="form-group">
                                        <label for="semester">学期</label>
                                        <input type="number" class="form-control" id="semester" name="semester" value="<%= isEdit && course.getSemester() != null ? course.getSemester() : "" %>" min="1" max="8">
                                    </div>
                                    <div class="form-group">
                                        <label for="description">课程描述</label>
                                        <textarea class="form-control" id="description" name="description" rows="4"><%= isEdit && course.getDescription() != null ? course.getDescription() : "" %></textarea>
                                    </div>
                                    <div class="form-group">
                                        <button type="submit" class="btn btn-primary">保存</button>
                                        <a href="${pageContext.request.contextPath}/course" class="btn btn-default">返回</a>
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
        $('#course-form').on('submit', function(e) {
            e.preventDefault();
            
            // 表单验证
            if (!$('#courseId').val()) {
                alert('请输入课程编号');
                return false;
            }
            
            if (!$('#courseName').val()) {
                alert('请输入课程名称');
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
                        window.location.href = '${pageContext.request.contextPath}/course';
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