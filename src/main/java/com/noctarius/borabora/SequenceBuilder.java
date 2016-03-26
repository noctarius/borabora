package com.noctarius.borabora;

public interface SequenceBuilder<B>
        extends ValueBuilder<SequenceBuilder<B>> {

    B endSequence();

}
