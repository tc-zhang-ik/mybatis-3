/*
 *    Copyright 2009-2025 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session;

import java.lang.reflect.Type;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * Specify the behavior when detects an unknown column (or unknown property type) of automatic mapping target.
 *
 * @since 3.4.0
 *
 * @author Kazuki Shimizu
 */
// 在使用 MyBatis 自动映射（比如配置了 <resultType> 而不是 <resultMap>）时，
// MyBatis 会尝试把结果集的列映射到 Java 对象的属性。
// 如果结果集中出现了 Java 对象里没有的字段，就会触发这个配置的判断逻辑。
public enum AutoMappingUnknownColumnBehavior {

  /**
   * Do nothing (Default).
   */
  // 不处理，忽略 email 列
  NONE {
    @Override
    public void doAction(MappedStatement mappedStatement, String columnName, String property, Type propertyType) {
      // do nothing
    }
  },

  /**
   * Output warning log. Note: The log level of {@code 'org.apache.ibatis.session.AutoMappingUnknownColumnBehavior'}
   * must be set to {@code WARN}.
   */
  // 打印日志：未映射字段 email
  WARNING {
    @Override
    public void doAction(MappedStatement mappedStatement, String columnName, String property, Type propertyType) {
      LogHolder.log.warn(buildMessage(mappedStatement, columnName, property, propertyType));
    }
  },

  /**
   * Fail mapping. Note: throw {@link SqlSessionException}.
   */
  // 抛出异常，终止映射
  FAILING {
    @Override
    public void doAction(MappedStatement mappedStatement, String columnName, String property, Type propertyType) {
      throw new SqlSessionException(buildMessage(mappedStatement, columnName, property, propertyType));
    }
  };

  /**
   * Perform the action when detects an unknown column (or unknown property type) of automatic mapping target.
   *
   * @param mappedStatement
   *          current mapped statement
   * @param columnName
   *          column name for mapping target
   * @param propertyName
   *          property name for mapping target
   * @param propertyType
   *          property type for mapping target (If this argument is not null, {@link org.apache.ibatis.type.TypeHandler}
   *          for property type is not registered)
   */
  public abstract void doAction(MappedStatement mappedStatement, String columnName, String propertyName,
      Type propertyType);

  /**
   * build error message.
   */
  private static String buildMessage(MappedStatement mappedStatement, String columnName, String property,
      Type propertyType) {
    return new StringBuilder("Unknown column is detected on '").append(mappedStatement.getId())
        .append("' auto-mapping. Mapping parameters are ").append("[").append("columnName=").append(columnName)
        .append(",").append("propertyName=").append(property).append(",").append("propertyType=")
        .append(propertyType != null ? propertyType.getTypeName() : null).append("]").toString();
  }

  private static class LogHolder {
    private static final Log log = LogFactory.getLog(AutoMappingUnknownColumnBehavior.class);
  }

}
