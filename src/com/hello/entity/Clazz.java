package com.hello.entity;

public class Clazz {
    //不在数据库里面的
    private Integer stuCount;//班级人数

    private String clazzno;//班级编号
    private String name;//班级名

    public Integer getStuCount() {
        return stuCount;
    }

    public void setStuCount(Integer stuCount) {
        this.stuCount = stuCount;
    }

    public String getClazzno() {
        return clazzno;
    }

    public void setClazzno(String clazzno) {
        this.clazzno = clazzno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

