package dev.lazurite.rayon.physics.helper;

import com.bulletphysics.collision.shapes.BoxShape;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lazurite.rayon.physics.block.BlockRigidBody;
import dev.lazurite.rayon.physics.world.MinecraftDynamicsWorld;
import dev.lazurite.rayon.physics.util.Constants;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.*;

public class BlockHelper {
    private final MinecraftDynamicsWorld dynamicsWorld;
    private final JsonArray blockProperties;
    private final List<BlockRigidBody> toKeep;

    public BlockHelper(MinecraftDynamicsWorld dynamicsWorld)  {
        this.dynamicsWorld = dynamicsWorld;
        this.blockProperties = PropertyHelper.get("blocks");
        this.toKeep = Lists.newArrayList();
    }

    public void load(EntityHelper entities) {
        entities.getEntities().forEach(this::load);
        purge();
    }

    private void load(Entity entity) {
        World world = dynamicsWorld.getWorld();
        Box area = new Box(new BlockPos(entity.getPos())).expand(Constants.BLOCK_RADIUS);
        Map<BlockPos, BlockState> blockList = getBlockList(world, area);
        BlockView blockView = world.getChunkManager().getChunk(entity.chunkX, entity.chunkZ);

        blockList.forEach((blockPos, blockState) -> {
            float friction = 1.0f;
            boolean permeable = false;

            /* Get properties for this specific block */
            for (JsonElement property : blockProperties) {
                String currentBlock = blockState.getBlock().getTranslationKey();
                String name = ((JsonObject) property).get("name").toString();

                if (currentBlock.equals(name)) {
                    friction = ((JsonObject) property).get("friction").getAsFloat();
                    permeable = ((JsonObject) property).get("permeable").getAsBoolean();
                }
            }

            /* Check if block is solid or not */
            if (!blockState.getBlock().canMobSpawnInside() && !permeable) {
                Box box = blockState.getCollisionShape(blockView, blockPos).getBoundingBox();
                BoxShape shape = new BoxShape(new Vector3f(
                        (float) (box.maxX - box.minX) / 2.0f,
                        (float) (box.maxY - box.minY) / 2.0f,
                        (float) (box.maxZ - box.minZ) / 2.0f));
                BlockRigidBody body = BlockRigidBody.create(blockPos, shape, friction);

                /* Check if the block isn't already in the dynamics world */
                if (!dynamicsWorld.getCollisionObjectArray().contains(body)) {
                    dynamicsWorld.addRigidBody(body);
                }

                toKeep.add(body);
            }
        });
    }

    public void purge() {
        List<BlockRigidBody> toRemove = Lists.newArrayList();

        dynamicsWorld.getCollisionObjectArray().forEach(body -> {
            if (body instanceof BlockRigidBody) {
                BlockRigidBody block = (BlockRigidBody) body;

                if (!toKeep.contains(block)) {
                    toRemove.add(block);
                }
            }
        });

        toRemove.forEach(dynamicsWorld::removeRigidBody);
        toKeep.clear();
    }

    /*/**
     * Finds and returns a {@link Set} of {@link Block} objects that the
     * {@link Entity} is touching based on the provided {@link Direction}(s)
     * @param directions the {@link Direction}s of the desired touching {@link Block}s
     * @return a list of touching {@link Block}s
     */
    /*public static Set<Block> getTouchingBlocks(Entity entity, Direction... directions) {
        PhysicsWorld physicsWorld = PhysicsWorld.INSTANCE;
//        DynamicBodyComposition physics = ((DynamicBody) entity).getDynamicBody();

        Dispatcher dispatcher = physicsWorld.getDispatcher();
        Set<Block> blocks = Sets.newHashSet();

        for (int manifoldNum = 0; manifoldNum < dispatcher.getNumManifolds(); ++manifoldNum) {
            PersistentManifold manifold = dispatcher.getManifoldByIndexInternal(manifoldNum);

            if (physicsWorld.blockHelper.contains((RigidBody) manifold.getBody0()) &&
                    physicsWorld.getBlockHelper().contains((RigidBody) manifold.getBody1())) {
                continue;
            }

            for (int contactNum = 0; contactNum < manifold.getNumContacts(); ++contactNum) {
                if (manifold.getContactPoint(contactNum).getDistance() <= 0.0f) {
//                    if (physics.getRigidBody().equals(manifold.getBody0()) || physics.getRigidBody().equals(manifold.getBody1())) {
//                        Vector3f droneRigidBodyPos = physics.getRigidBody().equals(manifold.getBody0()) ? ((RigidBody) manifold.getBody0()).getCenterOfMassPosition(new Vector3f()) : ((RigidBody) manifold.getBody1()).getCenterOfMassPosition(new Vector3f());
//                        Vector3f otherRigidBodyPos = physics.getRigidBody().equals(manifold.getBody0()) ? ((RigidBody) manifold.getBody1()).getCenterOfMassPosition(new Vector3f()) : ((RigidBody) manifold.getBody0()).getCenterOfMassPosition(new Vector3f());
//
//                        for (Direction direction : directions) {
//                            switch (direction) {
//                                case UP:
//                                    if (droneRigidBodyPos.y < otherRigidBodyPos.y) {
//                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
//                                    }
//                                    break;
//                                case DOWN:
//                                    if (droneRigidBodyPos.y > otherRigidBodyPos.y) {
//                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
//                                    }
//                                    break;
//                                case EAST:
//                                    if (droneRigidBodyPos.x < otherRigidBodyPos.x) {
//                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
//                                    }
//                                    break;
//                                case WEST:
//                                    if (droneRigidBodyPos.x > otherRigidBodyPos.x) {
//                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
//                                    }
//                                    break;
//                                case NORTH:
//                                    if (droneRigidBodyPos.z < otherRigidBodyPos.z) {
//                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
//                                    }
//                                    break;
//                                case SOUTH:
//                                    if (droneRigidBodyPos.z > otherRigidBodyPos.z) {
//                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
//                                    }
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                    }
                }
            }
        }
        return blocks;
    } */

    public static Map<BlockPos, BlockState> getBlockList(World world, Box area) {
        Map<BlockPos, BlockState> map = Maps.newHashMap();
        for (int i = (int) area.minX; i < area.maxX; i++) {
            for (int j = (int) area.minY; j < area.maxY; j++) {
                for (int k = (int) area.minZ; k < area.maxZ; k++) {
                    BlockPos blockPos = new BlockPos(i, j, k);
                    BlockState blockState = world.getWorldChunk(blockPos).getBlockState(blockPos);
                    map.put(blockPos, blockState);
                }
            }
        }

        return map;
    }
}
