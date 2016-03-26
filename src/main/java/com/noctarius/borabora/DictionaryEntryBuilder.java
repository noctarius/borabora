package com.noctarius.borabora;

public interface DictionaryEntryBuilder<B>
        extends ValueBuilder<DictionaryEntryBuilder<B>> {

    DictionaryBuilder<B> endEntry();

}
