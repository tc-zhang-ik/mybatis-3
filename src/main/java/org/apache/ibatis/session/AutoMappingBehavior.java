/*
 *    Copyright 2009-2022 the original author or authors.
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

/**
 * Specifies if and how MyBatis should automatically map columns to fields/properties.
 *
 * @author Eduardo Macarron
 */
// AutoMappingBehavior 是 MyBatis 中控制自动映射行为的枚举类型，它决定了 MyBatis 在执行结果映射时如何处理未明确映射的列。
public enum AutoMappingBehavior {

  /**
   * Disables auto-mapping.
   */
  // 完全禁用自动映射，必须通过 <resultMap> 明确指定所有映射关系。
  NONE,

  /**
   * Will only auto-map results with no nested result mappings defined inside.
   */
  // 只对 没有在 <resultMap> 中手动配置的列，MyBatis 才会尝试自动映射它们到 Java 对象属性。
  PARTIAL,

  /**
   * Will auto-map result mappings of any complexity (containing nested or otherwise).
   */
  // 自动映射所有属性，包括嵌套结果。
  FULL
}
