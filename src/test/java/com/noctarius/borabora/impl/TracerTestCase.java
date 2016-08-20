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
package com.noctarius.borabora.impl;

import org.junit.Test;

import java.lang.reflect.Field;

public class TracerTestCase {

    @Test
    public void test_console_tracer() {
        Tracer tracer = new Tracer.ConsoleTracer();
        tracer.traceCall0("test_console_tracer");
        tracer.traceInfo0("test_console_tracer");
        tracer.traceCall0("test_console_tracer");
        tracer.traceReturn0("test_console_tracer");
        tracer.traceReturn0("test_console_tracer");
    }

    @Test
    public void test_noop_tracer() {
        Tracer tracer = new Tracer.NoopTracer();
        tracer.traceCall0("test_console_tracer");
        tracer.traceInfo0("test_console_tracer");
        tracer.traceReturn0("test_console_tracer");
    }

    @Test
    public void test_tracer_disabled() {
        Tracer.traceCall("", null);
        Tracer.traceInfo("", null);
        Tracer.traceReturn("", null);
    }

    @Test
    public void test_tracer_enabled()
            throws Exception {

        Field field = Tracer.class.getDeclaredField("TRACE_ENABLED");
        try {
            field.setAccessible(true);
            field.set(Tracer.class, true);
            Tracer.traceCall("", new Object());
            Tracer.traceInfo("", new Object());
            Tracer.traceReturn("", new Object());
        } finally {
            field.set(Tracer.class, false);
        }
    }

}
