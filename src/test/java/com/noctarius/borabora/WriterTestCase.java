package com.noctarius.borabora;

import org.junit.Test;

public class WriterTestCase {

    @Test
    public void test_write() throws Exception {
        byte[] data = new byte[20];
        Output output = Output.toByteArray(data);

        Writer writer = Writer.newBuilder(output).build();

        ObjectGraph graph = ObjectGraph.newBuilder()
                .putNumber(1)
                    .putString("foo")
                .putSequence()
                    .putNumber(2)
                    .putString("bar")
                .endSequence()
                .putDictionary(2)
                    .putEntry()
                        .putString("key1")
                        .putBoolean(true)
                    .endEntry()
                    .putEntry()
                        .putString("key2")
                        .putBoolean(false)
                    .endEntry()
                .endDictionary().build();

        writer.write(graph);
    }

}
