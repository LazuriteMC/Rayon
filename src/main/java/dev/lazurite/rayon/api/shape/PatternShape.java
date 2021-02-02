package dev.lazurite.rayon.api.shape;

import com.google.common.collect.Lists;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.transporter.Disassembler;
import dev.lazurite.rayon.impl.transporter.Pattern;
import dev.lazurite.rayon.impl.util.helper.math.VectorHelper;

import java.util.List;

/**
 * This collision shape is unique in that it uses rendered vertices to
 * build a shape out of quads that represents exactly what you see on-screen.
 * In doing so, it relies on clients being able to provide that information
 * at runtime.<br>
 *
 * @since 1.1.0
 * @see Pattern
 * @see CompoundCollisionShape
 */
public class PatternShape extends CompoundCollisionShape {
    private final Pattern pattern;

    public PatternShape(Pattern pattern) {
        this.pattern = pattern;

        for (Pattern.Quad quad : pattern.getQuads()) {
            List<Vector3f> points = Lists.newArrayList();
            quad.getPoints().forEach(vector -> points.add(VectorHelper.minecraftToBullet(vector)));
            addChildShape(new HullCollisionShape(points), new Transform());
        }
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public static EntityShapeFactory getFactory() {
        return (entity) -> {
            if (entity.getEntityWorld().isClient()) {
                return new PatternShape(Disassembler.getPattern(entity));
            } else {
                /* Just use a bounding box shape until the vertex data is received. */
                return new BoundingBoxShape(entity.getBoundingBox());
            }
        };
    }
}
