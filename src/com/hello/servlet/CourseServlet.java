package com.hello.servlet;

import com.hello.entity.Course;
import com.hello.entity.Teacher;
import com.hello.service.CourseService;
import com.hello.service.TeacherService;
import com.hello.utils.ApiResult;
import com.hello.utils.vo.PagerVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// 已在web.xml中配置URL映射，删除注解
public class CourseServlet extends HttpServlet {
    private CourseService courseService = new CourseService();
    private TeacherService teacherService = new TeacherService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        if (path.equals("/course") || path.equals("/course/")) {
            list(req, resp);
        } else if (path.equals("/course/edit")) {
            edit(req, resp);
        } else if (path.equals("/course/save")) {
            save(req, resp);
        } else if (path.equals("/course/delete")) {
            delete(req, resp);
        } else if (path.equals("/course/detail")) {
            detail(req, resp);
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

        try {
            String pageStr = req.getParameter("page");
            String sizeStr = req.getParameter("size");
            String courseName = req.getParameter("courseName");
            String courseType = req.getParameter("courseType");
            String department = req.getParameter("department");

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

            // 构建查询条件
            StringBuilder whereBuilder = new StringBuilder(" where 1=1 ");
            if (courseName != null && !courseName.trim().isEmpty()) {
                whereBuilder.append(" and c.course_name like '%").append(courseName.trim()).append("%'");
            }
            if (courseType != null && !courseType.trim().isEmpty()) {
                whereBuilder.append(" and c.course_type = '").append(courseType.trim()).append("'");
            }
            if (department != null && !department.trim().isEmpty()) {
                whereBuilder.append(" and c.department like '%").append(department.trim()).append("%'");
            }

            PagerVO<Course> pager = courseService.page(page, size, whereBuilder.toString());

            // 设置查询参数，用于回显
            req.setAttribute("pager", pager);
            req.setAttribute("courseName", courseName);
            req.setAttribute("courseType", courseType);
            req.setAttribute("department", department);

            req.getRequestDispatcher("/WEB-INF/views/course/list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "查询课程列表失败：" + e.getMessage());
        }
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String courseId = req.getParameter("courseId");
        Course course = null;
        if (courseId != null && !courseId.isEmpty()) {
            course = courseService.getByCourseId(courseId);
        }
        req.setAttribute("course", course);

        // 获取所有教师列表，用于选择授课教师
        List<Teacher> teachers = teacherService.page(1, 1000, "").getList();
        req.setAttribute("teachers", teachers);

        req.getRequestDispatcher("/WEB-INF/views/course/edit.jsp").forward(req, resp);
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

        String courseId = req.getParameter("courseId");
        String courseName = req.getParameter("courseName");
        String courseType = req.getParameter("courseType");
        String creditStr = req.getParameter("credit");
        String tno = req.getParameter("tno");
        String department = req.getParameter("department");
        String description = req.getParameter("description");
        String semesterStr = req.getParameter("semester");

        if (courseId == null || courseId.isEmpty()) {
            out.print(ApiResult.json(false, "课程ID不能为空"));
            return;
        }

        if (courseName == null || courseName.isEmpty()) {
            out.print(ApiResult.json(false, "课程名称不能为空"));
            return;
        }

        Course course = new Course();
        course.setCourseId(courseId);
        course.setCourseName(courseName);
        course.setCourseType(courseType);
        course.setTno(tno);
        course.setDepartment(department);
        course.setDescription(description);

        try {
            if (creditStr != null && !creditStr.isEmpty()) {
                int credit = Integer.parseInt(creditStr);
                course.setCredit(credit);
            }
        } catch (NumberFormatException e) {
            out.print(ApiResult.json(false, "学分必须是数字"));
            return;
        }

        try {
            if (semesterStr != null && !semesterStr.isEmpty()) {
                int semester = Integer.parseInt(semesterStr);
                course.setSemester(semester);
            }
        } catch (NumberFormatException e) {
            out.print(ApiResult.json(false, "学期必须是数字"));
            return;
        }

        int result = courseService.save(course);
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

        String courseId = req.getParameter("courseId");
        if (courseId == null || courseId.isEmpty()) {
            out.print(ApiResult.json(false, "课程ID不能为空"));
            return;
        }

        int result = courseService.delete(courseId);
        if (result > 0) {
            out.print(ApiResult.json(true, "删除成功"));
        } else {
            out.print(ApiResult.json(false, "删除失败"));
        }
    }

    private void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String courseId = req.getParameter("courseId");
        if (courseId == null || courseId.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing course ID");
            return;
        }

        Course course = courseService.getByCourseId(courseId);
        if (course == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
            return;
        }

        req.setAttribute("course", course);
        req.getRequestDispatcher("/WEB-INF/views/course/detail.jsp").forward(req, resp);
    }
} 