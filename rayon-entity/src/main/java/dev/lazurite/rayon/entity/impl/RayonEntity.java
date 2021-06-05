package dev.lazurite.rayon.entity.impl;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.core.impl.bullet.collision.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.space.supplier.player.ClientPlayerSupplier;
import dev.lazurite.rayon.core.impl.bullet.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.bullet.math.VectorHelper;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.space.MinecraftSpace;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The common entrypoint for Rayon Entity. Handles entity loading/tracking as well
 * as receiving {@link EntityPhysicsElement} movement updates from prioritized clients.
 * @see RayonEntityClient
 */
public class RayonEntity implements ModInitializer {
	public static final String MODID = "rayon-entity";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Entity");

	public static final Identifier MOVEMENT_UPDATE = new Identifier(MODID, "entity_movement_update");
	public static final Identifier SPAWN = new Identifier(RayonCore.MODID, "element_spawn");
	public static final Identifier PROPERTIES = new Identifier(RayonCore.MODID, "element_properties");

	@Override
	public void onInitialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof EntityPhysicsElement && !PlayerLookup.tracking(entity).isEmpty()) {
				MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());
				space.getWorkerThread().execute(() -> space.addPhysicsElement((EntityPhysicsElement) entity));
			}
		});

		EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
			if (entity instanceof EntityPhysicsElement) {
				MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());
				space.getWorkerThread().execute(() -> space.addPhysicsElement((EntityPhysicsElement) entity));
			}
		});

		EntityTrackingEvents.STOP_TRACKING.register((entity, player) -> {
			if (entity instanceof EntityPhysicsElement && PlayerLookup.tracking(entity).isEmpty()) {
				MinecraftSpace space = MinecraftSpace.get(entity.getEntityWorld());
				space.getWorkerThread().execute(() -> space.removePhysicsElement((EntityPhysicsElement) entity));
			}
		});

		PhysicsSpaceEvents.STEP.register(space -> {
			space.getRigidBodiesByClass(ElementRigidBody.class).stream().filter(rigidBody -> rigidBody.getElement() instanceof EntityPhysicsElement).forEach(rigidBody -> {
				EntityPhysicsElement element = (EntityPhysicsElement) rigidBody.getElement();

				/* Movement Updates */
				if (rigidBody.isActive() && rigidBody.needsMovementUpdate()) {
					if ((space.isServer() && rigidBody.getPriorityPlayer() == null) || (!space.isServer() && ClientPlayerSupplier.get().equals(rigidBody.getPriorityPlayer()))) {
						element.sendMovementUpdate(false);
					}
				}

				/* Server Properties */
				if (space.isServer() && rigidBody.arePropertiesDirty()) {
					element.sendProperties();
				}

				/* Set the entity's position */
				Vector3f location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
				float offset = rigidBody.getFrame().getBox(new BoundingBox(), 1.0f).getYExtent();
				element.asEntity().updatePosition(location.x, location.y - offset, location.z);
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(MOVEMENT_UPDATE, (server, player, handler, buf, sender) -> {
			World world = player.getEntityWorld();

			int entityId = buf.readInt();
			RegistryKey<World> worldKey = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
			boolean reset = buf.readBoolean();

			Quaternion rotation = QuaternionHelper.fromBuffer(buf);
			Vector3f location = VectorHelper.fromBuffer(buf);
			Vector3f linearVelocity = VectorHelper.fromBuffer(buf);
			Vector3f angularVelocity = VectorHelper.fromBuffer(buf);

			PhysicsThread.get(server).execute(() -> {
				if (world.getRegistryKey().equals(worldKey)) {
					Entity entity = world.getEntityById(entityId);

					if (entity instanceof EntityPhysicsElement) {
						ElementRigidBody rigidBody = ((EntityPhysicsElement) entity).getRigidBody();

						if (player.equals(rigidBody.getPriorityPlayer())) {
							rigidBody.setPhysicsRotation(rotation);
							rigidBody.setPhysicsLocation(location);
							rigidBody.setLinearVelocity(linearVelocity);
							rigidBody.setAngularVelocity(angularVelocity);
							rigidBody.activate();

							if (reset) {
								rigidBody.scheduleFrameReset();
							}
						}

						((EntityPhysicsElement) entity).sendMovementUpdate(false);
					}
				}
			});
		});
	}
}