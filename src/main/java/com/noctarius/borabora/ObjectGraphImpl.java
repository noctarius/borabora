package com.noctarius.borabora;

import java.util.List;

class ObjectGraphImpl
        implements ObjectGraph {

    private final List<Object> values;

    ObjectGraphImpl(List<Object> values) {
        this.values = values;
    }

}
