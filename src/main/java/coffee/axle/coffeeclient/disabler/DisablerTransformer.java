package coffee.axle.coffeeclient.disabler;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import coffee.axle.coffeeclient.disabler.util.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DisablerTransformer implements IClassTransformer {

    private static final String TARGET_PACKAGE_PREFIX = "com/moonsworth/";
    private static final String REPLACEMENT_PREFIX = "disabledByCoffee_";

    private static final Set<String> TARGET_KEYS = new HashSet<>(Arrays.asList(
            "modSettings",
            "pinnedServers",
            "disable-shaders",
            "override-brightness",
            "brightness",
            "nametag-render-distance",
            "override-nametag-render-distance",
            "disable-broadcasting",
            "competitive-game",
            "competitive-commands",
            "disable-chunk-reloading",
            "blocked-text-inputs",
            "block-chat-message-text-inputs",
            "block-text-inputs",
            "disable-miss-penalty",
            "freelook.enabled",
            "freelook"
    ));

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !name.replace('.', '/').startsWith(TARGET_PACKAGE_PREFIX)) {
            Logger.info("[Disabler] Skipping non-target class: " + name);
            return basicClass;
        }

        try {
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            StringReplacerClassVisitor visitor = new StringReplacerClassVisitor(classWriter, name);
            classReader.accept(visitor, 0);
            Logger.info("[Disabler] Finished visiting class: " + name + ", modified: " + visitor.isModified());
            return visitor.isModified() ? classWriter.toByteArray() : basicClass;
        } catch (Exception e) {
            Logger.info("[Disabler] Error transforming " + name + ": " + e.getMessage());
            return basicClass;
        }
    }

    private static class StringReplacerClassVisitor extends ClassVisitor {

        private final String className;
        private boolean modified = false;

        StringReplacerClassVisitor(ClassVisitor cv, String className) {
            super(589824 /* ASM9 */, cv);
            this.className = className;
        }

        boolean isModified() {
            return modified;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            Logger.info("[Disabler] Visiting method: " + name + " in " + className);
            return new StringReplacerMethodVisitor(mv);
        }

        private class StringReplacerMethodVisitor extends MethodVisitor {

            StringReplacerMethodVisitor(MethodVisitor mv) {
                super(589824 /* ASM9 */, mv);
            }

            @Override
            public void visitLdcInsn(Object value) {
                if (value instanceof String) {
                    String s = (String) value;
                    if (TARGET_KEYS.contains(s)) {
                        Logger.info("[Disabler] Blinded key: " + s + " in " + className);
                        super.visitLdcInsn(REPLACEMENT_PREFIX + s);
                        modified = true;
                        return;
                    }
                }
                super.visitLdcInsn(value);
            }
        }
    }
}
