package com.example.service;

import com.example.dao.UserDAO;
import com.example.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    public boolean verifyUser(String username, String password) {
        User user = userDAO.getUserByUN(username);
        if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }
}
