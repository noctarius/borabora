package com.noctarius.borabora;

final class ObjectGraphBuilderImpl
        extends AbstractValueBuilder<ObjectGraphBuilder>
        implements ObjectGraphBuilder {

    @Override
    public ObjectGraph build() {
        return new ObjectGraphImpl(values());
    }

}
