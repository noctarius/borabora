package com.noctarius.borabora;

public interface DictionaryBuilder<B> {

    DictionaryEntryBuilder<B> putEntry();

    B endDictionary();

}
