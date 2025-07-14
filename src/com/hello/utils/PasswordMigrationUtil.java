package com.hello.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 密码迁移工具类
 * 用于将数据库中的明文密码转换为加密密码
 */
public class PasswordMigrationUtil {
    
    /**
     * 迁移管理员密码
     */
    public static void migrateAdminPasswords() {
        LogUtil.info("开始迁移管理员密码...");
        JdbcHelper helper = new JdbcHelper();
        
        try {
            if (!helper.isConnected()) {
                LogUtil.error("数据库连接失败，无法迁移密码");
                return;
            }
            
            // 查询所有管理员
            ResultSet rs = helper.executeQuery("SELECT username, password FROM tb_admin");
            int count = 0;
            
            while (rs != null && rs.next()) {
                String username = rs.getString("username");
                String plainPassword = rs.getString("password");
                
                // 检查密码是否已经是加密格式
                if (plainPassword != null && !plainPassword.contains(":")) {
                    // 明文密码，需要加密
                    String encryptedPassword = PasswordUtil.encryptPassword(plainPassword);
                    
                    // 更新数据库
                    int result = helper.excuteUpdate(
                        "UPDATE tb_admin SET password = ? WHERE username = ?",
                        encryptedPassword, username
                    );
                    
                    if (result > 0) {
                        LogUtil.info("管理员 " + username + " 密码加密成功");
                        count++;
                    } else {
                        LogUtil.warning("管理员 " + username + " 密码加密失败");
                    }
                } else {
                    LogUtil.debug("管理员 " + username + " 密码已经是加密格式，跳过");
                }
            }
            
            LogUtil.info("管理员密码迁移完成，共处理 " + count + " 个账户");
            
        } catch (SQLException e) {
            LogUtil.error("迁移管理员密码时出错: " + e.getMessage(), e);
        } finally {
            helper.closeDB();
        }
    }
    
    /**
     * 迁移教师密码
     */
    public static void migrateTeacherPasswords() {
        LogUtil.info("开始迁移教师密码...");
        JdbcHelper helper = new JdbcHelper();
        
        try {
            if (!helper.isConnected()) {
                LogUtil.error("数据库连接失败，无法迁移密码");
                return;
            }
            
            // 查询所有教师
            ResultSet rs = helper.executeQuery("SELECT tno, password FROM tb_teacher");
            int count = 0;
            
            while (rs != null && rs.next()) {
                String tno = rs.getString("tno");
                String plainPassword = rs.getString("password");
                
                // 检查密码是否已经是加密格式
                if (plainPassword != null && !plainPassword.contains(":")) {
                    // 明文密码，需要加密
                    String encryptedPassword = PasswordUtil.encryptPassword(plainPassword);
                    
                    // 更新数据库
                    int result = helper.excuteUpdate(
                        "UPDATE tb_teacher SET password = ? WHERE tno = ?",
                        encryptedPassword, tno
                    );
                    
                    if (result > 0) {
                        LogUtil.info("教师 " + tno + " 密码加密成功");
                        count++;
                    } else {
                        LogUtil.warning("教师 " + tno + " 密码加密失败");
                    }
                } else {
                    LogUtil.debug("教师 " + tno + " 密码已经是加密格式，跳过");
                }
            }
            
            LogUtil.info("教师密码迁移完成，共处理 " + count + " 个账户");
            
        } catch (SQLException e) {
            LogUtil.error("迁移教师密码时出错: " + e.getMessage(), e);
        } finally {
            helper.closeDB();
        }
    }
    
    /**
     * 迁移学生密码
     */
    public static void migrateStudentPasswords() {
        LogUtil.info("开始迁移学生密码...");
        JdbcHelper helper = new JdbcHelper();
        
        try {
            if (!helper.isConnected()) {
                LogUtil.error("数据库连接失败，无法迁移密码");
                return;
            }
            
            // 查询所有学生
            ResultSet rs = helper.executeQuery("SELECT sno, password FROM tb_student");
            int count = 0;
            
            while (rs != null && rs.next()) {
                String sno = rs.getString("sno");
                String plainPassword = rs.getString("password");
                
                // 检查密码是否已经是加密格式
                if (plainPassword != null && !plainPassword.contains(":")) {
                    // 明文密码，需要加密
                    String encryptedPassword = PasswordUtil.encryptPassword(plainPassword);
                    
                    // 更新数据库
                    int result = helper.excuteUpdate(
                        "UPDATE tb_student SET password = ? WHERE sno = ?",
                        encryptedPassword, sno
                    );
                    
                    if (result > 0) {
                        LogUtil.info("学生 " + sno + " 密码加密成功");
                        count++;
                    } else {
                        LogUtil.warning("学生 " + sno + " 密码加密失败");
                    }
                } else {
                    LogUtil.debug("学生 " + sno + " 密码已经是加密格式，跳过");
                }
            }
            
            LogUtil.info("学生密码迁移完成，共处理 " + count + " 个账户");
            
        } catch (SQLException e) {
            LogUtil.error("迁移学生密码时出错: " + e.getMessage(), e);
        } finally {
            helper.closeDB();
        }
    }
    
    /**
     * 迁移所有用户密码
     */
    public static void migrateAllPasswords() {
        LogUtil.info("开始迁移所有用户密码...");
        
        migrateAdminPasswords();
        migrateTeacherPasswords();
        migrateStudentPasswords();
        
        LogUtil.info("所有用户密码迁移完成");
    }
    
    /**
     * 创建测试用户（用于测试密码加密功能）
     */
    public static void createTestUsers() {
        LogUtil.info("创建测试用户...");
        JdbcHelper helper = new JdbcHelper();
        
        try {
            if (!helper.isConnected()) {
                LogUtil.error("数据库连接失败，无法创建测试用户");
                return;
            }
            
            // 创建测试管理员
            String adminPassword = PasswordUtil.encryptPassword("admin123");
            helper.excuteUpdate(
                "INSERT INTO tb_admin (username, password) VALUES (?, ?) ON DUPLICATE KEY UPDATE password = ?",
                "admin", adminPassword, adminPassword
            );
            
            // 创建测试教师
            String teacherPassword = PasswordUtil.encryptPassword("teacher123");
            helper.excuteUpdate(
                "INSERT INTO tb_teacher (tno, password, name, department) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE password = ?",
                "T001", teacherPassword, "测试教师", "计算机系", teacherPassword
            );
            
            // 创建测试学生
            String studentPassword = PasswordUtil.encryptPassword("student123");
            helper.excuteUpdate(
                "INSERT INTO tb_student (sno, password, name, clazzno) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE password = ?",
                "S001", studentPassword, "测试学生", "CS2021", studentPassword
            );
            
            LogUtil.info("测试用户创建完成");
            LogUtil.info("测试账号：");
            LogUtil.info("管理员 - 用户名: admin, 密码: admin123");
            LogUtil.info("教师 - 工号: T001, 密码: teacher123");
            LogUtil.info("学生 - 学号: S001, 密码: student123");
            
        } catch (Exception e) {
            LogUtil.error("创建测试用户时出错: " + e.getMessage(), e);
        } finally {
            helper.closeDB();
        }
    }
    
    /**
     * 主方法，用于测试
     */
    public static void main(String[] args) {
        LogUtil.info("密码迁移工具启动");
        
        // 创建测试用户
        createTestUsers();
        
        // 迁移现有密码（如果有的话）
        migrateAllPasswords();
        
        LogUtil.info("密码迁移工具执行完成");
    }
} 