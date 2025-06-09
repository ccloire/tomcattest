<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"/>
    <title>修改班级</title>
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
                                <form id="myForm" action="${pageContext.request.contextPath}/clazz?r=edit" method="post">
                                    <div class="form-group">
                                        <label >班级编号</label>
                                        <input readonly class="form-control" type="text" value="${entity.clazzno}" name="clazzno" placeholder="请输入班级编号">
                                    </div>
                                    <div class="form-group">
                                        <label >班级名</label>
                                        <input class="form-control" type="text" value="${entity.name}"  name="name" placeholder="请输入班级名">
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
                        lightyear.url('clazz');
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
