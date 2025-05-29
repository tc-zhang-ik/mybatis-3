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
 * @author Clinton Begin
 */
public enum ExecutorType {
  // 默认模式，每次执行语句都会创建新的 PreparedStatement，不做缓存
  SIMPLE,
  // 会复用 PreparedStatement，只要 SQL 相同就重用，适合执行多次相同 SQL 的场景
  REUSE,
  // 批处理模式，会把多次执行合并为一批操作，在最后统一执行（如批量 INSERT、UPDATE）
  BATCH

}
