<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hello.entity.Certificate" %>
<%@ page import="com.hello.entity.Student" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>编辑证书 - 学生学业管理系统</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/bootstrap-datepicker3.min.css" rel="stylesheet">
</head>

<body>
<%
    // 检查权限
    String userRole = (String)session.getAttribute("role");
    if (userRole == null || !"admin".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Certificate certificate = (Certificate) request.getAttribute("certificate");
    List<Student> students = (List<Student>) request.getAttribute("students");
    boolean isNew = certificate == null || certificate.getId() == null;
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
                                <h4><%= isNew ? "添加" : "编辑" %>证书</h4>
                            </div>
                            <div class="card-body">
                                <form id="certificateForm" class="row" action="javascript:void(0);">
                                    <input type="hidden" name="id" value="<%= isNew ? "" : certificate.getId() %>">
                                    
                                    <div class="form-group col-md-6">
                                        <label for="sno">学生学号</label>
                                        <select class="form-control" id="sno" name="sno" required<%= !isNew ? " disabled" : "" %>>
                                            <option value="">请选择学生</option>
                                            <% 
                                                if (students != null && !students.isEmpty()) {
                                                    for (Student student : students) {
                                                        boolean isSelected = !isNew && student.getSno().equals(certificate.getSno());
                                            %>
                                            <option value="<%= student.getSno() %>" <%= isSelected ? "selected" : "" %>><%= student.getSno() %> - <%= student.getName() %></option>
                                            <% 
                                                    }
                                                }
                                            %>
                                        </select>
                                        <% if (!isNew) { %>
                                        <input type="hidden" name="sno" value="<%= certificate.getSno() %>">
                                        <% } %>
                                    </div>
                                    
                                    <div class="form-group col-md-6">
                                        <label for="certType">证书类型</label>
                                        <select class="form-control" id="certType" name="certType" required>
                                            <option value="">请选择证书类型</option>
                                            <option value="四六级" <%= !isNew && "四六级".equals(certificate.getCertType()) ? "selected" : "" %>>四六级</option>
                                            <option value="计算机等级" <%= !isNew && "计算机等级".equals(certificate.getCertType()) ? "selected" : "" %>>计算机等级</option>
                                            <option value="职业资格" <%= !isNew && "职业资格".equals(certificate.getCertType()) ? "selected" : "" %>>职业资格</option>
                                            <option value="考研成绩" <%= !isNew && "考研成绩".equals(certificate.getCertType()) ? "selected" : "" %>>考研成绩</option>
                                            <option value="其他" <%= !isNew && "其他".equals(certificate.getCertType()) ? "selected" : "" %>>其他</option>
                                        </select>
                                    </div>
                                    
                                    <div class="form-group col-md-6">
                                        <label for="certNumber">证书编号</label>
                                        <input type="text" class="form-control" id="certNumber" name="certNumber" value="<%= !isNew && certificate.getCertNumber() != null ? certificate.getCertNumber() : "" %>" placeholder="请输入证书编号">
                                    </div>
                                    
                                    <div class="form-group col-md-6">
                                        <label for="score">分数/成绩</label>
                                        <input type="number" class="form-control" id="score" name="score" value="<%= !isNew && certificate.getScore() != null ? certificate.getScore() : "" %>" step="0.01" placeholder="请输入分数">
                                    </div>
                                    
                                    <div class="form-group col-md-6">
                                        <label for="issueDate">颁发日期</label>
                                        <input type="text" class="form-control datepicker" id="issueDate" name="issueDate" value="<%= !isNew && certificate.getIssueDate() != null ? sdf.format(certificate.getIssueDate()) : "" %>" placeholder="yyyy-MM-dd">
                                    </div>
                                    
                                    <div class="form-group col-md-6">
                                        <label for="validUntil">有效期至</label>
                                        <input type="text" class="form-control datepicker" id="validUntil" name="validUntil" value="<%= !isNew && certificate.getValidUntil() != null ? sdf.format(certificate.getValidUntil()) : "" %>" placeholder="yyyy-MM-dd，留空表示长期有效">
                                    </div>
                                    
                                    <div class="form-group col-md-6">
                                        <label for="scanPath">证书扫描件路径</label>
                                        <input type="text" class="form-control" id="scanPath" name="scanPath" value="<%= !isNew && certificate.getScanPath() != null ? certificate.getScanPath() : "" %>" placeholder="证书扫描件存储路径">
                                    </div>
                                    
                                    <div class="form-group col-md-6">
                                        <label for="verifyStatus">验证状态</label>
                                        <select class="form-control" id="verifyStatus" name="verifyStatus">
                                            <option value="未验证" <%= isNew || (certificate.getVerifyStatus() != null && "未验证".equals(certificate.getVerifyStatus())) ? "selected" : "" %>>未验证</option>
                                            <option value="已验证" <%= !isNew && certificate.getVerifyStatus() != null && "已验证".equals(certificate.getVerifyStatus()) ? "selected" : "" %>>已验证</option>
                                            <option value="验证失败" <%= !isNew && certificate.getVerifyStatus() != null && "验证失败".equals(certificate.getVerifyStatus()) ? "selected" : "" %>>验证失败</option>
                                        </select>
                                    </div>
                                    
                                    <div class="form-group col-md-12">
                                        <label for="verifyRemark">验证备注</label>
                                        <textarea class="form-control" id="verifyRemark" name="verifyRemark" rows="3" placeholder="验证备注信息"><%= !isNew && certificate.getVerifyRemark() != null ? certificate.getVerifyRemark() : "" %></textarea>
                                    </div>
                                    
                                    <div class="form-group col-md-12">
                                        <button type="button" class="btn btn-primary" id="submitBtn">保存</button>
                                        <a href="${pageContext.request.contextPath}/certificate" class="btn btn-default">返回列表</a>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
        // 初始化日期选择器
        $('.datepicker').datepicker({
            format: 'yyyy-mm-dd',
            language: 'zh-CN',
            autoclose: true,
            todayHighlight: true
        });
        
        // 提交表单
        $('#submitBtn').on('click', function() {
            var sno = $('#sno').val();
            if (!sno) {
                alert('请选择学生');
                return;
            }
            
            var certType = $('#certType').val();
            if (!certType) {
                alert('请选择证书类型');
                return;
            }
            
            // 收集表单数据
            var formData = $('#certificateForm').serialize();
            
            // 发送AJAX请求
            $.ajax({
                url: '${pageContext.request.contextPath}/certificate/save',
                type: 'post',
                data: formData,
                dataType: 'json',
                success: function(data) {
                    if (data.success) {
                        alert('保存成功');
                        window.location.href = '${pageContext.request.contextPath}/certificate';
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