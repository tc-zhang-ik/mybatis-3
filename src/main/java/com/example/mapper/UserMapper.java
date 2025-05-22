package com.example.mapper;

import com.example.model.User;

import java.util.List;

public interface UserMapper {
  void insertUser(User user);

  User selectUser(int id);

  List<User> selectAll();
}
