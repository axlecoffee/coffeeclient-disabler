package coffee.axle.coffeeclient.disabler;

import coffee.axle.coffeeclient.disabler.util.Logger;
import com.replaymod.coffeeclient.hook.CoffeeMod;
import com.replaymod.coffeeclient.hook.event.CLInitEvent;
import com.replaymod.coffeeclient.hook.event.CLMixinInitEvent;
import com.replaymod.coffeeclient.hook.event.CLNEUInitEvent;
import com.replaymod.coffeeclient.hook.event.CLPostInitEvent;
import com.replaymod.coffeeclient.hook.event.CLPreInitEvent;
import com.replaymod.coffeeclient.hook.event.CLReplayModInitEvent;

import java.io.File;
import java.util.Enumeration;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@CoffeeMod(name = "CoffeeClient-Disabler", version = "1.0.0")
public class Disabler {

    private static final TreeSet<String> logged = new TreeSet<>();

    @CoffeeMod.EventHandler
    public void onMixinInit(CLMixinInitEvent event) {}

    @CoffeeMod.EventHandler
    public void onNEUInit(CLNEUInitEvent event) {}

    @CoffeeMod.EventHandler
    public void onEarlyInit(CLReplayModInitEvent event) {}

    @CoffeeMod.EventHandler
    public void onPreInit(CLPreInitEvent event) {}

    @CoffeeMod.EventHandler
    public void onInit(CLInitEvent event) {}

    @CoffeeMod.EventHandler
    public void onPostInit(CLPostInitEvent event) {
        probeMoonsworthClasses();
        Logger.info("[Disabler] Post-init complete");
    }

    private static void probeMoonsworthClasses() {
        File lunarDir = new File(System.getProperty("user.home"), ".lunarclient");
        Logger.info("[Disabler] === Moonsworth Class Probe (disk) [" + lunarDir + "] ===");
        if (!lunarDir.exists()) {
            Logger.error("[Disabler] .lunarclient not found: " + lunarDir);
            return;
        }
        scanJarsRecursive(lunarDir);
        Logger.info("[Disabler] === End Probe ===");
    }

    private static void scanJarsRecursive(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                scanJarsRecursive(f);
            } else if (f.getName().endsWith(".jar")) {
                scanJar(f);
            }
        }
    }

    private static void scanJar(File jar) {
        try (JarFile jf = new JarFile(jar)) {
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith("com/moonsworth/") && name.endsWith(".class")) {
                    String className = name.replace('/', '.').substring(0, name.length() - 6);
                    if (logged.add(className)) {
                        Logger.info("[Disabler]   " + className + " [" + jar.getName() + "]");
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
