package com.example.dao;

import com.example.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserDAO {
    public User getUserByUN(@Param("un") String username);
}
