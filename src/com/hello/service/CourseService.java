package com.hello.service;

import com.hello.dao.CourseDao;
import com.hello.entity.Course;
import com.hello.utils.vo.PagerVO;

import java.util.List;

public class CourseService {
    private CourseDao courseDao = new CourseDao();

    public Course getByCourseId(String courseId) {
        return courseDao.getByCourseId(courseId);
    }

    public PagerVO<Course> page(int current, int size, String whereSql) {
        return courseDao.page(current, size, whereSql);
    }

    public int save(Course course) {
        // 新增
        if (getByCourseId(course.getCourseId()) == null) {
            return courseDao.insert(course);
        } else {
            // 更新
            return courseDao.update(course);
        }
    }

    public int delete(String courseId) {
        return courseDao.delete(courseId);
    }

    public int count() {
        return courseDao.count();
    }
    
    public List<Course> getCoursesByTeacher(String tno) {
        return courseDao.getCoursesByTeacher(tno);
    }
} 