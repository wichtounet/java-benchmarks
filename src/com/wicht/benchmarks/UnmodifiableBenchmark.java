package com.wicht.benchmarks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.wicht.benchmark.utils.BenchUtils;
import com.wicht.benchmark.utils.Benchs;

/**
 * A benchmark to test the performances of unmodifiable collection versus creating a copy of the list.
 *
 * @author Baptiste Wicht
 */
public class UnmodifiableBenchmark {
    public static void main(String[] args) {
        bench(100);
        bench(1000);
        bench(10000);
    }

    private static void bench(int size) {
        Benchs benchs = new Benchs("Collection creation (" + size + " elements)");

        benchs.setFolder("/home/wichtounet/Desktop/");

        final Collection<Integer> list = BenchUtils.newRandomIntegerList(size);

        benchs.bench("New List", new Runnable(){
            @Override
            public void run() {
                Collection<Integer> newList = new ArrayList<Integer>(list);
                newList.size();
            }
        });

        benchs.bench("Unmodifiable List", new Runnable() {
            @Override
            public void run() {
                Collection<Integer> notModifiable = Collections.unmodifiableCollection(list);
                notModifiable.size();
            }
        });

        benchs.generateCharts();
    }
}