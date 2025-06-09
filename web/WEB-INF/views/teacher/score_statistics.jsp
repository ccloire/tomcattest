<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Course" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.hello.entity.Teacher" %>
<%@ page import="java.util.Enumeration" %>
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

    System.out.println("=== score_statistics.jsp 开始渲染 ===");
    System.out.println("教师编号: " + tno);
    System.out.println("用户角色: " + userRole);
%>

<div class="lyear-layout-web">
    <div class="lyear-layout-container">
        <!-- 引入侧边栏和顶部栏 -->
        <jsp:include page="../_aside_header.jsp" />

        <!--页面主要内容-->
        <main class="lyear-layout-content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="card">
                            <div class="card-header">
                                <h4>成绩统计分析</h4>
                            </div>
                            <div class="card-body">
                                <div class="accordion" id="accordionExample">
                                    <!-- 调试信息部分 -->
                                    <div class="card">
                                        <div class="card-header" id="headingOne">
                                            <h5 class="mb-0">
                                                <button class="btn btn-link" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                                    调试信息
                                                </button>
                                            </h5>
                                        </div>
                                        <div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#accordionExample">
                                            <div class="card-body">
                                                <div class="alert alert-info">
                                                    <p>当前教师编号: <%= tno %></p>
                                                    <p>会话中的角色: <%= userRole %></p>
                                                    <p>会话中的用户对象: <%= teacherObj != null ? "存在" : "不存在" %></p>
                                                    <p>课程列表: <%= request.getAttribute("courses") != null ? ((List)request.getAttribute("courses")).size() + "个课程" : "无课程数据" %></p>

                                                    <% if (request.getAttribute("courses") != null) { %>
                                                    <div>
                                                        <h6>课程详情:</h6>
                                                        <ul>
                                                            <%
                                                                List<Course> debugCourses = (List<Course>)request.getAttribute("courses");
                                                                for (Course course : debugCourses) {
                                                            %>
                                                            <li><%= course.getCourseId() %> - <%= course.getCourseName() %> (教师: <%= course.getTno() %>)</li>
                                                            <% } %>
                                                        </ul>
                                                    </div>
                                                    <% } %>

                                                    <h6>手动API测试:</h6>
                                                    <form action="${pageContext.request.contextPath}/teacher/score/getStatistics" method="get" target="_blank">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">课程ID</span>
                                                            <input type="text" class="form-control" name="courseId" value="C001">
                                                            <span class="input-group-btn">
                                                                <button class="btn btn-primary" type="submit">直接请求API</button>
                                                            </span>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="card">
                                    <div class="card-header">
                                        <h4>选择课程</h4>
                                    </div>
                                    <div class="card-body">
                                        <form id="statisticsForm" class="form-horizontal">
                                            <div class="form-group">
                                                <label class="col-md-3 control-label">课程:</label>
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
                                                <div class="col-md-9 col-md-offset-3">
                                                    <button type="button" id="viewStatisticsBtn" class="btn btn-primary">查看统计</button>
                                                    <button type="button" id="testDataBtn" class="btn btn-info">测试数据</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>

                                <!-- 统计结果容器 -->
                                <div id="statisticsContainer"></div>
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
        console.log('页面已加载完成');

        // 测试数据按钮点击事件
        $('#testDataBtn').click(function() {
            // 模拟统计数据
            var mockData = {
                success: true,
                message: "获取统计数据成功",
                data: {
                    totalStudents: 30,
                    avgScore: 78.5,
                    maxScore: 98,
                    minScore: 45,
                    passRate: 0.85,
                    gradeDistribution: {
                        "A+": 3,
                        "A": 5,
                        "B+": 7,
                        "B": 8,
                        "C+": 4,
                        "C": 2,
                        "D": 1,
                        "F": 0
                    },
                    scoreRangeDistribution: {
                        "0-60": 4,
                        "60-70": 6,
                        "70-80": 8,
                        "80-90": 7,
                        "90-100": 5
                    }
                }
            };

            console.log('使用模拟数据:', mockData);

            // 显示模拟数据
            var statistics = mockData.data;

            // 构建HTML显示统计数据
            var html = '<div class="card">' +
                '<div class="card-header"><h4>测试统计结果 (模拟数据)</h4></div>' +
                '<div class="card-body">' +
                '<div class="alert alert-warning">这是模拟数据，用于测试界面显示</div>' +
                '<div class="row">' +
                '<div class="col-md-12">' +
                '<div class="card bg-light">' +
                '<div class="card-body">' +
                '<div class="alert alert-info">API返回的原始data字段: ' + JSON.stringify(mockData.data) + '</div>' +
                '<table class="table table-bordered table-hover">' +
                '<thead class="thead-light">' +
                '<tr><th width="30%">统计项</th><th>数值</th></tr>' +
                '</thead>' +
                '<tbody>' +
                '<tr><td>总人数</td><td>' + (statistics.totalStudents || 0) + ' 人</td></tr>' +
                '<tr><td>平均分</td><td>' + (statistics.avgScore || 0).toFixed(2) + ' 分</td></tr>' +
                '<tr><td>最高分</td><td>' + (statistics.maxScore || 0) + ' 分</td></tr>' +
                '<tr><td>最低分</td><td>' + (statistics.minScore || 0) + ' 分</td></tr>' +
                '<tr><td>及格率</td><td>' + ((statistics.passRate || 0) * 100).toFixed(2) + '%</td></tr>' +
                '</tbody>' +
                '</table>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '<div class="row mt-3">' +
                '<div class="col-md-12">' +
                '<div class="card">' +
                '<div class="card-header"><h4>原始JSON数据</h4></div>' +
                '<div class="card-body">' +
                '<pre style="max-height: 300px; overflow: auto;">' + JSON.stringify(mockData, null, 2) + '</pre>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>';

            $('#statisticsContainer').html(html);
        });

        // 查看统计按钮点击事件
        $('#viewStatisticsBtn').click(function() {
            var courseId = $('#courseId').val();

            if (!courseId) {
                $.alert({
                    title: '提示',
                    content: '请选择课程',
                    type: 'red'
                });
                return;
            }

            console.log('开始查看统计，课程ID: ' + courseId);

            // 显示加载提示
            $('#statisticsContainer').html('<div class="card"><div class="card-body"><div class="text-center"><div class="spinner-border text-primary" role="status"><span class="sr-only">正在加载...</span></div><p class="mt-2">正在加载统计数据，请稍候...</p></div></div></div>');

            // 构建API URL
            var apiUrl = '${pageContext.request.contextPath}/teacher/score/getStatistics?courseId=' + encodeURIComponent(courseId);

            // 使用AJAX发送请求
            $.ajax({
                url: apiUrl,
                type: 'get',
                dataType: 'json',
                success: function(data) {
                    console.log('AJAX请求成功，返回数据:', data);

                    if (data.success) {
                        var statistics = data.data;
                        console.log('解析后的统计数据:', statistics);

                        // 检查statistics是否为字符串，如果是则尝试解析
                        if (typeof statistics === 'string') {
                            try {
                                statistics = JSON.parse(statistics);
                                console.log('从字符串解析的统计数据:', statistics);
                            } catch (e) {
                                console.error('解析统计数据字符串失败:', e);
                            }
                        }

                        // 构建HTML显示统计数据
                        var html = '<div class="card">' +
                            '<div class="card-header"><h4>统计结果</h4></div>' +
                            '<div class="card-body">' +
                            '<div class="row">' +
                            '<div class="col-md-12">' +
                            '<div class="card bg-light">' +
                            '<div class="card-body">' +
                            '<div class="alert alert-info">API返回的原始data字段: ' + JSON.stringify(data.data) + '</div>' +
                            '<div class="alert alert-info">API响应状态: ' + data.success + ', 消息: ' + data.message + '</div>' +
                            '<table class="table table-bordered table-hover">' +
                            '<thead class="thead-light">' +
                            '<tr><th width="30%">统计项</th><th>数值</th></tr>' +
                            '</thead>' +
                            '<tbody>' +
                            '<tr><td>总人数</td><td>' + (statistics.totalStudents || 0) + ' 人</td></tr>' +
                            '<tr><td>平均分</td><td>' + (statistics.avgScore || 0).toFixed(2) + ' 分</td></tr>' +
                            '<tr><td>最高分</td><td>' + (statistics.maxScore || 0) + ' 分</td></tr>' +
                            '<tr><td>最低分</td><td>' + (statistics.minScore || 0) + ' 分</td></tr>' +
                            '<tr><td>及格率</td><td>' + ((statistics.passRate || 0) * 100).toFixed(2) + '%</td></tr>' +
                            '</tbody>' +
                            '</table>' +
                            '</div>' +
                            '</div>' +
                            '</div>' +
                            '</div>' +
                            '<div class="row mt-3">' +
                            '<div class="col-md-12">' +
                            '<div class="card">' +
                            '<div class="card-header"><h4>原始JSON数据</h4></div>' +
                            '<div class="card-body">' +
                            '<pre style="max-height: 300px; overflow: auto;">' + JSON.stringify(data, null, 2) + '</pre>' +
                            '</div>' +
                            '</div>' +
                            '</div>' +
                            '</div>' +
                            '</div>' +
                            '</div>';

                        $('#statisticsContainer').html(html);
                    } else {
                        $('#statisticsContainer').html('<div class="card"><div class="card-body"><div class="alert alert-danger"><i class="mdi mdi-alert-circle"></i> 获取统计数据失败: ' + data.message + '</div></div></div>');
                    }
                },
                error: function(xhr, status, error) {
                    $('#statisticsContainer').html('<div class="card"><div class="card-body"><div class="alert alert-danger"><i class="mdi mdi-alert-circle"></i> 服务器错误，请稍后再试</div><p>错误详情: ' + error + '</p></div></div>');
                    console.error('AJAX错误:', status, error);
                    console.error('响应文本:', xhr.responseText);
                }
            });
        });
    });
</script>
</body>
</html> 