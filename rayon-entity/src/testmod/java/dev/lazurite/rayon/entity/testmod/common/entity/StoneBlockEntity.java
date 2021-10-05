package dev.lazurite.rayon.entity.testmod.common.entity;

import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class StoneBlockEntity extends LivingEntity implements EntityPhysicsElement {
    private final EntityRigidBody rigidBody = new EntityRigidBody(this);

    public StoneBlockEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return null;
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
