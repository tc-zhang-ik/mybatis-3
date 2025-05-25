package com.example.builder;

import java.sql.*;

public class JDBCExample {
  public static void main(String[] args) {
    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://114.96.88.66:3306/mydb"; // 会创建一个名为 mydb 的数据库
    String username = "root";
    String password = "Welc@me1";

    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    // 建立连接
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      Statement stmt = conn.createStatement();

      // 创建表
      // stmt.executeUpdate("DROP TABLE users");
      // stmt.executeUpdate("CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(50))");

      // 插入数据
      // stmt.executeUpdate("INSERT INTO users VALUES (1, 'Alice')");
      // stmt.executeUpdate("INSERT INTO users VALUES (2, 'Bob')");

      // 查询数据
      ResultSet rs1 = stmt.executeQuery("SELECT * FROM users");

      //
      while (rs1.next()) {
        System.out.println("ID: " + rs1.getInt("id") + ", Name: " + rs1.getString("name"));
      }

      // 使用 prepareStatement
      PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users");
      preparedStatement.execute();

      ResultSet rs2 = preparedStatement.getResultSet();
      while (rs2.next()) {
        System.out.println("ID: " + rs2.getInt("id") + ", Name: " + rs2.getString("name"));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
