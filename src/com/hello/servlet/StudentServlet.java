package com.hello.servlet;

import com.hello.entity.Clazz;
import com.hello.entity.Student;
import com.hello.service.ClazzService;
import com.hello.service.StudentService;
import com.hello.utils.ApiResult;
import com.hello.utils.MyUtils;
import com.hello.utils.vo.PagerVO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// 注解已移至web.xml配置
public class StudentServlet extends HttpServlet {

    StudentService studentService = new StudentService();
    ClazzService clazzService = new ClazzService();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        if (path.equals("/student") || path.equals("/student/")) {
            list(req, resp);
        } else if (path.equals("/student/profile")) {
            profile(req, resp);
        } else if (path.equals("/student/password")) {
            password(req, resp);
        } else if (path.equals("/student/updatePassword")) {
            updatePassword(req, resp);
        } else if (path.equals("/student/score")) {
            scoreList(req, resp);
        } else if (path.equals("/student/certificate")) {
            certificateList(req, resp);
        } else if (path.equals("/student/certificate/upload")) {
            certificateUpload(req, resp);
        } else if (path.equals("/student/edit")) {
            edit(req, resp);
        } else if (path.equals("/student/save")) {
            save(req, resp);
        } else if (path.equals("/student/delete")) {
            delete(req, resp);
        } else if (path.equals("/student/updateProfile")) {
            updateProfile(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
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
        String sno = req.getParameter("sno");
        String ageStr = req.getParameter("age");
        String ageOpStr = req.getParameter("ageOp");

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

        // 获取性别和班级参数
        String gender = req.getParameter("gender");
        String clazzno = req.getParameter("clazzno");

        // 构建查询条件
        StringBuilder whereBuilder = new StringBuilder(" where 1=1 ");
        if (name != null && !name.isEmpty()) {
            whereBuilder.append(" and name like '%").append(name).append("%'");
        }
        if (sno != null && !sno.isEmpty()) {
            whereBuilder.append(" and sno like '%").append(sno).append("%'");
        }

        // 处理年龄范围查询
        if (ageStr != null && !ageStr.isEmpty() && ageOpStr != null && !ageOpStr.isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr);
                if ("gt".equals(ageOpStr)) {
                    whereBuilder.append(" and age > ").append(age);
                } else if ("lt".equals(ageOpStr)) {
                    whereBuilder.append(" and age < ").append(age);
                } else if ("eq".equals(ageOpStr)) {
                    whereBuilder.append(" and age = ").append(age);
                } else if ("between".equals(ageOpStr)) {
                    String ageEndStr = req.getParameter("ageEnd");
                    if (ageEndStr != null && !ageEndStr.isEmpty()) {
                        try {
                            int ageEnd = Integer.parseInt(ageEndStr);
                            whereBuilder.append(" and age between ").append(age).append(" and ").append(ageEnd);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        PagerVO<Student> pager = studentService.page(page, size, sno, name, gender, clazzno);
        req.setAttribute("pager", pager);
        req.setAttribute("name", name);
        req.setAttribute("sno", sno);
        req.setAttribute("age", ageStr);
        req.setAttribute("ageOp", ageOpStr);
        req.setAttribute("ageEnd", req.getParameter("ageEnd"));
        req.getRequestDispatcher("/WEB-INF/views/student/list.jsp").forward(req, resp);
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String sno = req.getParameter("sno");
        Student student = null;
        if (sno != null && !"".equals(sno)) {
            student = studentService.getBySno(sno);
        }
        req.setAttribute("student", student);
        List<Clazz> clazzList = clazzService.listAll();
        req.setAttribute("clazzList", clazzList);

        req.getRequestDispatcher("/WEB-INF/views/student-edit.jsp").forward(req, resp);
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

        String sno = req.getParameter("sno");
        String password = req.getParameter("password");
        if (password == null || "".equals(password.trim())) {
            password = "123"; // 默认密码
        }
        String name = req.getParameter("name");
        String tele = req.getParameter("tele");
        String enterdateStr = req.getParameter("enterdate");
        String ageStr = req.getParameter("age");
        String gender = req.getParameter("gender");
        String address = req.getParameter("address");
        String clazzno = req.getParameter("clazzno");

        if (sno == null || "".equals(sno.trim())) {
            out.print(ApiResult.json(false, "学号不能为空"));
            return;
        }

        if (name == null || "".equals(name.trim())) {
            out.print(ApiResult.json(false, "姓名不能为空"));
            return;
        }

        Student student = new Student();
        student.setSno(sno);
        student.setPassword(password);
        student.setName(name);
        student.setTele(tele);
        student.setGender(gender);
        student.setAddress(address);
        student.setClazzno(clazzno);

        try {
            if (enterdateStr != null && !"".equals(enterdateStr.trim())) {
                Date enterdate = sdf.parse(enterdateStr);
                student.setEnterdate(enterdate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            if (ageStr != null && !"".equals(ageStr.trim())) {
                int age = Integer.parseInt(ageStr);
                student.setAge(age);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        int result = studentService.save(student);
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

        String sno = req.getParameter("sno");
        if (sno == null || "".equals(sno.trim())) {
            out.print(ApiResult.json(false, "学号不能为空"));
            return;
        }

        int result = studentService.delete(sno);
        if (result > 0) {
            out.print(ApiResult.json(true, "删除成功"));
        } else {
            out.print(ApiResult.json(false, "删除失败"));
        }
    }

    private void profile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"student".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        // 获取当前学生的信息
        Student student = (Student) req.getSession().getAttribute("user");
        if (student != null) {
            // 重新从数据库获取最新数据
            student = studentService.getBySno(student.getSno());
            req.setAttribute("student", student);
            req.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/logout");
        }
    }

    private void updateProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        String role = (String) req.getSession().getAttribute("role");
        if (!"student".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        Student student = (Student) req.getSession().getAttribute("user");
        if (student == null) {
            resp.getWriter().print(ApiResult.json(false, "用户会话已过期，请重新登录"));
            return;
        }

        // 允许学生更新的信息
        String tele = req.getParameter("tele");
        String address = req.getParameter("address");

        Student updateStudent = new Student();
        updateStudent.setSno(student.getSno());
        updateStudent.setTele(tele);
        updateStudent.setAddress(address);

        int result = studentService.updateReturnInt(updateStudent);
        if (result > 0) {
            resp.getWriter().print(ApiResult.json(true, "更新成功"));
        } else {
            resp.getWriter().print(ApiResult.json(false, "更新失败"));
        }
    }

    private void password(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"student".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/student/password.jsp").forward(req, resp);
    }

    private void updatePassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        String role = (String) req.getSession().getAttribute("role");
        if (!"student".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        Student student = (Student) req.getSession().getAttribute("user");
        if (student == null) {
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

        // 重新从数据库获取学生信息
        Student currentStudent = studentService.getBySno(student.getSno());
        if (!currentStudent.getPassword().equals(oldPassword)) {
            resp.getWriter().print(ApiResult.json(false, "原密码错误"));
            return;
        }

        Student updateStudent = new Student();
        updateStudent.setSno(student.getSno());
        updateStudent.setPassword(newPassword);

        int result = studentService.updateReturnInt(updateStudent);
        if (result > 0) {
            resp.getWriter().print(ApiResult.json(true, "密码修改成功"));
        } else {
            resp.getWriter().print(ApiResult.json(false, "密码修改失败"));
        }
    }

    private void scoreList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"student".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        Student student = (Student) req.getSession().getAttribute("user");
        if (student == null) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }

        System.out.println("=== StudentServlet.scoreList 开始 ===");
        System.out.println("学生学号: " + student.getSno());
        System.out.println("学生姓名: " + student.getName());

        try {
            // 引入ScoreService
            com.hello.service.ScoreService scoreService = new com.hello.service.ScoreService();
            List<com.hello.entity.Score> scores = scoreService.getScoresBySno(student.getSno());
            req.setAttribute("scores", scores);

            System.out.println("查询到成绩记录数: " + (scores != null ? scores.size() : 0));
            if (scores != null && !scores.isEmpty()) {
                System.out.println("第一条成绩记录: " + scores.get(0).getCourseId() + ", " + scores.get(0).getCourseName() + ", " + scores.get(0).getTotalScore());
            }

            Double averageScore = scoreService.getStudentAverageScore(student.getSno(), null, null);
            req.setAttribute("averageScore", averageScore);
            System.out.println("平均分: " + averageScore);

            // 获取学生已修课程学分总和
            // 由于Score实体中没有学分字段，需要通过CourseService查询
            int totalCredits = 0;
            try {
                com.hello.service.CourseService courseService = new com.hello.service.CourseService();
                if (scores != null) {
                    for (com.hello.entity.Score score : scores) {
                        // 只计算及格的课程学分
                        if (score.getTotalScore() != null && score.getTotalScore() >= 60) {
                            com.hello.entity.Course course = courseService.getByCourseId(score.getCourseId());
                            if (course != null && course.getCredit() != null) {
                                totalCredits += course.getCredit();
                                System.out.println("课程: " + course.getCourseId() + ", 学分: " + course.getCredit());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("计算学分失败: " + e.getMessage());
                e.printStackTrace();
            }
            req.setAttribute("totalCredits", totalCredits);
            System.out.println("总学分: " + totalCredits);
        } catch (Exception e) {
            System.err.println("获取学生成绩失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== StudentServlet.scoreList 结束 ===");
        req.getRequestDispatcher("/WEB-INF/views/student/score_list.jsp").forward(req, resp);
    }

    private void certificateList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"student".equals(role)) {
            System.err.println("StudentServlet.certificateList - 用户角色不是学生: " + role);
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        Student student = (Student) req.getSession().getAttribute("user");
        if (student == null) {
            System.err.println("StudentServlet.certificateList - 用户会话失效");
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }

        System.out.println("StudentServlet.certificateList - 开始查询学生证书，学号: " + student.getSno() + ", 姓名: " + student.getName());

        try {
            // 引入CertificateService
            com.hello.service.CertificateService certificateService = new com.hello.service.CertificateService();
            List<com.hello.entity.Certificate> certificates = certificateService.getCertificatesBySno(student.getSno());

            System.out.println("StudentServlet.certificateList - 查询结果: " +
                    (certificates == null ? "null" : "共 " + certificates.size() + " 条记录"));

            if (certificates != null && !certificates.isEmpty()) {
                System.out.println("StudentServlet.certificateList - 第一条证书: " + certificates.get(0));
            }

            req.setAttribute("certificates", certificates);
        } catch (Exception e) {
            System.err.println("StudentServlet.certificateList - 获取学生证书失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("StudentServlet.certificateList - 转发到证书列表页面");
        req.getRequestDispatcher("/WEB-INF/views/student/certificate_list.jsp").forward(req, resp);
    }

    private void certificateUpload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getSession().getAttribute("role");
        if (!"student".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/student/certificate_upload.jsp").forward(req, resp);
    }
}
