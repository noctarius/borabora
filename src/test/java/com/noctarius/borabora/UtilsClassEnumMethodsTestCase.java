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

import com.noctarius.borabora.spi.TypeSpecs;
import com.noctarius.borabora.spi.codec.ByteSizes;
import com.noctarius.borabora.spi.codec.ElementCounts;
import com.noctarius.borabora.spi.codec.StringEncoders;
import com.noctarius.borabora.spi.pipeline.VisitResult;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UtilsClassEnumMethodsTestCase {

    @Test
    public void call_all_shitty_utils_class_enum_methods() {
        call(ValueTypes.class.getName());
        call(ValuePrettyPrinter.class.getName());
        call(MajorType.class.getName());
        call(TypeSpecs.class.getName());
        call(StringEncoders.class.getName());
        call(ElementCounts.class.getName());
        call("com.noctarius.borabora.spi.codec.Bytes");
        call(VisitResult.class.getName());
        call("com.noctarius.borabora.impl.query.PipelineStagePrinter");
    }

    private void call(String className) {
        try {
            Class<?> enumClass = Class.forName(className);
            Enum[] enumConstants = (Enum[]) enumClass.getEnumConstants();

            try {
                Method method = enumClass.getMethod("valueOf", String.class);
                method.setAccessible(true);
                if (enumConstants.length > 0) {
                    method.invoke(enumClass, enumConstants[0].name());
                } else {
                    method.invoke(enumClass, "");
                }
            } catch (InvocationTargetException ignore) {
                if (!(ignore.getCause() instanceof IllegalArgumentException)) {
                    throw ignore;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
