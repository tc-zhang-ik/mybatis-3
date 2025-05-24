package com.example.builder;

import com.example.mapper.UserMapper;
import com.example.model.User;

import java.io.Reader;
import java.sql.Connection;
import java.sql.Statement;

import lombok.extern.log4j.Log4j2;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

@Log4j2
public class MyBatisExample {
  public static void main(String[] args) throws Exception {
    // 加载 MyBatis 配置，根据 mybatis-config.xml 去 classpath 下查找对应的文件并转为 InputStreamReader
    Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
    // DefaultSqlSessionFactory
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

    try (SqlSession session = factory.openSession()) {
      // 建表（只做一次）
      try {
        Connection conn = session.getConnection();
        Statement stmt = conn.createStatement();
        // stmt.execute("DROP TABLE users");
        // stmt.executeUpdate("CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(50))");
        stmt.close();
      } catch (Exception e) {
        log.error("表可能已存在：{}", e.getMessage());
      }

      // 使用 Mapper
      UserMapper mapper = session.getMapper(UserMapper.class);

      User user = mapper.selectUser(1);
      log.debug("User: {} / {}/{}", user.getId(), user.getName(), user.getCreateTime());
    }
  }
}
