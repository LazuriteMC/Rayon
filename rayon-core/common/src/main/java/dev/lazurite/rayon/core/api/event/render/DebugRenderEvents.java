package dev.lazurite.rayon.core.api.event.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.lazurite.rayon.core.impl.util.debug.CollisionObjectDebugger;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.phys.Vec3;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Before Render:</b> Called before each frame of the {@link CollisionObjectDebugger}</li>
 * </ul>
 * @since 1.3.0
 */
public class DebugRenderEvents {
    public static final Event<BeforeRender> BEFORE_RENDER = EventFactory.of((callbacks) -> (context) -> {
        for (final var event : callbacks) {
            event.onRender(context);
        }
    });

    private DebugRenderEvents() { }

    @FunctionalInterface
    public interface BeforeRender {
        void onRender(Context context);
    }

    public record Context(MinecraftSpace space, VertexConsumer vertices, PoseStack matrices, Vec3 cameraPos, float tickDelta) { }
}

