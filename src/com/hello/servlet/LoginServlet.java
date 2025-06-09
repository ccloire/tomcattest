package com.hello.servlet;

import com.hello.entity.Admin;
import com.hello.entity.Student;
import com.hello.entity.Teacher;
import com.hello.service.AdminService;
import com.hello.service.StudentService;
import com.hello.service.TeacherService;
import com.hello.utils.ApiResult;

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
            System.out.println("LoginServlet初始化: MySQL驱动加载成功!");
            getServletContext().setAttribute("jdbc_driver_loaded", true);
        } catch (ClassNotFoundException e) {
            System.err.println("LoginServlet初始化: MySQL驱动加载失败! " + e.getMessage());
            getServletContext().setAttribute("jdbc_driver_loaded", false);
            getServletContext().setAttribute("jdbc_driver_error", e.getMessage());

            try {
                // 尝试使用绝对路径加载驱动
                String webInfPath = getServletContext().getRealPath("/WEB-INF/lib");
                System.out.println("WEB-INF/lib路径: " + webInfPath);

                // 尝试列出lib目录中的所有JAR文件
                java.io.File libDir = new java.io.File(webInfPath);
                if (libDir.exists() && libDir.isDirectory()) {
                    System.out.println("lib目录存在，正在列出JAR文件:");
                    java.io.File[] jarFiles = libDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
                    if (jarFiles != null) {
                        for (java.io.File jarFile : jarFiles) {
                            System.out.println("  - " + jarFile.getName());
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("列出lib目录失败: " + ex.getMessage());
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
                System.err.println("登录失败: MySQL驱动未加载! " + errorMsg);
                resp.getWriter().print(ApiResult.json(false, "系统错误: 数据库驱动未加载，请联系管理员"));
                return;
            }

            // 记录所有请求参数，帮助排查问题
            System.out.println("===== 登录请求开始 =====");
            System.out.println("请求URL: " + req.getRequestURL().toString());
            System.out.println("请求参数:");
            Enumeration<String> paramNames = req.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (!"password".equals(paramName)) { // 不打印密码
                    System.out.println("  " + paramName + ": " + req.getParameter(paramName));
                } else {
                    System.out.println("  password: ******");
                }
            }

            String captcha = req.getParameter("captcha");
            Object sessionCaptcha = req.getSession().getAttribute("captcha");
            System.out.println("验证码比较: 输入=" + captcha + ", 会话中=" + sessionCaptcha);

            if(captcha == null || !captcha.equalsIgnoreCase((String) sessionCaptcha)){
                System.out.println("验证码错误，登录失败");
                resp.getWriter().print(ApiResult.json(false,"验证码输入错误！"));
                return;
            }

            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String usertype = req.getParameter("usertype");
            System.out.println("用户尝试登录: 用户名=" + username + ", 类型=" + usertype);

            //判断角色
            if("admin".equals(usertype)){
                System.out.println("尝试以管理员身份登录");
                Admin admin = adminService.getByUsername(username);
                if(admin == null){
                    System.out.println("管理员用户名 " + username + " 不存在");
                    // 检查是否是因为数据库连接问题导致的
                    Throwable dbError = (Throwable) req.getServletContext().getAttribute("db_connection_error");
                    if (dbError != null) {
                        System.out.println("数据库连接错误: " + dbError.getMessage());
                        resp.getWriter().print(ApiResult.json(false, "数据库连接失败，请联系管理员"));
                        return;
                    }
                    resp.getWriter().print(ApiResult.json(false,"用户不存在"));
                    return;
                }

                System.out.println("找到管理员: " + admin.getUsername() + ", 正在验证密码");
                if(admin.getPassword().equals(password)){
                    System.out.println("密码验证成功，登录成功");
                    req.getSession().setAttribute("user",admin);
                    req.getSession().setAttribute("role","admin");
                    req.getSession().setAttribute("username", admin.getUsername());
                    resp.getWriter().print(ApiResult.json(true,"登录成功"));
                    return;
                }else {
                    System.out.println("密码错误，登录失败");
                    resp.getWriter().print(ApiResult.json(false,"密码错误"));
                    return;
                }
            } else if("teacher".equals(usertype)){
                System.out.println("尝试以教师身份登录");
                Teacher teacher = teacherService.getByTno(username);
                if(teacher == null){
                    System.out.println("教师工号 " + username + " 不存在");
                    // 检查是否是因为数据库连接问题导致的
                    Throwable dbError = (Throwable) req.getServletContext().getAttribute("db_connection_error");
                    if (dbError != null) {
                        System.out.println("数据库连接错误: " + dbError.getMessage());
                        resp.getWriter().print(ApiResult.json(false, "数据库连接失败，请联系管理员"));
                        return;
                    }
                    resp.getWriter().print(ApiResult.json(false,"用户不存在"));
                    return;
                }

                System.out.println("找到教师: " + teacher.getTno() + ", 正在验证密码");
                if(teacher.getPassword().equals(password)){
                    System.out.println("密码验证成功，登录成功");
                    req.getSession().setAttribute("user", teacher);
                    req.getSession().setAttribute("role", "teacher");
                    req.getSession().setAttribute("username", teacher.getTno());
                    resp.getWriter().print(ApiResult.json(true,"登录成功"));
                    return;
                }else {
                    System.out.println("密码错误，登录失败");
                    resp.getWriter().print(ApiResult.json(false,"密码错误"));
                    return;
                }
            } else {
                System.out.println("尝试以学生身份登录");
                Student student = studentService.getBySno(username);
                if(student == null){
                    System.out.println("学生学号 " + username + " 不存在");
                    // 检查是否是因为数据库连接问题导致的
                    Throwable dbError = (Throwable) req.getServletContext().getAttribute("db_connection_error");
                    if (dbError != null) {
                        System.out.println("数据库连接错误: " + dbError.getMessage());
                        resp.getWriter().print(ApiResult.json(false, "数据库连接失败，请联系管理员"));
                        return;
                    }
                    resp.getWriter().print(ApiResult.json(false,"用户不存在"));
                    return;
                }

                System.out.println("找到学生: " + student.getSno() + ", 正在验证密码");
                if(student.getPassword().equals(password)){
                    System.out.println("密码验证成功，登录成功");
                    req.getSession().setAttribute("user",student);
                    req.getSession().setAttribute("role","student");
                    req.getSession().setAttribute("username", student.getSno());
                    resp.getWriter().print(ApiResult.json(true,"登录成功"));
                    return;
                }else {
                    System.out.println("密码错误，登录失败");
                    resp.getWriter().print(ApiResult.json(false,"密码错误"));
                    return;
                }
            }
        } catch (Exception e) {
            // 记录详细错误到日志
            System.err.println("登录处理错误: " + e.getMessage());
            e.printStackTrace();

            // 存储数据库连接错误信息到应用上下文，以便在其他地方使用
            if (e instanceof NullPointerException && e.getMessage() != null &&
                    e.getMessage().contains("Connection")) {
                req.getServletContext().setAttribute("db_connection_error", e);
            }

            // 返回友好错误信息给客户端
            resp.getWriter().print(ApiResult.json(false, "服务器内部错误: " + e.getMessage()));
        } finally {
            System.out.println("===== 登录请求结束 =====");
        }
    }
}
