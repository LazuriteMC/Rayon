package dev.lazurite.rayon.core.api.event;

import dev.lazurite.rayon.core.impl.physics.debug.CollisionObjectDebugger;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.BufferBuilder;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Before Render:</b> Called before each frame of the {@link CollisionObjectDebugger}</li>
 * </ul>
 * @since 1.3.0
 */
public class DebugRenderEvents {
    public static final Event<BeforeRender> BEFORE_RENDER = EventFactory.createArrayBacked(BeforeRender.class, (callbacks) -> (context) -> {
//        for (BeforeRender event : callbacks) {
//            event.onRender(new Context(space, builder, tickDelta));
//        }
    });

    private DebugRenderEvents() { }

    @FunctionalInterface
    public interface BeforeRender {
        void onRender(Context context);
    }

    public record Context(MinecraftSpace space, BufferBuilder builder, float tickDelta) {

    }
}

