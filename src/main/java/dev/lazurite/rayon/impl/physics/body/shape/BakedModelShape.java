package dev.lazurite.rayon.impl.physics.body.shape;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Predicate;

/**
 * This class is designed to be a link between Minecraft's {@link BakedModel} objects and
 * Bullet's {@link CollisionShape} objects. The idea is that each {@link BakedQuad} taken
 * from a {@link BakedModel} object can be added to this class a "hull plate" to form a
 * three dimensional concave shape.
 */
public class BakedModelShape extends CompoundCollisionShape {
    private final BakedModel bakedModel;

    /**
     * This constructor is meant to work with any {@link BakedModel} given
     * to it. This would typically be used for entity models. If you want to
     * import a block model, use the constructor which takes a {@link BlockState}.
     * @param bakedModel the baked model
     */
    public BakedModelShape(BakedModel bakedModel) {
        this(bakedModel, null);
    }

    /**
     * This constructor is for adding a block model. In order to get the correct {@link BakedQuad}
     * objects from a block model, you also need the {@link BlockState} object.
     * @param bakedModel the block's baked model
     * @param blockState the block state object
     */
    public BakedModelShape(BakedModel bakedModel, @Nullable BlockState blockState) {
        this.bakedModel = bakedModel;

        if (bakedModel instanceof MultipartBakedModel) {
            MultipartBakedModel multi = (MultipartBakedModel) bakedModel;

            for (Pair<Predicate<BlockState>, BakedModel> component : multi.components) {
                BakedModel inner = component.getRight();

                if (inner instanceof BasicBakedModel) {
                    BasicBakedModel basic = (BasicBakedModel) inner;

                    for (Direction direction : Direction.values()) {
                        for (BakedQuad bakedQuad : basic.faceQuads.get(direction)) {
                            addQuad(bakedQuad);
                        }
                    }
                }
            }
        } else {
            for (Direction direction : Direction.values()) {
                for (BakedQuad quad : bakedModel.getQuads(blockState, direction, new Random())) {
                    addQuad(quad);
                }
            }
        }
    }

    /**
     * Converts a {@link BakedQuad} object into a {@link HullCollisionShape}
     * which is then added as a child shape. This way, you can build a
     * shape out of "hull plates".
     * @param quad the baked quad
     */
    public void addQuad(BakedQuad quad) {
        ObjectArrayList<Vector3f> points = new ObjectArrayList<>();
        int[] v = quad.getVertexData();

        /* Loop 4 times through the 32 byte queue */
        for (int i = 0; i < v.length; i += 8) {

            /* Convert IEEE 754 encoded ints to floats (and also round them) */
            float x = Math.round(Float.intBitsToFloat(v[i]));
            float y = Math.round(Float.intBitsToFloat(v[i + 1]));
            float z = Math.round(Float.intBitsToFloat(v[i + 2]));

            Vector3f point = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
            points.add(point);

        }
        /* Make a new hull shape and scale it down */
        HullCollisionShape hull = new HullCollisionShape(points);
        hull.setScale(0.25f);
        hull.setMargin(0.04f);

        /* Add the shape as a child */
        Transform trans = new Transform();
        this.addChildShape(hull, trans);
    }

    /**
     * Get the {@link BakedModel} associated with this shape.
     * @return the {@link BakedModel} object
     */
    public BakedModel getBakedModel() {
        return this.bakedModel;
    }
}