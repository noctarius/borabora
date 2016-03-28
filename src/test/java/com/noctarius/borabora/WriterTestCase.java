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
package com.noctarius.borabora;

import org.junit.Test;

public class WriterTestCase {

    @Test
    public void test_write() throws Exception {
        byte[] data = new byte[20];
        Output output = Output.toByteArray(data);

        Writer writer = Writer.newBuilder(output).build();

        ObjectGraph graph = ObjectGraph.newBuilder()
                .putNumber(1)
                    .putString("foo")
                .putSequence()
                    .putNumber(2)
                    .putString("bar")
                .endSequence()
                .putDictionary(2)
                    .putEntry()
                        .putString("key1")
                        .putBoolean(true)
                    .endEntry()
                    .putEntry()
                        .putString("key2")
                        .putBoolean(false)
                    .endEntry()
                .endDictionary().build();

        writer.write(graph);
    }

}
