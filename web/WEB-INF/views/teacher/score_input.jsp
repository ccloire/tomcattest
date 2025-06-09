<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Course" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>成绩录入 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>

<body>
<%
    String userRole = (String)session.getAttribute("role");
    if (userRole == null || !"teacher".equals(userRole)) {
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
                                <h4>成绩录入</h4>
                            </div>
                            <div class="card-body">
                                <form id="scoreInputForm" class="form-horizontal">
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">选择课程</label>
                                        <div class="col-md-7">
                                            <select class="form-control" id="courseId" name="courseId">
                                                <option value="">请选择课程</option>
                                                <%
                                                    List<Course> courses = (List<Course>)request.getAttribute("courses");
                                                    if (courses != null && !courses.isEmpty()) {
                                                        for (Course course : courses) {
                                                %>
                                                <option value="<%= course.getCourseId() %>"><%= course.getCourseName() %> (<%= course.getCourseId() %>)</option>
                                                <%
                                                        }
                                                    }
                                                %>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">学期</label>
                                        <div class="col-md-7">
                                            <select class="form-control" id="semester" name="semester">
                                                <option value="">请选择学期</option>
                                                <option value="1">大一上学期</option>
                                                <option value="2">大一下学期</option>
                                                <option value="3">大二上学期</option>
                                                <option value="4">大二下学期</option>
                                                <option value="5">大三上学期</option>
                                                <option value="6">大三下学期</option>
                                                <option value="7">大四上学期</option>
                                                <option value="8">大四下学期</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">学年</label>
                                        <div class="col-md-7">
                                            <input type="text" class="form-control" id="schoolYear" name="schoolYear" placeholder="例如：2023-2024">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-md-9 col-md-offset-3">
                                            <button type="button" class="btn btn-primary" id="searchStudentsBtn">查询学生名单</button>
                                        </div>
                                    </div>
                                </form>
                                
                                <div id="studentListContainer" style="display: none;">
                                    <hr>
                                    <h4>学生成绩录入</h4>
                                    <div class="table-responsive">
                                        <table class="table table-bordered" id="studentScoreTable">
                                            <thead>
                                                <tr>
                                                    <th>学号</th>
                                                    <th>姓名</th>
                                                    <th>平时成绩(30%)</th>
                                                    <th>期中成绩(30%)</th>
                                                    <th>期末成绩(40%)</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <!-- 这里将通过AJAX动态加载学生数据 -->
                                            </tbody>
                                        </table>
                                        <button type="button" class="btn btn-success" id="saveAllScoresBtn">保存所有成绩</button>
                                    </div>
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
<script type="text/javascript">
$(document).ready(function() {
    // 查询学生名单按钮点击事件
    $('#searchStudentsBtn').click(function() {
        var courseId = $('#courseId').val();
        var semester = $('#semester').val();
        var schoolYear = $('#schoolYear').val();
        
        if (!courseId) {
            alert('请选择课程');
            return;
        }
        if (!semester) {
            alert('请选择学期');
            return;
        }
        if (!schoolYear) {
            alert('请输入学年');
            return;
        }
        
        // AJAX请求获取学生名单
        $.ajax({
            url: '${pageContext.request.contextPath}/teacher/score/getStudents',
            type: 'get',
            data: {
                courseId: courseId,
                semester: semester,
                schoolYear: schoolYear
            },
            dataType: 'json',
            success: function(data) {
                if (data.success) {
                    // 清空表格
                    $('#studentScoreTable tbody').empty();
                    
                    // 填充表格数据
                    var students = data.data;
                    if (students && students.length > 0) {
                        $.each(students, function(index, student) {
                            var row = '<tr data-id="' + (student.id || '') + '" data-sno="' + student.sno + '">' +
                                      '<td>' + student.sno + '</td>' +
                                      '<td>' + student.name + '</td>' +
                                      '<td><input type="number" class="form-control daily-score" min="0" max="100" value="' + (student.dailyScore || '') + '"></td>' +
                                      '<td><input type="number" class="form-control midterm-score" min="0" max="100" value="' + (student.midtermScore || '') + '"></td>' +
                                      '<td><input type="number" class="form-control final-score" min="0" max="100" value="' + (student.finalScore || '') + '"></td>' +
                                      '</tr>';
                            $('#studentScoreTable tbody').append(row);
                        });
                        
                        // 显示学生列表容器
                        $('#studentListContainer').show();
                    } else {
                        alert('该课程下没有学生数据');
                    }
                } else {
                    alert('获取学生名单失败: ' + data.message);
                }
            },
            error: function() {
                alert('服务器错误，请稍后再试');
            }
        });
    });
    
    // 保存所有成绩按钮点击事件
    $('#saveAllScoresBtn').click(function() {
        var courseId = $('#courseId').val();
        var semester = $('#semester').val();
        var schoolYear = $('#schoolYear').val();
        
        var scoreData = [];
        
        // 收集表格中的成绩数据
        $('#studentScoreTable tbody tr').each(function() {
            var $row = $(this);
            var scoreId = $row.data('id');
            var sno = $row.data('sno');
            var dailyScore = $row.find('.daily-score').val();
            var midtermScore = $row.find('.midterm-score').val();
            var finalScore = $row.find('.final-score').val();
            
            scoreData.push({
                id: scoreId,
                sno: sno,
                courseId: courseId,
                semester: semester,
                schoolYear: schoolYear,
                dailyScore: dailyScore,
                midtermScore: midtermScore,
                finalScore: finalScore
            });
        });
        
        // AJAX提交成绩数据
        $.ajax({
            url: '${pageContext.request.contextPath}/teacher/score/saveScores',
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify(scoreData),
            dataType: 'json',
            success: function(data) {
                if (data.success) {
                    alert('成绩保存成功！');
                } else {
                    alert('成绩保存失败: ' + data.message);
                }
            },
            error: function() {
                alert('服务器错误，请稍后再试');
            }
        });
    });
});
</script>
</body>
</html> 