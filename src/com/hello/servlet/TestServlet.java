package com.hello.servlet;

import com.hello.utils.JdbcHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");

        String path = req.getServletPath();
        System.out.println("TestServlet处理路径: " + path);

        if ("/test_db".equals(path)) {
            testDb(req, resp);
        } else if ("/insert_test_data".equals(path)) {
            insertTestData(req, resp);
        } else if ("/insert_student_score".equals(path)) {
            insertStudentScore(req, resp);
        } else {
            resp.getWriter().write("未知的请求路径");
        }
    }

    private void testDb(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JdbcHelper helper = new JdbcHelper();

        if (helper.isConnected()) {
            // 测试查询
            try {
                ResultSet rs = helper.executeQuery("SELECT COUNT(*) FROM tb_score");
                if (rs != null && rs.next()) {
                    int count = rs.getInt(1);
                    resp.getWriter().write("数据库连接成功！成绩表中有 " + count + " 条记录。");
                } else {
                    resp.getWriter().write("数据库连接成功，但查询失败。");
                }
            } catch (SQLException e) {
                resp.getWriter().write("数据库连接成功，但查询出错: " + e.getMessage());
                e.printStackTrace();
            } finally {
                helper.closeDB();
            }
        } else {
            resp.getWriter().write("数据库连接失败！");
        }
    }

    private void insertTestData(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JdbcHelper helper = new JdbcHelper();

        if (!helper.isConnected()) {
            resp.getWriter().write("数据库连接失败，无法插入测试数据！");
            return;
        }

        StringBuilder result = new StringBuilder();
        int successCount = 0;
        int failCount = 0;

        try {
            // 检查数据库表结构
            try {
                ResultSet rs = helper.executeQuery("SHOW TABLES");
                result.append("数据库表:\n");
                while (rs != null && rs.next()) {
                    result.append("- ").append(rs.getString(1)).append("\n");
                }
                result.append("\n");
            } catch (Exception e) {
                result.append("查询数据库表结构失败: ").append(e.getMessage()).append("\n");
            }

            // 检查课程是否存在，如果不存在则创建
            ResultSet rs = helper.executeQuery("SELECT COUNT(*) FROM tb_course WHERE course_id = 'C001'");
            if (rs != null && rs.next() && rs.getInt(1) == 0) {
                int res = helper.excuteUpdate(
                        "INSERT INTO tb_course(course_id, course_name, credit, tno) VALUES(?, ?, ?, ?)",
                        "C001", "数据结构", 4, "T001"
                );
                if (res > 0) {
                    result.append("创建测试课程成功\n");
                } else {
                    result.append("创建测试课程失败\n");
                }
            } else {
                result.append("课程C001已存在\n");
            }

            // 检查教师是否存在，如果不存在则创建
            rs = helper.executeQuery("SELECT COUNT(*) FROM tb_teacher WHERE tno = 'T001'");
            if (rs != null && rs.next() && rs.getInt(1) == 0) {
                int res = helper.excuteUpdate(
                        "INSERT INTO tb_teacher(tno, name, password) VALUES(?, ?, ?)",
                        "T001", "测试教师", "123456"
                );
                if (res > 0) {
                    result.append("创建测试教师成功\n");
                } else {
                    result.append("创建测试教师失败\n");
                }
            } else {
                result.append("教师T001已存在\n");
            }

            // 插入一些测试学生
            String[] students = {
                    "S001,张三",
                    "S002,李四",
                    "S003,王五",
                    "S004,赵六",
                    "S005,钱七"
            };

            for (String student : students) {
                String[] parts = student.split(",");
                String sno = parts[0];
                String name = parts[1];

                rs = helper.executeQuery("SELECT COUNT(*) FROM tb_student WHERE sno = ?", sno);
                if (rs != null && rs.next() && rs.getInt(1) == 0) {
                    int res = helper.excuteUpdate(
                            "INSERT INTO tb_student(sno, name, password) VALUES(?, ?, ?)",
                            sno, name, "123456"
                    );
                    if (res > 0) {
                        result.append("创建学生 ").append(name).append(" 成功\n");
                    } else {
                        result.append("创建学生 ").append(name).append(" 失败\n");
                    }
                } else {
                    result.append("学生 ").append(name).append(" 已存在\n");
                }
            }

            // 插入测试成绩
            Object[][] scores = {
                    {"S001", "C001", "T001", 85.0, 80.0, 90.0, 86.0, "B+", 1, "2023-2024"},
                    {"S002", "C001", "T001", 90.0, 92.0, 88.0, 89.5, "A", 1, "2023-2024"},
                    {"S003", "C001", "T001", 75.0, 78.0, 80.0, 78.0, "B", 1, "2023-2024"},
                    {"S004", "C001", "T001", 60.0, 65.0, 70.0, 66.0, "C", 1, "2023-2024"},
                    {"S005", "C001", "T001", 95.0, 98.0, 92.0, 94.5, "A+", 1, "2023-2024"}
            };

            // 先清除现有的测试数据
            int deleteCount = helper.excuteUpdate("DELETE FROM tb_score WHERE course_id = 'C001'");
            result.append("删除了 ").append(deleteCount).append(" 条现有成绩记录\n");

            for (Object[] score : scores) {
                try {
                    int res = helper.excuteUpdate(
                            "INSERT INTO tb_score(sno, course_id, tno, daily_score, midterm_score, final_score, " +
                                    "total_score, grade, semester, school_year) " +
                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            score
                    );
                    if (res > 0) {
                        successCount++;
                    } else {
                        failCount++;
                        result.append("插入成绩失败: ").append(score[0]).append(", ").append(score[1]).append("\n");
                    }
                } catch (Exception e) {
                    failCount++;
                    result.append("插入成绩异常: ").append(e.getMessage()).append("\n");
                }
            }

            // 验证插入结果
            try {
                rs = helper.executeQuery("SELECT COUNT(*) FROM tb_score WHERE course_id = 'C001'");
                if (rs != null && rs.next()) {
                    int count = rs.getInt(1);
                    result.append("验证: 数据库中有 ").append(count).append(" 条C001课程的成绩记录\n");
                }
            } catch (Exception e) {
                result.append("验证插入结果失败: ").append(e.getMessage()).append("\n");
            }

            result.append("成功插入 ").append(successCount).append(" 条成绩记录，失败 ").append(failCount).append(" 条");
            resp.getWriter().write(result.toString());

        } catch (Exception e) {
            resp.getWriter().write("插入测试数据出错: " + e.getMessage() + "\n" + result.toString());
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
    }

    private void insertStudentScore(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JdbcHelper helper = new JdbcHelper();

        if (!helper.isConnected()) {
            resp.getWriter().write("数据库连接失败，无法插入测试数据！");
            return;
        }

        StringBuilder result = new StringBuilder();

        // 获取当前登录的学生信息
        String sno = (String) req.getSession().getAttribute("username");
        if (sno == null) {
            Object user = req.getSession().getAttribute("user");
            if (user != null && user.getClass().getName().contains("Student")) {
                try {
                    java.lang.reflect.Method getSnoMethod = user.getClass().getMethod("getSno");
                    sno = (String) getSnoMethod.invoke(user);
                } catch (Exception e) {
                    result.append("获取学生学号失败: ").append(e.getMessage()).append("\n");
                }
            }
        }

        if (sno == null) {
            resp.getWriter().write("无法获取当前登录学生的学号，请确保已登录");
            return;
        }

        result.append("当前学生学号: ").append(sno).append("\n");

        try {
            // 检查课程是否存在，如果不存在则创建
            ResultSet rs = helper.executeQuery("SELECT COUNT(*) FROM tb_course WHERE course_id = 'C001'");
            if (rs != null && rs.next() && rs.getInt(1) == 0) {
                int res = helper.excuteUpdate(
                        "INSERT INTO tb_course(course_id, course_name, credit, tno) VALUES(?, ?, ?, ?)",
                        "C001", "数据结构", 4, "T001"
                );
                if (res > 0) {
                    result.append("创建测试课程成功\n");
                } else {
                    result.append("创建测试课程失败\n");
                }
            } else {
                result.append("课程C001已存在\n");
            }

            // 检查教师是否存在，如果不存在则创建
            rs = helper.executeQuery("SELECT COUNT(*) FROM tb_teacher WHERE tno = 'T001'");
            if (rs != null && rs.next() && rs.getInt(1) == 0) {
                int res = helper.excuteUpdate(
                        "INSERT INTO tb_teacher(tno, name, password) VALUES(?, ?, ?)",
                        "T001", "测试教师", "123456"
                );
                if (res > 0) {
                    result.append("创建测试教师成功\n");
                } else {
                    result.append("创建测试教师失败\n");
                }
            } else {
                result.append("教师T001已存在\n");
            }

            // 为当前学生添加成绩
            // 先检查是否已有成绩
            rs = helper.executeQuery(
                    "SELECT COUNT(*) FROM tb_score WHERE CONVERT(sno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci AND course_id = 'C001'",
                    sno
            );

            if (rs != null && rs.next() && rs.getInt(1) > 0) {
                // 已有成绩，更新
                int res = helper.excuteUpdate(
                        "UPDATE tb_score SET daily_score = ?, midterm_score = ?, final_score = ?, total_score = ?, grade = ? " +
                                "WHERE CONVERT(sno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci AND course_id = 'C001'",
                        85.0, 80.0, 90.0, 86.0, "B+", sno
                );
                if (res > 0) {
                    result.append("更新学生成绩成功\n");
                } else {
                    result.append("更新学生成绩失败\n");
                }
            } else {
                // 没有成绩，插入
                int res = helper.excuteUpdate(
                        "INSERT INTO tb_score(sno, course_id, tno, daily_score, midterm_score, final_score, total_score, grade, semester, school_year) " +
                                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        sno, "C001", "T001", 85.0, 80.0, 90.0, 86.0, "B+", 1, "2023-2024"
                );
                if (res > 0) {
                    result.append("插入学生成绩成功\n");
                } else {
                    result.append("插入学生成绩失败\n");
                }
            }

            // 添加第二门课程
            rs = helper.executeQuery("SELECT COUNT(*) FROM tb_course WHERE course_id = 'C002'");
            if (rs != null && rs.next() && rs.getInt(1) == 0) {
                int res = helper.excuteUpdate(
                        "INSERT INTO tb_course(course_id, course_name, credit, tno) VALUES(?, ?, ?, ?)",
                        "C002", "Java程序设计", 3, "T001"
                );
                if (res > 0) {
                    result.append("创建第二门测试课程成功\n");
                } else {
                    result.append("创建第二门测试课程失败\n");
                }
            }

            // 为当前学生添加第二门课程成绩
            rs = helper.executeQuery(
                    "SELECT COUNT(*) FROM tb_score WHERE CONVERT(sno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci AND course_id = 'C002'",
                    sno
            );

            if (rs != null && rs.next() && rs.getInt(1) > 0) {
                // 已有成绩，更新
                int res = helper.excuteUpdate(
                        "UPDATE tb_score SET daily_score = ?, midterm_score = ?, final_score = ?, total_score = ?, grade = ? " +
                                "WHERE CONVERT(sno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci AND course_id = 'C002'",
                        90.0, 92.0, 88.0, 89.5, "A", sno
                );
                if (res > 0) {
                    result.append("更新第二门课程成绩成功\n");
                } else {
                    result.append("更新第二门课程成绩失败\n");
                }
            } else {
                // 没有成绩，插入
                int res = helper.excuteUpdate(
                        "INSERT INTO tb_score(sno, course_id, tno, daily_score, midterm_score, final_score, total_score, grade, semester, school_year) " +
                                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        sno, "C002", "T001", 90.0, 92.0, 88.0, 89.5, "A", 1, "2023-2024"
                );
                if (res > 0) {
                    result.append("插入第二门课程成绩成功\n");
                } else {
                    result.append("插入第二门课程成绩失败\n");
                }
            }

            // 验证插入结果
            try {
                rs = helper.executeQuery(
                        "SELECT COUNT(*) FROM tb_score WHERE CONVERT(sno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci",
                        sno
                );
                if (rs != null && rs.next()) {
                    int count = rs.getInt(1);
                    result.append("验证: 数据库中有 ").append(count).append(" 条该学生的成绩记录\n");
                }
            } catch (Exception e) {
                result.append("验证插入结果失败: ").append(e.getMessage()).append("\n");
            }

            resp.getWriter().write(result.toString());

        } catch (Exception e) {
            resp.getWriter().write("插入学生成绩数据出错: " + e.getMessage() + "\n" + result.toString());
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
    }
} 