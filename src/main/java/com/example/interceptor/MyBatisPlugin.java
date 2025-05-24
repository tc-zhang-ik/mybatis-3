package com.example.interceptor;

import java.sql.Statement;

import lombok.extern.log4j.Log4j2;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;

@Intercepts({
    @Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }),
    @Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
    @Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }) })
@Log4j2
public class MyBatisPlugin implements Interceptor {

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    long startTime = System.currentTimeMillis();

    try {
      return invocation.proceed();
    } finally {
      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;

      StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
      BoundSql boundSql = statementHandler.getBoundSql();
      String sql = boundSql.getSql();

      log.debug("SQL执行时间: {}ms", duration);
      log.debug("执行的SQL: {}", sql);
    }
  }
}
