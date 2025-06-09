package com.hello.dao;

import com.hello.entity.Admin;
import com.hello.utils.JdbcHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDao {

    public Admin getByUsername(String username){
        System.out.println("AdminDao: 尝试查询管理员用户: " + username);
        JdbcHelper helper = new JdbcHelper();

        // 检查数据库连接是否成功
        if (!helper.isConnected()) {
            System.err.println("AdminDao: 管理员查询失败 - 数据库连接失败");
            helper.closeDB();
            return null;
        }

        try {
            System.out.println("AdminDao: 正在执行SQL查询: select * from tb_admin where username = '" + username + "'");
            ResultSet resultSet = helper.executeQuery("select * from tb_admin where username = ?", username);

            if(resultSet == null) {
                System.out.println("AdminDao: 查询结果为null");
                return null;
            }

            if(resultSet.next()){
                Admin admin = new Admin();
                admin.setUsername(resultSet.getString("username"));
                admin.setPassword(resultSet.getString("password"));
                System.out.println("AdminDao: 找到管理员用户 - username: " + admin.getUsername() + ", password长度: " + admin.getPassword().length());
                return admin;
            } else {
                System.out.println("AdminDao: 未找到管理员用户: " + username);
            }
        } catch (SQLException e) {
            System.err.println("AdminDao: 查询管理员信息错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            helper.closeDB();
            System.out.println("AdminDao: 数据库连接已关闭");
        }

        System.out.println("AdminDao: 返回null，表示未找到用户");
        return null;
    }

}
