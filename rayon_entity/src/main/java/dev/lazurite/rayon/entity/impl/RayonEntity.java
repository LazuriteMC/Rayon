package dev.lazurite.rayon.entity.impl;

import dev.lazurite.rayon.entity.impl.network.RayonEntityPacketHandler;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * The common entrypoint for Rayon Entity.
 */
@Mod(RayonEntity.MODID)
public class RayonEntity{
	public static final String MODID = "rayon_entity";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Entity");

	public RayonEntity(){
		RayonEntityPacketHandler.registerPackets();
		LOGGER.info("Rayon Entity initialized");
	}

	public static Set<ServerPlayer> getPlayerTrackingEntity(Entity entity){
		ChunkMap chunkMap = ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).chunkMap;
		return chunkMap.entityMap.get(entity.getId()).seenBy.stream().map(ServerPlayerConnection::getPlayer).collect(Collectors.toSet());
	}
}