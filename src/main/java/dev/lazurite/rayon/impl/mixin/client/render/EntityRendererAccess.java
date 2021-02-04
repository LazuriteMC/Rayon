package dev.lazurite.rayon.impl.mixin.client.render;

import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class)
public interface EntityRendererAccess {
    @Accessor float getShadowRadius();
    @Accessor float getShadowOpacity();
}
