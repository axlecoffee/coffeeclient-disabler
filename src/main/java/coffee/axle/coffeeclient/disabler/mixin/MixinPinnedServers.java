package coffee.axle.coffeeclient.disabler.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.moonsworth.lunar.client.RIRIIOROHRIOIRICOICIRCORROOROH.ORIORIRRRRROIIOOOHICOCOROCICCC.OOOCOHCHCHHCRCCIOOROHHOOHCOHII.IOORCHIRRCIHCHHOCROCROROCHCCCI", remap = false)
public class MixinPinnedServers {

    @Inject(method = "HCRRHCRCOHHCRHRIORHIRRORIRRRCH", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelPinnedServers(CallbackInfo ci) {
        ci.cancel();
    }
}
