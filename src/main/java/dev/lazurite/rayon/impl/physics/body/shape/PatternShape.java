package dev.lazurite.rayon.impl.physics.body.shape;

import com.google.common.collect.Lists;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.transporter.Pattern;
import dev.lazurite.rayon.impl.transporter.PatternType;
import dev.lazurite.rayon.impl.util.helper.math.VectorHelper;

import java.util.List;

public class PatternShape extends CompoundCollisionShape {
    public PatternShape(Pattern pattern) {
        int i = 0;

        for (Pattern.Quad quad : pattern.getQuads()) {
            if (pattern.getType().equals(PatternType.ITEM) && i < 3) {
                ++i;
                continue;
            }

            List<Vector3f> points = Lists.newArrayList();
            quad.getPoints().forEach(vector -> points.add(VectorHelper.minecraftToBullet(vector)));
            addChildShape(new HullCollisionShape(points), new Transform());
        }
    }
}
