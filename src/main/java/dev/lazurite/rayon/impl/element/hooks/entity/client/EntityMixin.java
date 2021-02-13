package dev.lazurite.rayon.impl.element.hooks.entity.client;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.element.ElementRigidBody;
import dev.lazurite.rayon.impl.element.interpolate.Frame;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements Frame.Storage {
    @Unique private Frame frame;

    @Override
    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    @Override
    public Frame getFrame() {
        return frame;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo info) {
        if (this instanceof PhysicsElement) {
            ElementRigidBody body = ((PhysicsElement) this).getRigidBody();
            Frame.Storage storage = (Frame.Storage) body.getElement();
            Frame prevFrame = storage.getFrame();

            if (prevFrame == null) {
                storage.setFrame(new Frame(
                        body.getPhysicsLocation(new Vector3f()),
                        body.getPhysicsRotation(new Quaternion())
                ));
            } else {
                storage.setFrame(new Frame(
                        prevFrame,
                        body.getPhysicsLocation(new Vector3f()),
                        body.getPhysicsRotation(new Quaternion())
                ));
            }

            body.prevRotation.set(body.tickRotation);
            body.tickRotation.set(body.getPhysicsRotation(new Quaternion()));
            body.prevLocation.set(body.tickLocation);
            body.tickLocation.set(body.getPhysicsLocation(new Vector3f()));
        }
    }
}
