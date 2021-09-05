package dev.lazurite.rayon.core.api.event.render;

import dev.lazurite.rayon.core.impl.util.debug.CollisionObjectDebugger;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Before Render:</b> Called before each frame of the {@link CollisionObjectDebugger}</li>
 * </ul>
 * @since 1.3.0
 */
public class DebugRenderEvents {
    public static final Event<BeforeRender> BEFORE_RENDER = EventFactory.createArrayBacked(BeforeRender.class, (callbacks) -> (context) -> {
        for (BeforeRender event : callbacks) {
            event.onRender(context);
        }
    });

    private DebugRenderEvents() { }

    @FunctionalInterface
    public interface BeforeRender {
        void onRender(Context context);
    }

    public record Context(MinecraftSpace space, VertexConsumer vertices, MatrixStack matrices, Vec3d cameraPos, float tickDelta) { }
}

