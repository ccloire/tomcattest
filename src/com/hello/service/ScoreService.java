package com.hello.service;

import com.hello.dao.ScoreDao;
import com.hello.entity.Score;
import com.hello.utils.vo.PagerVO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ScoreService {
    private ScoreDao scoreDao = new ScoreDao();

    public Score getById(int id) {
        return scoreDao.getById(id);
    }

    public PagerVO<Score> page(int current, int size, String whereSql) {
        return scoreDao.page(current, size, whereSql);
    }

    public int save(Score score) {
        // 新增
        if (score.getId() == null) {
            return scoreDao.insert(score);
        } else {
            // 更新
            return scoreDao.update(score);
        }
    }

    public int delete(int id) {
        return scoreDao.delete(id);
    }

    public List<Score> getScoresBySno(String sno) {
        return scoreDao.getScoresBySno(sno);
    }

    public List<Score> getScoresByTeacher(String tno) {
        return scoreDao.getScoresByTeacher(tno);
    }

    public Map<String, Object> getCourseStatistics(String courseId, Integer semester, String schoolYear) {
        System.out.println("=== ScoreService.getCourseStatistics 开始 ===");
        System.out.println("课程ID: " + courseId);
        System.out.println("学期: " + semester);
        System.out.println("学年: " + schoolYear);

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> statistics = scoreDao.getCourseStatistics(courseId, semester, schoolYear);
            if (statistics != null) {
                result.putAll(statistics);
                System.out.println("获取到统计数据: " + statistics);
            } else {
                System.out.println("未获取到统计数据");
            }
        } catch (Exception e) {
            System.err.println("获取统计数据出错: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== ScoreService.getCourseStatistics 结束 ===");
        return result;
    }

    public Double getStudentAverageScore(String sno, Integer semester, String schoolYear) {
        return scoreDao.getStudentAverageScore(sno, semester, schoolYear);
    }

    public List<Map<String, Object>> getStudentScoresByCourse(String courseId, Integer semester, String schoolYear, String tno) {
        return scoreDao.getStudentScoresByCourse(courseId, semester, schoolYear, tno);
    }
}