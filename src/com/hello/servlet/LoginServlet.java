package com.hello.servlet;

import com.hello.entity.Admin;
import com.hello.entity.Student;
import com.hello.entity.Teacher;
import com.hello.service.AdminService;
import com.hello.service.StudentService;
import com.hello.service.TeacherService;
import com.hello.utils.ApiResult;
import com.hello.utils.PasswordUtil;
import com.hello.utils.LogUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    AdminService adminService = new AdminService();
    StudentService studentService = new StudentService();
    TeacherService teacherService = new TeacherService();

    @Override
    public void init() throws ServletException {
        super.init();

        // 尝试在Servlet初始化时加载JDBC驱动
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LogUtil.info("LoginServlet初始化: MySQL驱动加载成功!");
            getServletContext().setAttribute("jdbc_driver_loaded", true);
        } catch (ClassNotFoundException e) {
            LogUtil.error("LoginServlet初始化: MySQL驱动加载失败! " + e.getMessage());
            getServletContext().setAttribute("jdbc_driver_loaded", false);
            getServletContext().setAttribute("jdbc_driver_error", e.getMessage());

            try {
                // 尝试使用绝对路径加载驱动
                String webInfPath = getServletContext().getRealPath("/WEB-INF/lib");
                LogUtil.debug("WEB-INF/lib路径: " + webInfPath);

                // 尝试列出lib目录中的所有JAR文件
                java.io.File libDir = new java.io.File(webInfPath);
                if (libDir.exists() && libDir.isDirectory()) {
                    LogUtil.debug("lib目录存在，正在列出JAR文件:");
                    java.io.File[] jarFiles = libDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
                    if (jarFiles != null) {
                        for (java.io.File jarFile : jarFiles) {
                            LogUtil.debug("  - " + jarFile.getName());
                        }
                    }
                }
            } catch (Exception ex) {
                LogUtil.error("列出lib目录失败: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setCharacterEncoding("utf-8");
            resp.setContentType("application/json; charset=utf-8");

            // 检查JDBC驱动是否加载成功
            Boolean driverLoaded = (Boolean) getServletContext().getAttribute("jdbc_driver_loaded");
            if (driverLoaded == null || !driverLoaded) {
                String errorMsg = (String) getServletContext().getAttribute("jdbc_driver_error");
                LogUtil.error("登录失败: MySQL驱动未加载! " + errorMsg);
                resp.getWriter().print(ApiResult.json(false, "系统错误: 数据库驱动未加载，请联系管理员"));
                return;
            }

            // 记录所有请求参数，帮助排查问题
            LogUtil.info("===== 登录请求开始 =====");
            LogUtil.debug("请求URL: " + req.getRequestURL().toString());
            LogUtil.debug("请求参数:");
            Enumeration<String> paramNames = req.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (!"password".equals(paramName)) { // 不打印密码
                    LogUtil.debug("  " + paramName + ": " + req.getParameter(paramName));
                } else {
                    LogUtil.debug("  password: ******");
                }
            }

            String captcha = req.getParameter("captcha");
            Object sessionCaptcha = req.getSession().getAttribute("captcha");
            LogUtil.debug("验证码比较: 输入=" + captcha + ", 会话中=" + sessionCaptcha);

            if(captcha == null || !captcha.equalsIgnoreCase((String) sessionCaptcha)){
                LogUtil.warning("验证码错误，登录失败");
                resp.getWriter().print(ApiResult.json(false,"验证码输入错误！"));
                return;
            }

            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String usertype = req.getParameter("usertype");
            LogUtil.info("用户尝试登录: 用户名=" + username + ", 类型=" + usertype);

            //判断角色
            if("admin".equals(usertype)){
                LogUtil.debug("尝试以管理员身份登录");
                Admin admin = adminService.getByUsername(username);
                if(admin == null){
                    LogUtil.warning("管理员用户名 " + username + " 不存在");
                    // 检查是否是因为数据库连接问题导致的
                    Throwable dbError = (Throwable) req.getServletContext().getAttribute("db_connection_error");
                    if (dbError != null) {
                        LogUtil.error("数据库连接错误: " + dbError.getMessage());
                        resp.getWriter().print(ApiResult.json(false, "数据库连接失败，请联系管理员"));
                        return;
                    }
                    resp.getWriter().print(ApiResult.json(false,"用户不存在"));
                    return;
                }

                LogUtil.debug("找到管理员: " + admin.getUsername() + ", 正在验证密码");
                // 使用密码加密验证
                if(PasswordUtil.verifyPassword(password, admin.getPassword())){
                    LogUtil.info("管理员密码验证成功，登录成功");
                    LogUtil.logUserAction(admin.getUsername(), "登录", "管理员登录成功");
                    req.getSession().setAttribute("user",admin);
                    req.getSession().setAttribute("role","admin");
                    req.getSession().setAttribute("username", admin.getUsername());
                    resp.getWriter().print(ApiResult.json(true,"登录成功"));
                    return;
                }else {
                    LogUtil.warning("管理员密码错误，登录失败");
                    LogUtil.logSecurity("登录失败", "管理员 " + username + " 密码错误");
                    resp.getWriter().print(ApiResult.json(false,"密码错误"));
                    return;
                }
            } else if("teacher".equals(usertype)){
                LogUtil.debug("尝试以教师身份登录");
                Teacher teacher = teacherService.getByTno(username);
                if(teacher == null){
                    LogUtil.warning("教师工号 " + username + " 不存在");
                    // 检查是否是因为数据库连接问题导致的
                    Throwable dbError = (Throwable) req.getServletContext().getAttribute("db_connection_error");
                    if (dbError != null) {
                        LogUtil.error("数据库连接错误: " + dbError.getMessage());
                        resp.getWriter().print(ApiResult.json(false, "数据库连接失败，请联系管理员"));
                        return;
                    }
                    resp.getWriter().print(ApiResult.json(false,"用户不存在"));
                    return;
                }

                LogUtil.debug("找到教师: " + teacher.getTno() + ", 正在验证密码");
                // 使用密码加密验证
                if(PasswordUtil.verifyPassword(password, teacher.getPassword())){
                    LogUtil.info("教师密码验证成功，登录成功");
                    LogUtil.logUserAction(teacher.getTno(), "登录", "教师登录成功");
                    req.getSession().setAttribute("user", teacher);
                    req.getSession().setAttribute("role", "teacher");
                    req.getSession().setAttribute("username", teacher.getTno());
                    resp.getWriter().print(ApiResult.json(true,"登录成功"));
                    return;
                }else {
                    LogUtil.warning("教师密码错误，登录失败");
                    LogUtil.logSecurity("登录失败", "教师 " + username + " 密码错误");
                    resp.getWriter().print(ApiResult.json(false,"密码错误"));
                    return;
                }
            } else {
                LogUtil.debug("尝试以学生身份登录");
                Student student = studentService.getBySno(username);
                if(student == null){
                    LogUtil.warning("学生学号 " + username + " 不存在");
                    // 检查是否是因为数据库连接问题导致的
                    Throwable dbError = (Throwable) req.getServletContext().getAttribute("db_connection_error");
                    if (dbError != null) {
                        LogUtil.error("数据库连接错误: " + dbError.getMessage());
                        resp.getWriter().print(ApiResult.json(false, "数据库连接失败，请联系管理员"));
                        return;
                    }
                    resp.getWriter().print(ApiResult.json(false,"用户不存在"));
                    return;
                }

                LogUtil.debug("找到学生: " + student.getSno() + ", 正在验证密码");
                // 使用密码加密验证
                if(PasswordUtil.verifyPassword(password, student.getPassword())){
                    LogUtil.info("学生密码验证成功，登录成功");
                    LogUtil.logUserAction(student.getSno(), "登录", "学生登录成功");
                    req.getSession().setAttribute("user",student);
                    req.getSession().setAttribute("role","student");
                    req.getSession().setAttribute("username", student.getSno());
                    resp.getWriter().print(ApiResult.json(true,"登录成功"));
                    return;
                }else {
                    LogUtil.warning("学生密码错误，登录失败");
                    LogUtil.logSecurity("登录失败", "学生 " + username + " 密码错误");
                    resp.getWriter().print(ApiResult.json(false,"密码错误"));
                    return;
                }
            }
        } catch (Exception e) {
            // 记录详细错误到日志
            LogUtil.error("登录处理错误: " + e.getMessage(), e);

            // 存储数据库连接错误信息到应用上下文，以便在其他地方使用
            if (e instanceof NullPointerException && e.getMessage() != null &&
                    e.getMessage().contains("Connection")) {
                req.getServletContext().setAttribute("db_connection_error", e);
            }

            // 返回友好错误信息给客户端
            resp.getWriter().print(ApiResult.json(false, "服务器内部错误: " + e.getMessage()));
        } finally {
            LogUtil.info("===== 登录请求结束 =====");
        }
    }
}
