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

/**
 * This exception is commonly thrown on occasions where a wrong type
 * is assumed by a query. Queries have multiple ways to provide type
 * information to the query execution pipeline implicitly and explicitly:
 * <ul>
 * <li>explicit: <tt>#-&gt;dictionary{'...'}</tt> or
 * <tt>#-&gt;?dictionary{'...'}</tt></li>
 * <li>implicit: <tt>#{'...'}</tt></li>
 * </ul>
 * <p>
 * For explicit type definitions you can also define if the expected type
 * is necessary or optional. In case of optional, <tt>null</tt> will be
 * returned instead of the <tt>WrongTypeException</tt> being thrown. Both
 * other options will always throw this exception type when the expected
 * type is not met.
 */
public class WrongTypeException
        extends RuntimeException {

    private final long offset;

    /**
     * Creates a new instance of the <tt>WrongTypeException</tt> class,
     * containing the given message.
     *
     * @param message the message to present to the user
     */
    public WrongTypeException(String message) {
        super(message);
        this.offset = -1;
    }

    /**
     * Creates a new instance of the <tt>WrongTypeException</tt> class,
     * containing the given message and offset.
     *
     * @param offset  the offset where the exception happened
     * @param message the message to present to the user
     */
    public WrongTypeException(long offset, String message) {
        super(message);
        this.offset = offset;
    }

    public long getOffset() {
        return offset;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + (offset == -1 ? "" : "[offset=" + getOffset() + "]");
    }
}
