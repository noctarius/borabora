package com.noctarius.borabora;

final class WriterBuilderImpl
        implements WriterBuilder {

    private final Output output;

    WriterBuilderImpl(Output output) {
        this.output = output;
    }

    @Override
    public Writer build() {
        return new WriterImpl(output);
    }
}
