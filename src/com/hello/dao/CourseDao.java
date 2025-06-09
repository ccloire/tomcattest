package com.hello.dao;

import com.hello.entity.Course;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    public PagerVO<Course> page(int current, int size, String whereSql) {
        PagerVO<Course> pager = new PagerVO<>();
        pager.setCurrent(current);
        pager.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        try {
            // 先查询总数
            ResultSet countRs = helper.executeQuery("select count(1) from tb_course c " +
                    (whereSql != null ? whereSql : ""));
            if (countRs.next()) {
                int total = countRs.getInt(1);
                pager.setTotal(total);
            }

            // 查询分页数据
            String sql = "select c.*, t.name as teacher_name from tb_course c " +
                    "left join tb_teacher t on CONVERT(c.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(t.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                    (whereSql != null ? whereSql : "") +
                    " order by c.course_id " +
                    "limit " + ((current - 1) * size) + "," + size;

            System.out.println("课程查询SQL: " + sql);
            ResultSet resultSet = helper.executeQuery(sql);

            List<Course> list = new ArrayList<>();
            while (resultSet != null && resultSet.next()) {
                Course course = toEntity(resultSet);
                list.add(course);
            }
            pager.setList(list);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("查询课程列表出错: " + e.getMessage());
        } finally {
            helper.closeDB();
        }
        return pager;
    }

    public int insert(Course course) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.excuteUpdate("insert into tb_course(course_id, course_name, course_type, credit, tno, department, description, semester) values(?,?,?,?,?,?,?,?)",
                course.getCourseId(), course.getCourseName(), course.getCourseType(),
                course.getCredit(), course.getTno(), course.getDepartment(),
                course.getDescription(), course.getSemester()
        );
        helper.closeDB();
        return res;
    }

    public int update(Course course) {
        JdbcHelper helper = new JdbcHelper();
        int res = 0;
        String sql = "update tb_course set ";
        List<Object> params = new ArrayList<>();
        if (course.getCourseName() != null) {
            sql += "course_name = ?,";
            params.add(course.getCourseName());
        }
        if (course.getCourseType() != null) {
            sql += "course_type = ?,";
            params.add(course.getCourseType());
        }
        if (course.getCredit() != null) {
            sql += "credit = ?,";
            params.add(course.getCredit());
        }
        if (course.getTno() != null) {
            sql += "tno = ?,";
            params.add(course.getTno());
        }
        if (course.getDepartment() != null) {
            sql += "department = ?,";
            params.add(course.getDepartment());
        }
        if (course.getDescription() != null) {
            sql += "description = ?,";
            params.add(course.getDescription());
        }
        if (course.getSemester() != null) {
            sql += "semester = ?,";
            params.add(course.getSemester());
        }

        sql = sql.substring(0, sql.length() - 1);
        sql += " where course_id = '" + course.getCourseId() + "'";
        System.out.println(sql);
        res = helper.excuteUpdate(sql, params.toArray());
        helper.closeDB();
        return res;
    }

    public int delete(String courseId) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.excuteUpdate("delete from tb_course where course_id = ?", courseId);
        helper.closeDB();
        return res;
    }

    public int count(String whereSql) {
        if (whereSql == null) {
            whereSql = "";
        }
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_course" + whereSql);
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return 0;
    }

    public int count() {
        return count("");
    }

    public Course getByCourseId(String courseId) {
        JdbcHelper helper = new JdbcHelper();

        if (!helper.isConnected()) {
            System.err.println("课程查询失败：数据库连接失败");
            helper.closeDB();
            return null;
        }

        ResultSet resultSet = helper.executeQuery(
                "select c.*, t.name as teacher_name from tb_course c " +
                        "left join tb_teacher t on c.tno = t.tno " +
                        "where c.course_id = ?", courseId);
        try {
            if (resultSet != null && resultSet.next()) {
                return toEntity(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("查询课程信息错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return null;
    }

    public List<Course> getCoursesByTeacher(String tno) {
        JdbcHelper helper = new JdbcHelper();
        List<Course> list = new ArrayList<>();

        ResultSet resultSet = helper.executeQuery(
                "select c.*, t.name as teacher_name from tb_course c " +
                        "left join tb_teacher t on CONVERT(c.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(t.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                        "where CONVERT(c.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci", tno);
        try {
            while (resultSet != null && resultSet.next()) {
                Course course = toEntity(resultSet);
                list.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return list;
    }

    public Course toEntity(ResultSet resultSet) throws SQLException {
        Course course = new Course();
        course.setCourseId(resultSet.getString("course_id"));
        course.setCourseName(resultSet.getString("course_name"));
        course.setCourseType(resultSet.getString("course_type"));
        course.setCredit(resultSet.getInt("credit"));
        course.setTno(resultSet.getString("tno"));
        course.setDepartment(resultSet.getString("department"));
        course.setDescription(resultSet.getString("description"));
        course.setSemester(resultSet.getInt("semester"));

        try {
            course.setTeacherName(resultSet.getString("teacher_name"));
        } catch (SQLException e) {
            // teacher_name column might not be selected
        }

        return course;
    }
} 