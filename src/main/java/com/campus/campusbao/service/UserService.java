package com.campus.campusbao.service;

import com.campus.campusbao.entity.User;

public interface UserService {
    User login(String username, String password);
    User resetPassword(User user);
    User getById(Integer id);
    void updateById(User user);
    void removeById(Integer id);
    String register(User user);


}