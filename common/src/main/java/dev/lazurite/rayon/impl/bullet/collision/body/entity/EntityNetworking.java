package dev.lazurite.rayon.impl.bullet.collision.body.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;

public interface EntityNetworking {
    @ExpectPlatform
    static void sendMovement(EntityRigidBody rigidBody) {
        throw new AssertionError();
    }

    @ExpectPlatform
    static void sendProperties(EntityRigidBody rigidBody) {
        throw new AssertionError();
    }
}