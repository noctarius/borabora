/*
 * Copyright (c) 2008-2016-2018, Hazelcast, Inc. All Rights Reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;

public class OutputStreamOutputTestCase {

    @Test(expected = IllegalStateException.class)
    public void fail_write() {
        Output output = Output.toOutputStream(new ExceptionalOutputStream());
        output.write(0, (byte) 0);
    }

    @Test
    public void test_write_verify_returned_offset() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = Output.toOutputStream(baos);
        long offset = output.write(5, (byte) 0x1);
        assertEquals(6, offset);
    }

    @Test(expected = IllegalStateException.class)
    public void fail_write_bytearray() {
        Output output = Output.toOutputStream(new ExceptionalOutputStream());
        output.write(new byte[1], 0, 1);
    }

    private static class ExceptionalOutputStream
            extends OutputStream {

        @Override
        public void write(int b)
                throws IOException {

            throw new IOException();
        }
    }

}