package com.hello.service;

import com.hello.dao.TeacherDao;
import com.hello.entity.Teacher;
import com.hello.utils.vo.PagerVO;

public class TeacherService {
    private TeacherDao teacherDao = new TeacherDao();

    public Teacher getByTno(String tno){
        return teacherDao.getByTno(tno);
    }

    public PagerVO<Teacher> page(int current, int size, String name) {
        String whereSql = " where 1=1 ";
        if (name != null && !"".equals(name)) {
            whereSql += " and name like '%" + name + "%'";
        }
        
        return teacherDao.page(current, size, whereSql);
    }

    public int save(Teacher teacher) {
        // 新增
        if (getByTno(teacher.getTno()) == null) {
            return teacherDao.insert(teacher);
        } else {
            // 更新
            return teacherDao.update(teacher);
        }
    }

    public int delete(String tno) {
        return teacherDao.delete(tno);
    }

    public int updatePassword(String tno, String newPassword) {
        Teacher teacher = new Teacher();
        teacher.setTno(tno);
        teacher.setPassword(newPassword);
        return teacherDao.update(teacher);
    }
    
    public int count() {
        return teacherDao.count();
    }
} 