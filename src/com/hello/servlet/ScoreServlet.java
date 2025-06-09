package com.hello.servlet;

import com.hello.entity.Course;
import com.hello.entity.Score;
import com.hello.entity.Student;
import com.hello.service.CourseService;
import com.hello.service.ScoreService;
import com.hello.service.StudentService;
import com.hello.utils.ApiResult;
import com.hello.utils.vo.PagerVO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ScoreServlet extends HttpServlet {
    private ScoreService scoreService = new ScoreService();
    private StudentService studentService = new StudentService();
    private CourseService courseService = new CourseService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        if (path.equals("/score") || path.equals("/score/")) {
            list(req, resp);
        } else if (path.equals("/score/edit")) {
            edit(req, resp);
        } else if (path.equals("/score/save")) {
            save(req, resp);
        } else if (path.equals("/score/delete")) {
            delete(req, resp);
        } else if (path.equals("/score/statistics")) {
            statistics(req, resp);
        } else if (path.equals("/score/test")) {
            test(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        String tno = (String) req.getSession().getAttribute("username");
        if (!"admin".equals(role) && !"teacher".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        System.out.println("=== 成绩列表查询开始 ===");
        System.out.println("当前用户角色: " + role);
        System.out.println("当前用户名: " + tno);

        String pageStr = req.getParameter("page");
        String sizeStr = req.getParameter("size");
        String sno = req.getParameter("sno");
        String studentName = req.getParameter("studentName");
        String courseId = req.getParameter("courseId");
        String courseName = req.getParameter("courseName");
        String semester = req.getParameter("semester");
        String schoolYear = req.getParameter("schoolYear");

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

        // 如果是教师角色，只能查看自己教授课程的成绩
        if ("teacher".equals(role)) {
            whereBuilder.append(" and CONVERT(s.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT('")
                    .append(tno)
                    .append("' USING utf8mb4) COLLATE utf8mb4_unicode_ci");
        }

        if (sno != null && !sno.isEmpty()) {
            whereBuilder.append(" and s.sno like '%").append(sno).append("%'");
        }

        if (studentName != null && !studentName.isEmpty()) {
            whereBuilder.append(" and st.name like '%").append(studentName).append("%'");
        }

        if (courseId != null && !courseId.isEmpty()) {
            whereBuilder.append(" and s.course_id = '").append(courseId).append("'");
        }

        if (courseName != null && !courseName.isEmpty()) {
            whereBuilder.append(" and c.course_name like '%").append(courseName).append("%'");
        }

        if (semester != null && !semester.isEmpty()) {
            try {
                int semesterInt = Integer.parseInt(semester);
                whereBuilder.append(" and s.semester = ").append(semesterInt);
            } catch (NumberFormatException e) {
                // Invalid semester, ignore
            }
        }

        if (schoolYear != null && !schoolYear.isEmpty()) {
            whereBuilder.append(" and s.school_year like '%").append(schoolYear).append("%'");
        }

        System.out.println("查询条件: " + whereBuilder.toString());

        PagerVO<Score> pager = scoreService.page(page, size, whereBuilder.toString());
        System.out.println("查询结果总数: " + pager.getTotal());
        System.out.println("查询结果列表大小: " + (pager.getList() != null ? pager.getList().size() : 0));

        if (pager.getList() != null && !pager.getList().isEmpty()) {
            System.out.println("第一条记录: " + pager.getList().get(0).getSno() + ", " + pager.getList().get(0).getCourseName());
        } else {
            System.out.println("查询结果为空");
        }

        req.setAttribute("pager", pager);
        req.setAttribute("sno", sno);
        req.setAttribute("studentName", studentName);
        req.setAttribute("courseId", courseId);
        req.setAttribute("courseName", courseName);
        req.setAttribute("semester", semester);
        req.setAttribute("schoolYear", schoolYear);

        // 获取所有课程，用于筛选
        // 如果是教师角色，只显示自己教授的课程
        String courseWhere = "";
        if ("teacher".equals(role)) {
            courseWhere = " where CONVERT(tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT('" + tno + "' USING utf8mb4) COLLATE utf8mb4_unicode_ci";
        }
        List<Course> courses = courseService.page(1, 1000, courseWhere).getList();
        System.out.println("课程列表大小: " + (courses != null ? courses.size() : 0));
        req.setAttribute("courses", courses);

        System.out.println("=== 成绩列表查询结束 ===");
        req.getRequestDispatcher("/WEB-INF/views/score/list.jsp").forward(req, resp);
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role) && !"teacher".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String idStr = req.getParameter("id");
        Score score = null;
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                score = scoreService.getById(id);
            } catch (NumberFormatException e) {
                // Invalid ID, continue with new score
            }
        }
        req.setAttribute("score", score);

        // 获取所有学生列表，用于选择学生
        List<Student> students = studentService.page(1, 1000, null, null, null, null).getList();
        req.setAttribute("students", students);

        // 获取所有课程列表，用于选择课程
        List<Course> courses = courseService.page(1, 1000, "").getList();
        req.setAttribute("courses", courses);

        req.getRequestDispatcher("/WEB-INF/views/score/edit.jsp").forward(req, resp);
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role) && !"teacher".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        PrintWriter out = resp.getWriter();

        String idStr = req.getParameter("id");
        String sno = req.getParameter("sno");
        String courseId = req.getParameter("courseId");
        String tno = req.getParameter("tno");
        String dailyScoreStr = req.getParameter("dailyScore");
        String midtermScoreStr = req.getParameter("midtermScore");
        String finalScoreStr = req.getParameter("finalScore");
        String semesterStr = req.getParameter("semester");
        String schoolYear = req.getParameter("schoolYear");
        String remark = req.getParameter("remark");

        if (sno == null || sno.isEmpty()) {
            out.print(ApiResult.json(false, "学号不能为空"));
            return;
        }

        if (courseId == null || courseId.isEmpty()) {
            out.print(ApiResult.json(false, "课程ID不能为空"));
            return;
        }

        Score score = new Score();
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                score.setId(id);
            } catch (NumberFormatException e) {
                // Invalid ID, continue with new score
            }
        }

        score.setSno(sno);
        score.setCourseId(courseId);
        score.setTno(tno);
        score.setRemark(remark);
        score.setSchoolYear(schoolYear);

        try {
            if (dailyScoreStr != null && !dailyScoreStr.isEmpty()) {
                double dailyScore = Double.parseDouble(dailyScoreStr);
                score.setDailyScore(dailyScore);
            }
        } catch (NumberFormatException e) {
            out.print(ApiResult.json(false, "平时成绩必须是数字"));
            return;
        }

        try {
            if (midtermScoreStr != null && !midtermScoreStr.isEmpty()) {
                double midtermScore = Double.parseDouble(midtermScoreStr);
                score.setMidtermScore(midtermScore);
            }
        } catch (NumberFormatException e) {
            out.print(ApiResult.json(false, "期中成绩必须是数字"));
            return;
        }

        try {
            if (finalScoreStr != null && !finalScoreStr.isEmpty()) {
                double finalScore = Double.parseDouble(finalScoreStr);
                score.setFinalScore(finalScore);
            }
        } catch (NumberFormatException e) {
            out.print(ApiResult.json(false, "期末成绩必须是数字"));
            return;
        }

        try {
            if (semesterStr != null && !semesterStr.isEmpty()) {
                int semester = Integer.parseInt(semesterStr);
                score.setSemester(semester);
            }
        } catch (NumberFormatException e) {
            out.print(ApiResult.json(false, "学期必须是数字"));
            return;
        }

        // 计算总成绩和等级
        score.calculateTotalScore();

        int result = scoreService.save(score);
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

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            out.print(ApiResult.json(false, "成绩ID不能为空"));
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            int result = scoreService.delete(id);
            if (result > 0) {
                out.print(ApiResult.json(true, "删除成功"));
            } else {
                out.print(ApiResult.json(false, "删除失败"));
            }
        } catch (NumberFormatException e) {
            out.print(ApiResult.json(false, "无效的成绩ID"));
        }
    }

    private void statistics(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        String tno = (String) req.getSession().getAttribute("username");
        if (!"admin".equals(role) && !"teacher".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        // 获取所有课程，用于选择
        // 如果是教师角色，只显示自己教授的课程
        String courseWhere = "";
        if ("teacher".equals(role)) {
            courseWhere = " where tno = '" + tno + "'";
        }
        List<Course> courses = courseService.page(1, 1000, courseWhere).getList();
        req.setAttribute("courses", courses);

        // 如果指定了课程，则获取统计信息
        String courseId = req.getParameter("courseId");
        String semester = req.getParameter("semester");
        String schoolYear = req.getParameter("schoolYear");

        Integer semesterInt = null;
        if (semester != null && !semester.isEmpty()) {
            try {
                semesterInt = Integer.parseInt(semester);
            } catch (NumberFormatException e) {
                // Invalid semester, ignore
            }
        }

        if (courseId != null && !courseId.isEmpty()) {
            // 如果是教师角色，验证课程是否是自己教授的
            if ("teacher".equals(role)) {
                Course course = courseService.getByCourseId(courseId);
                if (course == null || !tno.equals(course.getTno())) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "您没有权限查看该课程的统计信息");
                    return;
                }
            }

            Course course = courseService.getByCourseId(courseId);
            if (course != null) {
                Map<String, Object> statistics = scoreService.getCourseStatistics(courseId, semesterInt, schoolYear);

                // 设置默认值，避免JSP页面出现空指针异常
                if (statistics != null) {
                    // 转为JSON格式字符串，在JSP页面中直接使用JavaScript解析
                    StringBuilder jsonBuilder = new StringBuilder("{");

                    // 基本统计信息
                    jsonBuilder.append("\"totalStudents\": ").append(statistics.getOrDefault("totalStudents", 0)).append(",");
                    jsonBuilder.append("\"averageScore\": ").append(statistics.getOrDefault("averageScore", 0.0)).append(",");
                    jsonBuilder.append("\"maxScore\": ").append(statistics.getOrDefault("maxScore", 0.0)).append(",");
                    jsonBuilder.append("\"minScore\": ").append(statistics.getOrDefault("minScore", 0.0)).append(",");

                    // 分数范围数据
                    jsonBuilder.append("\"scoreRanges\": [");
                    jsonBuilder.append(statistics.getOrDefault("scoreRange0_59", 0)).append(",");
                    jsonBuilder.append(statistics.getOrDefault("scoreRange60_69", 0)).append(",");
                    jsonBuilder.append(statistics.getOrDefault("scoreRange70_79", 0)).append(",");
                    jsonBuilder.append(statistics.getOrDefault("scoreRange80_89", 0)).append(",");
                    jsonBuilder.append(statistics.getOrDefault("scoreRange90_100", 0));
                    jsonBuilder.append("],");

                    // 等级分布数据
                    jsonBuilder.append("\"grades\": [");
                    jsonBuilder.append(statistics.getOrDefault("gradeA", 0)).append(",");
                    jsonBuilder.append(statistics.getOrDefault("gradeB", 0)).append(",");
                    jsonBuilder.append(statistics.getOrDefault("gradeC", 0)).append(",");
                    jsonBuilder.append(statistics.getOrDefault("gradeD", 0)).append(",");
                    jsonBuilder.append(statistics.getOrDefault("gradeF", 0));
                    jsonBuilder.append("]");

                    jsonBuilder.append("}");

                    req.setAttribute("statisticsJson", jsonBuilder.toString());
                    req.setAttribute("statistics", statistics);
                }

                req.setAttribute("course", course);
            }
        }

        req.setAttribute("courseId", courseId);
        req.setAttribute("semester", semester);
        req.setAttribute("schoolYear", schoolYear);

        req.getRequestDispatcher("/WEB-INF/views/score/statistics.jsp").forward(req, resp);
    }

    private void test(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 转发到测试JSP页面
        req.getRequestDispatcher("/WEB-INF/views/score/test.jsp").forward(req, resp);
    }
} 