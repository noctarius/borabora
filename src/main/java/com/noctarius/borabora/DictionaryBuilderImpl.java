package com.noctarius.borabora;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class DictionaryBuilderImpl<B>
        implements DictionaryBuilder<B> {

    private final Map<Object, Object> dictionary;
    private final List<Object> outerValues;
    private final int maxElements;
    private final B builder;

    DictionaryBuilderImpl(B builder, List<Object> outerValues) {
        this(-1, builder, outerValues);
    }

    DictionaryBuilderImpl(int maxElements, B builder, List<Object> outerValues) {
        this.dictionary = maxElements > -1 ? new HashMap<>(maxElements) : new HashMap<>();
        this.outerValues = outerValues;
        this.maxElements = maxElements;
        this.builder = builder;
    }

    @Override
    public DictionaryEntryBuilder<B> putEntry() {
        validate();
        return new DictionaryEntryBuilderImpl<>(this, dictionary);
    }

    @Override
    public B endDictionary() {
        outerValues.add(dictionary);
        return builder;
    }

    private void validate() {
        if (maxElements > -1 && dictionary.size() >= maxElements) {
            throw new IllegalStateException("Cannot add another element, maximum element count reached");
        }
    }

}
