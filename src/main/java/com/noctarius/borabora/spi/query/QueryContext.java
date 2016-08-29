/*
 * Copyright (c) 2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.borabora.spi.query;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.spi.codec.TagStrategy;

import java.util.List;

public interface QueryContext {

    void consume(long offset);

    void consume(Value value);

    <T> T applyDecoder(long offset, MajorType majorType, ValueType valueType);

    <T> void queryStackPush(T element);

    <T> T queryStackPop();

    <T> T queryStackPeek();

    Input input();

    long offset();

    void offset(long offset);

    ValueType valueType(long offset);

    List<TagStrategy> tagStrategies();

    SelectStatementStrategy selectStatementStrategy();

    QueryContextFactory queryContextFactory();

}
