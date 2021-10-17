package dev.lazurite.rayon.entity.testmod;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.api.event.collision.CollisionEvent;
import dev.lazurite.rayon.core.impl.bullet.collision.body.TerrainObject;
import dev.lazurite.rayon.entity.testmod.client.render.StoneBlockEntityModel;
import dev.lazurite.rayon.entity.testmod.client.render.StoneBlockEntityRenderer;
import dev.lazurite.rayon.entity.testmod.common.entity.EntityTestEntities;
import dev.lazurite.rayon.entity.testmod.common.entity.StoneBlockEntity;
import dev.lazurite.rayon.entity.testmod.common.item.EntityTestItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EntityTestMod.MODID)
public class EntityTestMod {
    public static final String MODID = "testmod_entity";
    public static final Logger LOGGER = LogManager.getLogger("Rayon Entity Test Mod");

    public static Block BLUE_DIRT;

    public EntityTestMod() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLUE_DIRT = new Block(BlockBehaviour.Properties.of(Material.METAL)
                .strength(1.0F))
                .setRegistryName(new ResourceLocation(MODID, "blue_dirt"));
        EntityTestEntities.ENTITIES.register(bus);
        EntityTestItems.ITEMS.register(bus);
        LOGGER.info("Rayon Entity Test Mod activated");
    }

    @SubscribeEvent
    public void onTerrailCollision(CollisionEvent.TerrainCollisionEvent event) {
        PhysicsElement element = event.getElementA();
        TerrainObject terrainObject = event.getTerrainObject();
        float impulse = event.getImpulse();
        if (element instanceof StoneBlockEntity) {
            terrainObject.getBlockState().ifPresent(blockState -> {
                if (blockState.getBlock().equals(Blocks.BRICKS)) {
                    LOGGER.info("Touching bricks!!" + impulse);
                    ((StoneBlockEntity) element).kill();
                }
            });
        }
    }

    @SubscribeEvent
    public void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
        blockRegistryEvent.getRegistry().register(BLUE_DIRT);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RendererRegister {
        @SubscribeEvent
        public static void onRendererRegister(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityTestEntities.STONE_BLOCK_ENTITY.get(), (context) -> new StoneBlockEntityRenderer(context, new StoneBlockEntityModel(8)));
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Initializer{
        @SubscribeEvent
        public static void setup(EntityAttributeCreationEvent event){
            event.put(EntityTestEntities.STONE_BLOCK_ENTITY.get(), LivingEntity.createLivingAttributes().build());
        }
    }
}
