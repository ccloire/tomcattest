<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- 不使用JSTL标签库 --%>

<!-- 添加自定义CSS引用 -->
<script>
    // 动态添加CSS到head标签
    document.addEventListener('DOMContentLoaded', function() {
        var link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = '${pageContext.request.contextPath}/assets/css/custom-bg.css';
        document.head.appendChild(link);
    });
</script>

<!--左侧导航-->
<aside class="lyear-layout-sidebar">

    <!-- logo -->
    <div id="logo" class="sidebar-header">
        <a href="${pageContext.request.contextPath}/index.jsp"><h4 style="color: white; padding: 15px;">学生学业管理系统</h4></a>
    </div>
    <div class="lyear-layout-sidebar-scroll">

        <nav class="sidebar-main">
            <ul class="nav nav-drawer">
                <li class="nav-item active"> <a href="${pageContext.request.contextPath}/"><i class="mdi mdi-home"></i> 后台首页</a> </li>

                <%
                    String role = (String)session.getAttribute("role");

                    // 管理员菜单
                    if ("admin".equals(role)) {
                %>
                <li class="nav-item nav-item-has-subnav">
                    <a href="javascript:void(0)"><i class="mdi mdi-account-multiple"></i> 用户管理</a>
                    <ul class="nav nav-subnav">
                        <li> <a href="${pageContext.request.contextPath}/student">学生管理</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/teacher">教师管理</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/clazz">班级管理</a> </li>
                    </ul>
                </li>
                <li class="nav-item nav-item-has-subnav">
                    <a href="javascript:void(0)"><i class="mdi mdi-book-open-page-variant"></i> 课程管理</a>
                    <ul class="nav nav-subnav">
                        <li> <a href="${pageContext.request.contextPath}/course">课程信息</a> </li>
                    </ul>
                </li>
                <li class="nav-item nav-item-has-subnav">
                    <a href="javascript:void(0)"><i class="mdi mdi-file-document"></i> 成绩管理</a>
                    <ul class="nav nav-subnav">
                        <li> <a href="${pageContext.request.contextPath}/score">成绩信息</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/score/statistics">成绩统计</a> </li>
                    </ul>
                </li>
                <li class="nav-item nav-item-has-subnav">
                    <a href="javascript:void(0)"><i class="mdi mdi-certificate"></i> 证书管理</a>
                    <ul class="nav nav-subnav">
                        <li> <a href="${pageContext.request.contextPath}/certificate">证书信息</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/certificate/verify">证书验证</a> </li>
                    </ul>
                </li>
                <%
                    // 教师菜单
                } else if ("teacher".equals(role)) {
                %>
                <li class="nav-item nav-item-has-subnav">
                    <a href="javascript:void(0)"><i class="mdi mdi-book-open-page-variant"></i> 我的课程</a>
                    <ul class="nav nav-subnav">
                        <li> <a href="${pageContext.request.contextPath}/teacher/course">课程列表</a> </li>
                    </ul>
                </li>
                <li class="nav-item nav-item-has-subnav">
                    <a href="javascript:void(0)"><i class="mdi mdi-file-document"></i> 成绩管理</a>
                    <ul class="nav nav-subnav">
                        <li> <a href="${pageContext.request.contextPath}/teacher/score">学生成绩</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/teacher/score/input">成绩录入</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/teacher/score/statistics">成绩统计</a> </li>
                    </ul>
                </li>
                <li class="nav-item nav-item-has-subnav">
                    <a href="javascript:void(0)"><i class="mdi mdi-account"></i> 个人信息</a>
                    <ul class="nav nav-subnav">
                        <li> <a href="${pageContext.request.contextPath}/teacher/profile">基本信息</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/teacher/password">修改密码</a> </li>
                    </ul>
                </li>
                <%
                    // 学生菜单
                } else {
                %>
                <li class="nav-item nav-item-has-subnav">
                    <a href="javascript:void(0)"><i class="mdi mdi-file-document"></i> 学业管理</a>
                    <ul class="nav nav-subnav">
                        <li> <a href="${pageContext.request.contextPath}/student/score">我的成绩</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/student/certificate">我的证书</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/student/certificate/upload">上传证书</a> </li>
                    </ul>
                </li>
                <li class="nav-item nav-item-has-subnav">
                    <a href="javascript:void(0)"><i class="mdi mdi-account"></i> 个人管理</a>
                    <ul class="nav nav-subnav">
                        <li> <a href="${pageContext.request.contextPath}/student/profile">个人信息</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/student/password">修改密码</a> </li>
                    </ul>
                </li>
                <% } %>
            </ul>
        </nav>

        <div class="sidebar-footer">
            <p class="copyright">学生学业管理系统</p>
        </div>
    </div>

</aside>
<!--End 左侧导航-->

<!--头部信息-->
<header class="lyear-layout-header">

    <nav class="navbar navbar-default">
        <div class="topbar">

            <div class="topbar-left">
                <div class="lyear-aside-toggler">
                    <span class="lyear-toggler-bar"></span>
                    <span class="lyear-toggler-bar"></span>
                    <span class="lyear-toggler-bar"></span>
                </div>
                <span class="navbar-page-title">  </span>
            </div>

            <ul class="topbar-right">
                <li class="dropdown dropdown-profile">
                    <a href="javascript:void(0)" data-toggle="dropdown">
                        <span style="margin-right: 0;">
                        <%
                            String userRole = (String)session.getAttribute("role");
                            if ("admin".equals(userRole)) {
                                Object user = session.getAttribute("user");
                                if (user != null) {
                                    // 使用反射获取username属性
                                    try {
                                        java.lang.reflect.Method method = user.getClass().getMethod("getUsername");
                                        String username = (String)method.invoke(user);
                                        out.print(username);
                                    } catch (Exception e) {
                                        out.print("管理员");
                                    }
                                } else {
                                    out.print("管理员");
                                }
                            } else if ("teacher".equals(userRole)) {
                                Object user = session.getAttribute("user");
                                if (user != null) {
                                    // 使用反射获取name属性
                                    try {
                                        java.lang.reflect.Method method = user.getClass().getMethod("getName");
                                        String name = (String)method.invoke(user);
                                        out.print(name);
                                    } catch (Exception e) {
                                        out.print("教师");
                                    }
                                } else {
                                    out.print("教师");
                                }
                            } else {
                                Object user = session.getAttribute("user");
                                if (user != null) {
                                    // 使用反射获取name属性
                                    try {
                                        java.lang.reflect.Method method = user.getClass().getMethod("getName");
                                        String name = (String)method.invoke(user);
                                        out.print(name);
                                    } catch (Exception e) {
                                        out.print("学生");
                                    }
                                } else {
                                    out.print("学生");
                                }
                            }
                        %>
                        <span class="caret"></span></span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-right">
                        <% if ("teacher".equals(userRole)) { %>
                        <li> <a href="${pageContext.request.contextPath}/teacher/profile"><i class="mdi mdi-account"></i> 个人信息</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/teacher/password"><i class="mdi mdi-lock-outline"></i> 修改密码</a> </li>
                        <li class="divider"></li>
                        <% } else if ("student".equals(userRole)) { %>
                        <li> <a href="${pageContext.request.contextPath}/student/profile"><i class="mdi mdi-account"></i> 个人信息</a> </li>
                        <li> <a href="${pageContext.request.contextPath}/student/password"><i class="mdi mdi-lock-outline"></i> 修改密码</a> </li>
                        <li class="divider"></li>
                        <% } %>
                        <li> <a href="${pageContext.request.contextPath}/logout"><i class="mdi mdi-logout-variant"></i> 退出登录</a> </li>
                    </ul>
                </li>
                <!--切换主题配色-->
                <li class="dropdown dropdown-skin">
                    <span data-toggle="dropdown" class="icon-palette"><i class="mdi mdi-palette"></i></span>
                    <ul class="dropdown-menu dropdown-menu-right" data-stopPropagation="true">
                        <li class="drop-title"><p>主题</p></li>
                        <li class="drop-skin-li clearfix">
                          <span class="inverse">
                            <input type="radio" name="site_theme" value="default" id="site_theme_1" checked>
                            <label for="site_theme_1"></label>
                          </span>
                            <span>
                            <input type="radio" name="site_theme" value="dark" id="site_theme_2">
                            <label for="site_theme_2"></label>
                          </span>
                            <span>
                            <input type="radio" name="site_theme" value="translucent" id="site_theme_3">
                            <label for="site_theme_3"></label>
                          </span>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    </nav>
</header>
<!--End 头部信息-->
