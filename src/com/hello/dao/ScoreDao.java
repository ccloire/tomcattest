package com.hello.dao;

import com.hello.entity.Score;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreDao {

    public PagerVO<Score> page(int current, int size, String whereSql) {
        PagerVO<Score> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();

        System.out.println("=== ScoreDao.page 开始执行 ===");

        // 修改计数查询，使用相同的表连接
        String countSql = "select count(1) from tb_score s " +
                "left join tb_student st on CONVERT(s.sno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(st.sno USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                "left join tb_course c on CONVERT(s.course_id USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(c.course_id USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                "left join tb_teacher t on CONVERT(s.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(t.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                whereSql;
        System.out.println("计数SQL: " + countSql);

        try {
            ResultSet resultSet = helper.executeQuery(countSql);
            resultSet.next();
            int total = resultSet.getInt(1);
            pagerVO.setTotal(total);
            System.out.println("查询总数: " + total);

            // 实际查询数据
            String dataSql = "select s.*, st.name as student_name, c.course_name, t.name as teacher_name " +
                    "from tb_score s " +
                    "left join tb_student st on CONVERT(s.sno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(st.sno USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                    "left join tb_course c on CONVERT(s.course_id USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(c.course_id USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                    "left join tb_teacher t on CONVERT(s.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(t.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                    whereSql +
                    " order by s.course_id, s.sno " +  // 添加排序
                    "limit " + ((current - 1) * size) + "," + size;
            System.out.println("数据SQL: " + dataSql);

            resultSet = helper.executeQuery(dataSql);

            List<Score> list = new ArrayList<>();
            while (resultSet.next()) {
                Score score = toEntity(resultSet);
                list.add(score);
                System.out.println("查询到成绩: " + score.getSno() + ", " + score.getCourseId() + ", " + score.getTno());
            }
            pagerVO.setList(list);
            System.out.println("查询结果数量: " + list.size());
            System.out.println("=== ScoreDao.page 执行完成 ===");
            return pagerVO;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("分页查询出错: " + e.getMessage());
        } finally {
            helper.closeDB();
        }
        System.out.println("=== ScoreDao.page 执行异常结束 ===");
        return pagerVO;
    }

    public int insert(Score score) {
        JdbcHelper helper = new JdbcHelper();

        // Calculate total score and grade before inserting
        if (score.getTotalScore() == null &&
                score.getDailyScore() != null &&
                score.getMidtermScore() != null &&
                score.getFinalScore() != null) {
            score.calculateTotalScore();
        }

        int res = helper.excuteUpdate(
                "insert into tb_score(sno, course_id, tno, daily_score, midterm_score, final_score, " +
                        "total_score, grade, semester, school_year, remark) " +
                        "values(?,?,?,?,?,?,?,?,?,?,?)",
                score.getSno(), score.getCourseId(), score.getTno(),
                score.getDailyScore(), score.getMidtermScore(), score.getFinalScore(),
                score.getTotalScore(), score.getGrade(), score.getSemester(),
                score.getSchoolYear(), score.getRemark()
        );
        helper.closeDB();
        return res;
    }

    public int update(Score score) {
        JdbcHelper helper = new JdbcHelper();

        // Calculate total score if component scores are provided
        if (score.getDailyScore() != null && score.getMidtermScore() != null && score.getFinalScore() != null) {
            score.calculateTotalScore();
        }

        int res = 0;
        String sql = "update tb_score set ";
        List<Object> params = new ArrayList<>();

        if (score.getDailyScore() != null) {
            sql += "daily_score = ?,";
            params.add(score.getDailyScore());
        }
        if (score.getMidtermScore() != null) {
            sql += "midterm_score = ?,";
            params.add(score.getMidtermScore());
        }
        if (score.getFinalScore() != null) {
            sql += "final_score = ?,";
            params.add(score.getFinalScore());
        }
        if (score.getTotalScore() != null) {
            sql += "total_score = ?,";
            params.add(score.getTotalScore());
        }
        if (score.getGrade() != null) {
            sql += "grade = ?,";
            params.add(score.getGrade());
        }
        if (score.getSemester() != null) {
            sql += "semester = ?,";
            params.add(score.getSemester());
        }
        if (score.getSchoolYear() != null) {
            sql += "school_year = ?,";
            params.add(score.getSchoolYear());
        }
        if (score.getRemark() != null) {
            sql += "remark = ?,";
            params.add(score.getRemark());
        }

        if (params.isEmpty()) {
            return 0; // Nothing to update
        }

        sql = sql.substring(0, sql.length() - 1);
        sql += " where id = " + score.getId();
        System.out.println(sql);
        res = helper.excuteUpdate(sql, params.toArray());
        helper.closeDB();
        return res;
    }

    public int delete(int id) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.excuteUpdate("delete from tb_score where id = ?", id);
        helper.closeDB();
        return res;
    }

    public int count(String whereSql) {
        if (whereSql == null) {
            whereSql = "";
        }
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_score" + whereSql);
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

    public Score getById(int id) {
        JdbcHelper helper = new JdbcHelper();

        if (!helper.isConnected()) {
            System.err.println("成绩查询失败：数据库连接失败");
            helper.closeDB();
            return null;
        }

        ResultSet resultSet = helper.executeQuery(
                "select s.*, st.name as student_name, c.course_name, t.name as teacher_name " +
                        "from tb_score s " +
                        "left join tb_student st on s.sno = st.sno " +
                        "left join tb_course c on s.course_id = c.course_id " +
                        "left join tb_teacher t on s.tno = t.tno " +
                        "where s.id = ?", id);
        try {
            if (resultSet != null && resultSet.next()) {
                return toEntity(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("查询成绩信息错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return null;
    }

    public List<Score> getScoresBySno(String sno) {
        JdbcHelper helper = new JdbcHelper();
        List<Score> list = new ArrayList<>();

        ResultSet resultSet = helper.executeQuery(
                "select s.*, st.name as student_name, c.course_name, t.name as teacher_name " +
                        "from tb_score s " +
                        "left join tb_student st on CONVERT(s.sno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(st.sno USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                        "left join tb_course c on CONVERT(s.course_id USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(c.course_id USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                        "left join tb_teacher t on CONVERT(s.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(t.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                        "where CONVERT(s.sno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                        "order by s.semester, s.course_id", sno);
        try {
            while (resultSet != null && resultSet.next()) {
                Score score = toEntity(resultSet);
                list.add(score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return list;
    }

    public List<Score> getScoresByTeacher(String tno) {
        JdbcHelper helper = new JdbcHelper();
        List<Score> list = new ArrayList<>();

        ResultSet resultSet = helper.executeQuery(
                "select s.*, st.name as student_name, c.course_name, t.name as teacher_name " +
                        "from tb_score s " +
                        "left join tb_student st on s.sno = st.sno " +
                        "left join tb_course c on s.course_id = c.course_id " +
                        "left join tb_teacher t on s.tno = t.tno " +
                        "where CONVERT(s.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci " +
                        "order by s.course_id, s.sno", tno);
        try {
            while (resultSet != null && resultSet.next()) {
                Score score = toEntity(resultSet);
                list.add(score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return list;
    }

    // Get statistics for a course
    public Map<String, Object> getCourseStatistics(String courseId, Integer semester, String schoolYear) {
        System.out.println("=== ScoreDao.getCourseStatistics 开始 ===");
        System.out.println("课程ID: " + courseId);
        System.out.println("学期: " + semester);
        System.out.println("学年: " + schoolYear);

        JdbcHelper helper = new JdbcHelper();
        Map<String, Object> stats = new HashMap<>();

        String sql = "select count(*) as total_students, " +
                "avg(total_score) as average_score, " +
                "max(total_score) as max_score, " +
                "min(total_score) as min_score, " +
                "sum(case when grade = 'A+' then 1 else 0 end) as a_plus_count, " +
                "sum(case when grade = 'A' then 1 else 0 end) as a_count, " +
                "sum(case when grade = 'B+' then 1 else 0 end) as b_plus_count, " +
                "sum(case when grade = 'B' then 1 else 0 end) as b_count, " +
                "sum(case when grade = 'C+' then 1 else 0 end) as c_plus_count, " +
                "sum(case when grade = 'C' then 1 else 0 end) as c_count, " +
                "sum(case when grade = 'D' then 1 else 0 end) as d_count, " +
                "sum(case when grade = 'F' then 1 else 0 end) as f_count, " +
                "sum(case when total_score < 60 then 1 else 0 end) as score_range0_59, " +
                "sum(case when total_score >= 60 and total_score < 70 then 1 else 0 end) as score_range60_69, " +
                "sum(case when total_score >= 70 and total_score < 80 then 1 else 0 end) as score_range70_79, " +
                "sum(case when total_score >= 80 and total_score < 90 then 1 else 0 end) as score_range80_89, " +
                "sum(case when total_score >= 90 then 1 else 0 end) as score_range90_100 " +
                "from tb_score where CONVERT(course_id USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci";

        List<Object> params = new ArrayList<>();
        params.add(courseId);

        if (semester != null) {
            sql += " and semester = ?";
            params.add(semester);
        }

        if (schoolYear != null && !schoolYear.isEmpty()) {
            sql += " and school_year = ?";
            params.add(schoolYear);
        }

        System.out.println("执行SQL: " + sql);
        System.out.println("参数: " + params);

        ResultSet resultSet = helper.executeQuery(sql, params.toArray());
        try {
            if (resultSet != null && resultSet.next()) {
                int totalStudents = resultSet.getInt("total_students");
                double avgScore = resultSet.getDouble("average_score");
                double maxScore = resultSet.getDouble("max_score");
                double minScore = resultSet.getDouble("min_score");

                stats.put("totalStudents", totalStudents);
                stats.put("avgScore", avgScore);
                stats.put("maxScore", maxScore);
                stats.put("minScore", minScore);
                stats.put("passRate", totalStudents > 0 ?
                        (double)(totalStudents - resultSet.getInt("score_range0_59")) / totalStudents : 0);

                // 等级分布
                Map<String, Integer> gradeDistribution = new HashMap<>();
                gradeDistribution.put("A+", resultSet.getInt("a_plus_count"));
                gradeDistribution.put("A", resultSet.getInt("a_count"));
                gradeDistribution.put("B+", resultSet.getInt("b_plus_count"));
                gradeDistribution.put("B", resultSet.getInt("b_count"));
                gradeDistribution.put("C+", resultSet.getInt("c_plus_count"));
                gradeDistribution.put("C", resultSet.getInt("c_count"));
                gradeDistribution.put("D", resultSet.getInt("d_count"));
                gradeDistribution.put("F", resultSet.getInt("f_count"));
                stats.put("gradeDistribution", gradeDistribution);

                // 分数区间分布
                Map<String, Integer> scoreRangeDistribution = new HashMap<>();
                scoreRangeDistribution.put("0-60", resultSet.getInt("score_range0_59"));
                scoreRangeDistribution.put("60-70", resultSet.getInt("score_range60_69"));
                scoreRangeDistribution.put("70-80", resultSet.getInt("score_range70_79"));
                scoreRangeDistribution.put("80-90", resultSet.getInt("score_range80_89"));
                scoreRangeDistribution.put("90-100", resultSet.getInt("score_range90_100"));
                stats.put("scoreRangeDistribution", scoreRangeDistribution);

                System.out.println("查询结果: 总人数=" + totalStudents + ", 平均分=" + avgScore +
                        ", 最高分=" + maxScore + ", 最低分=" + minScore);
                System.out.println("等级分布: " + gradeDistribution);
                System.out.println("分数区间分布: " + scoreRangeDistribution);
            } else {
                System.out.println("未查询到结果");
            }
        } catch (SQLException e) {
            System.err.println("查询统计数据出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }

        System.out.println("=== ScoreDao.getCourseStatistics 结束 ===");
        return stats;
    }

    // Get student average score
    public Double getStudentAverageScore(String sno, Integer semester, String schoolYear) {
        JdbcHelper helper = new JdbcHelper();

        String sql = "select avg(total_score) as avg_score from tb_score where sno = ?";

        List<Object> params = new ArrayList<>();
        params.add(sno);

        if (semester != null) {
            sql += " and semester = ?";
            params.add(semester);
        }

        if (schoolYear != null && !schoolYear.isEmpty()) {
            sql += " and school_year = ?";
            params.add(schoolYear);
        }

        ResultSet resultSet = helper.executeQuery(sql, params.toArray());
        try {
            if (resultSet != null && resultSet.next()) {
                return resultSet.getDouble("avg_score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }

        return null;
    }

    public List<Map<String, Object>> getStudentScoresByCourse(String courseId, Integer semester, String schoolYear, String tno) {
        JdbcHelper helper = new JdbcHelper();
        List<Map<String, Object>> students = new ArrayList<>();

        String sql = "SELECT s.sno, s.name as student_name, sc.id as score_id, " +
                "sc.daily_score, sc.midterm_score, sc.final_score, sc.total_score, sc.grade " +
                "FROM tb_student s " +
                "LEFT JOIN tb_score sc ON s.sno = sc.sno AND sc.course_id = ? AND " +
                "CONVERT(sc.tno USING utf8mb4) COLLATE utf8mb4_unicode_ci = CONVERT(? USING utf8mb4) COLLATE utf8mb4_unicode_ci";

        List<Object> params = new ArrayList<>();
        params.add(courseId);
        params.add(tno);

        if (semester != null) {
            sql += " AND sc.semester = ?";
            params.add(semester);
        }

        if (schoolYear != null && !schoolYear.isEmpty()) {
            sql += " AND sc.school_year = ?";
            params.add(schoolYear);
        }

        sql += " ORDER BY s.sno";

        ResultSet resultSet = helper.executeQuery(sql, params.toArray());
        try {
            while (resultSet != null && resultSet.next()) {
                Map<String, Object> student = new HashMap<>();
                student.put("sno", resultSet.getString("sno"));
                student.put("studentName", resultSet.getString("student_name"));
                student.put("scoreId", resultSet.getObject("score_id"));
                student.put("dailyScore", resultSet.getObject("daily_score"));
                student.put("midtermScore", resultSet.getObject("midterm_score"));
                student.put("finalScore", resultSet.getObject("final_score"));
                student.put("totalScore", resultSet.getObject("total_score"));
                student.put("grade", resultSet.getString("grade"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }

        return students;
    }

    public Score toEntity(ResultSet resultSet) throws SQLException {
        Score score = new Score();
        score.setId(resultSet.getInt("id"));
        score.setSno(resultSet.getString("sno"));
        score.setCourseId(resultSet.getString("course_id"));
        score.setTno(resultSet.getString("tno"));
        score.setDailyScore(resultSet.getDouble("daily_score"));
        score.setMidtermScore(resultSet.getDouble("midterm_score"));
        score.setFinalScore(resultSet.getDouble("final_score"));
        score.setTotalScore(resultSet.getDouble("total_score"));
        score.setGrade(resultSet.getString("grade"));
        score.setSemester(resultSet.getInt("semester"));
        score.setSchoolYear(resultSet.getString("school_year"));
        score.setRemark(resultSet.getString("remark"));

        // Get related entity names if available
        try {
            score.setStudentName(resultSet.getString("student_name"));
        } catch (SQLException e) {
            // student_name might not be selected
        }

        try {
            score.setCourseName(resultSet.getString("course_name"));
        } catch (SQLException e) {
            // course_name might not be selected
        }

        try {
            score.setTeacherName(resultSet.getString("teacher_name"));
        } catch (SQLException e) {
            // teacher_name might not be selected
        }

        return score;
    }
} 