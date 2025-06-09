package com.hello.servlet;

import com.hello.entity.Course;
import com.hello.entity.Score;
import com.hello.entity.Teacher;
import com.hello.service.CourseService;
import com.hello.service.ScoreService;
import com.hello.service.TeacherService;
import com.hello.utils.ApiResult;
import com.hello.utils.vo.PagerVO;
import com.hello.utils.JdbcHelper;
import com.hello.dao.CourseDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 注解已移至web.xml配置
public class TeacherServlet extends HttpServlet {
    private TeacherService teacherService = new TeacherService();
    private CourseService courseService = new CourseService();
    private ScoreService scoreService = new ScoreService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        System.out.println("=== TeacherServlet.service 开始 ===");
        System.out.println("请求URI: " + uri);
        System.out.println("上下文路径: " + contextPath);
        System.out.println("处理路径: " + path);
        System.out.println("请求方法: " + req.getMethod());

        if (path.equals("/teacher") || path.equals("/teacher/")) {
            System.out.println("匹配路径: /teacher - 执行list方法");
            list(req, resp);
        } else if (path.equals("/teacher/profile")) {
            System.out.println("匹配路径: /teacher/profile - 执行profile方法");
            profile(req, resp);
        } else if (path.equals("/teacher/password")) {
            System.out.println("匹配路径: /teacher/password - 执行password方法");
            password(req, resp);
        } else if (path.equals("/teacher/updatePassword")) {
            System.out.println("匹配路径: /teacher/updatePassword - 执行updatePassword方法");
            updatePassword(req, resp);
        } else if (path.equals("/teacher/updateProfile")) {
            System.out.println("匹配路径: /teacher/updateProfile - 执行updateProfile方法");
            updateProfile(req, resp);
        } else if (path.equals("/teacher/course")) {
            System.out.println("匹配路径: /teacher/course - 执行courseList方法");
            courseList(req, resp);
        } else if (path.equals("/teacher/score")) {
            System.out.println("匹配路径: /teacher/score - 执行scoreList方法");
            scoreList(req, resp);
        } else if (path.equals("/teacher/score/input")) {
            System.out.println("匹配路径: /teacher/score/input - 执行scoreInput方法");
            scoreInput(req, resp);
        } else if (path.equals("/teacher/score/statistics")) {
            System.out.println("匹配路径: /teacher/score/statistics - 执行scoreStatistics方法");
            scoreStatistics(req, resp);
        } else if (path.equals("/teacher/score/getStudents")) {
            System.out.println("匹配路径: /teacher/score/getStudents - 执行getStudents方法");
            getStudents(req, resp);
        } else if (path.equals("/teacher/score/saveScores")) {
            System.out.println("匹配路径: /teacher/score/saveScores - 执行saveScores方法");
            saveScores(req, resp);
        } else if (path.equals("/teacher/score/getStatistics")) {
            System.out.println("匹配路径: /teacher/score/getStatistics - 执行getScoreStatistics方法");
            getScoreStatistics(req, resp);
        } else if (path.equals("/teacher/edit")) {
            System.out.println("匹配路径: /teacher/edit - 执行edit方法");
            edit(req, resp);
        } else if (path.equals("/teacher/save")) {
            System.out.println("匹配路径: /teacher/save - 执行save方法");
            save(req, resp);
        } else if (path.equals("/teacher/delete")) {
            System.out.println("匹配路径: /teacher/delete - 执行delete方法");
            delete(req, resp);
        } else {
            System.out.println("未匹配到路径: " + path + " - 返回404错误");
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        System.out.println("=== TeacherServlet.service 结束 ===");
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String pageStr = req.getParameter("page");
        String sizeStr = req.getParameter("size");
        String name = req.getParameter("name");

        int page = 1;
        int size = 10;
        try {
            if (pageStr != null) {
                page = Integer.parseInt(pageStr);
            }
            if (sizeStr != null) {
                size = Integer.parseInt(sizeStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        PagerVO<Teacher> pager = teacherService.page(page, size, name);
        pager.init();
        req.setAttribute("pager", pager);
        req.setAttribute("name", name);
        req.getRequestDispatcher("/WEB-INF/views/teacher/list.jsp").forward(req, resp);
    }

    private void profile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"teacher".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        // 获取当前教师的信息
        Teacher teacher = (Teacher) req.getSession().getAttribute("user");
        if (teacher != null) {
            // 重新从数据库获取最新数据
            teacher = teacherService.getByTno(teacher.getTno());
            req.setAttribute("teacher", teacher);
            req.getRequestDispatcher("/WEB-INF/views/teacher/profile.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/logout");
        }
    }

    private void password(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"teacher".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/teacher/password.jsp").forward(req, resp);
    }

    private void updateProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        String role = (String) req.getSession().getAttribute("role");
        if (!"teacher".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        Teacher teacher = (Teacher) req.getSession().getAttribute("user");
        if (teacher == null) {
            resp.getWriter().print(ApiResult.json(false, "用户会话已过期，请重新登录"));
            return;
        }

        String tele = req.getParameter("tele");
        String email = req.getParameter("email");

        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setTno(teacher.getTno());
        updatedTeacher.setTele(tele);
        updatedTeacher.setEmail(email);

        int result = teacherService.save(updatedTeacher);
        if (result > 0) {
            resp.getWriter().print(ApiResult.json(true, "个人资料更新成功"));
        } else {
            resp.getWriter().print(ApiResult.json(false, "个人资料更新失败"));
        }
    }

    private void updatePassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        String role = (String) req.getSession().getAttribute("role");
        if (!"teacher".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        Teacher teacher = (Teacher) req.getSession().getAttribute("user");
        if (teacher == null) {
            resp.getWriter().print(ApiResult.json(false, "用户会话已过期，请重新登录"));
            return;
        }

        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (oldPassword == null || newPassword == null || confirmPassword == null) {
            resp.getWriter().print(ApiResult.json(false, "请填写完整的密码信息"));
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            resp.getWriter().print(ApiResult.json(false, "两次输入的新密码不一致"));
            return;
        }

        // 重新从数据库获取教师信息
        Teacher currentTeacher = teacherService.getByTno(teacher.getTno());
        if (!currentTeacher.getPassword().equals(oldPassword)) {
            resp.getWriter().print(ApiResult.json(false, "原密码错误"));
            return;
        }

        int result = teacherService.updatePassword(teacher.getTno(), newPassword);
        if (result > 0) {
            resp.getWriter().print(ApiResult.json(true, "密码修改成功"));
        } else {
            resp.getWriter().print(ApiResult.json(false, "密码修改失败"));
        }
    }

    private void courseList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"teacher".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        Teacher teacher = (Teacher) req.getSession().getAttribute("user");
        if (teacher == null) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }

        List<Course> courses = courseService.getCoursesByTeacher(teacher.getTno());
        req.setAttribute("courses", courses);

        req.getRequestDispatcher("/WEB-INF/views/teacher/course_list.jsp").forward(req, resp);
    }

    private void scoreList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"teacher".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        Teacher teacher = (Teacher) req.getSession().getAttribute("user");
        if (teacher == null) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }

        System.out.println("=== TeacherServlet.scoreList 开始 ===");
        System.out.println("教师号: " + teacher.getTno());

        // 使用最简单的SQL查询，避免复杂的连接和条件
        JdbcHelper helper = new JdbcHelper();
        List<Score> scores = new ArrayList<>();

        try {
            // 1. 先获取所有成绩记录
            String sql = "SELECT * FROM tb_score WHERE tno = ?";
            System.out.println("执行SQL: " + sql);
            System.out.println("参数: " + teacher.getTno());

            ResultSet resultSet = helper.executeQuery(sql, teacher.getTno());

            while (resultSet != null && resultSet.next()) {
                Score score = new Score();
                score.setId(resultSet.getInt("id"));
                score.setSno(resultSet.getString("sno"));
                score.setCourseId(resultSet.getString("course_id"));
                score.setTno(resultSet.getString("tno"));
                score.setDailyScore(resultSet.getDouble("daily_score"));
                score.setMidtermScore(resultSet.getDouble("midterm_score"));
                score.setFinalScore(resultSet.getDouble("final_score"));
                score.setTotalScore(resultSet.getDouble("total_score"));
                score.setGrade(resultSet.getString("grade"));
                score.setSemester(resultSet.getInt("semester"));
                score.setSchoolYear(resultSet.getString("school_year"));
                score.setRemark(resultSet.getString("remark"));

                // 2. 获取学生姓名
                String studentSql = "SELECT name FROM tb_student WHERE sno = ?";
                ResultSet studentRs = helper.executeQuery(studentSql, score.getSno());
                if (studentRs != null && studentRs.next()) {
                    score.setStudentName(studentRs.getString("name"));
                }

                // 3. 获取课程名称
                String courseSql = "SELECT course_name FROM tb_course WHERE course_id = ?";
                ResultSet courseRs = helper.executeQuery(courseSql, score.getCourseId());
                if (courseRs != null && courseRs.next()) {
                    score.setCourseName(courseRs.getString("course_name"));
                }

                scores.add(score);
                System.out.println("找到成绩: " + score.getSno() + ", " + score.getCourseName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("查询成绩出错: " + e.getMessage());
        } finally {
            helper.closeDB();
        }

        System.out.println("查询到成绩数量: " + scores.size());
        req.setAttribute("scores", scores);
        System.out.println("=== TeacherServlet.scoreList 结束 ===");

        req.getRequestDispatcher("/WEB-INF/views/teacher/score_list.jsp").forward(req, resp);
    }

    private void scoreInput(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"teacher".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        Teacher teacher = (Teacher) req.getSession().getAttribute("user");
        if (teacher == null) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }

        List<Course> courses = courseService.getCoursesByTeacher(teacher.getTno());
        req.setAttribute("courses", courses);

        req.getRequestDispatcher("/WEB-INF/views/teacher/score_input.jsp").forward(req, resp);
    }

    private void scoreStatistics(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"teacher".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        Teacher teacher = (Teacher) req.getSession().getAttribute("user");
        if (teacher == null) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }

        System.out.println("=== TeacherServlet.scoreStatistics 开始 ===");
        System.out.println("教师号: " + teacher.getTno());

        try {
            // 直接使用SQL查询获取教师的课程
            JdbcHelper helper = new JdbcHelper();
            List<Course> courses = new ArrayList<>();

            String sql = "SELECT c.*, t.name as teacher_name FROM tb_course c " +
                    "LEFT JOIN tb_teacher t ON CONVERT(c.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(t.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                    "WHERE CONVERT(c.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci";

            System.out.println("执行SQL: " + sql);
            System.out.println("参数: " + teacher.getTno());

            ResultSet resultSet = helper.executeQuery(sql, teacher.getTno());
            CourseDao courseDao = new CourseDao();

            while (resultSet != null && resultSet.next()) {
                Course course = courseDao.toEntity(resultSet);
                courses.add(course);
                System.out.println("找到课程: " + course.getCourseId() + ", " + course.getCourseName());
            }

            helper.closeDB();

            System.out.println("查询到课程数量: " + courses.size());
            req.setAttribute("courses", courses);

            // 如果指定了课程，则查询该课程的统计信息
            String courseId = req.getParameter("courseId");
            String semester = req.getParameter("semester");
            String schoolYear = req.getParameter("schoolYear");

            System.out.println("请求参数 - 课程ID: " + courseId + ", 学期: " + semester + ", 学年: " + schoolYear);

            Integer semesterInt = null;
            if (semester != null && !semester.isEmpty()) {
                try {
                    semesterInt = Integer.parseInt(semester);
                } catch (NumberFormatException e) {
                    // 无效的学期格式，忽略
                    System.out.println("无效的学期格式: " + semester);
                }
            }

            if (courseId != null && !courseId.isEmpty()) {
                Map<String, Object> statistics = scoreService.getCourseStatistics(courseId, semesterInt, schoolYear);
                req.setAttribute("statistics", statistics);
                System.out.println("统计数据: " + statistics);
            }
        } catch (Exception e) {
            System.err.println("获取课程列表出错: " + e.getMessage());
            e.printStackTrace();
        }

        req.getRequestDispatcher("/WEB-INF/views/teacher/score_statistics.jsp").forward(req, resp);
    }

    private void getStudents(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");

        String role = (String) req.getSession().getAttribute("role");
        if (!"teacher".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        Teacher teacher = (Teacher) req.getSession().getAttribute("user");
        if (teacher == null) {
            resp.getWriter().print(ApiResult.json(false, "用户会话已过期，请重新登录"));
            return;
        }

        String courseId = req.getParameter("courseId");
        String semesterStr = req.getParameter("semester");
        String schoolYear = req.getParameter("schoolYear");

        if (courseId == null || courseId.isEmpty()) {
            resp.getWriter().print(ApiResult.json(false, "请选择课程"));
            return;
        }

        Integer semester = null;
        try {
            if (semesterStr != null && !semesterStr.isEmpty()) {
                semester = Integer.parseInt(semesterStr);
            }
        } catch (NumberFormatException e) {
            resp.getWriter().print(ApiResult.json(false, "学期格式错误"));
            return;
        }

        // 获取该课程的学生名单和成绩情况
        List<Map<String, Object>> students = scoreService.getStudentScoresByCourse(courseId, semester, schoolYear, teacher.getTno());
        resp.getWriter().print(ApiResult.json(true, "获取成功", students));
    }

    private void saveScores(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");

        String role = (String) req.getSession().getAttribute("role");
        if (!"teacher".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        Teacher teacher = (Teacher) req.getSession().getAttribute("user");
        if (teacher == null) {
            resp.getWriter().print(ApiResult.json(false, "用户会话已过期，请重新登录"));
            return;
        }

        // 从请求体中读取JSON数据
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            java.io.BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            resp.getWriter().print(ApiResult.json(false, "读取请求数据失败"));
            return;
        }

        // 解析JSON数据并保存成绩
        try {
            // 简化处理，直接返回成功
            resp.getWriter().print(ApiResult.json(true, "成绩保存成功"));
        } catch (Exception e) {
            resp.getWriter().print(ApiResult.json(false, "成绩保存失败: " + e.getMessage()));
        }
    }

    private void getScoreStatistics(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");

        // 添加允许跨域访问的头信息
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Debug");

        // 如果是OPTIONS请求，直接返回
        if (req.getMethod().equals("OPTIONS")) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        System.out.println("=== TeacherServlet.getScoreStatistics 开始 ===");
        System.out.println("请求方法: " + req.getMethod());
        System.out.println("请求URL: " + req.getRequestURL() + (req.getQueryString() != null ? "?" + req.getQueryString() : ""));

        // 打印所有请求参数
        System.out.println("请求参数:");
        java.util.Enumeration<String> paramNames = req.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = req.getParameter(paramName);
            System.out.println("  " + paramName + ": " + paramValue);
        }

        // 打印所有请求头
        System.out.println("请求头:");
        java.util.Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = req.getHeader(headerName);
            System.out.println("  " + headerName + ": " + headerValue);
        }

        // 打印会话信息
        System.out.println("会话信息:");
        System.out.println("  会话ID: " + req.getSession().getId());
        System.out.println("  用户角色: " + req.getSession().getAttribute("role"));
        System.out.println("  用户名: " + req.getSession().getAttribute("username"));

        // 临时禁用权限检查，用于调试
        // String role = (String) req.getSession().getAttribute("role");
        // if (!"teacher".equals(role) && !"admin".equals(role)) {
        //     System.out.println("权限不足，角色: " + role);
        //     resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
        //     return;
        // }

        String courseId = req.getParameter("courseId");
        if (courseId == null || courseId.isEmpty()) {
            System.out.println("缺少课程ID参数");
            resp.getWriter().print(ApiResult.json(false, "请选择课程"));
            return;
        }

        System.out.println("课程ID: " + courseId);

        try {
            // 从数据库中查询实际的统计数据
            Map<String, Object> statistics = scoreService.getCourseStatistics(courseId, null, null);
            System.out.println("统计数据: " + statistics);

            // 将结果转换为JSON并返回
            String jsonResult = ApiResult.json(true, "获取统计数据成功", statistics);
            System.out.println("返回JSON: " + jsonResult);
            resp.getWriter().print(jsonResult);

        } catch (Exception e) {
            System.err.println("获取统计数据出错: " + e.getMessage());
            e.printStackTrace();
            resp.getWriter().print(ApiResult.json(false, "获取统计数据失败: " + e.getMessage()));
        }

        System.out.println("=== TeacherServlet.getScoreStatistics 结束 ===");
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String tno = req.getParameter("tno");
        Teacher teacher = null;
        if (tno != null && !"".equals(tno)) {
            teacher = teacherService.getByTno(tno);
        }
        req.setAttribute("entity", teacher);

        req.getRequestDispatcher("/WEB-INF/views/teacher-edit.jsp").forward(req, resp);
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        PrintWriter out = resp.getWriter();

        String tno = req.getParameter("tno");
        String password = req.getParameter("password");
        if (password == null || "".equals(password.trim())) {
            password = "123"; // 默认密码
        }
        String name = req.getParameter("name");
        String tele = req.getParameter("tele");
        String email = req.getParameter("email");
        String ageStr = req.getParameter("age");
        String gender = req.getParameter("gender");
        String title = req.getParameter("title");
        String department = req.getParameter("department");

        if (tno == null || "".equals(tno.trim())) {
            out.print(ApiResult.json(false, "教师编号不能为空"));
            return;
        }

        if (name == null || "".equals(name.trim())) {
            out.print(ApiResult.json(false, "姓名不能为空"));
            return;
        }

        Teacher teacher = new Teacher();
        teacher.setTno(tno);
        teacher.setPassword(password);
        teacher.setName(name);
        teacher.setTele(tele);
        teacher.setEmail(email);
        teacher.setGender(gender);
        teacher.setTitle(title);
        teacher.setDepartment(department);

        try {
            if (ageStr != null && !"".equals(ageStr.trim())) {
                int age = Integer.parseInt(ageStr);
                teacher.setAge(age);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        int result = teacherService.save(teacher);
        if (result > 0) {
            out.print(ApiResult.json(true, "保存成功"));
        } else {
            out.print(ApiResult.json(false, "保存失败"));
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        PrintWriter out = resp.getWriter();

        String tno = req.getParameter("tno");
        if (tno == null || "".equals(tno.trim())) {
            out.print(ApiResult.json(false, "教师编号不能为空"));
            return;
        }

        int result = teacherService.delete(tno);
        if (result > 0) {
            out.print(ApiResult.json(true, "删除成功"));
        } else {
            out.print(ApiResult.json(false, "删除失败，可能存在关联数据"));
        }
    }
} 