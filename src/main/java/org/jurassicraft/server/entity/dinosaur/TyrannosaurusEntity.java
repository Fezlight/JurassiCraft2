package org.jurassicraft.server.entity.dinosaur;

import net.ilexiconn.llibrary.server.animation.Animation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jurassicraft.client.model.animation.EntityAnimation;
import org.jurassicraft.client.sound.SoundHandler;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.GoatEntity;

public class TyrannosaurusEntity extends DinosaurEntity {
    private int stepCount = 0;

    private float heightLeft, heightRight, prevHeightLeft, prevHeightRight;

    public TyrannosaurusEntity(World world) {
        super(world);
        this.target(GoatEntity.class, EntityPlayer.class, EntityAnimal.class, EntityVillager.class, EntityMob.class, DilophosaurusEntity.class, GallimimusEntity.class, TriceratopsEntity.class, ParasaurolophusEntity.class, VelociraptorEntity.class, BrachiosaurusEntity.class, MicroraptorEntity.class, MussaurusEntity.class);
    }

    @Override
    public SoundEvent getSoundForAnimation(Animation animation) {
        switch (EntityAnimation.getAnimation(animation)) {
            case SPEAK:
                return SoundHandler.TYRANNOSAURUS_LIVING;
            case CALLING:
                return SoundHandler.TYRANNOSAURUS_ROAR;
            case ROARING:
                return SoundHandler.TYRANNOSAURUS_ROAR;
            case DYING:
                return SoundHandler.TYRANNOSAURUS_DEATH;
            case INJURED:
                return SoundHandler.TYRANNOSAURUS_HURT;
        }

        return null;
    }

    @Override
    public SoundEvent getBreathingSound() {
        return SoundHandler.TYRANNOSAURUS_BREATHING;
    }

    public float getHeightLeft(float delta) {
        return this.prevHeightLeft + (this.heightLeft - this.prevHeightLeft) * delta;
    }

    public float getHeightRight(float delta) {
        return this.prevHeightRight + (this.heightRight - this.prevHeightRight) * delta;
    }
   
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote) {
            double theta = this.renderYawOffset / (180 / Math.PI);
            double dx = Math.cos(theta) * 1.2;
            double dz = Math.sin(theta) * 1.2;
            this.prevHeightLeft = this.heightLeft;
            this.prevHeightRight = this.heightRight;
            this.heightLeft = this.settleLeg(this.posX + dx, this.posY, this.posZ + dz, this.heightLeft);
            this.heightRight = this.settleLeg(this.posX - dx, this.posY, this.posZ - dz, this.heightRight);
        }
        if (this.onGround && !this.isInWater()) {
            if (this.moveForward > 0 && (this.posX - this.prevPosX > 0 || this.posZ - this.prevPosZ > 0) && this.stepCount <= 0) {
                this.playSound(SoundHandler.STOMP, (float) this.interpolate(0.1F, 1.0F), this.getSoundPitch());
                this.stepCount = 65;
            }
            this.stepCount -= this.moveForward * 9.5;
        }
    }

    private float settleLeg(double x, double y, double z, float height) {
        BlockPos pos = new BlockPos(x, y + 0.001, z).down();
        IBlockState state = this.world.getBlockState(pos);
        AxisAlignedBB aabb = state.getCollisionBoundingBox(this.world, pos);
        float dist = aabb == null ? 1 : 1 - Math.min((float) aabb.maxY, 1);
        if (this.onGround && height <= dist) {
            return Math.min(height + 0.3F, dist);
        } else if (height > 0) {
            return Math.max(height - 0.2F, 0);
        }
        return height;
    }
}
