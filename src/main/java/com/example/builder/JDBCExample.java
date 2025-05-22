package com.example.builder;

import java.sql.*;

public class JDBCExample {
  public static void main(String[] args) {
    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    String url = "jdbc:derby:mydb;create=true"; // 会创建一个名为 mydb 的数据库

    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    // 建立连接
    try (Connection conn = DriverManager.getConnection(url)) {
      Statement stmt = conn.createStatement();

      // 创建表
      stmt.executeUpdate("DROP TABLE users");
      stmt.executeUpdate("CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(50))");

      // 插入数据
      stmt.executeUpdate("INSERT INTO users VALUES (1, 'Alice')");
      stmt.executeUpdate("INSERT INTO users VALUES (2, 'Bob')");

      // 查询数据
      ResultSet rs = stmt.executeQuery("SELECT * FROM users");

      //
      while (rs.next()) {
        System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
      }

      // 关闭资源（try-with-resources 自动处理）
      // 正确关闭 Derby（可选）
      try {
        DriverManager.getConnection("jdbc:derby:;shutdown=true");
      } catch (SQLException se) {
        if (se.getErrorCode() == 50000 && "XJ015".equals(se.getSQLState())) {
          System.out.println("Derby 正常关闭");
        } else {
          throw se;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
