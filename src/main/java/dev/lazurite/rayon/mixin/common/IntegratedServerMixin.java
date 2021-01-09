package dev.lazurite.rayon.mixin.common;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.mixin.common.world.ServerWorldMixin;
import dev.lazurite.rayon.mixin.client.MinecraftClientMixin;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.function.BooleanSupplier;

/**
 * If the game is paused, it's important to clear all delta time
 * within {@link MinecraftDynamicsWorld}. Otherwise, dynamic entities
 * tend to go flying or fall out of the world.<br><br>
 *
 * Since a normal {@link DedicatedServer} is unable to pause, but an
 * {@link IntegratedServer} can, this mixin only affects the
 * {@link IntegratedServer} class.<br><br>
 *
 * @see ServerWorldMixin
 * @see MinecraftClientMixin
 */
@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer {
    @Shadow private boolean paused;

    /* sadge */
    private IntegratedServerMixin(Thread thread, DynamicRegistryManager.Impl impl, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(thread, impl, session, saveProperties, resourcePackManager, proxy, dataFixer, serverResourceManager, minecraftSessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        if (paused) {
            getWorlds().forEach(world -> MinecraftDynamicsWorld.get(world).getClock().reset());
        }
    }
}
