package dev.lazurite.rayon.core.impl.event;

import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvent;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.storage.SpaceStorage;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.level.ClientLevelSupplier;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.util.debug.CollisionObjectDebugger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RayonCore.MODID, value = Dist.CLIENT)
public final class ClientEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    @SubscribeEvent
    public static void onStartLevelTick(TickEvent.ClientTickEvent event) {//Trying to mimic behavior of Client tick start
        Level level = Minecraft.getInstance().level;
        if(level == null)return;
        if(event.side != LogicalSide.CLIENT)return;
        if(event.phase != TickEvent.Phase.START)return;
        final var space = MinecraftSpace.get(level);
        if (!space.getWorkerThread().isPaused()) {
            space.step();
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(WorldEvent.Load event) {
        if(!(event.getWorld().isClientSide()))return;
        if(thread==null || !thread.running){
            Minecraft minecraft = Minecraft.getInstance();
//        var supplier = RayonCore.isImmersivePortalsPresent() ? new ImmersiveWorldSupplier(minecraft) : new ClientLevelSupplier(minecraft);
            final var supplier = new ClientLevelSupplier(minecraft);
            thread = new PhysicsThread(minecraft, Thread.currentThread(), supplier, "Client Physics Thread");
        }
        Level level = (ClientLevel) event.getWorld();
        final var space = new MinecraftSpace(thread, level);
        ((SpaceStorage) level).setSpace(space);
        MinecraftForge.EVENT_BUS.post(new PhysicsSpaceEvent.Init(space));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.END)return;
        if (thread != null && thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        if(Minecraft.getInstance().level != null) thread.destroy();
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event){
        if (CollisionObjectDebugger.getInstance().isEnabled()) {
            CollisionObjectDebugger.getInstance().renderSpace(MinecraftSpace.get(Minecraft.getInstance().level), event.getPartialTicks(), event.getMatrixStack());
        }
    }
}
