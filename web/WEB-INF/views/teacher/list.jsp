<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Teacher" %>
<%@ page import="com.hello.utils.vo.PagerVO" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>教师管理 - 学生学业管理系统</title>
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
                                <h4>教师管理</h4>
                            </div>
                            <div class="card-body">
                                <div class="search-bar">
                                    <form action="${pageContext.request.contextPath}/teacher" method="get">
                                        <div class="row">
                                            <div class="col-md-3">
                                                <div class="form-group">
                                                    <label for="name">姓名</label>
                                                    <input type="text" class="form-control" id="name" name="name" placeholder="请输入姓名关键字" value="${name}">
                                                </div>
                                            </div>
                                            <div class="col-md-2">
                                                <div class="form-group">
                                                    <label>&nbsp;</label>
                                                    <button type="submit" class="btn btn-primary btn-block">搜索</button>
                                                </div>
                                            </div>
                                            <div class="col-md-2">
                                                <div class="form-group">
                                                    <label>&nbsp;</label>
                                                    <a href="${pageContext.request.contextPath}/teacher/edit" class="btn btn-success btn-block">添加教师</a>
                                                </div>
                                            </div>
                                        </div>
                                    </form>
                                </div>

                                <div class="table-responsive">
                                    <table class="table table-bordered">
                                        <thead>
                                        <tr>
                                            <th>教师号</th>
                                            <th>姓名</th>
                                            <th>性别</th>
                                            <th>年龄</th>
                                            <th>职称</th>
                                            <th>所属院系</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            try {
                                                PagerVO<Teacher> pager = (PagerVO<Teacher>)request.getAttribute("pager");
                                                if (pager != null && pager.getList() != null && !pager.getList().isEmpty()) {
                                                    for (Teacher t : pager.getList()) {
                                        %>
                                        <tr>
                                            <td><%= t.getTno() %></td>
                                            <td><%= t.getName() != null ? t.getName() : "" %></td>
                                            <td><%= t.getGender() != null ? t.getGender() : "" %></td>
                                            <td><%= t.getAge() != null ? t.getAge() : "" %></td>
                                            <td><%= t.getTitle() != null ? t.getTitle() : "" %></td>
                                            <td><%= t.getDepartment() != null ? t.getDepartment() : "" %></td>
                                            <td>
                                                <div class="btn-group">
                                                    <a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/teacher/edit?tno=<%= t.getTno() %>" title="编辑" data-toggle="tooltip"><i class="mdi mdi-pencil"></i></a>
                                                    <a class="btn btn-xs btn-default delete-btn" href="javascript:void(0);" data-tno="<%= t.getTno() %>" title="删除" data-toggle="tooltip"><i class="mdi mdi-window-close"></i></a>
                                                </div>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        } else {
                                        %>
                                        <tr>
                                            <td colspan="7" class="text-center">没有找到匹配的教师记录</td>
                                        </tr>
                                        <%
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        %>
                                        <tr>
                                            <td colspan="7" class="text-center">加载数据时出错</td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- 分页 -->
                                <jsp:include page="../_pager.jsp">
                                    <jsp:param name="pageUrl" value="${pageContext.request.contextPath}/teacher" />
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
            var tno = $(this).data('tno');
            if (confirm('确定要删除教师号为 ' + tno + ' 的教师吗？')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/teacher/delete',
                    type: 'post',
                    data: {tno: tno},
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