/*
 * Copyright (c) 2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.borabora.benchmark;

import com.noctarius.borabora.Input;
import com.noctarius.borabora.Output;
import com.noctarius.borabora.Parser;
import com.noctarius.borabora.Query;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValuePrettyPrinter;
import com.noctarius.borabora.ValueTypes;
import com.noctarius.borabora.Writer;
import com.noctarius.borabora.builder.encoder.DictionaryBuilder;
import com.noctarius.borabora.builder.encoder.DictionaryEntryBuilder;
import com.noctarius.borabora.builder.encoder.GraphBuilder;
import com.noctarius.borabora.builder.encoder.SequenceBuilder;
import com.noctarius.borabora.builder.encoder.ValueBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Benchmark {

    @Test
    @Ignore
    public void generator()
            throws Exception {

        Random random = new Random(1);

        try (FileOutputStream fos = new FileOutputStream(new File("target/benchmark.cbor"))) {
            Output output = Output.toOutputStream(fos);

            Writer writer = Writer.newBuilder().build();
            GraphBuilder graphBuilder = writer.newGraphBuilder(output);

            generateDeterministicContent(graphBuilder, random, 100000);
        }
    }

    @Test
    @Ignore
    public void benchmark()
            throws Exception {

        int warmup_cycles = 100;
        int measure_cycles = 100;
        int samples = 10;

        Parser parser = Parser.newBuilder().build();
        Query query = Query.newBuilder().multiStream().build();

        Input input;
        File cborFile = new File("target/benchmark.cbor");
        try (FileInputStream fis = new FileInputStream(cborFile)) {
            byte[] bytes = new byte[(int) cborFile.length()];
            fis.read(bytes);
            input = Input.fromByteArray(bytes);
        }

        System.out.print("Warmup cycles (" + warmup_cycles + ")...");
        for (int i = 0; i < warmup_cycles; i++) {
            Thread.yield();
            measure(parser, input, query);
        }
        System.out.println(" done.");
        System.out.print("Offering time to JIT and cleanup...");
        TimeUnit.SECONDS.sleep(5);
        System.runFinalization();
        System.gc();
        System.gc();
        System.out.println(" done.");

        long[] sampling = new long[measure_cycles * samples];
        for (int i = 0; i < samples; i++) {
            System.out.print("Measure sampling round " + (i + 1) + "/" + samples + "...");
            Thread.yield();
            for (int o = 0; o < measure_cycles; o++) {
                sampling[i * measure_cycles + o] = measure(parser, input, query);
            }
            System.out.println(" done.");

            System.out.print("Offering time to JIT and cleanup...");
            TimeUnit.SECONDS.sleep(5);
            System.runFinalization();
            System.gc();
            System.gc();
            System.out.println(" done.");
        }

        long sum = Arrays.stream(sampling).sum();
        long avg = sum / sampling.length;
        System.out.println("Avg runtime after " + sampling.length + " cycles: " + TimeUnit.NANOSECONDS.toMicros(avg) + " µs/ops");
    }

    private long measure(Parser parser, Input input, Query query) {
        StringBuilder sb = new StringBuilder();
        long start = System.nanoTime();
        List<Value> values = new ArrayList<>();
        parser.read(input, query, values::add);
        for (int i = 0; i < values.size(); i++) {
            Value value = values.get(i);
            sb.append(ValuePrettyPrinter.asStringPrettyPrint(value)).append('\n');
        }
        long diff = System.nanoTime() - start;

        // Blackhole
        /*try (FileOutputStream fos = new FileOutputStream(new File("/dev/null")); //
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {

            writer.write(sb.hashCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        return diff;
    }

    private void generateDeterministicContent(ValueBuilder valueBuilder, Random random, int remaining)
            throws Exception {

        do {
            remaining = generateContent(valueBuilder, random, remaining, 0);
        } while (remaining > 0);
    }

    private int generateContent(ValueBuilder valueBuilder, Random random, int remaining, int level)
            throws Exception {

        ValueTypes valueType;
        do {
            ValueTypes[] valueTypes = ValueTypes.values();
            int itemType = random.nextInt(valueTypes.length);
            valueType = valueTypes[itemType];
        } while (level >= 10 && (valueType == ValueTypes.Dictionary || valueType == ValueTypes.Sequence));

        switch (valueType) {
            case Dictionary:
                return generateDictionary(valueBuilder, random, --remaining, ++level);

            case Sequence:
                return generateSequence(valueBuilder, random, --remaining, ++level);

            case Bool:
                trace("Bool", level);
                valueBuilder.putBoolean(random.nextBoolean());
                break;

            case ByteString:
                trace("ByteString", level);
                String bs = generateByteString(random);
                valueBuilder.putByteString(bs);
                break;

            case DateTime:
                trace("DateTime", level);
                long tsDate = Math.abs(random.nextInt());
                Instant tsInstant = Instant.ofEpochSecond(tsDate);
                valueBuilder.putDateTime(tsInstant);
                break;

            case Float:
                trace("Float", level);
                double fl = random.nextDouble();
                valueBuilder.putNumber(fl);
                break;

            case Fraction:
                trace("Fraction", level);
                BigDecimal bigDecimal = BigDecimal.valueOf(random.nextDouble());
                valueBuilder.putFraction(bigDecimal);
                break;

            case NBigNum:
                trace("NBigNum", level);
                BigInteger negBigInteger = BigInteger.valueOf(random.nextLong());
                if (negBigInteger.compareTo(BigInteger.ZERO) >= 0) {
                    negBigInteger = negBigInteger.multiply(BigInteger.valueOf(-1));
                }
                valueBuilder.putBigInteger(negBigInteger);
                break;

            case NInt:
                trace("NInt", level);
                long negLong = random.nextLong();
                if (negLong > 0) {
                    negLong = negLong * -1;
                }
                valueBuilder.putNumber(negLong);
                break;

            case Number:
            case Int:
                trace("Number", level);
                valueBuilder.putNumber(random.nextLong());
                break;

            case String:
                trace("String", level);
                if (random.nextBoolean()) {
                    valueBuilder.putByteString(generateByteString(random));
                } else {
                    valueBuilder.putTextString(generateTextString(random));
                }
                break;

            case TextString:
                trace("TextString", level);
                String ts = generateTextString(random);
                valueBuilder.putTextString(ts);
                break;

            case Timestamp:
                trace("Timestamp", level);
                valueBuilder.putTimestamp(Math.abs(random.nextInt()));
                break;

            case UBigNum:
                trace("UBigNum", level);
                BigInteger posBigInteger = BigInteger.valueOf(Math.abs(random.nextLong()));
                valueBuilder.putBigInteger(posBigInteger);
                break;

            case UInt:
                trace("UInt", level);
                long posLong = Math.abs(random.nextLong());
                valueBuilder.putNumber(posLong);
                break;

            case URI:
                trace("URI", level);
                int uriSize = random.nextInt(30);
                valueBuilder.putURI(new URI(generateDomainName(random, uriSize)));
                break;

            case EncCBOR:
            case Null:
            case Undefined:
            case Unknown:
                trace("<null>", level);
                valueBuilder.putString(null);
                break;
        }
        return --remaining;
    }

    private void trace(String item, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        System.out.println(item);
    }

    private int generateSequence(ValueBuilder valueBuilder, Random random, int remaining, int level)
            throws Exception {

        boolean indefinite = random.nextBoolean();
        int min = Math.max(1, remaining);
        int size = random.nextInt(Math.min(min, 100)) + 1;

        trace("Sequence[" + (indefinite ? "-1/" + size : size) + "]", level - 1);
        SequenceBuilder sequenceBuilder = indefinite ? valueBuilder.putSequence() : valueBuilder.putSequence(size);
        for (int i = 0; i < size; i++) {
            remaining = generateContent(sequenceBuilder, random, remaining, level);
        }
        sequenceBuilder.endSequence();
        return remaining;
    }

    private int generateDictionary(ValueBuilder valueBuilder, Random random, int remaining, int level)
            throws Exception {

        boolean indefinite = random.nextBoolean();
        int min = Math.max(1, remaining);
        int size = random.nextInt(Math.min(min, 100)) + 1;

        trace("Dictionary[" + (indefinite ? "-1/" + size : size) + "]", level - 1);
        DictionaryBuilder dictionaryBuilder = indefinite ? valueBuilder.putDictionary() : valueBuilder.putDictionary(size);
        for (int i = 0; i < size; i++) {
            DictionaryEntryBuilder dictionaryEntryBuilder = dictionaryBuilder.putEntry();
            dictionaryEntryBuilder.putString(generateByteString(random));
            remaining = generateContent(dictionaryEntryBuilder, random, remaining, level); // value
            dictionaryEntryBuilder.endEntry();
        }
        dictionaryBuilder.endDictionary();
        return remaining;
    }

    private String generateByteString(Random random) {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return UUID.nameUUIDFromBytes(bytes).toString();
    }

    private String generateTextString(Random random) {
        int size = random.nextInt(100);
        StringBuilder sb = new StringBuilder();
        do {
            char c = (char) random.nextInt();
            if (Character.isDefined(c)) {
                sb.append(c);
            }
        } while (sb.length() < size);
        return sb.toString();
    }

    private String generateDomainName(Random random, int size) {
        String legalChars = "abcdefghijklmnopqrstuvwxyz";
        String[] tlds = {"de", "com", "net", "org", "io", "bs", "br", "op"};

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(legalChars.charAt(random.nextInt(legalChars.length())));
        }
        sb.append('.');
        sb.append(tlds[random.nextInt(tlds.length)]);
        return sb.toString();
    }
}
