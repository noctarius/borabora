package com.noctarius.borabora;

import java.util.List;

final class SequenceBuilderImpl<B>
        extends AbstractValueBuilder<SequenceBuilder<B>>
        implements SequenceBuilder<B> {

    private final List<Object> outerValues;
    private final int maxElements;
    private final B builder;

    SequenceBuilderImpl(B builder, List<Object> outerValues) {
        this(-1, builder, outerValues);
    }

    SequenceBuilderImpl(int maxElements, B builder, List<Object> outerValues) {
        this.outerValues = outerValues;
        this.maxElements = maxElements;
        this.builder = builder;
    }

    @Override
    public B endSequence() {
        outerValues.add(values());
        return builder;
    }

    @Override
    protected void validate() {
        if (maxElements > -1 && values().size() >= maxElements) {
            throw new IllegalStateException("Cannot add another element, maximum element count reached");
        }
    }

}
