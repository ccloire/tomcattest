<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"/>
    <title>班级列表</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>

<body>
<div class="lyear-layout-web">
    <div class="lyear-layout-container">

        <jsp:include page="../_aside_header.jsp"></jsp:include>

        <!--页面主要内容-->
        <main class="lyear-layout-content">

            <div class="container-fluid">

                <div class="row">
                    <div class="col-md-12">
                        <div class="card">
                            <div class="card-header"><h4>班级列表</h4></div>
                            <div class="card-body">
                                <form id="form" style="margin-bottom: 20px" class="form-inline" action="${pageContext.request.contextPath}/clazz" method="get">
                                    <input type="hidden" id="current" name="current" value="1">
                                    <div class="form-group">
                                        <label >班级编号</label>
                                        <input class="form-control" type="text" value="${clazzno}" name="clazzno" placeholder="请输入班级编号..">
                                    </div>
                                    <div class="form-group">
                                        <label>班级名</label>
                                        <input class="form-control" type="text" value="${name}" name="name" placeholder="请输入班级名.">
                                    </div>
                                    <div class="form-group">
                                        <button class="btn btn-brown btn-round" type="submit">查询</button>
                                        <c:if test="${sessionScope.role == 'admin'}">
                                            <button class="btn btn-brown btn-success" type="button" onclick="location.href='?r=add'">新增</button>
                                        </c:if>
                                    </div>
                                </form>

                                <table class="table table-bordered">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>班级编号</th>
                                        <th>班级名称</th>
                                        <c:if test="${sessionScope.role == 'admin'}"><th>操作</th></c:if>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${pagerVO.list}" var="i" varStatus="s">
                                        <tr>
                                            <th scope="row">${s.count}</th>
                                            <td>${i.clazzno}</td>
                                            <td>${i.name}</td>

                                            <c:if test="${sessionScope.role == 'admin'}">
                                                <td>
                                                    <button class="btn btn-primary btn-xs" type="button" onclick="location.href='?r=edit&clazzno=${i.clazzno}'">编辑</button>
                                                    <button class="btn btn-danger btn-xs" type="button" onclick="del('${i.clazzno}')">删除</button>
                                                </td>
                                            </c:if>

                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>

                                <%-- 将pagerVO设置为pager，并传递pageUrl参数 --%>
                                <c:set var="pager" value="${pagerVO}" scope="request"/>
                                <jsp:include page="../_pager.jsp">
                                    <jsp:param name="pageUrl" value="${pageContext.request.contextPath}/clazz" />
                                </jsp:include>

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
    $(document).ready(function (e) {

    });
    function gotoPage(page) {
        $("#current").val(page)
        $("#form").submit()
    }
    function del(clazzno) {
        if (confirm('确定要删除班级编号为 ' + clazzno + ' 的班级吗？')) {
            $.ajax({
                type:'post',
                url:'clazz?r=del',
                data:{clazzno},
                success:function (response) {
                    if(response.success){
                        alert('删除成功');
                        location.href='clazz';
                    }else{
                        alert('删除失败: ' + response.message);
                    }
                },
                error:function () {
                    alert('操作失败，请稍后重试');
                }
            });
        }
    }
</script>
</body>
</html> 