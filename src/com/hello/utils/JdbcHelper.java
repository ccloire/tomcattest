package com.hello.utils;

import java.sql.*;

/**
 * 1 数据库配置信息
 * 2 提供最基本的和数据库交互的方法
 */
public class JdbcHelper {

    // 更改驱动类名，尝试使用新版驱动
    private static final String className = "com.mysql.cj.jdbc.Driver";
    // 给URL添加更多参数以解决可能的连接问题，添加统一的字符集校对规则
    private static final String url = "jdbc:mysql://localhost:3306/stu_manage?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8&autoReconnect=true&connectionCollation=utf8mb4_unicode_ci";
    private static final String user = "root";
    private static final String pass = "123456";// 确认密码是否正确

    public static void main(String[] args) {
        JdbcHelper helper = new JdbcHelper();
        if (helper.isConnected()) {
            ResultSet resultSet = helper.executeQuery("select * from tb_student");
            try {
                while (resultSet != null && resultSet.next()){
                    System.out.println(resultSet.getString("sno"));
                    System.out.println(resultSet.getString("name"));
                    System.out.println(resultSet.getString("age"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                helper.closeDB();
            }
        } else {
            System.out.println("数据库连接失败");
        }
    }

    static {
        boolean driverLoaded = false;
        try {
            // 尝试直接加载驱动
            Class.forName(className);
            System.out.println("数据库驱动加载成功: " + className);
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            System.err.println("数据库驱动加载失败: " + e.getMessage());

            // 尝试加载不同的驱动类
            try {
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("尝试加载旧版驱动成功");
                driverLoaded = true;
            } catch (ClassNotFoundException ex) {
                System.err.println("尝试加载旧版驱动也失败: " + ex.getMessage());

                // 尝试手动注册驱动
                try {
                    java.sql.DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                    System.out.println("手动注册驱动成功");
                    driverLoaded = true;
                } catch (Exception regEx) {
                    System.err.println("手动注册驱动失败: " + regEx.getMessage());
                    regEx.printStackTrace();
                }
            }
        }

        if (!driverLoaded) {
            System.err.println("警告: 无法加载MySQL驱动，连接数据库可能会失败");
        }
    }

    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;
    private boolean connected = false;

    public JdbcHelper(){
        try {
            // 添加连接超时设置
            DriverManager.setLoginTimeout(5); // 设置登录超时时间为5秒
            conn = DriverManager.getConnection(url, user, pass);
            connected = true;
            System.out.println("数据库连接成功! URL: " + url);
        } catch (SQLException e) {
            connected = false;
            System.err.println("数据库连接失败: " + e.getMessage());
            System.err.println("连接URL: " + url);
            System.err.println("用户名: " + user);
            System.err.println("密码长度: " + (pass != null ? pass.length() : 0));
            // 添加更详细的错误信息
            System.err.println("SQL状态: " + e.getSQLState());
            System.err.println("错误代码: " + e.getErrorCode());
            e.printStackTrace();

            // 尝试使用不同的连接URL
            String alternativeUrl = "jdbc:mysql://127.0.0.1:3306/stu_manage?useUnicode=true&characterEncoding=utf-8";
            try {
                System.out.println("尝试使用替代URL连接: " + alternativeUrl);
                conn = DriverManager.getConnection(alternativeUrl, user, pass);
                connected = true;
                System.out.println("使用替代URL连接成功!");
            } catch (SQLException ex) {
                System.err.println("替代URL连接也失败: " + ex.getMessage());
            }
        }
    }

    public boolean isConnected() {
        if (!connected || conn == null) {
            return false;
        }

        // 尝试执行一个简单查询以确认连接是否真的可用
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            boolean valid = rs.next();
            rs.close();
            stmt.close();
            return valid;
        } catch (SQLException e) {
            System.err.println("连接检查失败: " + e.getMessage());
            return false;
        }
    }

    public ResultSet executeQuery(String sql, Object... params){
        if (!isConnected()) {
            System.err.println("数据库未连接，无法执行查询: " + sql);
            return null;
        }

        try {
            pstmt = conn.prepareStatement(sql);
            if(params!=null){
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i+1, params[i]);
                }
            }
            rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            System.err.println("SQL执行失败: " + sql);
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public int excuteUpdate(String sql, Object... params){
        if (!isConnected()) {
            System.err.println("数据库未连接，无法执行更新: " + sql);
            return -1;
        }

        int row = -1;
        try {
            pstmt = conn.prepareStatement(sql);
            if(params!=null){
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i+1, params[i]);
                }
            }
            row = pstmt.executeUpdate();//sql执行以后影响的行数
            return row;
        } catch (SQLException e) {
            System.err.println("SQL更新失败: " + sql);
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public void closeDB(){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(pstmt != null){
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn!=null){
            try {
                conn.close();
                connected = false;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
