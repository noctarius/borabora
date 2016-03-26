package com.noctarius.borabora;

import java.util.Map;

final class DictionaryEntryBuilderImpl<B>
        extends AbstractValueBuilder<DictionaryEntryBuilder<B>>
        implements DictionaryEntryBuilder<B> {

    private final Object NULL_OBJECT = new Object();

    private final Map<Object, Object> outerValues;
    private final DictionaryBuilder<B> builder;

    private Object key;
    private Object value;

    DictionaryEntryBuilderImpl(DictionaryBuilder<B> builder, Map<Object, Object> outerValues) {
        this.outerValues = outerValues;
        this.builder = builder;
    }

    @Override
    public DictionaryBuilder<B> endEntry() {
        outerValues.put(key, value);
        return builder;
    }

    @Override
    protected void validate() {
        if (key != null && (value != null || value == NULL_OBJECT)) {
            throw new IllegalStateException("Dictionary key and value already set");
        }
    }

    @Override
    protected void put(Object value) {
        if (key != null) {
            if (value == null) {
                throw new NullPointerException("Dictionary key cannot be null");
            }
            key = value;
        } else if (this.value == null) {
            this.value = value == null ? NULL_OBJECT : value;
        }
    }

}
