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

import java.util.Objects;

abstract class Tracer {

    private static boolean TRACE_ENABLED = false;

    private final static ThreadLocal<Tracer> TRACER_THREAD_LOCAL = new ThreadLocal<Tracer>() {
        @Override
        protected Tracer initialValue() {
            return new ConsoleTracer();
        }
    };

    public static void traceInfo(String s, Object instance) {
        if (TRACE_ENABLED) {
            Objects.requireNonNull(instance, "instance must not be null");
            Tracer tracer = TRACER_THREAD_LOCAL.get();
            tracer.traceInfo0("@" + instance.hashCode() + " " + s);
        }
    }

    public static void traceCall(String s, Object instance) {
        if (TRACE_ENABLED) {
            Objects.requireNonNull(instance, "instance must not be null");
            Tracer tracer = TRACER_THREAD_LOCAL.get();
            tracer.traceCall0("@" + instance.hashCode() + " " + s);
        }
    }

    public static void traceReturn(String s, Object instance) {
        if (TRACE_ENABLED) {
            Objects.requireNonNull(instance, "instance must not be null");
            Tracer tracer = TRACER_THREAD_LOCAL.get();
            tracer.traceReturn0("@" + instance.hashCode() + " " + s);
        }
    }

    protected abstract void traceInfo0(String s);

    protected abstract void traceCall0(String s);

    protected abstract void traceReturn0(String s);

    static final class ConsoleTracer
            extends Tracer {

        private int indentation = 0;

        @Override
        protected void traceInfo0(String s) {
            for (int i = 0; i < indentation; i++) {
                System.out.print(" ");
            }
            System.out.println("Info:   " + s);
        }

        @Override
        protected void traceCall0(String s) {
            for (int i = 0; i < indentation; i++) {
                System.out.print(" ");
            }
            System.out.println("Call:   " + s);
            indentation = indentation + 2;
        }

        @Override
        protected void traceReturn0(String s) {
            indentation = indentation - 2;
            for (int i = 0; i < indentation; i++) {
                System.out.print(" ");
            }
            System.out.println("Return: " + s);
        }
    }

    static final class NoopTracer
            extends Tracer {

        @Override
        protected void traceInfo0(String s) {
        }

        @Override
        protected void traceCall0(String s) {
        }

        @Override
        protected void traceReturn0(String s) {
        }
    }
}
