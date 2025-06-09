package com.hello.service;

import com.hello.dao.CertificateDao;
import com.hello.entity.Certificate;
import com.hello.utils.vo.PagerVO;

import java.util.List;

public class CertificateService {
    private CertificateDao certificateDao = new CertificateDao();

    public Certificate getById(int id) {
        return certificateDao.getById(id);
    }

    public PagerVO<Certificate> page(int current, int size, String whereSql) {
        return certificateDao.page(current, size, whereSql);
    }

    public int save(Certificate certificate) {
        System.out.println("CertificateService.save() - 开始保存证书: " + certificate);

        // 新增
        if (certificate.getId() == null) {
            System.out.println("CertificateService.save() - 执行新增操作");
            int result = certificateDao.insert(certificate);
            System.out.println("CertificateService.save() - 新增结果: " + result);
            return result;
        } else {
            // 更新
            System.out.println("CertificateService.save() - 执行更新操作，ID: " + certificate.getId());
            int result = certificateDao.update(certificate);
            System.out.println("CertificateService.save() - 更新结果: " + result);
            return result;
        }
    }

    public int delete(int id) {
        return certificateDao.delete(id);
    }

    public List<Certificate> getCertificatesBySno(String sno) {
        System.out.println("CertificateService.getCertificatesBySno() - 查询学号: " + sno + " 的证书");
        List<Certificate> certificates = certificateDao.getCertificatesBySno(sno);
        System.out.println("CertificateService.getCertificatesBySno() - 查询结果数量: " +
                (certificates != null ? certificates.size() : 0));
        if (certificates != null && !certificates.isEmpty()) {
            System.out.println("CertificateService.getCertificatesBySno() - 第一条记录: " + certificates.get(0));
        }
        return certificates;
    }

    public int updateVerifyStatus(int id, String status, String remark) {
        return certificateDao.updateVerifyStatus(id, status, remark);
    }
} 