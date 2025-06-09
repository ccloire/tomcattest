package com.hello.dao;

import com.hello.entity.Certificate;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CertificateDao {

    public PagerVO<Certificate> page(int current, int size, String whereSql) {
        PagerVO<Certificate> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();

        try {
            // 先构建基础查询
            String baseQuery = "from tb_certificate c left join tb_student s on c.sno = s.sno";

            // 计算总数
            String countSql = "select count(1) " + baseQuery + " " + whereSql;
            ResultSet countRs = helper.executeQuery(countSql);
            if (countRs.next()) {
                int total = countRs.getInt(1);
                pagerVO.setTotal(total);
            }

            // 查询分页数据
            String dataSql = "select c.*, s.name as student_name " + baseQuery + " " + whereSql +
                    " limit " + ((current - 1) * size) + "," + size;
            ResultSet resultSet = helper.executeQuery(dataSql);

            List<Certificate> list = new ArrayList<>();
            while (resultSet != null && resultSet.next()) {
                Certificate certificate = toEntity(resultSet);
                list.add(certificate);
            }
            pagerVO.setList(list);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("查询证书列表出错: " + e.getMessage());
        } finally {
            helper.closeDB();
        }
        return pagerVO;
    }

    public int insert(Certificate certificate) {
        JdbcHelper helper = new JdbcHelper();

        try {
            if (!helper.isConnected()) {
                System.err.println("证书保存失败：数据库连接失败");
                return 0;
            }

            System.out.println("开始保存证书数据: " + certificate.getCertType() + ", 学号: " + certificate.getSno());

            String sql = "insert into tb_certificate(sno, cert_type, cert_number, score, issue_date, valid_until, " +
                    "scan_path, verify_status, verify_remark, upload_time, verify_time, issue_org, remark) " +
                    "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

            System.out.println("执行SQL: " + sql);
            System.out.println("参数1 (sno): " + certificate.getSno());
            System.out.println("参数2 (cert_type): " + certificate.getCertType());
            System.out.println("参数3 (cert_number): " + certificate.getCertNumber());
            System.out.println("参数4 (score): " + certificate.getScore());
            System.out.println("参数5 (issue_date): " + certificate.getIssueDate());
            System.out.println("参数6 (valid_until): " + certificate.getValidUntil());
            System.out.println("参数7 (scan_path): " + certificate.getScanPath());
            System.out.println("参数8 (verify_status): " + certificate.getVerifyStatus());
            System.out.println("参数9 (verify_remark): " + certificate.getVerifyRemark());
            System.out.println("参数10 (upload_time): " + certificate.getUploadTime());
            System.out.println("参数11 (verify_time): " + certificate.getVerifyTime());
            System.out.println("参数12 (issue_org): " + certificate.getIssueOrg());
            System.out.println("参数13 (remark): " + certificate.getRemark());

            int res = helper.excuteUpdate(
                    sql,
                    certificate.getSno(), certificate.getCertType(), certificate.getCertNumber(),
                    certificate.getScore(), certificate.getIssueDate(), certificate.getValidUntil(),
                    certificate.getScanPath(), certificate.getVerifyStatus(), certificate.getVerifyRemark(),
                    certificate.getUploadTime(), certificate.getVerifyTime(), certificate.getIssueOrg(),
                    certificate.getRemark()
            );

            if (res > 0) {
                System.out.println("证书保存成功，影响行数: " + res);
            } else {
                System.err.println("证书保存失败，SQL执行成功但无行受影响");
            }

            return res;
        } catch (Exception e) {
            System.err.println("插入证书记录失败: " + e.getMessage());
            System.err.println("证书类型: " + certificate.getCertType());
            System.err.println("证书编号: " + certificate.getCertNumber());
            System.err.println("学生学号: " + certificate.getSno());
            e.printStackTrace();
            return 0;
        } finally {
            helper.closeDB();
        }
    }

    public int update(Certificate certificate) {
        JdbcHelper helper = new JdbcHelper();
        int res = 0;
        String sql = "update tb_certificate set ";
        List<Object> params = new ArrayList<>();

        if (certificate.getSno() != null) {
            sql += "sno = ?,";
            params.add(certificate.getSno());
        }
        if (certificate.getCertType() != null) {
            sql += "cert_type = ?,";
            params.add(certificate.getCertType());
        }
        if (certificate.getCertNumber() != null) {
            sql += "cert_number = ?,";
            params.add(certificate.getCertNumber());
        }
        if (certificate.getScore() != null) {
            sql += "score = ?,";
            params.add(certificate.getScore());
        }
        if (certificate.getIssueDate() != null) {
            sql += "issue_date = ?,";
            params.add(certificate.getIssueDate());
        }
        if (certificate.getValidUntil() != null) {
            sql += "valid_until = ?,";
            params.add(certificate.getValidUntil());
        }
        if (certificate.getScanPath() != null) {
            sql += "scan_path = ?,";
            params.add(certificate.getScanPath());
        }
        if (certificate.getVerifyStatus() != null) {
            sql += "verify_status = ?,";
            params.add(certificate.getVerifyStatus());
        }
        if (certificate.getVerifyRemark() != null) {
            sql += "verify_remark = ?,";
            params.add(certificate.getVerifyRemark());
        }
        if (certificate.getUploadTime() != null) {
            sql += "upload_time = ?,";
            params.add(certificate.getUploadTime());
        }
        if (certificate.getVerifyTime() != null) {
            sql += "verify_time = ?,";
            params.add(certificate.getVerifyTime());
        }
        if (certificate.getIssueOrg() != null) {
            sql += "issue_org = ?,";
            params.add(certificate.getIssueOrg());
        }
        if (certificate.getRemark() != null) {
            sql += "remark = ?,";
            params.add(certificate.getRemark());
        }

        if (params.isEmpty()) {
            return 0; // Nothing to update
        }

        sql = sql.substring(0, sql.length() - 1);
        sql += " where id = " + certificate.getId();
        System.out.println(sql);
        res = helper.excuteUpdate(sql, params.toArray());
        helper.closeDB();
        return res;
    }

    public int delete(int id) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.excuteUpdate("delete from tb_certificate where id = ?", id);
        helper.closeDB();
        return res;
    }

    public int count(String whereSql) {
        if (whereSql == null) {
            whereSql = "";
        }
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_certificate" + whereSql);
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

    public Certificate getById(int id) {
        JdbcHelper helper = new JdbcHelper();

        if (!helper.isConnected()) {
            System.err.println("证书查询失败：数据库连接失败");
            helper.closeDB();
            return null;
        }

        ResultSet resultSet = helper.executeQuery(
                "select c.*, s.name as student_name from tb_certificate c " +
                        "left join tb_student s on c.sno = s.sno " +
                        "where c.id = ?", id);
        try {
            if (resultSet != null && resultSet.next()) {
                return toEntity(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("查询证书信息错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return null;
    }

    public List<Certificate> getCertificatesBySno(String sno) {
        JdbcHelper helper = new JdbcHelper();
        List<Certificate> list = new ArrayList<>();

        System.out.println("CertificateDao.getCertificatesBySno() - 开始查询学号: " + sno + " 的证书");

        if (!helper.isConnected()) {
            System.err.println("CertificateDao.getCertificatesBySno() - 数据库连接失败");
            helper.closeDB();
            return list;
        }

        try {
            String sql = "select c.*, s.name as student_name from tb_certificate c " +
                    "left join tb_student s on c.sno = s.sno " +
                    "where c.sno = ? order by c.issue_date desc";
            System.out.println("CertificateDao.getCertificatesBySno() - 执行SQL: " + sql + ", 参数: " + sno);

            ResultSet resultSet = helper.executeQuery(sql, sno);

            if (resultSet == null) {
                System.err.println("CertificateDao.getCertificatesBySno() - 查询结果为null");
                return list;
            }

            int count = 0;
            while (resultSet.next()) {
                count++;
                Certificate certificate = toEntity(resultSet);
                list.add(certificate);
                System.out.println("CertificateDao.getCertificatesBySno() - 读取到第 " + count + " 条记录: " + certificate);
            }

            System.out.println("CertificateDao.getCertificatesBySno() - 查询完成，共找到 " + list.size() + " 条记录");
            return list;
        } catch (SQLException e) {
            System.err.println("CertificateDao.getCertificatesBySno() - 查询出错: " + e.getMessage());
            e.printStackTrace();
            return list;
        } finally {
            helper.closeDB();
        }
    }

    public int updateVerifyStatus(int id, String status, String remark) {
        JdbcHelper helper = new JdbcHelper();
        java.util.Date now = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(now.getTime());

        int res = helper.excuteUpdate(
                "update tb_certificate set verify_status = ?, verify_remark = ?, verify_time = ? where id = ?",
                status, remark, sqlDate, id);

        helper.closeDB();
        return res;
    }

    public Certificate toEntity(ResultSet resultSet) throws SQLException {
        Certificate certificate = new Certificate();
        certificate.setId(resultSet.getInt("id"));
        certificate.setSno(resultSet.getString("sno"));
        certificate.setCertType(resultSet.getString("cert_type"));
        certificate.setCertNumber(resultSet.getString("cert_number"));
        certificate.setScore(resultSet.getDouble("score"));
        certificate.setIssueDate(resultSet.getDate("issue_date"));
        certificate.setValidUntil(resultSet.getDate("valid_until"));
        certificate.setScanPath(resultSet.getString("scan_path"));
        certificate.setVerifyStatus(resultSet.getString("verify_status"));
        certificate.setVerifyRemark(resultSet.getString("verify_remark"));
        certificate.setUploadTime(resultSet.getTimestamp("upload_time"));
        certificate.setVerifyTime(resultSet.getTimestamp("verify_time"));

        // 读取发证机构和备注
        try {
            certificate.setIssueOrg(resultSet.getString("issue_org"));
        } catch (SQLException e) {
            // issue_org字段可能不存在
        }

        try {
            certificate.setRemark(resultSet.getString("remark"));
        } catch (SQLException e) {
            // remark字段可能不存在
        }

        // Get student name if available
        try {
            certificate.setStudentName(resultSet.getString("student_name"));
        } catch (SQLException e) {
            // student_name might not be selected
        }

        return certificate;
    }
} 