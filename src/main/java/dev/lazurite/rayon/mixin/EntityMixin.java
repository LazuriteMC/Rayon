package dev.lazurite.rayon.mixin;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.composition.PhysicsComposition;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.vecmath.Vector3f;

@Mixin(Entity.class)
public class EntityMixin {
    private final Entity entity = (Entity) (Object) this;

    @Inject(method = "setPos", at = @At("TAIL"))
    public void setPos(double x, double y, double z, CallbackInfo info) {
        PhysicsComposition physics = Rayon.getPhysics(entity);

        if (physics != null) {
            physics.getSynchronizer().set(PhysicsComposition.POSITION, new Vector3f((float) x, (float) y, (float) z));
        }
    }
}
