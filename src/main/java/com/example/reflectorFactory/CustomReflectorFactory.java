package com.example.reflectorFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.log4j.Log4j2;

import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;

@Log4j2
public class CustomReflectorFactory implements ReflectorFactory {

  private boolean classCacheEnabled = true;
  private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();
  private final Map<Class<?>, Long> accessCounts = new ConcurrentHashMap<>();

  @Override
  public boolean isClassCacheEnabled() {
    return classCacheEnabled;
  }

  @Override
  public void setClassCacheEnabled(boolean classCacheEnabled) {
    this.classCacheEnabled = classCacheEnabled;
    if (!classCacheEnabled) {
      reflectorMap.clear();
    }
  }

  public Reflector findForClass(Type type) {
    // 记录访问次数
    accessCounts.merge((Class<?>) type, 1L, Long::sum);

    if (classCacheEnabled) {
      return reflectorMap.computeIfAbsent((Class<?>) type, this::createReflector);
    } else {
      return createReflector((Class<?>) type);
    }
  }

  private Reflector createReflector(Class<?> type) {
    log.debug("创建 Reflector for: {}", type.getName());

    // 可以在这里添加自定义逻辑
    // if (type.isAnnotationPresent(CustomEntity.class)) {
    // return new CustomReflector(type);
    // }

    return new Reflector(type);
  }

  // 获取统计信息
  public void printStatistics() {
    log.debug("Reflector 访问统计:");
    accessCounts.forEach((type, count) -> log.debug("{}: {} 次", type.getSimpleName(), count));

    log.debug("缓存的 Reflector 数量: {}", reflectorMap.size());
  }

  // 清除指定类的缓存
  public void evictClass(Class<?> type) {
    reflectorMap.remove(type);
    accessCounts.remove(type);
  }

  // 清除所有缓存
  public void clearCache() {
    reflectorMap.clear();
    accessCounts.clear();
  }
}
