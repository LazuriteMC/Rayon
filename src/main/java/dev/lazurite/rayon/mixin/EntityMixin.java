package dev.lazurite.rayon.mixin;

import dev.lazurite.rayon.physics.composition.DynamicBodyComposition;
import dev.lazurite.rayon.physics.DynamicBody;
import dev.lazurite.thimble.Thimble;
import dev.lazurite.thimble.composition.Composition;
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

@Mixin(Entity.class)
public class EntityMixin implements DynamicBody {
    @Unique
    private final Entity entity = (Entity) (Object) this;

    /*
     * TODO
     *  figure out how to make entities unable to move (sounds included)
     */

    public boolean physicsCheck() {
        DynamicBodyComposition physics = getDynamicBody();

        if (physics != null) {
            return !entity.getEntityWorld().isClient();
        }

        return false;
    }

    @Inject(method = "setPos", at = @At("HEAD"), cancellable = true)
    public void setPos(double x, double y, double z, CallbackInfo info) {
        if (physicsCheck()) {
            info.cancel();
        }

//        physics.getSynchronizer().set(DynamicBodyComposition.POSITION, new Vector3f((float) x, (float) y, (float) z));
    }

    @Inject(method = "updatePosition", at = @At("HEAD"), cancellable = true)
    public void updatePosition(double x, double y, double z, CallbackInfo info) {
        if (physicsCheck()) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {
        if (physicsCheck()) {
            info.cancel();
        }
    }

    @Unique
    @Override
    public DynamicBodyComposition getDynamicBody() {
        for (Composition composition : Thimble.getStitches(entity)) {
            if (composition instanceof DynamicBodyComposition) {
                return (DynamicBodyComposition) composition;
            }
        }

        return null;
    }

    @Unique
    @Override
    public boolean hasDynamicBody() {
        return getDynamicBody() != null;
    }

    @Unique
    @Override
    @Environment(EnvType.CLIENT)
    public boolean belongsToClient() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        DynamicBodyComposition physics = getDynamicBody();

        if (physics != null && player != null) {
            return physics.belongsTo(player);
        }

        return false;
    }
}
