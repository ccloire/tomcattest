package com.hello.filter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 登录过滤器
 * 只有登录用户才能访问
 */
@WebFilter(urlPatterns = {"/index.jsp","/clazz/*","/student/*"})
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        Object user = request.getSession().getAttribute("user");
        if(user == null){
            request.getRequestDispatcher("/login.jsp").forward(request,servletResponse);
        }else{
            filterChain.doFilter(request,servletResponse);
        }
    }
    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("LoginFilter init....");
    }
    @Override
    public void destroy() {
        System.out.println("LoginFilter destroy....");
    }
}
