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
    // 解析 xml 配置文件，映射为 Configuration 对象
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

    try (SqlSession session = factory.openSession()) {
      // 建表（只做一次）
      try {
        // 通过 DriverManager.getConnection 获取 conn
        Connection conn = session.getConnection();
        Statement stmt = conn.createStatement();
        // stmt.execute("DROP TABLE users");
        // stmt.executeUpdate("CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(50))");
        stmt.close();
      } catch (Exception e) {
        log.error("表可能已存在：{}", e.getMessage());
      }

      // User user1 = session.selectOne("com.example.mapper.UserMapper.selectUser", 1);
      // log.debug("User: {} / {}/{}", user1.getId(), user1.getName(), user1.getCreateTime());

      // 使用 Mapper
      // 使用 prepareStatement 查询
      // 通过动态代理，创建 UserMapper 的代理对象 MapperProxy
      UserMapper mapper = session.getMapper(UserMapper.class);

      User user2 = mapper.selectUser(1);
      // 使用 sqlId 方式
      User user3 = session.selectOne("com.example.mapper.UserMapper.selectUser", 1);
      log.debug("User: {}", user3);
      log.debug("User: {}", user2);
    }
  }
}
