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
    <title>证书验证 - 学生学业管理系统</title>
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
                                <h4>待验证证书</h4>
                            </div>
                            <div class="card-body">
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
                                            <th>上传时间</th>
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
                                            <td><%= cert.getUploadTime() != null ? sdf.format(cert.getUploadTime()) : "" %></td>
                                            <td>
                                                <div class="btn-group">
                                                    <a class="btn btn-xs btn-default verify-btn" href="javascript:void(0);" data-id="<%= cert.getId() %>" data-toggle="modal" data-target="#verifyModal" title="验证" data-toggle="tooltip"><i class="mdi mdi-check-circle"></i></a>
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
                                            <td colspan="8" class="text-center">没有待验证的证书</td>
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
                                    <jsp:param name="pageUrl" value="${pageContext.request.contextPath}/certificate/verify" />
                                </jsp:include>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<!-- 验证模态框 -->
<div class="modal fade" id="verifyModal" tabindex="-1" role="dialog" aria-labelledby="verifyModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="verifyModalLabel">证书验证</h4>
            </div>
            <div class="modal-body">
                <form id="verifyForm">
                    <input type="hidden" id="certificateId" name="id">
                    <div class="form-group">
                        <label for="verifyStatus">验证结果</label>
                        <select class="form-control" id="verifyStatus" name="status" required>
                            <option value="">请选择验证结果</option>
                            <option value="已验证">通过验证</option>
                            <option value="验证失败">验证失败</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="verifyRemark">验证备注</label>
                        <textarea class="form-control" id="verifyRemark" name="remark" rows="3" placeholder="请输入验证备注，如验证通过的凭据或验证失败的原因"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" id="submitVerify">提交</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/perfect-scrollbar.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
        // 点击验证按钮，设置证书ID
        $('.verify-btn').on('click', function() {
            var certificateId = $(this).data('id');
            $('#certificateId').val(certificateId);
        });

        // 提交验证
        $('#submitVerify').on('click', function() {
            var certificateId = $('#certificateId').val();
            var status = $('#verifyStatus').val();
            var remark = $('#verifyRemark').val();

            if (!status) {
                alert('请选择验证结果');
                return;
            }

            $.ajax({
                url: '${pageContext.request.contextPath}/certificate/doVerify',
                type: 'post',
                data: {
                    id: certificateId,
                    status: status,
                    remark: remark
                },
                dataType: 'json',
                success: function(data) {
                    if (data.success) {
                        alert('验证成功');
                        location.reload();
                    } else {
                        alert('验证失败: ' + data.message);
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