package com.hello.dao;

import com.hello.entity.Teacher;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherDao {

    public PagerVO<Teacher> page(int current, int size, String whereSql) {
        PagerVO<Teacher> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_teacher " + whereSql);
        try {
            resultSet.next();
            int total = resultSet.getInt(1);
            pagerVO.setTotal(total);
            resultSet = helper.executeQuery("select * from tb_teacher "
                    + whereSql + "limit " + ((current - 1) * size) + "," + size);
            List<Teacher> list = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = toEntity(resultSet);
                list.add(teacher);
            }
            pagerVO.setList(list);
            return pagerVO;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return pagerVO;
    }

    public int insert(Teacher teacher) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.excuteUpdate("insert into tb_teacher values(?,?,?,?,?,?,?,?,?,?)",
                teacher.getTno(), teacher.getPassword(), teacher.getName(),
                teacher.getGender(), teacher.getAge(), teacher.getTele(),
                teacher.getEmail(), teacher.getSubject(), teacher.getTitle(), 
                teacher.getDepartment()
        );
        helper.closeDB();
        return res;
    }

    public int update(Teacher teacher) {
        JdbcHelper helper = new JdbcHelper();
        int res = 0;
        String sql = "update tb_teacher set ";
        List<Object> params = new ArrayList<>();
        if (teacher.getPassword() != null) {
            sql += "password = ?,";
            params.add(teacher.getPassword());
        }
        if (teacher.getName() != null) {
            sql += "name = ?,";
            params.add(teacher.getName());
        }
        if (teacher.getGender() != null) {
            sql += "gender = ?,";
            params.add(teacher.getGender());
        }
        if (teacher.getAge() != null) {
            sql += "age = ?,";
            params.add(teacher.getAge());
        }
        if (teacher.getTele() != null) {
            sql += "tele = ?,";
            params.add(teacher.getTele());
        }
        if (teacher.getEmail() != null) {
            sql += "email = ?,";
            params.add(teacher.getEmail());
        }
        if (teacher.getSubject() != null) {
            sql += "subject = ?,";
            params.add(teacher.getSubject());
        }
        if (teacher.getTitle() != null) {
            sql += "title = ?,";
            params.add(teacher.getTitle());
        }
        if (teacher.getDepartment() != null) {
            sql += "department = ?,";
            params.add(teacher.getDepartment());
        }

        sql = sql.substring(0, sql.length() - 1);
        sql += " where tno = '" + teacher.getTno() + "'";
        System.out.println(sql);
        res = helper.excuteUpdate(sql, params.toArray());
        helper.closeDB();
        return res;
    }

    public int delete(String tno) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.excuteUpdate("delete from tb_teacher where tno = ?", tno);
        helper.closeDB();
        return res;
    }

    public int count(String whereSql) {
        if (whereSql == null) {
            whereSql = "";
        }
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_teacher" + whereSql);
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

    public Teacher getByTno(String tno) {
        JdbcHelper helper = new JdbcHelper();
        
        if (!helper.isConnected()) {
            System.err.println("教师查询失败：数据库连接失败");
            helper.closeDB();
            return null;
        }
        
        ResultSet resultSet = helper.executeQuery("select * from tb_teacher where tno = ?", tno);
        try {
            if (resultSet != null && resultSet.next()) {
                return toEntity(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("查询教师信息错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return null;
    }

    public Teacher toEntity(ResultSet resultSet) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setTno(resultSet.getString("tno"));
        teacher.setPassword(resultSet.getString("password"));
        teacher.setName(resultSet.getString("name"));
        teacher.setGender(resultSet.getString("gender"));
        teacher.setAge(resultSet.getInt("age"));
        teacher.setTele(resultSet.getString("tele"));
        teacher.setEmail(resultSet.getString("email"));
        teacher.setSubject(resultSet.getString("subject"));
        teacher.setTitle(resultSet.getString("title"));
        teacher.setDepartment(resultSet.getString("department"));
        return teacher;
    }
} 