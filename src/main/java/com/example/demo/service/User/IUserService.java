package com.example.demo.service.User;

import com.example.demo.model.User;
import com.example.demo.service.IGenerate;

public interface IUserService extends IGenerate<User> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean checkPassword(String p1,String p2);
    int checkLogin(String username,String password);
}
