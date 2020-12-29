package dev.lazurite.rayon.mixin.step;

import dev.lazurite.rayon.physics.entity.DynamicPhysicsEntity;
import dev.lazurite.rayon.physics.thread.Delta;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientWorld.class)
@Environment(EnvType.CLIENT)
public class ClientStepMixinTest {
    @Unique private final Delta clock = new Delta();

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
//        /* Get this world and it's component */
//        World world = (World) (Object) this;
//        MinecraftDynamicsWorld dynamicsWorld = MinecraftDynamicsWorld.get(world);
//
//        /* Get delta. Should be around 1/20 */
//        float delta = clock.get();
//
//        /* Step the server world */
//        dynamicsWorld.step(delta);
//
//        /* Step every entity */
//        for (Entity entity : dynamicsWorld.getEntities()) {
//            DynamicPhysicsEntity.get(entity).step(delta);
//        }
    }
}
