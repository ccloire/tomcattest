package com.hello.servlet;

import com.hello.entity.Student;
import com.hello.service.StudentService;
import com.hello.utils.ApiResult;
import com.hello.utils.MyUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

// 注解已移至web.xml配置
public class StudentPersonal extends HttpServlet {

    StudentService studentService = new StudentService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");// 设置编码，否则从前端获取参数乱码
        resp.setContentType("application/json; charset=utf-8");
        if(!MyUtils.hasPermission(req,resp,true,"student")){
            return;
        }

        String sno = req.getParameter("sno");
        Student loginStudent = (Student) req.getSession().getAttribute("user");
        if(!loginStudent.getSno().equals(sno)){
            resp.getWriter().write(ApiResult.json(false,"只能修改自己的信息！拒绝访问"));
        }

        String r = req.getParameter("r");
        if("edit".equals(r)){
            //修改个人信息
            Student student = new Student();
            student.setSno(req.getParameter("sno"));
            student.setPassword(req.getParameter("password"));
            student.setName(req.getParameter("name"));
            student.setTele(req.getParameter("tele"));
            student.setGender(req.getParameter("gender"));
            student.setAddress(req.getParameter("address"));
            student.setEnterdate(MyUtils.strToDate(req.getParameter("enterdate")));
            String ageStr = req.getParameter("age");
            if(ageStr != null){
                student.setAge(Integer.parseInt(ageStr));
            }else {
                student.setAge(0);
            }
            String msg = studentService.update(student);
            if(msg!=null){
                resp.getWriter().write(ApiResult.json(false,msg));
                return;
            }else{
                req.getSession().setAttribute("user",studentService.getBySno(sno));
                resp.getWriter().write(ApiResult.json(true,"修改成功！"));
                return;
            }
        }
        if("pwd".equals(r)){
            String oldPwd = req.getParameter("oldPwd");
            String newPwd = req.getParameter("newPwd");
            String newPwd2 = req.getParameter("newPwd2");
            if(!Objects.equals(newPwd,newPwd2)){
                resp.getWriter().write(ApiResult.json(false,"两次新密码输入不一致！"));
                return;
            }
            if(!oldPwd.equals(loginStudent.getPassword())){
                resp.getWriter().write(ApiResult.json(false,"旧密码错误！"));
                return;
            }
            Student student = new Student();
            student.setSno(sno);
            student.setPassword(newPwd);
            studentService.update(student);
            loginStudent.setPassword(newPwd);
            req.getSession().setAttribute("user",loginStudent);
            resp.getWriter().write(ApiResult.json(true,"修改成功"));
            return;
        }
        //
    }
}
