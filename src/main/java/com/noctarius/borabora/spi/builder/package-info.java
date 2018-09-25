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
 * This package contains the semantic tag builder SPI, which can be used to
 * provide support for additional, not builtin, semantic tag data item types.
 * All builtin semantic tag types provide an implementation of this builder
 * pattern and fully support the
 * {@link com.noctarius.borabora.spi.builder.TagSupport#semanticTag(java.lang.Class)}
 * API. For further detail on that API, see the
 * {@link com.noctarius.borabora.spi.builder.TagSupport} Javadoc.
 */
package com.noctarius.borabora.spi.builder;