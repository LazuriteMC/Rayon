package dev.lazurite.rayon.core.impl.event;

import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvent;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.level.ServerLevelSupplier;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppingEvent;

@Mod.EventBusSubscriber(modid = RayonCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public final class ServerEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    @SubscribeEvent
    public static void onServerStart(FMLServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        thread = new PhysicsThread(server, Thread.currentThread(), new ServerLevelSupplier(server), "Server Physics Thread");
    }

    @SubscribeEvent
    private static void onServerStop(FMLServerStoppingEvent event) {
        thread.destroy();
    }

    @SubscribeEvent
    private static void onServerTick(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.END){
            if (thread.throwable != null) {
                throw new RuntimeException(thread.throwable);
            }
        }
    }

    @SubscribeEvent
    private static void onStartLevelTick(TickEvent.WorldTickEvent event) {
        if(event.phase == TickEvent.Phase.START){
            final var space = MinecraftSpace.get(event.world);

            if (!space.getWorkerThread().isPaused()) {
                space.step();
            }
        }
    }

    @SubscribeEvent
    private static void onLevelLoad(WorldEvent.Load event) {
        if(event.getWorld() instanceof ServerLevel level){
            final var space = new MinecraftSpace(thread, level);
            ((SpaceStorage) level).setSpace(space);
            MinecraftForge.EVENT_BUS.post(new PhysicsSpaceEvent.Init(space));
        }
    }
}