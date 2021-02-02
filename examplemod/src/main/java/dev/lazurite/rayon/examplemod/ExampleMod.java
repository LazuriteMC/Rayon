package dev.lazurite.rayon.examplemod;

import com.google.common.collect.Lists;
import com.jme3.bullet.collision.shapes.Box2dShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import dev.lazurite.rayon.api.builder.RigidBodyBuilder;
import dev.lazurite.rayon.api.builder.RigidBodyRegistry;
import dev.lazurite.rayon.api.event.EntityRigidBodyEvents;
import dev.lazurite.rayon.examplemod.entity.RectangularPrismEntity;
import dev.lazurite.rayon.examplemod.item.WandItem;
import dev.lazurite.rayon.examplemod.render.RectangularPrismEntityRenderer;
import dev.lazurite.rayon.impl.physics.body.shape.PatternShape;
import dev.lazurite.rayon.impl.transporter.Disassembler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ExampleMod implements ModInitializer, ClientModInitializer {
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogManager.getLogger("Rayon Example Mod");

    public static WandItem WAND_ITEM;
    public static EntityType<RectangularPrismEntity> RECTANGULAR_PRISM_ENTITY;

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(ExampleMod.RECTANGULAR_PRISM_ENTITY, (entityRenderDispatcher, context) -> new RectangularPrismEntityRenderer(entityRenderDispatcher));
    }

    @Override
    public void onInitialize() {
        WAND_ITEM = Registry.register(
                Registry.ITEM,
                new Identifier(MODID, "wand_item"),
                new WandItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC)));

        RECTANGULAR_PRISM_ENTITY = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(MODID, "rectangular_prism_entity"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, RectangularPrismEntity::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 1.0f)) // (8/16 x 16/16)
                        .trackedUpdateRate(3)
                        .trackRangeBlocks(80)
                        .forceTrackedVelocityUpdates(true)
                        .build()
        );

        /* An example of using the builder and registering the built rigid body entry */
        RigidBodyRegistry.register(
                RigidBodyBuilder.create(RectangularPrismEntity.class)
                    .setMass(2.0f)
                    .setDrag(0.05f)
                    .setFriction(0.5f)
                    .setRestitution(0.75f)
                    .build());

        /* An example of a block collision event */
        EntityRigidBodyEvents.BLOCK_COLLISION.register((entityBody, blockBody) -> {
            if (!entityBody.getDynamicsWorld().getWorld().isClient()) {
                if (blockBody.getBlockState().getBlock().equals(Blocks.BRICKS)) {
                    LOGGER.info("Touching bricks!!");
                    entityBody.getEntity().kill();
                }
            }
        });

        EntityRigidBodyEvents.ENTITY_BODY_LOAD.register((body, world) -> {
            body.setCollisionShape(new PatternShape(Disassembler.ItemPattern.getPattern(new ItemStack(Items.DIAMOND))));

//            List<Vector3f> points = Disassembler.getItemPattern(new ItemStack(Items.DIAMOND)).getPoints();
//            List<com.jme3.math.Vector3f> points2 = Lists.newArrayList();
//            points.forEach(point -> points2.add(new com.jme3.math.Vector3f(point.getX(), point.getY(), point.getZ())));
//            body.setCollisionShape(new HullCollisionShape(points2));
            // THONK
//            body.setCollisionShape(new HullCollisionShape(Lists.newArrayList(
//                    new Vector3f(0, 1, 0.01f),
//                    new Vector3f(1, 1, 0.01f),
//                    new Vector3f(1, 0, 0.01f),
//                    new Vector3f(0, 0, 0.01f)
//            )));
//            body.setCollisionShape(new Box2dShape(0.25f, 0.25f));
//            body.setCollisionShape(new BoxCollisionShape(0.25f, 0.25f, 0.25f));

//            body.setCollisionShape(new SimplexCollisionShape(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0)));
        });
    }
}
