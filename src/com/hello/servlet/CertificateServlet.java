package com.hello.servlet;

import com.hello.entity.Certificate;
import com.hello.entity.Student;
import com.hello.service.CertificateService;
import com.hello.service.StudentService;
import com.hello.utils.ApiResult;
import com.hello.utils.vo.PagerVO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

// 注解已移至web.xml配置
public class CertificateServlet extends HttpServlet {
    private CertificateService certificateService = new CertificateService();
    private StudentService studentService = new StudentService();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        if (path.equals("/certificate") || path.equals("/certificate/")) {
            list(req, resp);
        } else if (path.equals("/certificate/edit")) {
            edit(req, resp);
        } else if (path.equals("/certificate/save")) {
            save(req, resp);
        } else if (path.equals("/certificate/delete")) {
            delete(req, resp);
        } else if (path.equals("/certificate/verify")) {
            verifyList(req, resp);
        } else if (path.equals("/certificate/doVerify")) {
            doVerify(req, resp);
        } else if (path.equals("/certificate/upload")) {
            upload(req, resp);
        } else if (path.equals("/certificate/doUpload")) {
            doUpload(req, resp);
        } else if (path.equals("/certificate/detail")) {
            detail(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        try {
            String pageStr = req.getParameter("page");
            String sizeStr = req.getParameter("size");
            String sno = req.getParameter("sno");
            String studentName = req.getParameter("studentName");
            String certType = req.getParameter("certType");
            String verifyStatus = req.getParameter("verifyStatus");

            int page = 1;
            int size = 10;
            try {
                if (pageStr != null) {
                    page = Integer.parseInt(pageStr);
                }
                if (sizeStr != null) {
                    size = Integer.parseInt(sizeStr);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            // 构建查询条件
            StringBuilder whereBuilder = new StringBuilder(" where 1=1 ");

            if (sno != null && !sno.trim().isEmpty()) {
                whereBuilder.append(" and c.sno like '%").append(sno.trim()).append("%'");
            }

            if (studentName != null && !studentName.trim().isEmpty()) {
                whereBuilder.append(" and s.name like '%").append(studentName.trim()).append("%'");
            }

            if (certType != null && !certType.trim().isEmpty()) {
                whereBuilder.append(" and c.cert_type = '").append(certType.trim()).append("'");
            }

            if (verifyStatus != null && !verifyStatus.trim().isEmpty()) {
                whereBuilder.append(" and c.verify_status = '").append(verifyStatus.trim()).append("'");
            }

            // 添加排序
            whereBuilder.append(" order by c.upload_time desc");

            PagerVO<Certificate> pager = certificateService.page(page, size, whereBuilder.toString());

            // 设置查询参数，用于回显
            req.setAttribute("pager", pager);
            req.setAttribute("sno", sno);
            req.setAttribute("studentName", studentName);
            req.setAttribute("certType", certType);
            req.setAttribute("verifyStatus", verifyStatus);

            req.getRequestDispatcher("/WEB-INF/views/certificate/list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "查询证书列表失败：" + e.getMessage());
        }
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String idStr = req.getParameter("id");
        Certificate certificate = null;
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                certificate = certificateService.getById(id);
            } catch (NumberFormatException e) {
                // Invalid ID, continue with new certificate
            }
        }
        req.setAttribute("certificate", certificate);

        // 获取所有学生列表，用于选择学生
        List<Student> students = studentService.page(1, 1000, "", null, null, null).getList();
        req.setAttribute("students", students);

        req.getRequestDispatcher("/WEB-INF/views/certificate/edit.jsp").forward(req, resp);
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        PrintWriter out = resp.getWriter();

        String idStr = req.getParameter("id");
        String sno = req.getParameter("sno");
        String certType = req.getParameter("certType");
        String certNumber = req.getParameter("certNumber");
        String scoreStr = req.getParameter("score");
        String issueDateStr = req.getParameter("issueDate");
        String validUntilStr = req.getParameter("validUntil");
        String scanPath = req.getParameter("scanPath");
        String verifyStatus = req.getParameter("verifyStatus");
        String verifyRemark = req.getParameter("verifyRemark");

        if (sno == null || sno.isEmpty()) {
            out.print(ApiResult.json(false, "学号不能为空"));
            return;
        }

        if (certType == null || certType.isEmpty()) {
            out.print(ApiResult.json(false, "证书类型不能为空"));
            return;
        }

        Certificate certificate = new Certificate();
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                certificate.setId(id);
            } catch (NumberFormatException e) {
                // Invalid ID, continue with new certificate
            }
        }

        certificate.setSno(sno);
        certificate.setCertType(certType);
        certificate.setCertNumber(certNumber);
        certificate.setScanPath(scanPath);
        certificate.setVerifyStatus(verifyStatus);
        certificate.setVerifyRemark(verifyRemark);

        try {
            if (scoreStr != null && !scoreStr.isEmpty()) {
                double score = Double.parseDouble(scoreStr);
                certificate.setScore(score);
            }
        } catch (NumberFormatException e) {
            out.print(ApiResult.json(false, "分数必须是数字"));
            return;
        }

        try {
            if (issueDateStr != null && !issueDateStr.isEmpty()) {
                Date issueDate = sdf.parse(issueDateStr);
                certificate.setIssueDate(issueDate);
            }
        } catch (ParseException e) {
            out.print(ApiResult.json(false, "颁发日期格式不正确，请使用yyyy-MM-dd格式"));
            return;
        }

        try {
            if (validUntilStr != null && !validUntilStr.isEmpty()) {
                Date validUntil = sdf.parse(validUntilStr);
                certificate.setValidUntil(validUntil);
            }
        } catch (ParseException e) {
            out.print(ApiResult.json(false, "有效期格式不正确，请使用yyyy-MM-dd格式"));
            return;
        }

        // 设置当前时间
        if (certificate.getId() == null) {
            certificate.setUploadTime(new Date());
        }

        // 如果是管理员验证，设置验证时间
        if ("已验证".equals(verifyStatus) || "验证失败".equals(verifyStatus)) {
            certificate.setVerifyTime(new Date());
        }

        int result = certificateService.save(certificate);
        if (result > 0) {
            out.print(ApiResult.json(true, "保存成功"));
        } else {
            out.print(ApiResult.json(false, "保存失败"));
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        PrintWriter out = resp.getWriter();

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            out.print(ApiResult.json(false, "证书ID不能为空"));
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            // 获取证书信息，删除对应的文件
            Certificate certificate = certificateService.getById(id);
            if (certificate != null && certificate.getScanPath() != null) {
                String realPath = req.getServletContext().getRealPath("/");
                File file = new File(realPath + certificate.getScanPath());
                if (file.exists()) {
                    file.delete();
                }
            }

            int result = certificateService.delete(id);
            if (result > 0) {
                out.print(ApiResult.json(true, "删除成功"));
            } else {
                out.print(ApiResult.json(false, "删除失败"));
            }
        } catch (NumberFormatException e) {
            out.print(ApiResult.json(false, "无效的证书ID"));
        }
    }

    private void verifyList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String pageStr = req.getParameter("page");
        String sizeStr = req.getParameter("size");

        int page = 1;
        int size = 10;
        try {
            if (pageStr != null) {
                page = Integer.parseInt(pageStr);
            }
            if (sizeStr != null) {
                size = Integer.parseInt(sizeStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // 构建查询条件 - 只显示未验证的证书
        String whereSql = " where c.verify_status = '未验证'";

        PagerVO<Certificate> pager = certificateService.page(page, size, whereSql);
        req.setAttribute("pager", pager);

        req.getRequestDispatcher("/WEB-INF/views/certificate/verify_list.jsp").forward(req, resp);
    }

    private void doVerify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            resp.getWriter().print(ApiResult.json(false, "您没有权限执行此操作"));
            return;
        }

        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        PrintWriter out = resp.getWriter();

        String idStr = req.getParameter("id");
        String status = req.getParameter("status");
        String remark = req.getParameter("remark");

        if (idStr == null || idStr.isEmpty()) {
            out.print(ApiResult.json(false, "证书ID不能为空"));
            return;
        }

        if (status == null || status.isEmpty()) {
            out.print(ApiResult.json(false, "验证状态不能为空"));
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            int result = certificateService.updateVerifyStatus(id, status, remark);
            if (result > 0) {
                out.print(ApiResult.json(true, "验证成功"));
            } else {
                out.print(ApiResult.json(false, "验证失败"));
            }
        } catch (NumberFormatException e) {
            out.print(ApiResult.json(false, "无效的证书ID"));
        }
    }

    private void upload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"student".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/student/certificate_upload.jsp").forward(req, resp);
    }

    private void doUpload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查权限
        String role = (String) req.getSession().getAttribute("role");
        if (!"student".equals(role)) {
            System.err.println("证书上传失败：用户角色不是学生");
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        req.setCharacterEncoding("utf-8");
        System.out.println("开始处理证书上传请求...");

        Student student = (Student) req.getSession().getAttribute("user");
        if (student == null) {
            System.err.println("证书上传失败：用户会话失效");
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        System.out.println("上传用户：" + student.getSno() + ", " + student.getName());

        String certName = req.getParameter("certName");
        String certNo = req.getParameter("certNo");
        String obtainDateStr = req.getParameter("obtainDate");
        String expireDateStr = req.getParameter("expireDate");

        System.out.println("表单数据：证书名称=" + certName + ", 证书编号=" + certNo +
                ", 获得日期=" + obtainDateStr + ", 有效期至=" + expireDateStr);

        if (certName == null || certName.isEmpty()) {
            System.err.println("证书上传失败：证书名称为空");
            req.setAttribute("error", "证书名称不能为空");
            req.getRequestDispatcher("/WEB-INF/views/student/certificate_upload.jsp").forward(req, resp);
            return;
        }

        if (certNo == null || certNo.isEmpty()) {
            System.err.println("证书上传失败：证书编号为空");
            req.setAttribute("error", "证书编号不能为空");
            req.getRequestDispatcher("/WEB-INF/views/student/certificate_upload.jsp").forward(req, resp);
            return;
        }

        Certificate certificate = new Certificate();
        certificate.setSno(student.getSno());
        certificate.setCertType(certName);
        certificate.setCertNumber(certNo);
        certificate.setVerifyStatus("未验证");
        certificate.setUploadTime(new Date());

        try {
            if (obtainDateStr != null && !obtainDateStr.isEmpty()) {
                Date obtainDate = sdf.parse(obtainDateStr);
                certificate.setIssueDate(obtainDate);
                System.out.println("解析获得日期成功：" + obtainDate);
            }
        } catch (ParseException e) {
            System.err.println("证书上传失败：获得日期格式错误 - " + e.getMessage());
            req.setAttribute("error", "获得日期格式不正确，请使用yyyy-MM-dd格式");
            req.getRequestDispatcher("/WEB-INF/views/student/certificate_upload.jsp").forward(req, resp);
            return;
        }

        try {
            if (expireDateStr != null && !expireDateStr.isEmpty()) {
                Date expireDate = sdf.parse(expireDateStr);
                certificate.setValidUntil(expireDate);
                System.out.println("解析有效期成功：" + expireDate);
            }
        } catch (ParseException e) {
            System.err.println("证书上传失败：有效期格式错误 - " + e.getMessage());
            req.setAttribute("error", "有效期格式不正确，请使用yyyy-MM-dd格式");
            req.getRequestDispatcher("/WEB-INF/views/student/certificate_upload.jsp").forward(req, resp);
            return;
        }

        System.out.println("准备保存证书数据到数据库...");
        int result = certificateService.save(certificate);
        System.out.println("保存证书结果：" + result);

        if (result > 0) {
            System.out.println("证书上传成功，重定向到证书列表页面");
            resp.sendRedirect(req.getContextPath() + "/student/certificate");
        } else {
            System.err.println("证书上传失败：数据库操作返回0");
            req.setAttribute("error", "上传失败，请稍后再试");
            req.getRequestDispatcher("/WEB-INF/views/student/certificate_upload.jsp").forward(req, resp);
        }
    }

    private void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing certificate ID");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Certificate certificate = certificateService.getById(id);
            if (certificate == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found");
                return;
            }

            req.setAttribute("certificate", certificate);
            req.getRequestDispatcher("/WEB-INF/views/certificate/detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid certificate ID");
        }
    }

    // Helper method to get the submitted file name from a Part
    private String getSubmittedFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
} 