package pl.animekkk.anlogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class Logger {

    public enum Log {
        INFO("<INFO> ", "\033[0;32m"),
        ERROR("<ERROR>", "\033[0;31m"),
        WARN("<WARN> ", "\033[0;33m"),
        DEBUG("<DEBUG>", "\033[0;35m");

        private final String name;
        private final String color;

        Log(String name, String color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public String getColor() {
            return this.color;
        }
    }

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static boolean runtimeSave = false;
    private static int cacheSize = -1;
    //When cache size is set to -1 then you have to save logs manually using saveCache();

    private static final List<String> logsCache = new ArrayList<>();
    private static File outputFile = null;

    public static void setDatePattern(String pattern) {
        simpleDateFormat.applyPattern(pattern);
    }

    public static void setRuntimeSave(boolean runtime) {
        runtimeSave = runtime;
        if(runtimeSave) logsCache.clear();
    }

    public static void setMaxCache(int size) {
        cacheSize = size;
    }

    public static void setOutputFile(File file) {
        outputFile = file;
    }

    public static void log(boolean save, Log type, String... msg) {
        String log = type.getName() + " " + simpleDateFormat.format(System.currentTimeMillis()) + " " + String.join(" ", msg);
        System.out.println(type.getColor() + log + "\033[0m"); //Reset color
        if(!save) return;
        if(runtimeSave) {
            writeLog(log);
        } else {
            addToCache(log);
        }
    }

    public static void log(Log type, String... msg) {
        log(true, type, msg);
    }

    public static void log(Log type, Object... object) {
        String objectString = Arrays.toString(object);
        log(type, String.join(" ", objectString.substring(1, objectString.length()-1)));
    }

    public static void addToCache(String log) {
        logsCache.add(log);
        if(cacheSize > 0 && cacheSize <= logsCache.size()) saveCache();
    }

    public static void saveCache() {
        if(runtimeSave) {
            log(Log.WARN, "Cannot save cache when run-time save is enabled.");
            return;
        }
        writeLog(logsCache.toArray(new String[0]));
        logsCache.clear();
    }

    public static void writeLog(String... logs) {
        File logFile = outputFile;
        if(outputFile == null) {
            logFile = new File(simpleDateFormat.format(System.currentTimeMillis())
                    .replaceAll("/", "∕")
                    .replaceAll(":", "꞉")
                    .replaceAll("\\*", "")
                    .replaceAll("\\?", "")
                    .replaceAll("\"", "")
                    .replaceAll("<", "")
                    .replaceAll(">", "")
                    .replaceAll("\\|", "")
                    .replaceAll(" ", "-")
                    + ".txt");
            if(runtimeSave) outputFile = logFile;
        }
        if(!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
                log(false, Log.ERROR, "Cannot create log file.");
                return;
            }
        }
        try {
            Files.write(logFile.toPath(), Arrays.asList(logs), StandardOpenOption.APPEND);
        } catch (IOException exception) {
            exception.printStackTrace();
            log(false, Log.ERROR, "Cannot write log to file.");
        }

    }

}
