package com.noctarius.borabora;

public interface ObjectGraph {

    static ObjectGraphBuilder newBuilder() {
        return new ObjectGraphBuilderImpl();
    }

}
