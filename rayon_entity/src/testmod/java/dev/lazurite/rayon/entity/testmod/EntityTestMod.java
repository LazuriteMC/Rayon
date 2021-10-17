package dev.lazurite.rayon.entity.testmod;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.api.event.collision.CollisionEvent;
import dev.lazurite.rayon.core.impl.bullet.collision.body.TerrainObject;
import dev.lazurite.rayon.entity.testmod.client.render.StoneBlockEntityModel;
import dev.lazurite.rayon.entity.testmod.client.render.StoneBlockEntityRenderer;
import dev.lazurite.rayon.entity.testmod.common.entity.EntityTestEntities;
import dev.lazurite.rayon.entity.testmod.common.entity.StoneBlockEntity;
import dev.lazurite.rayon.entity.testmod.common.item.EntityTestItems;
import dev.lazurite.rayon.entity.testmod.common.item.WandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitialize);
        LOGGER.info("Rayon Entity Test Mod activated");
    }

    @SubscribeEvent
    public void onInitialize(FMLCommonSetupEvent event) {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.register(this);
        BLUE_DIRT = new Block(BlockBehaviour.Properties.of(Material.METAL)
                .strength(1.0F))
                .setRegistryName(new ResourceLocation(MODID, "blue_dirt"));
        EntityTestEntities.ENTITIES.register(bus);
        EntityTestItems.ITEMS.register(bus);


        /*STONE_BLOCK_ENTITY = Registry.register(
                Registry.ENTITY_TYPE,
                new ResourceLocation(MODID, "stone_block_entity"),
                FabricEntityTypeBuilder.createLiving()
                        .entityFactory(StoneBlockEntity::new)
                        .spawnGroup(MobCategory.AMBIENT.MISC)
                        .defaultAttributes(LivingEntity::createLivingAttributes)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(80)
                        .build()
        );*/

        /*BLUE_DIRT = Registry.register(
                Registry.BLOCK,
                new ResourceLocation(MODID, "blue_dirt"),
                new Block(FabricBlockSettings.of(Material.METAL).hardness(1.0f)));*/

        /*Registry.register(
                Registry.ITEM,
                new ResourceLocation(MODID, "blue_dirt"),
                new BlockItem(BLUE_DIRT, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));*/

        /* An example of a block collision event */
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

    /*
    public void onInitializeClient(FMLClientSetupEvent event) {
        EntityRendererRegistry.register(EntityTestMod.STONE_BLOCK_ENTITY, (context) -> new StoneBlockEntityRenderer(context, new StoneBlockEntityModel(8)));
    }*/

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RendererRegister {
        @SubscribeEvent
        public static void onRendererRegister(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityTestEntities.STONE_BLOCK_ENTITY.get(), (context) -> new StoneBlockEntityRenderer(context, new StoneBlockEntityModel(8)));
        }

    }
}
