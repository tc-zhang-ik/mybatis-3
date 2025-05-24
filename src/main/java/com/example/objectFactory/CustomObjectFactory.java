package com.example.objectFactory;

import com.example.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import lombok.extern.log4j.Log4j2;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

@Log4j2
public class CustomObjectFactory extends DefaultObjectFactory {

  @Override
  public <T> T create(Class<T> type) {
    log.debug("创建对象: {}", type.getName());
    return super.create(type);
  }

  @Override
  public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
    log.debug("使用构造参数创建对象: {}", type.getName());

    // 自定义创建逻辑
    if (type == User.class) {
      return (T) createUser(constructorArgTypes, constructorArgs);
    }

    return super.create(type, constructorArgTypes, constructorArgs);
  }

  private User createUser(List<Class<?>> types, List<Object> args) {
    // 自定义 User 对象创建逻辑
    User user = new User();
    user.setCreateTime(new Date()); // 设置默认创建时间
    return user;
  }

  @Override
  public void setProperties(Properties properties) {
    // 获取配置参数
    String property = properties.getProperty("customProperty");
    log.debug("ObjectFactory 配置参数: {}", property);
  }

  @Override
  public <T> boolean isCollection(Class<T> type) {
    return Collection.class.isAssignableFrom(type);
  }
}
