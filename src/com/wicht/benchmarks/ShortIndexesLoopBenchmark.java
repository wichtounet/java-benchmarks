package com.wicht.benchmarks;

import java.util.concurrent.Callable;

import bb.util.Benchmark;

/**
 * A benchmark to test which type is the most performing using as iteration index.
 *
 * @author Baptiste Wicht
 */
public class ShortIndexesLoopBenchmark {
    public static void main(String[] args) {
        Callable<Long> callableInt = new Callable<Long>(){
            @Override
            public Long call() throws Exception {
                long result = 0;

                for (int f = 0; f < 32760; f++){
                      result += 444;
                  }

                return result;
            }
        };

        Callable<Long> callableShort = new Callable<Long>(){
            @Override
            public Long call() throws Exception {
                long result = 0;

                for (short f = 0; f < 32760; f++){
                      result += 444;
                  }

                return result;
            }
        };

        Callable<Long> callableLong = new Callable<Long>(){
            @Override
            public Long call() throws Exception {
                long result = 0;

                for (long f = 0; f < 32760; f++){
                      result += 444;
                  }

                return result;
            }
        };

        Callable<Long> callableFloat = new Callable<Long>(){
            @Override
            public Long call() throws Exception {
                long result = 0;

                for (float f = 0; f < 32760; f++){
                      result += 444;
                  }

                return result;
            }
        };

        Callable<Long> callableDouble = new Callable<Long>(){
            @Override
            public Long call() throws Exception {
                long result = 0;

                for (double f = 0; f < 32760; f++){
                      result += 444;
                  }

                return result;
            }
        };

        try {
            Benchmark intBenchmark = new Benchmark(callableInt);

            System.out.println("Result with int ");
            System.out.println(intBenchmark.toString());

            System.out.println("Result with int full");
            System.out.println(intBenchmark.toStringFull());

            Benchmark shortBenchmark = new Benchmark(callableShort);

            System.out.println("Result with int ");
            System.out.println(shortBenchmark.toString());

            System.out.println("Result with int full");
            System.out.println(shortBenchmark.toStringFull());

            System.out.println("Result with short " + new Benchmark(callableShort));
            System.out.println("Result with long " + new Benchmark(callableLong));
            System.out.println("Result with float " + new Benchmark(callableFloat));
            System.out.println("Result with double " + new Benchmark(callableDouble));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}