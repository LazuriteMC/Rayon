package dev.lazurite.rayon.core.api.event.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.util.debug.CollisionObjectDebugger;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;

/**
 * The events available through this class are:
 * <ul>
 *     <li><b>Before Render:</b> Called before each frame of the {@link CollisionObjectDebugger}</li>
 * </ul>
 * @since 1.3.0
 */
public class DebugRenderEvents extends Event {
    private final Context context;

    public DebugRenderEvents(Context context) {
        this.context = context;
    }

    public DebugRenderEvents(MinecraftSpace space, VertexConsumer vertices, PoseStack matrices, Vec3 cameraPos, float tickDelta){
        this.context = new Context(space, vertices, matrices, cameraPos, tickDelta);
    }

    public Context getContext() {
        return context;
    }

    public record Context(MinecraftSpace space, VertexConsumer vertices, PoseStack matrices, Vec3 cameraPos, float tickDelta) { }
}

