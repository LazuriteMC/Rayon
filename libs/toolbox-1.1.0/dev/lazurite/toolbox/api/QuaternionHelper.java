package dev.lazurite.toolbox.api;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * A useful helper for dealing with Minecraft quaternions.
 */
public class QuaternionHelper {
    /**
     * Rotate the given {@link Quaternion} by the given number of degrees on the X axis.
     * @param quat the {@link Quaternion} to perform the operation on
     * @param deg number of degrees to rotate by
     */
    public static Quaternion rotateX(Quaternion quat, double deg) {
        var radHalfAngle = Math.toRadians(deg) / 2.0;
        quat.mul(new Quaternion((float) Math.sin(radHalfAngle), 0, 0, (float) Math.cos(radHalfAngle)));
        return quat;
    }

    /**
     * Rotate the given {@link Quaternion} by the given number of degrees on the Y axis.
     * @param quat the {@link Quaternion} to perform the operation on
     * @param deg number of degrees to rotate by
     */
    public static Quaternion rotateY(Quaternion quat, double deg) {
        var radHalfAngle = Math.toRadians(deg) / 2.0;
        quat.mul(new Quaternion(0, (float) Math.sin(radHalfAngle), 0, (float) Math.cos(radHalfAngle)));
        return quat;
    }

    /**
     * Rotate the given {@link Quaternion} by the given number of degrees on the Z axis.
     * @param quat the {@link Quaternion} to perform the operation on
     * @param deg number of degrees to rotate by
     */
    public static Quaternion rotateZ(Quaternion quat, double deg) {
        var radHalfAngle = Math.toRadians(deg) / 2.0;
        quat.mul(new Quaternion(0, 0, (float) Math.sin(radHalfAngle), (float) Math.cos(radHalfAngle)));
        return quat;
    }

    /**
     * Converts the given {@link Quaternion} to a vector containing three axes of rotation in degrees.
     * The order is (roll, pitch, yaw).
     * @param quat the {@link Quaternion} to extract the euler angles from
     * @return a new vector containing three rotations in degrees
     */
    public static Vector3f toEulerAngles(Quaternion quat) {
        final var q = new Quaternion(Quaternion.ONE);
        q.set(quat.i(), quat.j(), quat.k(), quat.r());

        var i = 0.0f;
        var j = 0.0f;
        var k = 0.0f;

        // roll (x-axis rotation)
        final var sinr_cosp = 2 * (q.r() * q.i() + q.j() * q.k());
        final var cosr_cosp = 1 - 2 * (q.i() * q.i() + q.j() * q.j());
        i = (float) Math.atan2(sinr_cosp, cosr_cosp);

        // pitch (y-axis rotation)
        final var sinp = 2 * (q.r() * q.j() - q.k() * q.i());
        if (Math.abs(sinp) >= 1) j = (float) Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else j = (float) Math.asin(sinp);

        // yaw (z-axis rotation)
        final var siny_cosp = 2 * (q.r() * q.k() + q.i() * q.j());
        final var cosy_cosp = 1 - 2 * (q.j() * q.j() + q.k() * q.k());
        k = (float) Math.atan2(siny_cosp, cosy_cosp);

        return new Vector3f(i, j ,k);
    }

    /**
     * Stores the given {@link Quaternion} into a new {@link CompoundTag}.
     * @param quat the {@link Quaternion} to store
     * @return the new {@link CompoundTag}
     */
    public static CompoundTag toTag(Quaternion quat) {
        final var tag = new CompoundTag();
        tag.putFloat("i", quat.i());
        tag.putFloat("j", quat.j());
        tag.putFloat("k", quat.k());
        tag.putFloat("r", quat.r());
        return tag;
    }

    /**
     * Retrieves a {@link Quaternion} from the given {@link CompoundTag}.
     * @param tag the {@link CompoundTag} to retrieve the {@link Quaternion} from
     * @return the new {@link Quaternion}
     */
    public static Quaternion fromTag(CompoundTag tag) {
        return new Quaternion(tag.getFloat("i"), tag.getFloat("j"), tag.getFloat("k"), tag.getFloat("r"));
    }

    /**
     * Serializes the given {@link Quaternion} into a {@link FriendlyByteBuf}.
     * @param buf  the {@link FriendlyByteBuf} to store the {@link Quaternion} in
     * @param quat the {@link Quaternion} to store
     */
    public static void toBuffer(FriendlyByteBuf buf, Quaternion quat) {
        buf.writeFloat(quat.i());
        buf.writeFloat(quat.j());
        buf.writeFloat(quat.k());
        buf.writeFloat(quat.r());
    }

    /**
     * Deserializes the given {@link FriendlyByteBuf} into a new {@link Quaternion}.
     * @param buf the {@link FriendlyByteBuf} to retrieve the {@link Quaternion} from
     * @return the new {@link Quaternion}
     */
    public static Quaternion fromBuffer(FriendlyByteBuf buf) {
        return new Quaternion(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    /**
     * Gets the yaw rotation from the given {@link Quaternion}.
     * @param quat the {@link Quaternion} to get the angle from
     * @return the yaw angle
     */
    public static float getYaw(Quaternion quat) {
        return -1 * (float) Math.toDegrees(QuaternionHelper.toEulerAngles(quat).z());
    }

    /**
     * Gets the pitch rotation from the given {@link Quaternion}.
     * @param quat the {@link Quaternion} to get the angle from
     * @return the pitch angle
     */
    public static float getPitch(Quaternion quat) {
        return (float) Math.toDegrees(QuaternionHelper.toEulerAngles(quat).y());
    }

    /**
     * Gets the roll rotation from the given {@link Quaternion}.
     * @param quat the {@link Quaternion} to get the angle from
     * @return the roll angle
     */
    public static float getRoll(Quaternion quat) {
        return (float) Math.toDegrees(QuaternionHelper.toEulerAngles(quat).x());
    }

    /**
     * Lerp, but for spherical stuff (hence Slerp).
     * @param q1 the first {@link Quaternion} to slerp
     * @param q2 the second {@link Quaternion} to slerp
     * @param t  the delta time
     * @return the slerped {@link Quaternion}
     */
    public static Quaternion slerp(Quaternion q1, Quaternion q2, float t) {
        q1.normalize();
        q2.normalize();

        if (q1.i() == q2.i() && q1.j() == q2.j() && q1.k() == q2.k() && q1.r() == q2.r()) {
            return new Quaternion(q1.i(), q1.j(), q1.k(), q1.r());
        }

        var result = (q1.i() * q2.i()) + (q1.j() * q2.j()) + (q1.k() * q2.k()) + (q1.r() * q2.r());

        if (result < 0.0f) {
            q2.set(-q2.i(), -q2.j(), -q2.k(), -q2.r());
            result = -result;
        }

        var scale0 = 1 - t;
        var scale1 = t;

        if ((1 - result) > 0.1f) {
            final var theta = (float) Math.acos(result);
            final var invSinTheta = 1f / (float) Math.sin(theta);

            scale0 = (float) Math.sin((1 - t) * theta) * invSinTheta;
            scale1 = (float) Math.sin((t * theta)) * invSinTheta;
        }

        final var out = new Quaternion(
                (scale0 * q1.i()) + (scale1 * q2.i()),
                (scale0 * q1.j()) + (scale1 * q2.j()),
                (scale0 * q1.k()) + (scale1 * q2.k()),
                (scale0 * q1.r()) + (scale1 * q2.r()));

        out.normalize();
        return out;
    }

    public static float dot(Quaternion q1, Quaternion q2) {
        return q1.i() * q2.i() +
                q1.j() * q2.j() +
                q1.k() * q2.k() +
                q1.r() * q2.r();
    }
}
