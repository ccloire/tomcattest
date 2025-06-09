package com.hello.service;

import com.hello.dao.AdminDao;
import com.hello.entity.Admin;

public class AdminService {

    AdminDao adminDao = new AdminDao();

    public Admin getByUsername(String username){
        return adminDao.getByUsername(username);
    }

}
