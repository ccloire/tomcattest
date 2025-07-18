<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"/>
    <title>编辑教师</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/custom-bg.css" rel="stylesheet">
</head>

<body>
<div class="lyear-layout-web">
    <div class="lyear-layout-container">

        <jsp:include page="_aside_header.jsp"></jsp:include>

        <!--页面主要内容-->
        <main class="lyear-layout-content">

            <div class="container-fluid">

                <div class="row">
                    <div class="col-md-12">
                        <div class="card">
                            <div class="card-body">
                                <form id="myForm" action="${pageContext.request.contextPath}/teacher/save" method="post">
                                    <div class="form-group">
                                        <label>教师编号</label>
                                        <input readonly value="${entity.tno}" class="form-control" type="text" name="tno">
                                    </div>
                                    <div class="form-group">
                                        <label>密码</label>
                                        <input required value="${entity.password}" class="form-control" type="password" name="password">
                                    </div>
                                    <div class="form-group">
                                        <label>姓名</label>
                                        <input required value="${entity.name}" class="form-control" type="text" name="name">
                                    </div>
                                    <div class="form-group">
                                        <label>电话</label>
                                        <input class="form-control" value="${entity.tele}" type="text" name="tele">
                                    </div>
                                    <div class="form-group">
                                        <label>邮箱</label>
                                        <input class="form-control" value="${entity.email}" type="email" name="email">
                                    </div>
                                    <div class="form-group">
                                        <label>年龄</label>
                                        <input value="${entity.age}" class="form-control" type="number" name="age">
                                    </div>
                                    <div class="form-group">
                                        <label>性别</label>
                                        <select class="form-control" name="gender" size="1">
                                            <option value="">请选择</option>
                                            <option <c:if test="${entity.gender == 'm'}">selected</c:if> value="m">男</option>
                                            <option <c:if test="${entity.gender == 'w'}">selected</c:if> value="w">女</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>职称</label>
                                        <select class="form-control" name="title" size="1">
                                            <option value="">请选择</option>
                                            <option <c:if test="${entity.title == '教授'}">selected</c:if> value="教授">教授</option>
                                            <option <c:if test="${entity.title == '副教授'}">selected</c:if> value="副教授">副教授</option>
                                            <option <c:if test="${entity.title == '讲师'}">selected</c:if> value="讲师">讲师</option>
                                            <option <c:if test="${entity.title == '助教'}">selected</c:if> value="助教">助教</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>所属院系</label>
                                        <input class="form-control" value="${entity.department}" type="text" name="department">
                                    </div>

                                    <div class="form-group">
                                        <button class="btn btn-primary" type="submit">提交</button>
                                    </div>
                                </form>

                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </main>
        <!--End 页面主要内容-->
    </div>
</div>

<jsp:include page="_js.jsp"></jsp:include>

<script type="text/javascript">
    $(document).ready(function (e) {
        $('#myForm').on('submit',function (event) {
            event.preventDefault();
            lightyear.loading('show');
            var formData = $(this).serialize();
            $.ajax({
                type:'post',
                url:$(this).attr('action'),
                data:formData,
                success:function (response) {
                    if(response.success){
                        lightyear.loading('hide');
                        lightyear.url('teacher');
                        lightyear.notify(response.message, 'success', 500);
                    }else{
                        lightyear.loading('hide');
                        lightyear.notify(response.message, 'danger', 3000);
                    }
                },
                error:function () {
                    lightyear.loading('hide');
                    lightyear.notify("请求失败，请检查！", 'danger', 3000);
                }
            })

        })
    });
</script>
</body>
</html> 