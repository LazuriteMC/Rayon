package dev.lazurite.rayon.impl.util.math;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

/**
 * A helper class for quaternion objects which contains useful
 * operations that don't come with bullet (or minecraft).
 */
public class QuaternionHelper {
    /**
     * Rotate the given {@link Quaternion} by the given number of degrees on the X axis.
     * @param quat the {@link Quaternion} to perform the operation on
     * @param deg  number of degrees to rotate by
     */
    public static Quaternion rotateX(Quaternion quat, double deg) {
        double radHalfAngle = Math.toRadians(deg) / 2.0;
        quat.set(quat.mult(new Quaternion((float) Math.sin(radHalfAngle), 0, 0, (float) Math.cos(radHalfAngle))));
        return quat;
    }

    /**
     * Rotate the given {@link Quaternion} by the given number of degrees on the Y axis.
     * @param quat the {@link Quaternion} to perform the operation on
     * @param deg  number of degrees to rotate by
     */
    public static Quaternion rotateY(Quaternion quat, double deg) {
        double radHalfAngle = Math.toRadians(deg) / 2.0;
        quat.set(quat.mult(new Quaternion(0, (float) Math.sin(radHalfAngle), 0, (float) Math.cos(radHalfAngle))));
        return quat;
    }

    /**
     * Rotate the given {@link Quaternion} by the given number of degrees on the Z axis.
     * @param quat the {@link Quaternion} to perform the operation on
     * @param deg  number of degrees to rotate by
     */
    public static Quaternion rotateZ(Quaternion quat, double deg) {
        double radHalfAngle = Math.toRadians(deg) / 2.0;
        quat.set(quat.mult(new Quaternion(0, 0, (float) Math.sin(radHalfAngle), (float) Math.cos(radHalfAngle))));
        return quat;
    }

    /**
     * Converts the given {@link Quaternion} to a vector containing three axes of rotation in degrees.
     * The order is (roll, pitch, yaw).
     * @param quat the {@link Quaternion} to extract the euler angles from
     * @return a new vector containing three rotations in degrees
     */
    public static Vector3f toEulerAngles(Quaternion quat) {
        Quaternion q = new Quaternion();
        q.set(quat.getZ(), quat.getX(), quat.getY(), quat.getW());

        Vector3f angles = new Vector3f();

        // roll (x-axis rotation)
        double sinr_cosp = 2 * (q.getW() * q.getX() + q.getY() * q.getZ());
        double cosr_cosp = 1 - 2 * (q.getX() * q.getX() + q.getY() * q.getY());
        angles.x = (float) Math.atan2(sinr_cosp, cosr_cosp);

        // pitch (y-axis rotation)
        double sinp = 2 * (q.getW() * q.getY() - q.getZ() * q.getX());
        if (Math.abs(sinp) >= 1)
            angles.y = (float) Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else
            angles.y = (float) Math.asin(sinp);

        // yaw (z-axis rotation)
        double siny_cosp = 2 * (q.getW() * q.getZ() + q.getX() * q.getY());
        double cosy_cosp = 1 - 2 * (q.getY() * q.getY() + q.getZ() * q.getZ());
        angles.z = (float) Math.atan2(siny_cosp, cosy_cosp);

        return angles;
    }

    /**
     * Converts a {@link net.minecraft.util.math.Quaternion} to a {@link Quaternion}.
     * @param quat the {@link net.minecraft.util.math.Quaternion} to convert
     * @return the new {@link Quaternion}
     */
    public static Quaternion minecrafToBullet(net.minecraft.util.math.Quaternion quat) {
        return new Quaternion(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    /**
     * Converts a {@link Quaternion} to a {@link net.minecraft.util.math.Quaternion}.
     * @param quat the {@link Quaternion} to convert
     * @return the new {@link net.minecraft.util.math.Quaternion}
     */
    public static net.minecraft.util.math.Quaternion bulletToMinecraft(Quaternion quat) {
        return new net.minecraft.util.math.Quaternion(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    /**
     * Stores the given {@link Quaternion} into a new {@link CompoundTag}.
     * @param quat the {@link Quaternion} to store
     * @return the new {@link CompoundTag}
     */
    public static CompoundTag toTag(Quaternion quat) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x", quat.getX());
        tag.putFloat("y", quat.getY());
        tag.putFloat("z", quat.getZ());
        tag.putFloat("w", quat.getW());
        return tag;
    }

    /**
     * Retrieves a {@link Quaternion} from the given {@link CompoundTag}.
     * @param tag the {@link CompoundTag} to retrieve the {@link Quaternion} from
     * @return the new {@link Quaternion}
     */
    public static Quaternion fromTag(CompoundTag tag) {
        return new Quaternion(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("z"), tag.getFloat("w"));
    }

    /**
     * Serializes the given {@link Quaternion} into a {@link PacketByteBuf}.
     * @param buf the {@link PacketByteBuf} to store the {@link Quaternion} in
     * @param quat the {@link Quaternion} to store
     */
    public static void toBuffer(PacketByteBuf buf, Quaternion quat) {
        buf.writeFloat(quat.getX());
        buf.writeFloat(quat.getY());
        buf.writeFloat(quat.getZ());
        buf.writeFloat(quat.getW());
    }

    /**
     * Deserializes the given {@link PacketByteBuf} into a new {@link Quaternion}.
     * @param buf the {@link PacketByteBuf} to retrieve the {@link Quaternion} from
     * @return the new {@link Quaternion}
     */
    public static Quaternion fromBuffer(PacketByteBuf buf) {
        return new Quaternion(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    /**
     * Gets the yaw rotation from the given {@link Quaternion}.
     *
     * @param quat the {@link Quaternion} to get the angle from
     * @return the yaw angle
     */
    public static float getYaw(Quaternion quat) {
        return -1 * (float) Math.toDegrees(QuaternionHelper.toEulerAngles(quat).z);
    }

    /**
     * Gets the pitch rotation from the given {@link Quaternion}.
     *
     * @param quat the {@link Quaternion} to get the angle from
     * @return the pitch angle
     */
    public static float getPitch(Quaternion quat) {
        return (float) Math.toDegrees(QuaternionHelper.toEulerAngles(quat).y);
    }

    /**
     * Lerp, but for spherical stuffs (hence Slerp).
     * @param q1 the first {@link net.minecraft.util.math.Quaternion} to slerp
     * @param q2 the second {@link net.minecraft.util.math.Quaternion} to slerp
     * @param t the delta time
     * @return the slerped {@link net.minecraft.util.math.Quaternion}
     */
    public static Quaternion slerp(Quaternion q1, Quaternion q2, float t) {
        q1.normalizeLocal();
        q2.normalizeLocal();

        if (q1.getX() == q2.getX() && q1.getY() == q2.getY() && q1.getZ() == q2.getZ() && q1.getW() == q2.getW()) {
            return new Quaternion(q1.getX(), q1.getY(), q1.getZ(), q1.getW());
        }

        float result = (q1.getX() * q2.getX()) + (q1.getY() * q2.getY()) + (q1.getZ() * q2.getZ()) + (q1.getW() * q2.getW());

        if (result < 0.0f) {
            q2.set(-q2.getX(), -q2.getY(), -q2.getZ(), -q2.getW());
            result = -result;
        }

        float scale0 = 1 - t;
        float scale1 = t;

        if ((1 - result) > 0.1f) {
            float theta = (float) Math.acos(result);
            float invSinTheta = 1f / (float) Math.sin(theta);

            scale0 = (float) Math.sin((1 - t) * theta) * invSinTheta;
            scale1 = (float) Math.sin((t * theta)) * invSinTheta;
        }

        Quaternion out = new Quaternion(
                (scale0 * q1.getX()) + (scale1 * q2.getX()),
                (scale0 * q1.getY()) + (scale1 * q2.getY()),
                (scale0 * q1.getZ()) + (scale1 * q2.getZ()),
                (scale0 * q1.getW()) + (scale1 * q2.getW()));
        out.normalizeLocal();
        return out;
    }
}