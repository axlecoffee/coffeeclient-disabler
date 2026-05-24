package coffee.axle.coffeeclient.disabler.util;

public final class Logger {

    private Logger() {
    }

    public static void info(String message) {
        com.replaymod.coffeeclient.hook.util.Logger.info(message);
    }

    public static void warn(String message) {
        com.replaymod.coffeeclient.hook.util.Logger.warn(message);
    }

    public static void error(String message) {
        com.replaymod.coffeeclient.hook.util.Logger.error(message);
    }
}
