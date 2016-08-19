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

import com.noctarius.borabora.Output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class CompositeBuffer
        implements Output {

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
    public void write(long offset, byte value) {
        Buffer buffer = bufferByOffset(offset);
        int chunkOffset = chunkOffset(offset);
        buffer.buffer[chunkOffset] = value;
        updateHighestOffset(offset);
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
        updateHighestOffset(offset + length);
        return length;
    }

    public byte[] toByteArray() {
        if (highestOffset > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                    "Cannot create an array bigger than Integer.MAX_SIZE but " + highestOffset + " bytes would be required");
        }

        byte[] data = new byte[(int) highestOffset];

        int targetOffset = 0;
        Buffer buffer = head;
        int remaining = (int) highestOffset;
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

        if (highestOffset > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                    "Cannot create an array bigger than Integer.MAX_SIZE but " + highestOffset + " bytes would be required");
        }

        Buffer buffer = head;
        int remaining = (int) highestOffset;
        for (int i = 0; i < nbOfChunks; i++) {
            int chunkLength = Math.min(remaining, chunksize);
            outputStream.write(buffer.buffer, 0, chunkLength);

            remaining -= chunkLength;
            buffer = buffer.next;
        }
        return highestOffset;
    }

    public ByteBuffer toByteBuffer() {
        // TODO
        return null;
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
            while (chunk > nbOfChunks) {
                buffer = appendBuffer();
            }
        }
        return buffer;
    }

    private Buffer appendBuffer() {
        Buffer tail = this.tail;

        byte[] buffer = new byte[chunksize];
        Buffer newTail = new Buffer(tail, buffer);

        nbOfChunks++;
        if (tail != null) {
            tail.next = newTail;
        }

        return this.tail = newTail;
    }

    private static class Buffer {
        private final Buffer previous;
        private final byte[] buffer;

        private Buffer next;

        private Buffer(Buffer previous, byte[] buffer) {
            this.previous = previous;
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
