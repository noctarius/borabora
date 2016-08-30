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
package com.noctarius.borabora.spi.codec;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.NoSuchByteException;
import com.noctarius.borabora.Output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class CompositeBuffer
        implements Output, Input {

    private final int chunksize;
    private final Buffer head;

    private Buffer tail;
    private int nbOfChunks;
    private long highestOffset;

    private CompositeBuffer(int chunksize) {
        this.chunksize = chunksize;
        this.head = appendBuffer();
    }

    @Override
    public long write(long offset, byte value) {
        Buffer buffer = bufferByOffset(offset);
        int chunkOffset = chunkOffset(offset);
        buffer.buffer[chunkOffset] = value;
        updateHighestOffset(offset);
        return offset;
    }

    @Override
    public long write(byte[] array, long offset, long length) {
        long remaining = length;
        int sourceOffset = 0;
        long targetOffset = offset;

        Buffer buffer = bufferByOffset(offset);
        do {
            int chunkOffset = chunkOffset(targetOffset);
            int chunkAvail = chunksize - chunkOffset;

            int chunkLength = (int) Math.min(chunkAvail, remaining);
            System.arraycopy(array, sourceOffset, buffer.buffer, chunkOffset, chunkLength);

            remaining -= chunkLength;
            sourceOffset += chunkLength;
            targetOffset += chunkLength;

            if (remaining > 0) {
                buffer = appendBuffer();
            }
        } while (remaining > 0);
        updateHighestOffset(offset + length - 1);
        return length;
    }

    @Override
    public byte read(long offset)
            throws NoSuchByteException {

        Buffer buffer = bufferByOffset(offset);
        int chunkOffset = chunkOffset(offset);
        return buffer.buffer[chunkOffset];
    }

    @Override
    public boolean offsetValid(long offset) {
        return offset <= highestOffset;
    }

    @Override
    public long read(byte[] array, long offset, long length) {
        if (length > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("length cannot be larger than Integer.MAX_VALUE");
        }

        Buffer buffer = bufferByOffset(offset);
        if (length <= chunksize) {
            int chunkOffset = chunkOffset(offset);
            System.arraycopy(buffer.buffer, chunkOffset, array, 0, (int) length);

        } else {
            int remaining = (int) length;

            int targetOffset = 0;
            do {
                int chunkLength = Math.min(remaining, chunksize);
                System.arraycopy(buffer.buffer, 0, array, targetOffset, chunkLength);

                targetOffset += chunkLength;
                remaining -= chunkLength;
                buffer = buffer.next;
            } while (remaining > 0);
        }
        return 0;
    }

    public long size() {
        return highestOffset + 1;
    }

    public byte[] toByteArray() {
        if (highestOffset > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                    "Cannot create an array bigger than Integer.MAX_SIZE but " + highestOffset + " bytes would be required");
        }

        int remaining = ((int) highestOffset) + 1;
        byte[] data = new byte[remaining];

        int targetOffset = 0;
        Buffer buffer = head;
        for (int i = 0; i < nbOfChunks; i++) {
            int chunkLength = Math.min(remaining, chunksize);
            System.arraycopy(buffer.buffer, 0, data, targetOffset, chunkLength);

            targetOffset += chunkLength;
            remaining -= chunkLength;
            buffer = buffer.next;
        }

        return data;
    }

    public long writeToOutputStream(OutputStream outputStream)
            throws IOException {

        Buffer buffer = head;
        long remaining = highestOffset + 1;
        for (int i = 0; i < nbOfChunks; i++) {
            long chunkLength = Math.min(remaining, chunksize);
            outputStream.write(buffer.buffer, 0, (int) chunkLength);

            remaining -= chunkLength;
            buffer = buffer.next;
        }
        return highestOffset + 1;
    }

    public ByteBuffer toByteBuffer() {
        return toByteBuffer(false);
    }

    public ByteBuffer toByteBuffer(boolean directByteBuffer) {
        if (highestOffset > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                    "Cannot create an array bigger than Integer.MAX_SIZE but " + highestOffset + " bytes would be required");
        }

        int remaining = ((int) highestOffset) + 1;

        ByteBuffer byteBuffer;
        if (directByteBuffer) {
            byteBuffer = ByteBuffer.allocateDirect(remaining);
        } else {
            byteBuffer = ByteBuffer.allocate(remaining);
        }

        Buffer buffer = head;
        for (int i = 0; i < nbOfChunks; i++) {
            int chunkLength = Math.min(remaining, chunksize);
            byteBuffer.put(buffer.buffer, 0, chunkLength);

            remaining -= chunkLength;
            buffer = buffer.next;
        }
        return byteBuffer;
    }

    private void updateHighestOffset(long maxOffset) {
        highestOffset = Math.max(highestOffset, maxOffset);
    }

    private int chunkOffset(long offset) {
        return (int) (offset % chunksize);
    }

    private Buffer bufferByOffset(long offset) {
        int chunk = (int) (offset / chunksize);

        Buffer buffer = tail;
        if (chunk < nbOfChunks) {
            buffer = head;
            for (int i = 0; i < chunk; i++) {
                buffer = buffer.next;
            }

        } else {
            while (chunk >= nbOfChunks) {
                buffer = appendBuffer();
            }
        }
        return buffer;
    }

    private Buffer appendBuffer() {
        Buffer tail = this.tail;

        byte[] buffer = new byte[chunksize];
        Buffer newTail = new Buffer(buffer);

        nbOfChunks++;
        if (tail != null) {
            tail.next = newTail;
        }

        return this.tail = newTail;
    }

    private static class Buffer {
        private final byte[] buffer;

        private Buffer next;

        private Buffer(byte[] buffer) {
            this.buffer = buffer;
        }
    }

    public static CompositeBuffer newCompositeBuffer() {
        return newCompositeBuffer(1024);
    }

    public static CompositeBuffer newCompositeBuffer(int chunksize) {
        return new CompositeBuffer(chunksize);
    }

}
