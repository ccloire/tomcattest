
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/perfect-scrollbar.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>
<!--消息提示-->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap-notify.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/lightyear.js"></script>
<!--日期选择插件-->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js"></script>

<c:if test="${sessionScope.role == 'student'}">
<script>
    $(function () {
        $("#studentPersonal").on('submit',function (event) {
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
                        lightyear.notify(response.message, 'success', 500);
                    }else{
                        lightyear.loading('hide');
                        lightyear.notify(response.message, 'danger', 2000);
                    }
                },
                error:function () {
                    lightyear.loading('hide');
                    lightyear.notify("请求失败，请检查！", 'danger', 3000);
                }
            })
        })
        $("#studentPassword").on('submit',function (event) {
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
                        lightyear.notify(response.message, 'success', 500);
                    }else{
                        lightyear.loading('hide');
                        lightyear.notify(response.message, 'danger', 2000);
                    }
                },
                error:function () {
                    lightyear.loading('hide');
                    lightyear.notify("请求失败，请检查！", 'danger', 3000);
                }
            })
        })
    })
</script>
</c:if>