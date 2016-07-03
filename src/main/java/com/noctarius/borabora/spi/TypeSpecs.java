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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.WrongTypeException;

import java.util.Collection;

import static com.noctarius.borabora.spi.Constants.TAG_DATE_TIME;
import static com.noctarius.borabora.spi.Constants.TAG_ENCCBOR;
import static com.noctarius.borabora.spi.Constants.TAG_TIMESTAMP;
import static com.noctarius.borabora.spi.Constants.TAG_URI;

public enum TypeSpecs
        implements TypeSpec {

    Number("number", ValueTypes.Number, ValueTypes.UInt, ValueTypes.NInt, ValueTypes.UBigNum, //
            ValueTypes.NBigNum, ValueTypes.Int, ValueTypes.UFloat, ValueTypes.NFloat, ValueTypes.Float),
    Int("int", Number, ValueTypes.UInt, ValueTypes.NInt, ValueTypes.UBigNum, ValueTypes.NBigNum, ValueTypes.Int),
    UInt("uint", Int, ValueTypes.UInt, ValueTypes.UBigNum),
    NInt("nint", Int, ValueTypes.NInt, ValueTypes.NBigNum),
    Float("float", Number, ValueTypes.UFloat, ValueTypes.NFloat, ValueTypes.Float),
    UFloat("ufloat", Float, ValueTypes.UFloat),
    NFloat("nfloat", Float, ValueTypes.NFloat),
    String("string", ValueTypes.ByteString, ValueTypes.TextString),
    Dictionary("dictionary", ValueTypes.Dictionary),
    Sequence("sequence", ValueTypes.Sequence),
    SemanticTag("tag", ValueTypes.Unknown),
    SpecializedSemanticTag("tag$", SemanticTag, ValueTypes.Unknown),
    Bool("bool", ValueTypes.Bool),
    DateTime("datetime", SpecializedSemanticTag, TAG_DATE_TIME, ValueTypes.DateTime),
    Timstamp("timestamp", SpecializedSemanticTag, TAG_TIMESTAMP, ValueTypes.Timestamp),
    URI("uri", SpecializedSemanticTag, TAG_URI, ValueTypes.URI),
    EncCBOR("enccbor", SpecializedSemanticTag, TAG_ENCCBOR, ValueTypes.EncCBOR),
    Unknown("unknown", ValueTypes.Unknown),
    Null("null", ValueTypes.Null);

    private static final TypeSpecs[] TYPE_SPECS_VALUES = values();

    private final ValueType[] legalValueTypes;
    private final TypeSpec superType;
    private final String spec;
    private final int tagId;

    TypeSpecs(String spec, ValueType... legalValueTypes) {
        this(spec, null, -1, legalValueTypes);
    }

    TypeSpecs(String spec, TypeSpec superType, ValueType... legalValueTypes) {
        this(spec, superType, -1, legalValueTypes);
    }

    TypeSpecs(String spec, TypeSpec superType, int tagId, ValueType... legalValueTypes) {
        this.legalValueTypes = legalValueTypes;
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
        if (superType() == null) {
            return false;
        }
        if (tagId != -1 && other.tagId() != -1 && tagId != other.tagId()) {
            return false;
        }
        return superType().matches(other);
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
    public boolean valid(MajorType majorType, Input input, long offset) {
        ValueType valueType = ValueTypes.valueType(input, offset);
        for (ValueType legalValueType : legalValueTypes) {
            if (valueType.matches(legalValueType)) {
                return true;
            }
        }
        return false;
    }

    public static TypeSpec typeSpec(String spec, Collection<TagDecoder> processors) {
        if (spec.contains("$")) {
            return specializedTag(spec, processors);
        }

        for (TypeSpec typeSpec : TYPE_SPECS_VALUES) {
            if (spec.equals(typeSpec.spec())) {
                return typeSpec;
            }
        }
        throw new WrongTypeException("Not type specification found for spec " + spec);
    }

    private static TypeSpec specializedTag(String spec, Collection<TagDecoder> processors) {
        int tagId = Integer.parseInt(spec.substring(spec.indexOf("$") + 1));

        for (TagDecoder processor : processors) {
            TypeSpec typeSpec = processor.handles(tagId);
            if (typeSpec != null) {
                return typeSpec;
            }
        }
        throw new WrongTypeException("No processor found for semantic tag with id " + tagId);
    }

}
