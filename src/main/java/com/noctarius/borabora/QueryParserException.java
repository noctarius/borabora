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
 * This exception type is thrown for any kind of parsing exception,
 * while parsing a query language string.
 */
public class QueryParserException
        extends RuntimeException {

    /**
     * Creates a new instance of the <tt>QueryParserException</tt> based
     * on the given cause.
     *
     * @param cause the cause to base on
     */
    public QueryParserException(Throwable cause) {
        super(cause);
    }

}
