package dev.lazurite.rayon.core.impl.util.model;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class AxisAligned extends BoundingBox {
    public final Vector3f topNorthWest;
    public final Vector3f topNorthEast;
    public final Vector3f topSouthWest;
    public final Vector3f topSouthEast;
    public final Vector3f bottomNorthWest;
    public final Vector3f bottomNorthEast;
    public final Vector3f bottomSouthWest;
    public final Vector3f bottomSouthEast;

    /*
    -Z : North
    +Z : South
    -X : West
    +X : East
     */

    public AxisAligned(BoundingBox boundingBox, Quaternion orientation) {
        super(boundingBox.getMin(new Vector3f()), boundingBox.getMax(new Vector3f()));

        topNorthWest = new Vector3f(
                -boundingBox.getXExtent(),
                boundingBox.getYExtent(),
                -boundingBox.getZExtent()
        );

        topNorthEast = new Vector3f(
                boundingBox.getXExtent(),
                boundingBox.getYExtent(),
                -boundingBox.getZExtent()
        );

        topSouthWest = new Vector3f(
                -boundingBox.getXExtent(),
                boundingBox.getYExtent(),
                boundingBox.getZExtent()
        );

        topSouthEast = new Vector3f(
                boundingBox.getXExtent(),
                boundingBox.getYExtent(),
                boundingBox.getZExtent()
        );

        bottomNorthWest = new Vector3f(
                -boundingBox.getXExtent(),
                -boundingBox.getYExtent(),
                -boundingBox.getZExtent()
        );

        bottomNorthEast = new Vector3f(
                boundingBox.getXExtent(),
                -boundingBox.getYExtent(),
                -boundingBox.getZExtent()
        );

        bottomSouthWest = new Vector3f(
                -boundingBox.getXExtent(),
                -boundingBox.getYExtent(),
                boundingBox.getZExtent()
        );

        bottomSouthEast = new Vector3f(
                boundingBox.getXExtent(),
                -boundingBox.getYExtent(),
                boundingBox.getZExtent()
        );
    }
}
