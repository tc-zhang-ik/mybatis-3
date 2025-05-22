package com.example.builder;

import com.example.mapper.UserMapper;
import com.example.model.User;

import java.io.Reader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisExample {
  public static void main(String[] args) throws Exception {
    // 加载 MyBatis 配置
    Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

    try (SqlSession session = factory.openSession()) {
      // 建表（只做一次）
      try {
        Connection conn = session.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE users");
        stmt.executeUpdate("CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(50))");
        stmt.close();
      } catch (Exception e) {
        System.out.println("表可能已存在：" + e.getMessage());
      }

      // 使用 Mapper
      UserMapper mapper = session.getMapper(UserMapper.class);

      User user = new User();
      user.setId(1);
      user.setName("Alice");

      mapper.insertUser(user);
      session.commit();

      List<User> users = mapper.selectAll();
      for (User u : users) {
        System.out.println("User: " + u.getId() + " / " + u.getName());
      }
    }
  }
}
