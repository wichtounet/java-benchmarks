package com.wicht.benchmarks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.wicht.benchmark.utils.Benchs;

/**
 * A little benchmark to test the performances of reflection.
 *
 * @author Baptiste Wicht
 */
public class ReflectionBenchmark {
    public static void main(String[] args) {
        new ReflectionBenchmark().start();
    }

    private void start() {
        Benchs benchs = new Benchs("Method Invocation");

        benchs.setFolder("/home/wichtounet/Desktop/");

        final String[] methods = {"update", "delete", "install", "remove", "cast", "test", "test1", "test2", "test3", "test4", "test5"};

        benchs.getParams().setNumberActions(methods.length);

        benchs.bench("Switch", new Runnable() {
            @Override
            public void run() {
                for (String method : methods) {
                    invokeDirectly(method);
                }
            }
        });

        benchs.bench("Reflection Invocation", new Runnable() {
            @Override
            public void run() {
                for (String method : methods) {
                    invokeReflective(method);
                }
            }
        });

        benchs.bench("Reflection Cached Invocation", new Runnable() {
            @Override
            public void run() {
                for (String method : methods) {
                    invokeReflectiveCached(method);
                }
            }
        });

        benchs.generateCharts();
    }

    public void invokeDirectly(String method) {
        if ("update".equals(method)) {
            update();
        } else if ("delete".equals(method)) {
            delete();
        } else if ("install".equals(method)) {
            install();
        } else if ("remove".equals(method)) {
            remove();
        } else if ("cast".equals(method)) {
            cast();
        } else if ("test".equals(method)) {
            test();
        } else if ("test1".equals(method)) {
            test1();
        } else if ("test2".equals(method)) {
            test2();
        } else if ("test3".equals(method)) {
            test3();
        } else if ("test4".equals(method)) {
            test4();
        } else if ("test5".equals(method)) {
            test5();
        }
    }

    public void invokeReflective(String methodName) {
        try {
            Method method = getClass().getMethod(methodName);

            method.invoke(this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private final Map<String, Method> cache = new HashMap<String, Method>(10);
    private final Class<? extends ReflectionBenchmark> type = getClass();

    public void invokeReflectiveCached(String methodName) {
        if (!cache.containsKey(methodName)) {
            try {
                cache.put(methodName, type.getMethod(methodName));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        try {
            cache.get(methodName).invoke(this);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        //Update
    }

    public void delete() {
        //Update
    }

    public void install() {
        //Update
    }

    public void remove() {
        //Update
    }

    public void cast() {
        //Update
    }

    public void test() {
        //Update
    }

    public void test1() {
        //Update
    }

    public void test2() {
        //Update
    }

    public void test3() {
        //Update
    }

    public void test4() {
        //Update
    }

    public void test5() {
        //Update
    }
}