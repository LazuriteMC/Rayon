package dev.lazurite.rayon.impl.physics.body.type;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.lazurite.rayon.impl.physics.body.BlockRigidBody;
import dev.lazurite.rayon.impl.physics.body.EntityRigidBody;
import dev.lazurite.rayon.impl.transporter.api.pattern.PatternBuffer;
import net.minecraft.util.Identifier;

/**
 * Allows {@link PhysicsRigidBody}s to provide identifiers so that
 * their vertex information can be identified later-on in the
 * Transporter {@link PatternBuffer}.
 *
 * @see PatternBuffer
 * @see BlockRigidBody
 * @see EntityRigidBody
 */
public interface IdentifierBody {
    Identifier getIdentifier();
}
