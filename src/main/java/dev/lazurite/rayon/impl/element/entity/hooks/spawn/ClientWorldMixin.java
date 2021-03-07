package dev.lazurite.rayon.impl.element.entity.hooks.spawn;

import com.jme3.bounding.BoundingBox;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.util.math.VectorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * This mixin automatically adds entities assigned
 * {@link PhysicsElement} to the {@link MinecraftSpace}.
 */
@Mixin(ClientWorld.class)
@Environment(EnvType.CLIENT)
public class ClientWorldMixin {
    @Inject(method = "addEntity", at = @At("TAIL"))
    public void addEntity(int id, Entity entity, CallbackInfo info) {
        if (entity instanceof PhysicsElement) {
            /* Set the position of the rigid body */
            ElementRigidBody rigidBody = ((PhysicsElement) entity).getRigidBody();
            rigidBody.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos().add(0, rigidBody.boundingBox(new BoundingBox()).getYExtent(), 0)));

            /* Add it to the world */
            MinecraftSpace space = Rayon.SPACE.get(this);
            space.getThread().execute(() -> space.addCollisionObject(rigidBody));
        }
    }

    @Inject(
            method = "removeEntity",
            at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void removeEntity(int entityId, CallbackInfo info, Entity entity) {
        if (entity instanceof PhysicsElement) {
            MinecraftSpace space = Rayon.SPACE.get(this);

            space.getThread().execute(() ->
                    space.removeCollisionObject(((PhysicsElement) entity).getRigidBody())
            );
        }
    }
}
