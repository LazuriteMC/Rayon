package dev.lazurite.rayon.impl.physics.body.shape;

import com.google.common.collect.Lists;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.Convex2dShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.shape.EntityShapeFactory;
import dev.lazurite.rayon.impl.transporter.api.Disassembler;
import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import dev.lazurite.rayon.impl.transporter.impl.pattern.part.Quad;
import dev.lazurite.rayon.impl.util.math.VectorHelper;

import java.util.List;

/**
 * This collision shape is unique in that it uses rendered vertices to
 * build a shape out of quads that represents exactly what you see on-screen.
 * In doing so, it relies on clients being able to provide that information
 * at runtime.
 *
 * @see Pattern
 * @see CompoundCollisionShape
 */
public class PatternShape extends CompoundCollisionShape {
    private final Pattern pattern;

    public PatternShape(Pattern pattern) {
        this(pattern, true);
    }

    public PatternShape(Pattern pattern, boolean translate) {
        this.pattern = pattern;

        for (Quad quad : pattern.getQuads()) {
            List<Vector3f> points = Lists.newArrayList();
            quad.getPoints().forEach(vector -> points.add(VectorHelper.vec3dToVector3f(vector)));
            addChildShape(new Convex2dShape(new HullCollisionShape(points)), new Transform());
        }

        if (translate) {
            BoundingBox box = boundingBox(new Vector3f(), new Quaternion(), new BoundingBox());
            this.translate(new Vector3f(-box.getXExtent() / 2.0f, -box.getYExtent() / 2.0f, -box.getZExtent() / 2.0f));
        }
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public static EntityShapeFactory getFactory() {
        return (entity) -> {
            if (entity.getEntityWorld().isClient()) {
                return new PatternShape(Disassembler.getEntity(entity, entity.getEntityWorld()));
            } else {
                return new BoundingBoxShape(entity.getBoundingBox());
            }
        };
    }
}
