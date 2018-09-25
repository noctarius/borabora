/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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

/**
 * This package contains the builder classes to encode values into CBOR data items.
 * Provided classes have builtin support for all common CBOR data types and the
 * out of the box provided semantic tag types. Anyhow
 * {@link com.noctarius.borabora.builder.encoder} provides semantic tag builders for
 * all supported semantic tag types for the
 * {@link com.noctarius.borabora.builder.encoder.ValueBuilder#putTag(com.noctarius.borabora.spi.builder.TagBuilderConsumer)}
 * SPI.
 */
package com.noctarius.borabora.builder.encoder;