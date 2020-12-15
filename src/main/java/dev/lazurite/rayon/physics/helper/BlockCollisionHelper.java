package dev.lazurite.rayon.physics.helper;

import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.composition.PhysicsComposition;
import dev.lazurite.rayon.physics.PhysicsWorld;
import dev.lazurite.rayon.util.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.*;

@Environment(EnvType.CLIENT)
public class BlockCollisionHelper {
    private final PhysicsWorld physicsWorld;
    private final Map<BlockPos, RigidBody> collisionBlocks;
    private final List<BlockPos> toKeepBlocks;

    public BlockCollisionHelper(PhysicsWorld physicsWorld)  {
        this.physicsWorld = physicsWorld;
        this.collisionBlocks = new HashMap<>();
        this.toKeepBlocks = new ArrayList<>();
    }

    public void load(Entity entity, ClientWorld world) {
        Box area = new Box(new BlockPos(entity.getPos())).expand(Constants.BLOCK_RADIUS);
        Map<BlockPos, BlockState> blockList = getBlockList(world, area);
        BlockView blockView = world.getChunkManager().getChunk(entity.chunkX, entity.chunkZ);

        blockList.forEach((blockPos, blockState) -> {
            if (!blockState.getBlock().canMobSpawnInside()) {
                if (!this.collisionBlocks.containsKey(blockPos)) {
                    VoxelShape coll = blockState.getCollisionShape(blockView, blockPos);

                    if (!coll.isEmpty()) {
                        Box b = coll.getBoundingBox();

                        Vector3f box = new Vector3f(
                                ((float) (b.maxX - b.minX) / 2.0F) + 0.005f,
                                ((float) (b.maxY - b.minY) / 2.0F) + 0.005f,
                                ((float) (b.maxZ - b.minZ) / 2.0F) + 0.005f);
                        CollisionShape shape = new BoxShape(box);

                        Vector3f position = new Vector3f(blockPos.getX() + box.x, blockPos.getY() + box.y, blockPos.getZ() + box.z);
                        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(), position, 1.0f)));
                        RigidBodyConstructionInfo ci = new RigidBodyConstructionInfo(0, motionState, shape, new Vector3f(0, 0, 0));
                        RigidBody body = new RigidBody(ci);

                        if (blockState.getBlock() instanceof IceBlock) {
                            body.setFriction(0.05f);
                        } else if (
                                blockState.getBlock() instanceof HoneyBlock ||
                                        blockState.getBlock() instanceof SlimeBlock ||
                                        blockState.getBlock() instanceof SoulSandBlock
                        ) {
                            body.setFriction(1.5f);
                        } else {
                            body.setFriction(0.9f);
                        }

                        this.collisionBlocks.put(blockPos, body);
                        this.physicsWorld.addRigidBody(body);
                    }
                }

                this.toKeepBlocks.add(blockPos);
            }
        });
    }

    public void unload() {
        List<BlockPos> toRemove = new ArrayList<>();

        this.collisionBlocks.forEach((pos, body) -> {
            if (!toKeepBlocks.contains(pos)) {
                this.physicsWorld.removeRigidBody(body);
                toRemove.add(pos);
            }
        });

        toRemove.forEach(this.collisionBlocks::remove);
        toKeepBlocks.clear();
    }

    public boolean contains(RigidBody body) {
        return this.collisionBlocks.containsValue(body);
    }

    public Collection<RigidBody> getRigidBodies() {
        return this.collisionBlocks.values();
    }

    /**
     * Finds and returns a {@link Set} of {@link Block} objects that the
     * {@link Entity} is touching based on the provided {@link Direction}(s)
     * @param directions the {@link Direction}s of the desired touching {@link Block}s
     * @return a list of touching {@link Block}s
     */
    public static Set<Block> getTouchingBlocks(Entity entity, Direction... directions) {
        PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
        PhysicsComposition physics = Rayon.getPhysics(entity);

        Dispatcher dispatcher = physicsWorld.getDispatcher();
        Set<Block> blocks = new HashSet<>();

        for (int manifoldNum = 0; manifoldNum < dispatcher.getNumManifolds(); ++manifoldNum) {
            PersistentManifold manifold = dispatcher.getManifoldByIndexInternal(manifoldNum);

            if (physicsWorld.getBlockCollisionHelper().contains((RigidBody) manifold.getBody0()) &&
                    physicsWorld.getBlockCollisionHelper().contains((RigidBody) manifold.getBody1())) {
                continue;
            }

            for (int contactNum = 0; contactNum < manifold.getNumContacts(); ++contactNum) {
                if (manifold.getContactPoint(contactNum).getDistance() <= 0.0f) {
                    if (physics.getRigidBody().equals(manifold.getBody0()) || physics.getRigidBody().equals(manifold.getBody1())) {
                        Vector3f droneRigidBodyPos = physics.getRigidBody().equals(manifold.getBody0()) ? ((RigidBody) manifold.getBody0()).getCenterOfMassPosition(new Vector3f()) : ((RigidBody) manifold.getBody1()).getCenterOfMassPosition(new Vector3f());
                        Vector3f otherRigidBodyPos = physics.getRigidBody().equals(manifold.getBody0()) ? ((RigidBody) manifold.getBody1()).getCenterOfMassPosition(new Vector3f()) : ((RigidBody) manifold.getBody0()).getCenterOfMassPosition(new Vector3f());

                        for (Direction direction : directions) {
                            switch (direction) {
                                case UP:
                                    if (droneRigidBodyPos.y < otherRigidBodyPos.y) {
                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
                                    }
                                    break;
                                case DOWN:
                                    if (droneRigidBodyPos.y > otherRigidBodyPos.y) {
                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
                                    }
                                    break;
                                case EAST:
                                    if (droneRigidBodyPos.x < otherRigidBodyPos.x) {
                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
                                    }
                                    break;
                                case WEST:
                                    if (droneRigidBodyPos.x > otherRigidBodyPos.x) {
                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
                                    }
                                    break;
                                case NORTH:
                                    if (droneRigidBodyPos.z < otherRigidBodyPos.z) {
                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
                                    }
                                    break;
                                case SOUTH:
                                    if (droneRigidBodyPos.z > otherRigidBodyPos.z) {
                                        blocks.add(entity.world.getBlockState(new BlockPos(otherRigidBodyPos.x, otherRigidBodyPos.y, otherRigidBodyPos.z)).getBlock());
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public static Map<BlockPos, BlockState> getBlockList(ClientWorld world, Box area) {
        Map<BlockPos, BlockState> map = new HashMap<>();
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
