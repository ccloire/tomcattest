package com.hello.service;

import com.hello.dao.StudentDao;
import com.hello.entity.Student;
import com.hello.utils.vo.PagerVO;

public class StudentService {

    StudentDao dao = new StudentDao();

    public String insert(Student student){
        if(student.getSno() == null || student.getSno().equals("")){
            return "学生学号不可为空";
        }if(student.getPassword() == null || student.getPassword().equals("")){
            return "密码不可为空";
        }if(student.getName() == null || student.getName().equals("")){
            return "学生姓名不可为空";
        }if(student.getClazzno() == null || student.getClazzno().equals("")){
            return "班级不可为空";
        }
        //是否存在相同学号的学生！
        Student ex = dao.getBySno(student.getSno());
        if(ex !=null){
            return "学号已存在！";
        }
        dao.insert(student);
        return null;
    }

    public String update(Student student){
        if(student.getSno() == null || student.getSno().equals("")){
            return "被修改的学生学号不可为空";
        }
        dao.update(student);
        return null;//成功
    }

    public Student getBySno(String sno){
        return dao.getBySno(sno);
    }

    public int count(){
        return dao.count();
    }

    public PagerVO<Student> page(int current,int size,String sno,String name,String gender,String clazzno){
        String whereSql = " where 1=1 ";
        //拼接
        if(sno!=null && !"".equals(sno)){
            whereSql += " and sno like '%" + sno + "%'";
        }
        if(name!=null && !"".equals(name)){
            whereSql += " and name like '%" + name + "%'";
        }
        if(gender!=null && !"".equals(gender)){
            whereSql += " and gender = '" + gender + "'";
        }
        if(clazzno!=null && !"".equals(clazzno)){
            whereSql += " and clazzno = '" + clazzno + "'";
        }
        return dao.page(current,size,whereSql);
    }

    public int delete(String sno){
        return dao.delete(sno);
    }

    public int save(Student student) {
        // 检查学生是否已存在
        Student existingStudent = getBySno(student.getSno());

        // 如果学生不存在，调用insert方法，否则调用update方法
        String result;
        if (existingStudent == null) {
            result = insert(student);
        } else {
            result = update(student);
        }

        // 如果result为null表示操作成功，返回1，否则返回0
        return (result == null) ? 1 : 0;
    }

    public int updateReturnInt(Student student) {
        // 调用原来的update方法并将结果转换为整数
        String result = update(student);
        // 如果result为null表示操作成功，返回1，否则返回0
        return (result == null) ? 1 : 0;
    }
}
