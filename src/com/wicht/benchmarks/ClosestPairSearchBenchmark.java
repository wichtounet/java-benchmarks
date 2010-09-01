package com.wicht.benchmarks;

import java.awt.Point;
import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import bb.util.Benchmark;
import com.wicht.benchmark.utils.BenchUtils;

/**
 * A benchmark to test two closest pair point search algorithms : the naive one and the sweeping plane one.
 *
 * @author Baptiste Wicht
 */
public class ClosestPairSearchBenchmark {
    private static final int MAX_COORD = 100000;

    public static void main(String[] args) {
        bench(10);
        bench(50);
        bench(100);
        bench(500);
        bench(1000);
        bench(5000);
    }

    public static void bench(int size) {
        Point[] t = BenchUtils.newRandomPointArray(size, MAX_COORD);

        System.out.println("Naive time with " + size + " elements  : " + new Benchmark(new NaiveSorter(t)));
        System.out.println("Sweeping time with \" + size + \" elements  : " + new Benchmark(new SweepingSorter(t)));
    }

    private static final class NaiveSorter implements Runnable {
        private final Point[] points;

        private NaiveSorter(Point[] points) {
            this.points = points;
        }

        @Override
        public void run() {
            naiveClosest(points);
        }

        private static Point[] naiveClosest(Point[] points) {
            Point pointA = null;
            Point pointB = null;

            double min = Double.MAX_VALUE;

            for (int i = 0; i < points.length; i++) {
                for (int j = i; j < points.length; j++) {
                    if (points[i] != points[j]) {
                        double dist = points[i].distance(points[j]);

                        if (dist < min) {
                            min = dist;

                            pointA = points[i];
                            pointB = points[j];
                        }
                    }
                }
            }

            return new Point[]{pointA, pointB};
        }
    }

    private static final class SweepingSorter implements Runnable {
        private final Point[] points;
        
        private static final Comparator<Point> VERTICAL_COMPARATOR = new Comparator<Point>() {
            @Override
            public int compare(Point a, Point b) {
                if (a.y < b.y) {
                    return -1;
                }

                if (a.y > b.y) {
                    return 1;
                }

                if (a.x < b.x) {
                    return -1;
                }

                if (a.x > b.x) {
                    return 1;
                }

                return 0;
            }
        };

        private static final Comparator<Point> HORIZONTAL_COMPARATOR = new Comparator<Point>() {
            @Override
            public int compare(Point a, Point b) {
                if (a.x < b.x) {
                    return -1;
                }

                if (a.x > b.x) {
                    return 1;
                }

                if (a.y < b.y) {
                    return -1;
                }

                if (a.y > b.y) {
                    return 1;
                }

                return 0;
            }
        };

        private SweepingSorter(Point[] points) {
            this.points = points;
        }

        @Override
        public void run() {
            closestPair(points);
        }

        private static Point[] closestPair(Point[] points) {
            Point[] closestPair = new Point[2];

            //When we start the min distance is the infinity
            double crtMinDist = Double.POSITIVE_INFINITY;

            //Get the points and sort them
            Point[] sorted = Arrays.copyOf(points, points.length);
            Arrays.sort(sorted, HORIZONTAL_COMPARATOR);

            //When we start the left most candidate is the first one
            int leftMostCandidateIndex = 0;

            //Vertically sorted set of candidates
            SortedSet<Point> candidates = new TreeSet<Point>(VERTICAL_COMPARATOR);

            //For each point from left to right
            for (Point current : sorted) {
                //Shrink the candidates
                while (current.x - sorted[leftMostCandidateIndex].x > crtMinDist) {
                    candidates.remove(sorted[leftMostCandidateIndex]);
                    leftMostCandidateIndex++;
                }

                //Compute the y head and the y tail of the candidates set
                Point head = new Point(current.x, (int) (current.y - crtMinDist));
                Point tail = new Point(current.x, (int) (current.y + crtMinDist));

                //We take only the interesting candidates in the y axis
                for (Point point : candidates.subSet(head, tail)) {
                    double distance = current.distance(point);

                    //Simple min computation
                    if (distance < crtMinDist) {
                        crtMinDist = distance;

                        closestPair[0] = current;
                        closestPair[1] = point;
                    }
                }

                //The current point is now a candidate
                candidates.add(current);
            }

            return closestPair;
        }
    }
}