package dev.lazurite.rayon.examplemod.entity;

import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

import java.util.ArrayList;

public class LivingCubeEntity extends LivingEntity implements PhysicsElement {
    private final ElementRigidBody rigidBody = new ElementRigidBody(this);

    public LivingCubeEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void step(MinecraftSpace space) {

    }

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
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
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
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return null;
    }
}
