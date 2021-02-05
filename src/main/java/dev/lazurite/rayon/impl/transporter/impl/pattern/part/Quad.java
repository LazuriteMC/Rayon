package dev.lazurite.rayon.impl.transporter.impl.pattern.part;

import com.google.common.collect.Lists;
import net.minecraft.network.PacketByteBuf;
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

    public void serialize(PacketByteBuf buf) {
        for (Vec3d point : getPoints()) {
            buf.writeDouble(point.getX());
            buf.writeDouble(point.getY());
            buf.writeDouble(point.getZ());
        }
    }

    public static Quad deserialize(PacketByteBuf buf) {
        List<Vec3d> points = Lists.newArrayList();

        for (int j = 0; j < 4; j++) {
            points.add(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }

        return new Quad(points);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Quad) {
            return ((Quad) obj).getPoints().equals(getPoints());
        }

        return false;
    }
}

