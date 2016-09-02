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

import com.noctarius.borabora.spi.codec.CompositeBuffer;

/**
 * An input instance represents a source for readable data. Input implementations are stateless
 * and thread-safe by design. They also have to support random access based on offsets.
 */
public interface Input {

    /**
     * Reads a single byte at the given <tt>offset</tt> from the underlying stream. If the offset
     * is outside the readable area a {@link NoSuchByteException} is thrown. Otherwise the byte
     * is returned.
     *
     * @param offset the offset to read from
     * @return the byte at the given offset
     * @throws NoSuchByteException if offset is outside the readable area
     */
    byte read(long offset)
            throws NoSuchByteException;

    /**
     * Reads zero or more bytes defined by the given <tt>length</tt>. Data is read into the given
     * byte-array (<tt>bytes</tt>) and reading starts at the provided <tt>offset</tt>. If
     * <tt>offset</tt> or <tt>offset</tt> plus <tt>length</tt> results in an offset larger than
     * the readable area, or <tt>length</tt> is larger then the given byte-array's length, a
     * {@link NoSuchByteException} is thrown.
     *
     * @param bytes  the byte-array to read to
     * @param offset the offset to start reading
     * @param length the number of bytes to read
     * @return returns the number of byte read
     * @throws NoSuchByteException if offset, offset+length is outside the readable area or
     *                             length is larger than the provided byte array
     */
    long read(byte[] bytes, long offset, int length)
            throws NoSuchByteException;

    /**
     * Returns <tt>true</tt> if the given <tt>offset</tt> is inside the readable area, otherwise
     * <tt>false</tt>.
     *
     * @param offset offset to validate
     * @return true if offset is valid, otherwise false
     */
    boolean offsetValid(long offset);

    /**
     * Returns an <tt>Input</tt> instance backed by the provided <tt>bytes</tt> byte-array. The instance
     * returned <b>does not</b> handle offsets larger than {@link Integer#MAX_VALUE} due to the limitation
     * of Java. All offsets larger will result in an {@link IllegalArgumentException} being thrown.
     *
     * @param bytes the byte-array to be used as the backing storage for the new input instance
     * @return the new input instance backed by the given byte-array
     */
    static Input fromByteArray(byte[] bytes) {
        return new ByteArrayInput(bytes);
    }

    /**
     * Returns an <tt>Input</tt> instance backed by the provided <tt>address</tt> which represents a
     * native memory address and the given <tt>size</tt> for the number of available bytes. The instance
     * returned is implemented using {@link sun.misc.Unsafe} to provide native memory access and to
     * provide 64 bit offsets. Not all JVMs support this kind of access and therefore it might fail to
     * be used.
     *
     * @param address the native memory address of the data
     * @param size    the number of bytes of the available data
     * @return the new input instance backed by the given address and size
     */
    static Input fromNative(long address, long size) {
        return new UnsafeByteInput(address, size);
    }

    /**
     * Returns an <tt>Input</tt> instance backed by the provided <tt>compositeBuffer</tt>. The
     * {@link CompositeBuffer} is implemented as a linked list of byte-array's called chunks. The buffer
     * is automatically growing and prevents recreation and copying of internal byte-arrays. This makes
     * it fast for writing, however the single linked list design makes random access read access slow.
     * That said, a CompositeBuffer is good for writing if the resulting stream size is unknown but
     * should only be used for reading in rare cases.
     *
     * @param compositeBuffer the CompositeBuffer to read from
     * @return the new input instance backed by the given compositeBuffer
     */
    static Input fromCompositeBuffer(CompositeBuffer compositeBuffer) {
        return new CompositeBufferInput(compositeBuffer);
    }

}
