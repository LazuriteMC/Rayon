package dev.lazurite.rayon.physics.util;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;

public class TypedRigidBody extends RigidBody {
    private final BodyType type;

    public TypedRigidBody(RigidBodyConstructionInfo constructionInfo, BodyType type) {
        super(constructionInfo);
        this.type = type;
    }

    public BodyType getBodyType() {
        return this.type;
    }
}

