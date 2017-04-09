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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args)
            throws IOException {

        File file = new File("dummy32.bin");
        byte[] data = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }

        Input input = Input.fromByteArray(data);
        Parser parser = Parser.newParser();

        for (int i = 0; i < 500; i++) {
            long start = System.nanoTime();
            Value value = parser.read(input, parser.newQueryBuilder().build());
            ValuePrettyPrinter.asStringPrettyPrint(value);
            System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
        }
    }

}
