package dev.lazurite.rayon.impl.transporter.impl.pattern.part;

import com.google.common.collect.Lists;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Quad {
    private final Vec3d p1;
    private final Vec3d p2;
    private final Vec3d p3;
    private final Vec3d p4;

    public Quad(List<Vec3d> points) {
        this(points.get(0), points.get(1), points.get(2), points.get(3));
    }

    public Quad(Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    public List<Vec3d> getPoints() {
        return Lists.newArrayList(p1, p2, p3, p4);
    }

    public Vec3d getCenterPoint() {
        double x = (p1.getX() + p2.getX() + p3.getX() + p4.getX()) / 4.0f;
        double y = (p1.getY() + p2.getY() + p3.getY() + p4.getY()) / 4.0f;
        double z = (p1.getZ() + p2.getZ() + p3.getZ() + p4.getZ()) / 4.0f;
        return new Vec3d(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Quad) {
            return getCenterPoint().equals(((Quad) obj).getCenterPoint());
        }

        return false;
    }
}

