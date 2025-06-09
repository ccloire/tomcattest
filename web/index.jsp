<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
  <title>首页 - 学生学业管理系统</title>
  <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.ico" type="image/ico">
  <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
  <style>
    .welcome-card {
      text-align: center;
      padding: 40px;
      border-radius: 15px;
      background-color: rgba(255, 255, 255, 0.9);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
      margin-top: 30px;
      transition: all 0.3s ease;
    }

    .welcome-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
    }

    .welcome-title {
      color: #5e4caf;
      font-size: 32px;
      font-weight: 600;
      margin-bottom: 20px;
    }

    .welcome-subtitle {
      color: #666;
      font-size: 18px;
      margin-bottom: 30px;
    }

    .welcome-icon {
      font-size: 80px;
      color: #7266ba;
      margin-bottom: 30px;
    }

    .feature-card {
      padding: 25px;
      border-radius: 10px;
      background-color: rgba(255, 255, 255, 0.9);
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
      margin-top: 20px;
      transition: all 0.3s ease;
      height: 100%;
    }

    .feature-card:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
    }

    .feature-icon {
      font-size: 40px;
      color: #7266ba;
      margin-bottom: 15px;
    }

    .feature-title {
      color: #5e4caf;
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 15px;
    }

    .feature-text {
      color: #666;
      font-size: 16px;
    }
  </style>
</head>

<body>
<%
  String userRole = (String)session.getAttribute("role");
  if (userRole == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
%>

<div class="lyear-layout-web">
  <div class="lyear-layout-container">
    <%-- 引入侧边栏和顶部栏 --%>
    <jsp:include page="/nonjstl_aside_header.jsp" />

    <main class="lyear-layout-content">
      <div class="container-fluid">
        <div class="row">
          <div class="col-md-10 col-md-offset-1">
            <div class="welcome-card">
              <div class="welcome-icon">
                <i class="mdi mdi-school"></i>
              </div>
              <h1 class="welcome-title">欢迎使用学生学业管理系统</h1>
              <p class="welcome-subtitle">
                <%
                  if ("admin".equals(userRole)) {
                    out.print("尊敬的管理员，欢迎回来！您可以管理学生、教师、课程和成绩信息。");
                  } else if ("teacher".equals(userRole)) {
                    out.print("尊敬的教师，欢迎回来！您可以查看课程和管理学生成绩。");
                  } else {
                    out.print("亲爱的同学，欢迎回来！您可以查看个人成绩和管理证书信息。");
                  }
                %>
              </p>
            </div>
          </div>
        </div>

        <div class="row" style="margin-top: 30px;">
          <%
            if ("admin".equals(userRole)) {
          %>
          <div class="col-md-4">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="mdi mdi-account-multiple"></i>
              </div>
              <h3 class="feature-title">用户管理</h3>
              <p class="feature-text">管理学生和教师信息，包括添加、编辑和删除用户。</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="mdi mdi-book-open-page-variant"></i>
              </div>
              <h3 class="feature-title">课程管理</h3>
              <p class="feature-text">管理课程信息，包括添加新课程、编辑课程详情。</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="mdi mdi-certificate"></i>
              </div>
              <h3 class="feature-title">证书验证</h3>
              <p class="feature-text">验证学生上传的证书，确保证书信息的真实性。</p>
            </div>
          </div>
          <%
          } else if ("teacher".equals(userRole)) {
          %>
          <div class="col-md-4">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="mdi mdi-book-open-page-variant"></i>
              </div>
              <h3 class="feature-title">我的课程</h3>
              <p class="feature-text">查看您负责的课程信息和学生名单。</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="mdi mdi-file-document"></i>
              </div>
              <h3 class="feature-title">成绩管理</h3>
              <p class="feature-text">录入和管理学生成绩，查看成绩统计信息。</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="mdi mdi-account"></i>
              </div>
              <h3 class="feature-title">个人信息</h3>
              <p class="feature-text">查看和更新您的个人信息，修改登录密码。</p>
            </div>
          </div>
          <%
          } else {
          %>
          <div class="col-md-4">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="mdi mdi-file-document"></i>
              </div>
              <h3 class="feature-title">我的成绩</h3>
              <p class="feature-text">查看您的课程成绩和学分情况。</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="mdi mdi-certificate"></i>
              </div>
              <h3 class="feature-title">证书管理</h3>
              <p class="feature-text">上传和管理您的证书信息。</p>
            </div>
          </div>
          <div class="col-md-4">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="mdi mdi-account"></i>
              </div>
              <h3 class="feature-title">个人信息</h3>
              <p class="feature-text">查看和更新您的个人信息，修改登录密码。</p>
            </div>
          </div>
          <%
            }
          %>
        </div>
      </div>
    </main>
  </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/perfect-scrollbar.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>

</body>
</html>
