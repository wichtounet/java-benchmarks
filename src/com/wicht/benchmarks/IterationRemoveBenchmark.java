package com.wicht.benchmarks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.wicht.benchmark.utils.BenchUtils;
import com.wicht.benchmark.utils.Benchs;

/**
 * A simple benchmark to test if it's interesting to remove the read elements from a list when we make several
 * iterations over the list.
 *
 * @author Baptiste Wicht
 */
public class IterationRemoveBenchmark {
    public static void main(String[] args) {
        bench(10);
        bench(1000);
        bench(100000);
    }

    private static void bench(int size) {
        Benchs benchs = new Benchs("Multiple Iterations (" + size + " elements)");

        benchs.setFolder("/home/wichtounet/Desktop/");

        final Collection<String> strings = Collections.unmodifiableCollection(BenchUtils.newRandomStringList(size, "data", "person", "lending"));

        benchs.bench("With Remove", new Runnable(){
            @Override
            public void run() {
                List<String> copy = new ArrayList<String>(strings);

                restoreRemove(copy.iterator(), "data");
                restoreRemove(copy.iterator(), "person");
                restoreRemove(copy.iterator(), "lending");
            }
        });

        benchs.bench("Normal", new Runnable() {
            @Override
            public void run() {
                restoreDirect(strings, "data");
                restoreDirect(strings, "person");
                restoreDirect(strings, "lending");
            }
        });

        benchs.generateCharts();
    }

    private static void restoreRemove(Iterator<String> stringIterator, String name) {
        int total = 0;

        while (stringIterator.hasNext()) {
            String current = stringIterator.next();

            if(name.equals(current)){
                total += current.length();

                stringIterator.remove();
            }
        }
    }

    private static void restoreDirect(Iterable<String> strings, String name) {
        int total = 0;
        
        for(String string : strings){
            if(name.equals(string)){
                total += string.length();
            }
        }
    }
}