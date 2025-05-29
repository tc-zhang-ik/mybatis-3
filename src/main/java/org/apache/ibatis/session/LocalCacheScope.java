/*
 *    Copyright 2009-2023 the original author or authors.
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
 * @author Eduardo Macarron
 */
public enum LocalCacheScope {
  // LocalCacheScope 是 MyBatis 中用于控制 一级缓存（也叫本地缓存）作用范围 的一个配置项。
  // SESSION - 一级缓存作用于整个 SqlSession，多次查询相同数据会命中缓存
  // STATEMENT - 每次查询都不使用缓存，相当于查询后就清理缓存，更严格、更实时
  SESSION, STATEMENT
}
