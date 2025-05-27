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
package org.apache.ibatis.scripting.defaults;

import java.lang.reflect.Type;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.ObjectTypeHandler;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class DefaultParameterHandler implements ParameterHandler {

  private final TypeHandlerRegistry typeHandlerRegistry;

  private final MappedStatement mappedStatement;
  private final Object parameterObject;
  private final BoundSql boundSql;
  private final Configuration configuration;

  private ParameterMetaData paramMetaData;
  private MetaObject paramMetaObject;
  private HashMap<Class<?>, MetaClass> metaClassCache = new HashMap<>();
  private static final ParameterMetaData NULL_PARAM_METADATA = new ParameterMetaData() {
    // @formatter:off
    public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
    public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
    public boolean isSigned(int param) throws SQLException { return false; }
    public int isNullable(int param) throws SQLException { return 0; }
    public int getScale(int param) throws SQLException { return 0; }
    public int getPrecision(int param) throws SQLException { return 0; }
    public String getParameterTypeName(int param) throws SQLException { return null; }
    public int getParameterType(int param) throws SQLException { return 0; }
    public int getParameterMode(int param) throws SQLException { return 0; }
    public int getParameterCount() throws SQLException { return 0; }
    public String getParameterClassName(int param) throws SQLException { return null; }
    // @formatter:on
  };

  public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    this.mappedStatement = mappedStatement;
    this.configuration = mappedStatement.getConfiguration();
    this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    this.parameterObject = parameterObject;
    this.boundSql = boundSql;
  }

  @Override
  public Object getParameterObject() {
    return parameterObject;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    // 1. 从 boundSql 获取参数映射列表
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
      ParamNameResolver paramNameResolver = mappedStatement.getParamNameResolver();
      // 2. 遍历参数映射列表
      for (int i = 0; i < parameterMappings.size(); i++) {
        ParameterMapping parameterMapping = parameterMappings.get(i);
        if (parameterMapping.getMode() != ParameterMode.OUT) {
          Object value;
          // 3. 获取参数的值、JdbcType、actualJdbcType 等
          String propertyName = parameterMapping.getProperty();
          JdbcType jdbcType = parameterMapping.getJdbcType();
          JdbcType actualJdbcType = jdbcType == null ? getParamJdbcType(ps, i + 1) : jdbcType;
          Type propertyGenericType = null;
          TypeHandler typeHandler = parameterMapping.getTypeHandler();
          // 4. 获取参数值的逻辑
          if (parameterMapping.hasValue()) {
            // 如果 `ParameterMapping` 已经包含值，则直接使用。
            value = parameterMapping.getValue();
          } else if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
            // 如果 `BoundSql` 中已经存在该参数，则直接使用。
            value = boundSql.getAdditionalParameter(propertyName);
          } else if (parameterObject == null) {
            // 如果参数为空，则值为 null
            value = null;
          } else {
            // 否则，尝试从 `parameterObject` 中提取值
            Class<? extends Object> parameterClass = parameterObject.getClass();
            // 根据参数类型从 typeHandlerRegistry 中获取类型处理器（如 Integer - > IntegerTypeHandler）
            TypeHandler paramTypeHandler = typeHandlerRegistry.getTypeHandler(parameterClass, actualJdbcType);
            if (paramTypeHandler != null) {
              value = parameterObject;
              typeHandler = paramTypeHandler;
            } else {
              paramMetaObject = getParamMetaObject();
              value = paramMetaObject.getValue(propertyName);
              if (typeHandler == null && value != null) {
                if (paramNameResolver != null && ParamMap.class.equals(parameterClass)) {
                  Type actualParamType = paramNameResolver.getType(propertyName);
                  if (actualParamType instanceof Class) {
                    Class<?> actualParamClass = (Class<?>) actualParamType;
                    MetaClass metaClass = metaClassCache.computeIfAbsent(actualParamClass,
                        k -> MetaClass.forClass(k, configuration.getReflectorFactory()));
                    PropertyTokenizer propertyTokenizer = new PropertyTokenizer(propertyName);
                    String multiParamsPropertyName;
                    if (propertyTokenizer.hasNext()) {
                      multiParamsPropertyName = propertyTokenizer.getChildren();
                      if (metaClass.hasGetter(multiParamsPropertyName)) {
                        Entry<Type, Class<?>> getterType = metaClass.getGenericGetterType(multiParamsPropertyName);
                        propertyGenericType = getterType.getKey();
                      }
                    } else {
                      propertyGenericType = actualParamClass;
                    }
                  } else {
                    propertyGenericType = actualParamType;
                  }
                } else {
                  try {
                    propertyGenericType = paramMetaObject.getGenericGetterType(propertyName).getKey();
                    typeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(propertyGenericType,
                        actualJdbcType, null);
                  } catch (Exception e) {
                    // Not always resolvable
                  }
                }
              }
            }
          }
          if (value == null) {
            if (jdbcType == null) {
              jdbcType = configuration.getJdbcTypeForNull();
            }
            if (typeHandler == null) {
              typeHandler = ObjectTypeHandler.INSTANCE;
            }
          } else if (typeHandler == null) {
            if (propertyGenericType == null) {
              propertyGenericType = value.getClass();
            }
            typeHandler = typeHandlerRegistry.getTypeHandler(propertyGenericType, actualJdbcType, null);
          }
          // 5. 如果没有找到对应的 TypeHandler，则尝试使用默认的 TypeHandler
          if (typeHandler == null) {
            typeHandler = typeHandlerRegistry.getTypeHandler(actualJdbcType);
          }
          if (typeHandler == null) {
            throw new TypeException("Could not find type handler for Java type '" + propertyGenericType.getTypeName()
                + "' nor JDBC type '" + actualJdbcType + "'");
          }
          // 6. 设置参数到 PreparedStatement 中
          try {
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
          } catch (TypeException | SQLException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
          }
        }
      }
    }
  }

  private MetaObject getParamMetaObject() {
    if (paramMetaObject != null) {
      return paramMetaObject;
    }
    paramMetaObject = configuration.newMetaObject(parameterObject);
    return paramMetaObject;
  }

  private JdbcType getParamJdbcType(PreparedStatement ps, int paramIndex) {
    try {
      if (paramMetaData == null) {
        paramMetaData = ps.getParameterMetaData();
      }
      return paramMetaData == NULL_PARAM_METADATA ? null : JdbcType.forCode(paramMetaData.getParameterType(paramIndex));
    } catch (SQLException e) {
      if (paramMetaData == null) {
        paramMetaData = NULL_PARAM_METADATA;
      }
      return null;
    }
  }

}
