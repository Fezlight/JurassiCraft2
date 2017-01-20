package org.jurassicraft.client.model.animation.entity;

import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.client.model.AnimatableModel;
import org.jurassicraft.client.model.animation.EntityAnimator;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.dinosaur.TyrannosaurusEntity;

@SideOnly(Side.CLIENT)
public class TyrannosaurusAnimator extends EntityAnimator<TyrannosaurusEntity> {
    @Override
    protected void performAnimations(AnimatableModel model, TyrannosaurusEntity entity, float f, float f1, float ticks, float rotationYaw, float rotationPitch, float scale) {
        AdvancedModelRenderer waist = model.getCube("Body 1");
        AdvancedModelRenderer stomach = model.getCube("Body 2");
        AdvancedModelRenderer chest = model.getCube("Body 3");

        AdvancedModelRenderer neck1 = model.getCube("Neck1");
        AdvancedModelRenderer neck2 = model.getCube("Neck2");
        AdvancedModelRenderer neck3 = model.getCube("Neck3");
        AdvancedModelRenderer neck4 = model.getCube("Neck4");
        AdvancedModelRenderer neck5 = model.getCube("Neck5");

        AdvancedModelRenderer head = model.getCube("Head");

        AdvancedModelRenderer tail1 = model.getCube("Tail 1");
        AdvancedModelRenderer tail2 = model.getCube("Tail 2");
        AdvancedModelRenderer tail3 = model.getCube("Tail 3");
        AdvancedModelRenderer tail4 = model.getCube("Tail 4");
        AdvancedModelRenderer tail5 = model.getCube("Tail 5");
        AdvancedModelRenderer tail6 = model.getCube("Tail 6");
        AdvancedModelRenderer tail7 = model.getCube("Tail 7");

        AdvancedModelRenderer handLeft = model.getCube("Hand LEFT");
        AdvancedModelRenderer lowerArmLeft = model.getCube("Lower Arm LEFT");

        AdvancedModelRenderer handRight = model.getCube("Hand Right");
        AdvancedModelRenderer lowerArmRight = model.getCube("Lower Arm Right");

        AdvancedModelRenderer leftThigh = model.getCube("Left Thigh");
        AdvancedModelRenderer rightThigh = model.getCube("Right Thigh");

        AdvancedModelRenderer[] tailParts = new AdvancedModelRenderer[] { tail7, tail6, tail5, tail4, tail3, tail2, tail1 };
        AdvancedModelRenderer[] bodyParts = new AdvancedModelRenderer[] { head, neck5, neck4, neck3, neck2, neck1, chest, stomach, waist };
        AdvancedModelRenderer[] leftArmParts = new AdvancedModelRenderer[] { handLeft, lowerArmLeft };
        AdvancedModelRenderer[] rightArmParts = new AdvancedModelRenderer[] { handRight, lowerArmRight };

        float globalSpeed = 0.5F;
        float globalDegree = 0.5F;

        float delta = Minecraft.getMinecraft().getRenderPartialTicks();
        float heightLeft = entity.getHeightLeft(delta);
        float heightRight = entity.getHeightRight(delta);
        if (heightLeft > 0 || heightRight > 0) {
            AdvancedModelRenderer leftCalf = model.getCube("Left Calf 1");
            AdvancedModelRenderer rightCalf = model.getCube("Right Calf 1");
            float scaleModifier = entity.getAttributes().getScaleModifier();
            Dinosaur dino = entity.getDinosaur();
            float renderScale = (float) entity.interpolate(dino.getScaleInfant(), dino.getScaleAdult()) * scaleModifier;
            float average = (heightLeft + heightRight) / 2;
            float leftDelta = Math.max(0, (average - heightLeft) * 2);
            float rightDelta = Math.max(0, (average - heightRight) * 2);
            waist.rotationPointY += 16 / renderScale * average;
            leftThigh.rotationPointY += 16 / renderScale * Math.max(heightLeft, average);
            rightThigh.rotationPointY += 16 / renderScale * Math.max(heightRight, average);
            leftThigh.rotateAngleX -= 0.4F * leftDelta;
            leftCalf.rotateAngleX += 0.4F * leftDelta;
            rightThigh.rotateAngleX -= 0.4F * rightDelta;
            rightCalf.rotateAngleX += 0.4F * rightDelta;
        }

        model.bob(waist, globalSpeed * 0.5F, globalDegree * 1.5F, false, f, f1);
        model.bob(rightThigh, globalSpeed * 0.5F, globalDegree * 1.5F, false, f, f1);
        model.bob(leftThigh, globalSpeed * 0.5F, globalDegree * 1.5F, false, f, f1);

        model.chainWave(tailParts, globalSpeed * 0.5F, globalDegree * 0.05F, 1, f, f1);
        model.chainWave(bodyParts, globalSpeed * 0.5F, globalDegree * 0.025F, 3, f, f1);

        model.chainWave(bodyParts, 0.1F, -0.03F, 3, ticks, 0.25F);
        model.chainWave(rightArmParts, -0.1F, 0.2F, 4, ticks, 0.25F);
        model.chainWave(leftArmParts, -0.1F, 0.2F, 4, ticks, 0.25F);
        model.chainWave(tailParts, 0.1F, -0.1F, 2, ticks, 0.1F);

        model.faceTarget(rotationYaw, rotationPitch, 1.5F, chest, neck1, neck5, head);

        entity.tailBuffer.applyChainSwingBuffer(tailParts);
    }
}
