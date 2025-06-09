package com.hello.entity;

public class Course {
    private String courseId; // 课程ID
    private String courseName; // 课程名称
    private String courseType; // 课程类型（必修/选修）
    private Integer credit; // 学分
    private String tno; // 授课教师编号
    private String teacherName; // 授课教师姓名
    private String department; // 开课院系
    private String description; // 课程描述
    private Integer semester; // 学期（1-8表示大一上到大四下）

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

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseType='" + courseType + '\'' +
                ", credit=" + credit +
                ", tno='" + tno + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", department='" + department + '\'' +
                ", description='" + description + '\'' +
                ", semester=" + semester +
                '}';
    }
} 