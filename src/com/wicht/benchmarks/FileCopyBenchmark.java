package com.wicht.benchmarks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.wicht.benchmark.utils.Benchs;

/**
 * A benchmark on the different ways to make file copy in Java.
 *
 * @author Baptiste Wicht
 */
public final class FileCopyBenchmark {
    private static final int BUFFER = 8192;

    private static final String SRC_SAME_DISK = "/home/wichtounet/Desktop/src/";
    private static final String TARGET_SAME_DISK = "/home/wichtounet/Desktop/target/";
    private static final String SOURCE_BETWEEN_DISK = SRC_SAME_DISK;
    private static final String TARGET_BETWEEN_DISK = "/media/Data3/tmp/";

    public static void main(String[] args) {
        System.out.println("Bench on the same disk");
        bench("-same-disk", SRC_SAME_DISK, TARGET_SAME_DISK);

        System.out.println("Bench between two disks");
        bench("-between-disks", SOURCE_BETWEEN_DISK, TARGET_BETWEEN_DISK);
    }

    private static void bench(String suffix, String disk1, String disk2) {
        bench(suffix, disk1, disk2, true, "little-text", "medium-text", "big-text", "fat-text");
        bench(suffix, disk1, disk2, false, "little-binary", "medium-binary", "big-binary", "fat-binary", "enormous-binary");
    }

    private static void bench(String suffix, String disk1, String disk2, boolean text, String... files) {
        int size = 1;

        for (String file : files) {
            System.out.println("Start benchmark with " + file);

            File fileIn = new File(disk1 + file);
            File fileOut = new File(disk2 + file);

            bench(file + suffix, fileIn, fileOut, size++, text);
        }
    }

    private static void bench(String title, final File in, final File out, int size, boolean text) {
        Benchs benchs = new Benchs(title);

        benchs.setFolder("/home/wichtounet/Desktop/Graphs/");
        benchs.setExclusionFactor(40);

        benchs.bench("Native Copy", new Runnable() {
            @Override
            public void run() {
                nativeCopy(in, out);
                out.delete();
            }
        });

        if (size < 3) {
            benchs.bench("Naive Streams", new Runnable() {
                @Override
                public void run() {
                    naiveStreamsCopy(in, out);
                    out.delete();
                }
            });
        }

        if (size < 4 && text) {
            benchs.bench("Naive Readers", new Runnable() {
                @Override
                public void run() {
                    naiveReaderCopy(in, out);
                    out.delete();
                }
            });
        }

        if (size < 5) {
            benchs.bench("Buffered Streams", new Runnable() {
                @Override
                public void run() {
                    bufferedStreamsCopy(in, out);
                    out.delete();
                }
            });
        }

        if (size < 5 && text) {
            benchs.bench("Buffered Readers", new Runnable() {
                @Override
                public void run() {
                    bufferedReaderCopy(in, out);
                    out.delete();
                }
            });
        }

        benchs.bench("Custom Buffer Streams", new Runnable() {
            @Override
            public void run() {
                customBufferStreamCopy(in, out);
                out.delete();
            }
        });

        if (text) {
            benchs.bench("Custom Buffer Readers", new Runnable() {
                @Override
                public void run() {
                    customBufferReaderCopy(in, out);
                    out.delete();
                }
            });
        }

        benchs.bench("Custom Buffer Buffered Streams", new Runnable() {
            @Override
            public void run() {
                customBufferBufferedStreamCopy(in, out);
                out.delete();
            }
        });

        if (text) {
            benchs.bench("Custom Buffer Buffered Readers", new Runnable() {
                @Override
                public void run() {
                    customBufferBufferedReaderCopy(in, out);
                    out.delete();
                }
            });
        }

        benchs.bench("NIO Buffer", new Runnable() {
            @Override
            public void run() {
                nioBufferCopy(in, out);
                out.delete();
            }
        });

        benchs.bench("NIO Transfer", new Runnable() {
            @Override
            public void run() {
                nioTransferCopy(in, out);
                out.delete();
            }
        });

        /* Java 7 Only
        benchs.bench("Path (Java 7)", new Runnable() {
            @Override
            public void run() {
                pathCopy(in, out);
                out.delete();
            }
        });*/

        benchs.generateCharts();
    }
    
    private static void naiveStreamsCopy(File source, File target) {
        InputStream fin = null;
        OutputStream fout = null;
        try {
            fin = new FileInputStream(source);
            fout = new FileOutputStream(target);

            int c;
            while ((c = fin.read()) != -1) {
                fout.write(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fin);
            close(fout);
        }
    }

    private static void naiveReaderCopy(File source, File target) {
        Reader fin = null;
        Writer fout = null;
        try {
            fin = new FileReader(source);
            fout = new FileWriter(target);

            int c;
            while ((c = fin.read()) != -1) {
                fout.write(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fin);
            close(fout);
        }
    }

    private static void bufferedStreamsCopy(File source, File target) {
        InputStream fin = null;
        OutputStream fout = null;
        try {
            fin = new BufferedInputStream(new FileInputStream(source));
            fout = new BufferedOutputStream(new FileOutputStream(target));

            int data;
            while ((data = fin.read()) != -1) {
                fout.write(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fin);
            close(fout);
        }
    }

    private static void bufferedReaderCopy(File source, File target) {
        Reader fin = null;
        Writer fout = null;
        try {
            fin = new BufferedReader(new FileReader(source));
            fout = new BufferedWriter(new FileWriter(target));

            int c;
            while ((c = fin.read()) != -1) {
                fout.write(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fin);
            close(fout);
        }
    }

    private static void customBufferStreamCopy(File source, File target) {
        InputStream fis = null;
        OutputStream fos = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(target);

            byte[] buf = new byte[BUFFER];

            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fis);
            close(fos);
        }
    }

    private static void customBufferReaderCopy(File source, File target) {
        Reader fin = null;
        Writer fout = null;
        try {
            fin = new FileReader(source);
            fout = new FileWriter(target);

            char[] buf = new char[BUFFER / 2];

            int i;
            while ((i = fin.read(buf)) != -1) {
                fout.write(buf, 0, i);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fin);
            close(fout);
        }
    }

    private static void customBufferBufferedStreamCopy(File source, File target) {
        InputStream fis = null;
        OutputStream fos = null;
        try {
            fis = new BufferedInputStream(new FileInputStream(source));
            fos = new BufferedOutputStream(new FileOutputStream(target));

            byte[] buf = new byte[BUFFER];

            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fis);
            close(fos);
        }
    }

    private static void customBufferBufferedReaderCopy(File source, File target) {
        Reader fin = null;
        Writer fout = null;
        try {
            fin = new BufferedReader(new FileReader(source));
            fout = new BufferedWriter(new FileWriter(target));

            char[] buf = new char[BUFFER / 2];

            int i;
            while ((i = fin.read(buf)) != -1) {
                fout.write(buf, 0, i);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fin);
            close(fout);
        }
    }

    private static void nioBufferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;

        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(target).getChannel();

            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER);
            while (in.read(buffer) != -1) {
                buffer.flip();

                while(buffer.hasRemaining()){
                    out.write(buffer);
                }

                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
            close(out);
        }
    }

    private static void nioTransferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;

        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(target).getChannel();

            long size = in.size();
            long transferred = in.transferTo(0, size, out);

            while(transferred != size){
                transferred += in.transferTo(transferred, size - transferred, out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
            close(out);
        }
    }

    /* Java 7 Only :
    private static void pathCopy(File source, File target) {
        try {
            source.toPath().copyTo(target.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private static void nativeCopy(File source, File target) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(
                    new String[]{
                        "/bin/cp",
                        source.getAbsolutePath(),
                        target.getAbsolutePath()
            });

            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                close(p.getInputStream());
                close(p.getErrorStream());
                close(p.getOutputStream());

                p.destroy();
            }
        }
    }

private static void close(Closeable closable) {
    if (closable != null) {
        try {
            closable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
}
