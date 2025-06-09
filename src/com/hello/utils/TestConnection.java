package com.hello.utils;

import java.sql.*;

/**
 * 数据库连接测试类
 */
public class TestConnection {
    public static void main(String[] args) {
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("驱动加载成功!");
            
            // 连接数据库
            String url = "jdbc:mysql://localhost:3306/stu_manage?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";
            String password = "123456";
            
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("数据库连接成功!");
            
            // 测试查询admin表
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tb_admin");
            System.out.println("查询tb_admin表成功，内容如下：");
            while (rs.next()) {
                System.out.println("用户名: " + rs.getString("username") + ", 密码: " + rs.getString("password"));
            }
            
            // 关闭资源
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("连接测试完成!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("驱动加载失败: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("数据库连接或操作失败: " + e.getMessage());
            System.err.println("SQL状态: " + e.getSQLState());
            System.err.println("错误代码: " + e.getErrorCode());
            e.printStackTrace();
        }
    }
} 