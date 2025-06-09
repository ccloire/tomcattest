<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hello.entity.Certificate" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>我的证书 - 学生学业管理系统</title>
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
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
                                <h4>我的证书</h4>
                            </div>
                            <div class="card-toolbar">
                                <div class="toolbar-btn-action">
                                    <a class="btn btn-primary m-r-5" href="${pageContext.request.contextPath}/student/certificate/upload">
                                        <i class="mdi mdi-plus"></i> 上传新证书
                                    </a>
                                </div>
                            </div>
                            <div class="card-body">
                                <!-- 添加调试信息 -->
                                <%
                                    List<Certificate> debugCerts = (List<Certificate>)request.getAttribute("certificates");
                                    if (debugCerts == null) {
                                %>
                                <div class="alert alert-warning">
                                    <strong>调试信息:</strong> 证书列表为null
                                </div>
                                <% } else if (debugCerts.isEmpty()) { %>
                                <div class="alert alert-info">
                                    <strong>调试信息:</strong> 证书列表为空数组
                                </div>
                                <% } else { %>
                                <div class="alert alert-success">
                                    <strong>调试信息:</strong> 找到 <%= debugCerts.size() %> 个证书
                                </div>
                                <% } %>

                                <div class="table-responsive">
                                    <table class="table table-bordered table-striped">
                                        <thead>
                                        <tr>
                                            <th>证书名称</th>
                                            <th>获得日期</th>
                                            <th>证书编号</th>
                                            <th>验证状态</th>
                                            <th>上传时间</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            List<Certificate> certificates = (List<Certificate>)request.getAttribute("certificates");
                                            if (certificates != null && !certificates.isEmpty()) {
                                                for (Certificate cert : certificates) {
                                        %>
                                        <tr class="certificate-row" data-id="<%= cert.getId() %>">
                                            <td><%= cert.getCertType() %></td>
                                            <td><%= cert.getIssueDate() != null ? sdf.format(cert.getIssueDate()) : "未知" %></td>
                                            <td><%= cert.getCertNumber() %></td>
                                            <td>
                                                <% if ("已验证".equals(cert.getVerifyStatus())) { %>
                                                <span class="badge badge-success">已验证</span>
                                                <% } else if ("未验证".equals(cert.getVerifyStatus())) { %>
                                                <span class="badge badge-warning">待审核</span>
                                                <% } else if ("验证失败".equals(cert.getVerifyStatus())) { %>
                                                <span class="badge badge-danger">验证失败</span>
                                                <% } else { %>
                                                <span class="badge badge-secondary"><%= cert.getVerifyStatus() %></span>
                                                <% } %>
                                            </td>
                                            <td><%= cert.getUploadTime() != null ? sdf.format(cert.getUploadTime()) : "未知" %></td>
                                            <td>
                                                <div class="btn-group">
                                                    <a class="btn btn-xs btn-default" href="javascript:void(0);" onclick="viewCertificate(<%= cert.getId() %>)" title="查看详情" data-toggle="tooltip">
                                                        <i class="mdi mdi-eye"></i>
                                                    </a>
                                                    <% if ("未验证".equals(cert.getVerifyStatus())) { %>
                                                    <a class="btn btn-xs btn-danger" href="javascript:void(0);" onclick="deleteCertificate(<%= cert.getId() %>)" title="删除" data-toggle="tooltip">
                                                        <i class="mdi mdi-window-close"></i>
                                                    </a>
                                                    <% } %>
                                                </div>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        } else {
                                        %>
                                        <tr>
                                            <td colspan="6" class="text-center">
                                                <div class="empty-state">
                                                    <i class="mdi mdi-certificate-outline empty-state-icon"></i>
                                                    <h4>暂无证书数据</h4>
                                                    <p>您还没有上传任何证书，点击"上传新证书"按钮添加您的第一个证书。</p>
                                                    <a href="${pageContext.request.contextPath}/student/certificate/upload" class="btn btn-primary">
                                                        <i class="mdi mdi-plus"></i> 上传新证书
                                                    </a>
                                                </div>
                                            </td>
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
<script type="text/javascript">
    $(document).ready(function() {
        $('[data-toggle="tooltip"]').tooltip();

        // 添加行悬停效果
        $('.certificate-row').hover(
            function() {
                $(this).addClass('row-highlight');
            },
            function() {
                $(this).removeClass('row-highlight');
            }
        );
    });

    // 查看证书详情
    function viewCertificate(id) {
        // 这里可以实现查看详情的弹窗或跳转
        alert('查看证书ID: ' + id + ' 的详情');
    }

    function deleteCertificate(id) {
        if (confirm('确定要删除这个证书吗？')) {
            $.ajax({
                url: '${pageContext.request.contextPath}/certificate/delete',
                type: 'post',
                data: {id: id},
                dataType: 'json',
                success: function(data) {
                    if (data.success) {
                        alert('删除成功！');
                        location.reload();
                    } else {
                        alert('删除失败: ' + data.message);
                    }
                },
                error: function() {
                    alert('服务器错误，请稍后再试');
                }
            });
        }
    }
</script>

<style>
    /* 添加一些美化样式 */
    .certificate-row {
        transition: all 0.3s ease;
    }

    .row-highlight {
        background-color: rgba(114, 102, 186, 0.1) !important;
        transform: scale(1.01);
    }

    .badge {
        padding: 5px 10px;
        border-radius: 4px;
        font-weight: normal;
    }

    .empty-state {
        text-align: center;
        padding: 40px 0;
    }

    .empty-state-icon {
        font-size: 64px;
        color: #ddd;
        margin-bottom: 20px;
    }

    .empty-state h4 {
        color: #666;
        margin-bottom: 10px;
    }

    .empty-state p {
        color: #999;
        margin-bottom: 20px;
        max-width: 400px;
        margin-left: auto;
        margin-right: auto;
    }
</style>
</body>
</html> 