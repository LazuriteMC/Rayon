package dev.lazurite.rayon.mixin;

import dev.lazurite.rayon.physics.DynamicBody;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.vecmath.Vector3f;

@Mixin(Entity.class)
public class EntityMixin implements DynamicBody {
//    @Unique
//    private final Entity entity = (Entity) (Object) this;

    /*
     * TODO
     *  figure out how to make entities unable to move (sounds included)
     */

    @Inject(method = "setPos", at = @At("HEAD"), cancellable = true)
    public void setPos(double x, double y, double z, CallbackInfo info) {

    }

    @Inject(method = "updatePosition", at = @At("HEAD"), cancellable = true)
    public void updatePosition(double x, double y, double z, CallbackInfo info) {

    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {

    }

    @Unique @Override
    public boolean hasDynamicBody() {
        return false;
    }

    @Unique @Override
    @Environment(EnvType.CLIENT)
    public boolean belongsToClient() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        return false;
    }

    @Unique @Override
    public void updatePositionAndAngles(Vector3f position, float yaw, float pitch) {
//        entity.updatePositionAndAngles(position.x, position.y, position.z, yaw, pitch);
    }
}
