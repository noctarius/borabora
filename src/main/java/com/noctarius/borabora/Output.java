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

import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.spi.io.CompositeBuffer;

import java.io.OutputStream;

/**
 * An <tt>Ouput</tt> instance represents a target sink for writeable data. Output implementations
 * are considered to <b>neither be stateless not thread-safe</b> as data is stored which is
 * a non side-effect free situation. Whereas most implementations don't necessarily need to
 * use the provided <tt>offset</tt> which is passed to <tt>write</tt> methods, it can be used
 * to prevent storing internal offsets. As all writes coming from the generators are in order
 * and immediate, the offset is constantly increasing only and does not need random pokes.
 * <p>A common basic example for the usage of <tt>Output</tt> looks like:</p>
 * <pre>
 *     Output output = Output.toCompositeBuffer( ... );
 *     Writer writer = Writer.newBuilder().build();
 *     GraphBuilder graphBuilder = writer.newGraphBuilder( output );
 *     // generate the actual data stream
 * </pre>
 *
 * @see GraphBuilder
 * @see Writer
 * @see CompositeBuffer
 */
public interface Output {

    /**
     * Writes a single byte to the given <tt>offset</tt> and the underlying sink. If the offset
     * is outside the writable area a {@link NoSuchByteException} is thrown. Otherwise the byte
     * is written and the new offset is returned.
     *
     * @param offset the offset to write to
     * @param value  the value to write
     * @return the new offset after writing
     */
    long write(long offset, byte value);

    /**
     * Writes zero or more bytes defined by the given <tt>length</tt>. Data is written from the
     * given byte-array (<tt>bytes</tt>) and writing starts from the provided <tt>offset</tt>. If
     * <tt>offset</tt> or <tt>offset</tt> plus <tt>length</tt> results in an offset larger than
     * the writable area, or <tt>length</tt> is larger then the given byte-array's length, a
     * {@link NoSuchByteException} is thrown.
     *
     * @param array  the byte-array to write
     * @param offset the offset to write to
     * @param length the number of bytes to write
     * @return the new offset after writing
     */
    long write(byte[] array, long offset, int length);

    /**
     * Returns an <tt>Output</tt> instance backed by the provided <tt>compositeBuffer</tt>. The
     * {@link CompositeBuffer} is implemented as a linked list of byte-array's called chunks. The buffer
     * is automatically growing and prevents recreation and copying of internal byte-arrays. This makes
     * it fast for writing, however the single linked list design makes random access read access slow.
     * That said, a CompositeBuffer is good for writing if the resulting stream size is unknown but
     * should only be used for reading in rare cases.
     *
     * @param compositeBuffer the CompositeBuffer to write to
     * @return the new output instance backed by the given compositeBuffer
     * @see CompositeBuffer
     */
    static Output toCompositeBuffer(CompositeBuffer compositeBuffer) {
        return compositeBuffer;
    }

    /**
     * Returns an <tt>Output</tt> instance backed by the provided <tt>bytes</tt> byte-array. The instance
     * returned <b>does not</b> handle offsets larger than {@link Integer#MAX_VALUE} due to the limitation
     * of Java. All offsets larger will result in an {@link IllegalArgumentException} being thrown.
     *
     * @param bytes the byte-array to be used as the backing storage for the new out instance
     * @return the new output instance backed by the given byte-array
     */
    static Output toByteArray(byte[] bytes) {
        return new ByteArrayOutput(bytes);
    }

    /**
     * Returns an <tt>Output</tt> instance backed by the provided <tt>address</tt> which represents a
     * native memory address and the given <tt>size</tt> for the number of available bytes. The instance
     * returned is implemented using {@link sun.misc.Unsafe} to provide native memory access and to
     * provide 64 bit offsets. Not all JVMs support this kind of access and therefore it might fail to
     * be used.
     *
     * @param address the native memory address of the data
     * @param size    the number of bytes of the writable data
     * @return the new output instance backed by the given address and size
     */
    static Output toNative(long address, long size) {
        return new UnsafeByteOutput(address, size);
    }

    /**
     * Returns an <tt>Output</tt> instance backed by the provided <tt>out</tt> {@link OutputStream}
     * instance. The instance returned <b>does not</b> handle offsets larger than
     * {@link Integer#MAX_VALUE} due to the limitation of the Java API. All offsets larger will
     * result in an {@link IllegalArgumentException} being thrown.
     *
     * @param out the output stream instance to write to
     * @return the new output instance backed by the given output stream
     */
    static Output toOutputStream(OutputStream out) {
        return new OutputStreamOutput(out);
    }

}
