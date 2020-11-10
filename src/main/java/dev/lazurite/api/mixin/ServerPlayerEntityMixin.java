package dev.lazurite.api.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A mixin class for {@link ServerPlayerEntity} which
 * serves multiple purposes.
 * @author Ethan Johnson
 */
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    /**
     * @param info required by every mixin injection
     */
    @Inject(at = @At("TAIL"), method = "onDisconnect")
    public void onDisconnect(CallbackInfo info) {

    }
}
