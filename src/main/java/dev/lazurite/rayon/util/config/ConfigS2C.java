package dev.lazurite.rayon.util.config;

import dev.lazurite.rayon.Rayon;
import dev.lazurite.rayon.physics.helper.AirHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConfigS2C {
    public static final Identifier PACKET_ID = new Identifier(Rayon.MODID, "config_s2c");

    public static void accept(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        boolean isRemote = buf.readBoolean();
        float gravity = buf.readFloat();
        int blockDistance = buf.readInt();
        int stepRate = buf.readInt();
        float airDensity = buf.readFloat();
        AirHelper.Type airResistanceType = buf.readEnumConstant(AirHelper.Type.class);

        client.execute(() -> {
            Config.INSTANCE.isRemote = isRemote;
            Config.INSTANCE.gravity = gravity;
            Config.INSTANCE.blockDistance = blockDistance;
            Config.INSTANCE.stepRate = stepRate;
            Config.INSTANCE.airDensity = airDensity;
            Config.INSTANCE.airResistanceType = airResistanceType;
        });
    }

    public static void send(ServerPlayerEntity player, Config config) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeBoolean(player.getServer().isRemote());
        buf.writeFloat(config.gravity);
        buf.writeInt(config.blockDistance);
        buf.writeInt(config.stepRate);
        buf.writeFloat(config.airDensity);
        buf.writeEnumConstant(config.airResistanceType);

        ServerPlayNetworking.send(player, PACKET_ID, buf);
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PACKET_ID, ConfigS2C::accept);
    }
}
