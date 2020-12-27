package dev.lazurite.rayon.api.mixin.step;

import dev.lazurite.rayon.api.physics.util.Delta;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public class ServerStepMixin {
    @Unique private final Delta clock = new Delta();

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        float delta = clock.get(); // should be around 1/20 mostly


    }
}
