package dev.lazurite.rayon.impl.physics.helper.math;

import com.jme3.math.Quaternion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

/**
 * A helper class for {@link Quaternion} which contains methods to perform
 * many different operations.
 */
public class QuaternionHelper {
    /**
     * Rotate the given {@link Quaternion} by the given number of degrees on the X axis.
     * @param quat the {@link Quaternion} to perform the operation on
     * @param deg number of degrees to rotate by
     */
    public static Quaternion rotateX(Quaternion quat, double deg) {
        double radHalfAngle = Math.toRadians(deg) / 2.0;
        Quaternion rot = new Quaternion();
        rot.x = (float) Math.sin(radHalfAngle);
        rot.w = (float) Math.cos(radHalfAngle);
        quat.mul(rot);
        return quat;
    }

    /**
     * Rotate the given {@link Quaternion} by the given number of degrees on the Y axis.
     * @param quat the {@link Quaternion} to perform the operation on
     * @param deg number of degrees to rotate by
     */
    public static Quaternion rotateY(Quaternion quat, double deg) {
        double radHalfAngle = Math.toRadians(deg) / 2.0;
        Quaternion rot = new Quaternion();
        rot.y = (float) Math.sin(radHalfAngle);
        rot.w = (float) Math.cos(radHalfAngle);
        quat.mul(rot);
        return quat;
    }

    /**
     * Rotate the given {@link Quaternion} by the given number of degrees on the Z axis.
     * @param quat the {@link Quaternion} to perform the operation on
     * @param deg number of degrees to rotate by
     */
    public static Quaternion rotateZ(Quaternion quat, double deg) {
        double radHalfAngle = Math.toRadians(deg) / 2.0;
        Quaternion rot = new Quaternion();
        rot.z = (float) Math.sin(radHalfAngle);
        rot.w = (float) Math.cos(radHalfAngle);
        quat.mul(rot);
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
        q.set(quat.z, quat.x, quat.y, quat.w);

        Vector3f angles = new Vector3f();

        // roll (x-axis rotation)
        double sinr_cosp = 2 * (q.w * q.x + q.y * q.z);
        double cosr_cosp = 1 - 2 * (q.x * q.x + q.y * q.y);
        angles.x = (float) Math.atan2(sinr_cosp, cosr_cosp);

        // pitch (y-axis rotation)
        double sinp = 2 * (q.w * q.y - q.z * q.x);
        if (Math.abs(sinp) >= 1)
            angles.y = (float) Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else
            angles.y = (float) Math.asin(sinp);

        // yaw (z-axis rotation)
        double siny_cosp = 2 * (q.w * q.z + q.x * q.y);
        double cosy_cosp = 1 - 2 * (q.y * q.y + q.z * q.z);
        angles.z = (float) Math.atan2(siny_cosp, cosy_cosp);

        return angles;
    }

    /**
     * Converts a {@link Quaternion} to a {@link Quaternion}.
     * @param quat the {@link Quaternion} to convert
     * @return the new {@link Quaternion}
     */
    public static Quaternion QuaternionToQuaternion(Quaternion quat) {
        return new Quaternion(quat.x, quat.y, quat.z, quat.w);
    }

    /**
     * Converts a {@link Quaternion} to a {@link Quaternion}.
     * @param quat the {@link Quaternion} to convert
     * @return the new {@link Quaternion}
     */
    public static Quaternion quaternionToQuaternion(Quaternion quat) {
        Quaternion q = new Quaternion();
        q.x = quat.getX();
        q.y = quat.getY();
        q.z = quat.getZ();
        q.w = quat.getW();
        return q;
    }

    /**
     * Stores the given {@link Quaternion} into a new {@link CompoundTag}.
     * @param quat the {@link Quaternion} to store
     * @return the new {@link CompoundTag}
     */
    public static CompoundTag toTag(Quaternion quat) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x", quat.x);
        tag.putFloat("y", quat.y);
        tag.putFloat("z", quat.z);
        tag.putFloat("w", quat.w);
        return tag;
    }

    /**
     * Retrieves a {@link Quaternion} from the given {@link CompoundTag}.
     * @param tag the {@link CompoundTag} to retrieve the {@link Quaternion} from
     * @return the new {@link Quaternion}
     */
    public static Quaternion fromTag(CompoundTag tag) {
        return new Quaternion(
                tag.getFloat("x"),
                tag.getFloat("y"),
                tag.getFloat("z"),
                tag.getFloat("w")
        );
    }

    public static void toBuffer(PacketByteBuf buf, Quaternion Quaternion) {
        buf.writeFloat(Quaternion.x);
        buf.writeFloat(Quaternion.y);
        buf.writeFloat(Quaternion.z);
        buf.writeFloat(Quaternion.w);
    }

    public static Quaternion fromBuffer(PacketByteBuf buf) {
        return new Quaternion(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat());
    }

    /**
     * Gets the yaw rotation from the given {@link Quaternion}.
     * @param quat the {@link Quaternion} to get the angle from
     * @return the yaw angle
     */
    public static float getYaw(Quaternion quat) {
        return -1 * (float) Math.toDegrees(QuaternionHelper.toEulerAngles(quat).z);
    }

    /**
     * Gets the pitch rotation from the given {@link Quaternion}.
     * @param quat the {@link Quaternion} to get the angle from
     * @return the pitch angle
     */
    public static float getPitch(Quaternion quat) {
        return (float) Math.toDegrees(QuaternionHelper.toEulerAngles(quat).y);
    }

    /**
     * Lerp, but for spherical stuffs (hence Slerp).
     * @param q1 the first {@link Quaternion} to slerp
     * @param q2 the second {@link Quaternion} to slerp
     * @param t the delta time
     * @return the slerped {@link Quaternion}
     */
    public static Quaternion slerp(Quaternion q1, Quaternion q2, float t) {
        Quaternion out = new Quaternion();
        q1.normalize();
        q2.normalize();

        if (q1.x == q2.x && q1.y == q2.y && q1.z == q2.z && q1.w == q2.w) {
            out.set(q1);
            return out;
        }

        float result = (q1.x * q2.x) + (q1.y * q2.y) + (q1.z * q2.z) + (q1.w * q2.w);

        if (result < 0.0f) {
            q2.x = -q2.x;
            q2.y = -q2.y;
            q2.z = -q2.z;
            q2.w = -q2.w;
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

        out.x = (scale0 * q1.x) + (scale1 * q2.x);
        out.y = (scale0 * q1.y) + (scale1 * q2.y);
        out.z = (scale0 * q1.z) + (scale1 * q2.z);
        out.w = (scale0 * q1.w) + (scale1 * q2.w);

        out.normalize();
        return out;
    }

    public static Quaternion slerp2(Quaternion v0, Quaternion v1, float t) {
        v0.normalize();
        v1.normalize();
        float dot = (v0.x * v1.x) + (v0.y * v1.y) + (v0.z * v1.z) + (v0.w * v1.w);

        float DOT_THRESHOLD = 0.9995f;

        if (dot > DOT_THRESHOLD) {
            // If the inputs are too close for comfort, linearly interpolate
            // and normalize the result.

            Quaternion sub = new Quaternion();
            sub.sub(v1, v0);
            sub.scale(t);
            Quaternion result = new Quaternion();
            result.add(v0, sub);
            result.normalize();
            return result;
        }

        dot = MathHelper.clamp(dot, -1, 1);           // Robustness: Stay within domain of acos()
        float theta_0 = (float) Math.acos(dot);  // theta_0 = angle between input vectors
        float theta = theta_0*t;    // theta = angle between v0 and result

        Quaternion v2 = new Quaternion();
        Quaternion v01 = new Quaternion(v0);
        v01.scale(dot);
        v2.sub(v1, v01);
        v2.normalize();              // { v0, v2 } is now an orthonormal basis

        v0.scale((float) Math.cos(theta));
        v2.scale((float) Math.sin(theta));
        Quaternion out = new Quaternion();
        out.add(v0, v2);
        return out;
    }
}
