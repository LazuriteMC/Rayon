package dev.lazurite.rayon.core.impl.bullet.collision.space.generator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvent;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RayonCore.MODID)
public class PressureGenerator {

    @SubscribeEvent
    public static void step(PhysicsSpaceEvent.Step event) {
        MinecraftSpace space = event.getSpace();
        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            var rigidBodyBox = Convert.toMinecraft(rigidBody.getCollisionShape().boundingBox(rigidBody.getPhysicsLocation(null), new Quaternion(), null));
            rigidBodyBox = rigidBodyBox.contract(rigidBodyBox.getXsize() * 0.2, rigidBodyBox.getYsize() * 0.2, rigidBodyBox.getZsize() * 0.2);

            var volume = 0.0f;

            final var gravity = -1.0f * space.getGravity(null).y;
            final var maxVolume = rigidBodyBox.getXsize() * rigidBodyBox.getYsize() * rigidBodyBox.getZsize();
            final var fluidObjects = rigidBody.getTerrainObjects().values().stream()
                    .filter(terrainObject -> terrainObject.getFluidState().isPresent()).toList();

//            final var relativePoints = new ArrayList<Vector3f>();
//            relativePoints.add(new Vector3f((float) rigidBodyBox.getXsize() * 0.5f, (float) rigidBodyBox.getYsize(), (float) (-1.0f * rigidBodyBox.getZsize() * 0.5f)));
//            relativePoints.add(new Vector3f((float) (-1.0f * rigidBodyBox.getXsize() * 0.5f), (float) rigidBodyBox.getYsize(), (float) (-1.0f * rigidBodyBox.getZsize() * 0.5f)));
//            relativePoints.add(new Vector3f((float) rigidBodyBox.getXsize() * 0.5f, (float) rigidBodyBox.getYsize(), (float) rigidBodyBox.getZsize() * 0.5f));
//            relativePoints.add(new Vector3f((float) (-1.0f * rigidBodyBox.getXsize() * 0.5f), (float) rigidBodyBox.getYsize(), (float) rigidBodyBox.getZsize() * 0.5f));

            float centerY = 0.0f;
            int i = 0;

            for (var fluidObject : fluidObjects) {
                final var fluidBox = Convert.toMinecraft(fluidObject.getCollisionObject().boundingBox(null));

                if (fluidBox.intersects(rigidBodyBox)) {
                    final var intersection = fluidBox.intersect(rigidBodyBox);
                    volume += intersection.getXsize() * intersection.getYsize() * intersection.getZsize();

                    centerY += intersection.getCenter().y;
                    i++;
                }
            }

            final var force = (float) (gravity * 1000 * Math.min(volume, maxVolume)) * 0.25f;

            if (Float.isFinite(force) && force > 0.0f) {
                rigidBody.applyDragForce(1000);

//                final float y = centerY / (float) i;
                final var y = (float) rigidBodyBox.getCenter().y;
                final var rotation = Convert.toMinecraft(rigidBody.getPhysicsRotation(null));

                final var v1 = Convert.toMinecraft(new Vector3f((float) -rigidBodyBox.getXsize(), 0, (float) -rigidBodyBox.getZsize()));
                final var v2 = Convert.toMinecraft(new Vector3f((float) -rigidBodyBox.getXsize(), 0, (float) rigidBodyBox.getZsize()));
                final var v3 = Convert.toMinecraft(new Vector3f((float) rigidBodyBox.getXsize(), 0, (float) -rigidBodyBox.getZsize()));
                final var v4 = Convert.toMinecraft(new Vector3f((float) rigidBodyBox.getXsize(), 0, (float) rigidBodyBox.getZsize()));

                v1.transform(rotation);
                v2.transform(rotation);
                v3.transform(rotation);
                v4.transform(rotation);

                rigidBody.applyForce(new Vector3f(0.0f, force, 0.0f), Convert.toBullet(v1));
                rigidBody.applyForce(new Vector3f(0.0f, force, 0.0f), Convert.toBullet(v2));
                rigidBody.applyForce(new Vector3f(0.0f, force, 0.0f), Convert.toBullet(v3));
                rigidBody.applyForce(new Vector3f(0.0f, force, 0.0f), Convert.toBullet(v4));
            } else {
                rigidBody.applyDragForce(1.2f);
            }
        }
    }
}
