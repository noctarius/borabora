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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.spi.codec.EncoderContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

final class SemanticTagSupport0 {

    private SemanticTagSupport0() {
    }

    static <S> S proxy(Class<S> type, MethodInvocationHandler methodInvocationHandler) {
        Deque<Object> stack = methodInvocationHandler.stack;
        S proxy = newProxy(type, methodInvocationHandler);
        stack.push(proxy);
        return proxy;
    }

    private static <S> S newProxy(Class<S> type, MethodInvocationHandler methodInvocationHandler) {
        ClassLoader classLoader = type.getClassLoader();
        return (S) Proxy.newProxyInstance(classLoader, new Class[]{type}, methodInvocationHandler);
    }

    static class MethodInvocationHandler
            implements InvocationHandler {

        // EncoderContext must be thread-safe!
        private final Deque<Object> stack = new LinkedList<>();

        private final Class<?> type;
        private final List<MethodInvocation> methodInvocations;

        MethodInvocationHandler(Class<?> type, List<MethodInvocation> methodInvocations) {
            this.type = type;
            this.methodInvocations = methodInvocations;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {

            if (!method.getDeclaringClass().equals(Object.class)) {
                methodInvocations.add(new MethodInvocation(method, args));
            }

            if ("endSemanticTag".equals(method.getName()) //
                    && method.getReturnType().isAssignableFrom(SemanticTagBuilderConsumer.class)) {

                return new MethodInvocationPipeline(type, methodInvocations);
            }

            if (method.isAnnotationPresent(BuilderReturn.class)) {
                // Remove current element from stack
                stack.pop();

                // Return the previous element
                return stack.peek();
            }

            if (method.isAnnotationPresent(BuilderEnter.class)) {
                return proxy(method.getReturnType(), this);
            }

            return proxy;
        }
    }

    static class MethodInvocationPipeline<B>
            implements SemanticTagBuilderConsumer<B> {

        private final Class<?> type;
        private final List<MethodInvocation> methodInvocations;

        private MethodInvocationPipeline(Class<?> type, List<MethodInvocation> methodInvocations) {
            this.type = type;
            this.methodInvocations = methodInvocations;
        }

        @Override
        public B execute(EncoderContext encoderContext, B builder) {
            try {
                SemanticTagBuilderFactory factory = encoderContext.findSemanticTagBuilderFactory(type);
                Object target = factory.newSemanticTagBuilder(encoderContext);
                for (MethodInvocation methodInvocation : methodInvocations) {
                    target = methodInvocation.invoke(target);
                }
                return builder;

            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else if (e instanceof InvocationTargetException) {
                    Throwable targetException = ((InvocationTargetException) e).getTargetException();
                    if (targetException instanceof RuntimeException) {
                        throw (RuntimeException) targetException;
                    }
                    throw new RuntimeException(targetException);
                }
                throw new RuntimeException(e);
            }
        }
    }

    static class MethodInvocation {
        private final Method method;
        private final Object[] arguments;

        private MethodInvocation(Method method, Object[] arguments) {
            this.method = method;
            this.arguments = arguments;
        }

        private Object invoke(Object target)
                throws InvocationTargetException, IllegalAccessException {

            return method.invoke(target, arguments);
        }
    }

}
