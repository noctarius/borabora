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
package com.noctarius.borabora.spi.codec;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@Ignore
public class DateParserTestCase {

    @Test
    public void test_T_Z() {
        String date = "2003-12-13T18:30:02Z";
        benchmark(date);
    }

    @Test
    public void test_T_Fraction_Z() {
        String date = "2003-12-13T18:30:02.25Z";
        benchmark(date);
    }

    @Test
    public void test_T_Plus() {
        String date = "2003-12-13T18:30:02+01:00";
        benchmark(date);
    }

    @Test
    public void test_T_Fraction_Plus() {
        String date = "2003-12-13T18:30:02.25+01:00";
        benchmark(date);
    }

    private void benchmark(String date) {
        long warmUpLoops = 20000;
        long benchmarkLoops = 50000;
        int benchmarkSamples = 5;

        try {
            System.out.print("Wamup (" + date + ")... ");
            for (int i = 0; i < warmUpLoops; i++) {
                parse(date);
            }
            Thread.sleep(3000);
            System.runFinalization();
            System.gc();
            System.gc();
            System.out.println("done.");

            System.out.print("Benchmarking... ");
            long[] results = new long[(int) (benchmarkSamples * benchmarkLoops)];
            long[] runs = new long[benchmarkSamples];
            for (int run = 0; run < benchmarkSamples; run++) {
                long start = System.nanoTime();
                for (int i = 0; i < benchmarkLoops; i++) {
                    results[(i * run) + i] = parse(date).getTime();
                }
                long diff = System.nanoTime() - start;
                runs[run] = diff;
                System.runFinalization();
                System.gc();
                System.gc();
            }
            System.out.println("done.");
            long result = LongStream.of(results).sum();
            System.out.println("Result: " + result);
            long avg = LongStream.of(runs).sum() / (benchmarkLoops * benchmarkSamples);
            System.out.println(TimeUnit.NANOSECONDS.toMicros(avg) + " µs per parsing");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Date parse(String date) {
        return DateParser.parseDate(date, Locale.US);
    }

}
