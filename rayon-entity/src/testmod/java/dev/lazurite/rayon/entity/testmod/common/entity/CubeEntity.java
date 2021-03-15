package dev.lazurite.rayon.entity.testmod.common.entity;

import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.core.impl.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.space.MinecraftSpace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class CubeEntity extends LivingEntity implements EntityPhysicsElement {
    private final ElementRigidBody rigidBody = new ElementRigidBody(this);

    public CubeEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        getRigidBody().setAngularDamping(0.25f);
    }

    @Override
    public void step(MinecraftSpace space) { }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) { }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.equals(DamageSource.FALL)) {
            return false;
        }

        return super.damage(source, amount);
    }

    @Override
    public ElementRigidBody getRigidBody() {
        return this.rigidBody;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return new ItemStack(Items.AIR);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) { }

    @Override
    public Arm getMainArm() {
        return null;
    }
}
