package com.hello.dao;

import com.hello.entity.Student;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    public PagerVO<Student> page(int current,int size,String whereSql){
        PagerVO<Student> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_student " + whereSql);
        try {
            resultSet.next();
            int total = resultSet.getInt(1);
            pagerVO.setTotal(total);
            // select * from  tb_student where .. limit 10,10
            resultSet = helper.executeQuery("select * from tb_student "
                    + whereSql + "limit " + ((current-1)*size)+ ","+size);
            List<Student> list = new ArrayList<>();
            while (resultSet.next()){
                Student student = toEntity(resultSet);
                list.add(student);
            }
            pagerVO.setList(list);
            return pagerVO;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            helper.closeDB();
        }
        return pagerVO;
    }

    public int insert(Student student){
        JdbcHelper helper = new JdbcHelper();
        int res = helper.excuteUpdate("insert into tb_student values(?,?,?,?,?,?,?,?,?)",
                student.getSno(),student.getPassword(),student.getName(),
                student.getTele(),student.getEnterdate(),student.getAge(),
                student.getGender(),student.getAddress(),student.getClazzno()
        );
        return res;
    }

    /**
     * Student 里面有属性为null的话，就忽视，不是null的话，就加入更新的sql语句，进行更新
     * @param student
     * @return
     */
    public int update(Student student){
        JdbcHelper helper = new JdbcHelper();
        int res = 0;
        String sql = "update tb_student set ";
        List<Object> params = new ArrayList<>();
        if(student.getPassword()!=null){
            sql += "password = ?,";
            params.add(student.getPassword());
        }
        if(student.getName()!=null){
            sql += "name = ?,";
            params.add(student.getName());
        }
        if(student.getTele()!=null){
            sql += "tele = ?,";
            params.add(student.getTele());
        }if(student.getEnterdate()!=null){
            sql += "enterdate = ?,";
            params.add(student.getEnterdate());
        }if(student.getAge()!=null){
            sql += "age = ?,";
            params.add(student.getAge());
        }if(student.getGender()!=null){
            sql += "gender = ?,";
            params.add(student.getGender());
        }if(student.getAddress()!=null){
            sql += "address = ?,";
            params.add(student.getAddress());
        }if(student.getClazzno()!=null){
            sql += "clazzno = ?,";
            params.add(student.getClazzno());
        }
        sql = sql.substring(0,sql.length() - 1);
        sql += " where sno = '"+student.getSno() + "'";
        System.out.println(sql);
        res = helper.excuteUpdate(sql,params.toArray());
        helper.closeDB();
        return res;
    }

    public int delete(String sno){
        JdbcHelper helper = new JdbcHelper();
        int res = helper.excuteUpdate("delete from tb_student where sno = ?",sno);
        helper.closeDB();
        return res;
    }

    public int count(String whereSql){
        if(whereSql == null){
            whereSql = "";
        }
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_student" + whereSql);
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            helper.closeDB();
        }
        return 0;
    }

    public int count(){
        return count("");
    }

    public Student getBySno(String sno){
        JdbcHelper helper = new JdbcHelper();

        // 检查数据库连接是否成功
        if (!helper.isConnected()) {
            System.err.println("学生查询失败：数据库连接失败");
            helper.closeDB();
            return null;
        }

        ResultSet resultSet = helper.executeQuery("select * from tb_student where sno = ?", sno);
        try {
            if(resultSet != null && resultSet.next()){
                return toEntity(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("查询学生信息错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return null;
    }

    public Student toEntity(ResultSet resultSet) throws SQLException {
        Student student = new Student();
        student.setSno(resultSet.getString("sno"));
        student.setPassword(resultSet.getString("password"));
        student.setName(resultSet.getString("name"));
        student.setTele(resultSet.getString("tele"));
        student.setEnterdate(resultSet.getDate("enterdate"));
        student.setAge(resultSet.getInt("age"));
        student.setGender(resultSet.getString("gender"));
        student.setAddress(resultSet.getString("address"));
        student.setClazzno(resultSet.getString("clazzno"));
        return student;
    }



}
