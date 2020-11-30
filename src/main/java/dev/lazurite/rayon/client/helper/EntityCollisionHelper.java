package dev.lazurite.rayon.client.helper;

import net.minecraft.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class EntityCollisionHelper {
    public static Map<EntityType<?>, Float> entities;

    public static void register() {
        entities = new HashMap<>();
//        entities.put(EntityType.AREA_EFFECT_CLOUD, 0.0f);
//        entities.put(EntityType.ARROW, 0.0f);
        entities.put(EntityType.ARMOR_STAND, 5.0f);
        entities.put(EntityType.BAT, 0.5f);
        entities.put(EntityType.BEE, 1.2f); // O.o
        entities.put(EntityType.BLAZE, 50f);
        entities.put(EntityType.BOAT, 22f);
        entities.put(EntityType.CAT, 3.5f);
        entities.put(EntityType.CAVE_SPIDER, 10f);
        entities.put(EntityType.CHICKEN, 0.6f);
        entities.put(EntityType.COD, 30f);
        entities.put(EntityType.COW, 600f);
        entities.put(EntityType.CREEPER, 50f);
        entities.put(EntityType.DOLPHIN, 70f);
        entities.put(EntityType.DONKEY, 200f);
        entities.put(EntityType.DROWNED, 70f);
        entities.put(EntityType.ELDER_GUARDIAN, 1000f);
        entities.put(EntityType.ENDER_DRAGON, 2000f);
        entities.put(EntityType.ENDERMAN, 90f);
        entities.put(EntityType.ENDERMITE, 1.0f);
        entities.put(EntityType.EVOKER, 70f);
        entities.put(EntityType.FOX, 7.0f);
        entities.put(EntityType.GHAST, 1000f);
        entities.put(EntityType.GIANT, 1000f);
        entities.put(EntityType.GUARDIAN, 500f);
        entities.put(EntityType.HOGLIN, 150f);
        entities.put(EntityType.HORSE, 450f);
        entities.put(EntityType.HUSK, 70f);
        entities.put(EntityType.ILLUSIONER, 70f);
        entities.put(EntityType.IRON_GOLEM, 4000f);
        entities.put(EntityType.ITEM, 0.5f);

        entities.put(EntityType.SPIDER, 15f);
    }
//    public static final EntityType<ArrowEntity> ARROW;
//    public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL;
//    public static final EntityType<EndCrystalEntity> END_CRYSTAL;
//    public static final EntityType<EvokerFangsEntity> EVOKER_FANGS;
//    public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB;
//    public static final EntityType<EyeOfEnderEntity> EYE_OF_ENDER;
//    public static final EntityType<FallingBlockEntity> FALLING_BLOCK;
//    public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET;
//    public static final EntityType<ItemFrameEntity> ITEM_FRAME;
//    public static final EntityType<FireballEntity> FIREBALL;
//    public static final EntityType<LeashKnotEntity> LEASH_KNOT;
//    public static final EntityType<LightningEntity> LIGHTNING_BOLT;
//    public static final EntityType<LlamaSpitEntity> LLAMA_SPIT;

//    public static final EntityType<LlamaEntity> LLAMA;
//    public static final EntityType<MagmaCubeEntity> MAGMA_CUBE;
//    public static final EntityType<MinecartEntity> MINECART;
//    public static final EntityType<ChestMinecartEntity> CHEST_MINECART;
//    public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART;
//    public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART;
//    public static final EntityType<HopperMinecartEntity> HOPPER_MINECART;
//    public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART;
//    public static final EntityType<TntMinecartEntity> TNT_MINECART;
//    public static final EntityType<MuleEntity> MULE;
//    public static final EntityType<MooshroomEntity> MOOSHROOM;
//    public static final EntityType<OcelotEntity> OCELOT;
//    public static final EntityType<PaintingEntity> PAINTING;
//    public static final EntityType<PandaEntity> PANDA;
//    public static final EntityType<ParrotEntity> PARROT;
//    public static final EntityType<PhantomEntity> PHANTOM;
//    public static final EntityType<PigEntity> PIG;
//    public static final EntityType<PiglinEntity> PIGLIN;
//    public static final EntityType<PiglinBruteEntity> PIGLIN_BRUTE;
//    public static final EntityType<PillagerEntity> PILLAGER;
//    public static final EntityType<PolarBearEntity> POLAR_BEAR;
//    public static final EntityType<TntEntity> TNT;
//    public static final EntityType<PufferfishEntity> PUFFERFISH;
//    public static final EntityType<RabbitEntity> RABBIT;
//    public static final EntityType<RavagerEntity> RAVAGER;
//    public static final EntityType<SalmonEntity> SALMON;
//    public static final EntityType<SheepEntity> SHEEP;
//    public static final EntityType<ShulkerEntity> SHULKER;
//    public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET;
//    public static final EntityType<SilverfishEntity> SILVERFISH;
//    public static final EntityType<SkeletonEntity> SKELETON;
//    public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE;
//    public static final EntityType<SlimeEntity> SLIME;
//    public static final EntityType<SmallFireballEntity> SMALL_FIREBALL;
//    public static final EntityType<SnowGolemEntity> SNOW_GOLEM;
//    public static final EntityType<SnowballEntity> SNOWBALL;
//    public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW;
//    public static final EntityType<SpiderEntity> SPIDER;
//    public static final EntityType<SquidEntity> SQUID;
//    public static final EntityType<StrayEntity> STRAY;
//    public static final EntityType<StriderEntity> STRIDER;
//    public static final EntityType<EggEntity> EGG;
//    public static final EntityType<EnderPearlEntity> ENDER_PEARL;
//    public static final EntityType<ExperienceBottleEntity> EXPERIENCE_BOTTLE;
//    public static final EntityType<PotionEntity> POTION;
//    public static final EntityType<TridentEntity> TRIDENT;
//    public static final EntityType<TraderLlamaEntity> TRADER_LLAMA;
//    public static final EntityType<TropicalFishEntity> TROPICAL_FISH;
//    public static final EntityType<TurtleEntity> TURTLE;
//    public static final EntityType<VexEntity> VEX;
//    public static final EntityType<VillagerEntity> VILLAGER;
//    public static final EntityType<VindicatorEntity> VINDICATOR;
//    public static final EntityType<WanderingTraderEntity> WANDERING_TRADER;
//    public static final EntityType<WitchEntity> WITCH;
//    public static final EntityType<WitherEntity> WITHER;
//    public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON;
//    public static final EntityType<WitherSkullEntity> WITHER_SKULL;
//    public static final EntityType<WolfEntity> WOLF;
//    public static final EntityType<ZoglinEntity> ZOGLIN;
//    public static final EntityType<ZombieEntity> ZOMBIE;
//    public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE;
//    public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER;
//    public static final EntityType<ZombifiedPiglinEntity> ZOMBIFIED_PIGLIN;
//    public static final EntityType<PlayerEntity> PLAYER;
//    public static final EntityType<FishingBobberEntity> FISHING_BOBBER;
//
//    public EntityCollisions() {
//        this.
//    }
}