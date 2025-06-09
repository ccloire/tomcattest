<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Certificate" %>
<%@ page import="com.hello.utils.vo.PagerVO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>证书信息 - 学生学业管理系统</title>
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

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
                                <h4>证书信息</h4>
                            </div>
                            <div class="card-body">
                                <div class="search-bar">
                                    <form action="${pageContext.request.contextPath}/certificate" method="get">
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
                                            <div class="col-md-2">
                                                <div class="form-group">
                                                    <label for="certType">证书类型</label>
                                                    <select class="form-control" id="certType" name="certType">
                                                        <option value="">所有类型</option>
                                                        <option value="四六级" ${certType == '四六级' ? 'selected' : ''}>四六级</option>
                                                        <option value="计算机等级" ${certType == '计算机等级' ? 'selected' : ''}>计算机等级</option>
                                                        <option value="职业资格" ${certType == '职业资格' ? 'selected' : ''}>职业资格</option>
                                                        <option value="考研成绩" ${certType == '考研成绩' ? 'selected' : ''}>考研成绩</option>
                                                        <option value="其他" ${certType == '其他' ? 'selected' : ''}>其他</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="col-md-2">
                                                <div class="form-group">
                                                    <label for="verifyStatus">验证状态</label>
                                                    <select class="form-control" id="verifyStatus" name="verifyStatus">
                                                        <option value="">所有状态</option>
                                                        <option value="未验证" ${verifyStatus == '未验证' ? 'selected' : ''}>未验证</option>
                                                        <option value="已验证" ${verifyStatus == '已验证' ? 'selected' : ''}>已验证</option>
                                                        <option value="验证失败" ${verifyStatus == '验证失败' ? 'selected' : ''}>验证失败</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <div class="form-group">
                                                    <label>&nbsp;</label>
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <button type="submit" class="btn btn-primary btn-block">搜索</button>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <a href="${pageContext.request.contextPath}/certificate/edit" class="btn btn-success btn-block">添加证书</a>
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
                                            <th>证书类型</th>
                                            <th>证书编号</th>
                                            <th>分数/成绩</th>
                                            <th>颁发日期</th>
                                            <th>验证状态</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            try {
                                                PagerVO<Certificate> pager = (PagerVO<Certificate>)request.getAttribute("pager");
                                                if (pager != null && pager.getList() != null && !pager.getList().isEmpty()) {
                                                    for (Certificate cert : pager.getList()) {
                                        %>
                                        <tr>
                                            <td><%= cert.getSno() %></td>
                                            <td><%= cert.getStudentName() != null ? cert.getStudentName() : "" %></td>
                                            <td><%= cert.getCertType() %></td>
                                            <td><%= cert.getCertNumber() != null ? cert.getCertNumber() : "" %></td>
                                            <td><%= cert.getScore() != null ? cert.getScore() : "" %></td>
                                            <td><%= cert.getIssueDate() != null ? sdf.format(cert.getIssueDate()) : "" %></td>
                                            <td>
                                                <% if ("已验证".equals(cert.getVerifyStatus())) { %>
                                                <span class="label label-success">已验证</span>
                                                <% } else if ("验证失败".equals(cert.getVerifyStatus())) { %>
                                                <span class="label label-danger">验证失败</span>
                                                <% } else { %>
                                                <span class="label label-warning">未验证</span>
                                                <% } %>
                                            </td>
                                            <td>
                                                <div class="btn-group">
                                                    <a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/certificate/detail?id=<%= cert.getId() %>" title="详情" data-toggle="tooltip"><i class="mdi mdi-eye"></i></a>
                                                    <a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/certificate/edit?id=<%= cert.getId() %>" title="编辑" data-toggle="tooltip"><i class="mdi mdi-pencil"></i></a>
                                                    <a class="btn btn-xs btn-default delete-btn" href="javascript:void(0);" data-id="<%= cert.getId() %>" title="删除" data-toggle="tooltip"><i class="mdi mdi-window-close"></i></a>
                                                    <% if (cert.getScanPath() != null && !cert.getScanPath().isEmpty()) { %>
                                                    <a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/<%= cert.getScanPath() %>" target="_blank" title="查看扫描件" data-toggle="tooltip"><i class="mdi mdi-file-document"></i></a>
                                                    <% } %>
                                                </div>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        } else {
                                        %>
                                        <tr>
                                            <td colspan="8" class="text-center">没有找到匹配的证书记录</td>
                                        </tr>
                                        <%
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        %>
                                        <tr>
                                            <td colspan="8" class="text-center">加载数据时出错</td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- 分页 -->
                                <jsp:include page="../_pager.jsp">
                                    <jsp:param name="pageUrl" value="${pageContext.request.contextPath}/certificate" />
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
            var id = $(this).data('id');
            if (confirm('确定要删除此证书记录吗？')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/certificate/delete',
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