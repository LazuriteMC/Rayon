package dev.lazurite.rayon.impl.bullet.collision.space.supplier.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * This was created because {@link Minecraft} is not
 * allowed in server-side code.
 */
public interface ClientPlayerSupplier {
    static LocalPlayer get() {
        return Minecraft.getInstance().player;
    }
}
