package dev.lazurite.rayon.impl.transporter.disassembler;

import dev.lazurite.rayon.impl.transporter.Pattern;
import dev.lazurite.rayon.impl.transporter.PatternC2S;
import dev.lazurite.rayon.impl.transporter.PatternType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.registry.Registry;

public interface EntityDisassembler {
    static Pattern getPattern(Entity entity) {
        Pattern pattern = new Pattern(PatternType.ENTITY);
        MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity)
                .render(entity, 0, 0, new MatrixStack(), pattern.asProvider(), 0);
        PatternC2S.send(Registry.ENTITY_TYPE.getId(entity.getType()), pattern);
        return pattern;
    }

    static Pattern getPattern(EntityModel<?> model) {
        Pattern pattern = new Pattern(PatternType.ENTITY);
        model.render(new MatrixStack(), pattern, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f);
        return pattern;
    }
}
