package dev.lazurite.rayon.test.common.entity;

import com.mojang.math.Quaternion;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;

public class StoneBlockEntity extends LivingEntity implements EntityPhysicsElement {
    private final EntityRigidBody rigidBody = new EntityRigidBody(this);

    public StoneBlockEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.rigidBody.setMass(500);
        this.rigidBody.setDragCoefficient(1.0f);
        this.rigidBody.setCollisionShape(MinecraftShape.of(AABB.ofSize(this.position(), 2.0f, 2.0f, 0.5f)));

    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float f, float g, DamageSource damageSource) {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return new ItemStack(Items.AIR);
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {}

    @Override
    public EntityRigidBody getRigidBody() {
        return this.rigidBody;
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }
}