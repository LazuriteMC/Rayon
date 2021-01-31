package dev.lazurite.rayon.impl.physics.body.shape;

import com.google.common.collect.Lists;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.transporter.Pattern;
import dev.lazurite.rayon.impl.util.helper.math.VectorHelper;

import java.util.List;

public class CompoundQuadShape extends CompoundCollisionShape {
    public CompoundQuadShape(Pattern pattern) {
        for (Pattern.Quad quad : pattern.getQuads()) {
            List<Vector3f> points = Lists.newArrayList();
            quad.getPoints().forEach(vector -> points.add(VectorHelper.minecraftToBullet(vector)));
            addQuad(points);
        }
    }

    public void addQuad(List<Vector3f> points) {
        addChildShape(new HullCollisionShape(points), new Transform());
    }
}
