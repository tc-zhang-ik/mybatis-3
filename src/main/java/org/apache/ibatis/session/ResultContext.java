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
 *         </p>
 *         ResultContext 是 MyBatis 中用于 在处理结果集时封装当前行信息的上下文对象。 它主要在自定义结果处理器（ResultHandler）中使用，提供访问当前行数据和结果处理状态的能力。
 */
public interface ResultContext<T> {
  /** 获取当前处理的结果对象 */
  T getResultObject();

  /** 获取当前处理的结果序号，从 0 开始 */
  int getResultCount();

  /** 标记处理已经停止（如在处理器中调用，后续结果将不再处理） */
  boolean isStopped();

  /** 是否已停止处理 */
  void stop();

}
