package com.noctarius.borabora;

public interface Writer {

    void write(ObjectGraph graph);

    static WriterBuilder newBuilder(Output output) {
        return new WriterBuilderImpl(output);
    }

}
