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

import java.util.Collection;

public enum TypeSpecs
        implements TypeSpec {

    Number("number"),
    Int("int", Number),
    UInt("uint", Number),
    NInt("nint", Number),
    Float("float", Number),
    UFloat("ufloat", Number),
    NFloat("nfloat", Number),
    String("string"),
    Dictionary("dictionary"),
    Sequence("sequence"),
    SemanticTag("tag"),
    SpecializedSemanticTag("tag$", SemanticTag),
    Bool("bool"),
    DateTime("datetime", SpecializedSemanticTag),
    Timstamp("timestamp", SpecializedSemanticTag),
    URI("uri", SpecializedSemanticTag),
    EncCBOR("enccbor", SpecializedSemanticTag),
    Unknown("unknown"),
    Null("null");

    private final TypeSpec superType;
    private final String spec;
    private final int tagId;

    TypeSpecs(String spec) {
        this(spec, null, -1);
    }

    TypeSpecs(String spec, TypeSpec superType) {
        this(spec, superType, -1);
    }

    TypeSpecs(String spec, TypeSpec superType, int tagId) {
        this.superType = superType;
        this.tagId = tagId;
        this.spec = spec;
    }

    @Override
    public TypeSpec superType() {
        return superType;
    }

    @Override
    public String spec() {
        return spec;
    }

    @Override
    public int tagId() {
        return tagId;
    }

    @Override
    public boolean matches(TypeSpec other) {
        if (matchesExact(other)) {
            return true;
        }
        if (superType == null) {
            return false;
        }
        if (tagId != -1 && other.tagId() != -1 && tagId != other.tagId()) {
            return false;
        }
        return superType.matches(other);
    }

    @Override
    public boolean matchesExact(TypeSpec other) {
        if (this == other) {
            return true;
        }
        // Other type might override standard behavior
        return other.equals(this);
    }

    @Override
    public boolean valid(MajorType majorType, Decoder stream, long offset) {
        // TODO
        return false;
    }

    static TypeSpec typeSpec(String spec, Collection<SemanticTagProcessor> processors) {
        if (spec.contains("$")) {
            return specializedTag(spec, processors);
        }

        for (TypeSpec typeSpec : values()) {
            if (spec.equals(typeSpec.spec())) {
                return typeSpec;
            }
        }
        throw new WrongTypeException("Not type specification found for spec " + spec);
    }

    private static TypeSpec specializedTag(String spec, Collection<SemanticTagProcessor> processors) {
        int tagId = Integer.parseInt(spec.substring(spec.indexOf("$") + 1));

        for (SemanticTagProcessor processor : processors) {
            TypeSpec typeSpec = processor.handles(tagId);
            if (typeSpec != null) {
                return typeSpec;
            }
        }
        throw new WrongTypeException("Not processor found for semantic tag with id " + tagId);
    }

}
