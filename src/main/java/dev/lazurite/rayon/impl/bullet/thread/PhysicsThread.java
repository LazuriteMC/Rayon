package dev.lazurite.rayon.impl.bullet.thread;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.body.packet.SyncRigidBodyC2S;
import dev.lazurite.rayon.impl.bullet.body.packet.SyncRigidBodyS2C;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.util.math.QuaternionHelper;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class PhysicsThread extends Thread implements ComponentV3, CommonTickingComponent {
    private static final long STEP_SIZE = 15L;

    private final Queue<Consumer<MinecraftSpace>> tasks = new ConcurrentLinkedQueue<>();
    private final Thread mainThread;
    private final World world;
    private MinecraftSpace space;
    private long nextStep;

    public PhysicsThread(World world, Thread mainThread) {
        this.world = world;
        this.mainThread = mainThread;
        this.nextStep = Util.getMeasuringTimeMs() + STEP_SIZE;
        this.setName("Physics " + mainThread.getName());
        this.start();
    }

    @Override
    public void run() {
        this.space = new MinecraftSpace(this, world);

        while (!space.isDestroyed()) {
            if (Util.getMeasuringTimeMs() > nextStep) {
                nextStep = Util.getMeasuringTimeMs() + STEP_SIZE;

                while (!tasks.isEmpty()) {
                    tasks.poll().accept(space);
                }

                space.step();
            }
        }
    }

    @Override
    public synchronized void tick() {
        if (space != null) {
            space.getRigidBodiesByClass(ElementRigidBody.class).forEach(body -> {
                body.prevRotation.set(body.tickRotation);
                body.tickRotation.set(body.getPhysicsRotation(new Quaternion()));
                body.prevLocation.set(body.tickLocation);
                body.tickLocation.set(body.getPhysicsLocation(new Vector3f()));

                body.getElement().asEntity().updatePosition(body.tickLocation.x, body.tickLocation.y, body.tickLocation.z);
                body.getElement().asEntity().yaw = QuaternionHelper.getYaw(body.tickRotation);
                body.getElement().asEntity().pitch = QuaternionHelper.getPitch(body.tickRotation);

                if (body.getSyncMode().equals(ElementRigidBody.SyncMode.SERVER) && !world.isClient()) {
                    PlayerLookup.tracking(body.getElement().asEntity()).forEach(player -> SyncRigidBodyS2C.send(player, body));
                } else if (body.getSyncMode().equals(ElementRigidBody.SyncMode.CLIENT) && world.isClient()) {
                    SyncRigidBodyC2S.send(body);
                }
            });
        }
    }

    public void execute(Consumer<MinecraftSpace> consumer) {
        tasks.add(consumer);
    }

    public MinecraftSpace getSpace() {
        return this.space;
    }

    @Override
    public void readFromNbt(CompoundTag compoundTag) {

    }

    @Override
    public void writeToNbt(CompoundTag compoundTag) {

    }
}
