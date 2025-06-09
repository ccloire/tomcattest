package com.hello.entity;

public class Score {
    private Integer id; // 成绩ID
    private String sno; // 学号
    private String studentName; // 学生姓名
    private String courseId; // 课程ID
    private String courseName; // 课程名称
    private String tno; // 教师编号
    private String teacherName; // 教师姓名
    private Double dailyScore; // 平时成绩（占比30%）
    private Double midtermScore; // 期中成绩（占比30%）
    private Double finalScore; // 期末成绩（占比40%）
    private Double totalScore; // 总成绩
    private String grade; // 等级（A+, A, B+, B, C+, C, D, F）
    private Integer semester; // 学期
    private String schoolYear; // 学年
    private String remark; // 备注

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTno() {
        return tno;
    }

    public void setTno(String tno) {
        this.tno = tno;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Double getDailyScore() {
        return dailyScore;
    }

    public void setDailyScore(Double dailyScore) {
        this.dailyScore = dailyScore;
    }

    public Double getMidtermScore() {
        return midtermScore;
    }

    public void setMidtermScore(Double midtermScore) {
        this.midtermScore = midtermScore;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    // Calculate total score based on the components
    public void calculateTotalScore() {
        if (dailyScore != null && midtermScore != null && finalScore != null) {
            this.totalScore = dailyScore * 0.3 + midtermScore * 0.3 + finalScore * 0.4;
            
            // Set grade based on total score
            if (totalScore >= 95) {
                this.grade = "A+";
            } else if (totalScore >= 90) {
                this.grade = "A";
            } else if (totalScore >= 85) {
                this.grade = "B+";
            } else if (totalScore >= 80) {
                this.grade = "B";
            } else if (totalScore >= 75) {
                this.grade = "C+";
            } else if (totalScore >= 70) {
                this.grade = "C";
            } else if (totalScore >= 60) {
                this.grade = "D";
            } else {
                this.grade = "F";
            }
        }
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", sno='" + sno + '\'' +
                ", studentName='" + studentName + '\'' +
                ", courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", tno='" + tno + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", dailyScore=" + dailyScore +
                ", midtermScore=" + midtermScore +
                ", finalScore=" + finalScore +
                ", totalScore=" + totalScore +
                ", grade='" + grade + '\'' +
                ", semester=" + semester +
                ", schoolYear='" + schoolYear + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
} 