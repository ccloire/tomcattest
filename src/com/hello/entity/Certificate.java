package com.hello.entity;

import java.util.Date;

public class Certificate {
    private Integer id; // 证书ID
    private String sno; // 学号
    private String studentName; // 学生姓名
    private String certType; // 证书类型（CET-4, CET-6等）
    private String certNumber; // 证书编号
    private Double score; // 证书分数
    private Date issueDate; // 证书颁发日期
    private Date validUntil; // 证书有效期
    private String scanPath; // 证书扫描件路径
    private String verifyStatus; // 验证状态（未验证/已验证/验证失败）
    private String verifyRemark; // 验证备注
    private Date uploadTime; // 上传时间
    private Date verifyTime; // 验证时间
    private String issueOrg; // 发证机构
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

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertNumber() {
        return certNumber;
    }

    public void setCertNumber(String certNumber) {
        this.certNumber = certNumber;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public String getScanPath() {
        return scanPath;
    }

    public void setScanPath(String scanPath) {
        this.scanPath = scanPath;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getVerifyRemark() {
        return verifyRemark;
    }

    public void setVerifyRemark(String verifyRemark) {
        this.verifyRemark = verifyRemark;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Date getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(Date verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getIssueOrg() {
        return issueOrg;
    }

    public void setIssueOrg(String issueOrg) {
        this.issueOrg = issueOrg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "id=" + id +
                ", sno='" + sno + '\'' +
                ", certType='" + certType + '\'' +
                ", certNumber='" + certNumber + '\'' +
                ", score=" + score +
                ", issueDate=" + issueDate +
                ", validUntil=" + validUntil +
                ", scanPath='" + scanPath + '\'' +
                ", verifyStatus='" + verifyStatus + '\'' +
                ", verifyRemark='" + verifyRemark + '\'' +
                ", uploadTime=" + uploadTime +
                ", verifyTime=" + verifyTime +
                ", issueOrg='" + issueOrg + '\'' +
                ", remark='" + remark + '\'' +
                ", studentName='" + studentName + '\'' +
                '}';
    }
} 