package fr.redboxing.cosmeticsmod.cosmetics.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3f;

public class DragonWingsModel<T extends LivingEntity> extends AnimalModel<T> {
    private final ModelPart leftWing;
    private final ModelPart leftWingTip;
    private final ModelPart rightWing;
    private final ModelPart rightWingTip;
    private T entity;
    private float limbDistance;
    private float animationProgress;

    public DragonWingsModel(ModelPart root) {
        this.leftWing = root.getChild("left_wing");
        this.leftWingTip = this.leftWing.getChild("left_wing_tip");
        this.rightWing = root.getChild("right_wing");
        this.rightWingTip = this.rightWing.getChild("right_wing_tip");
    }

    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of();
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.entity = entity;
        this.limbDistance = limbDistance;
        this.animationProgress = animationProgress;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();

        float wingSpeedMultiplier = 65.0F;
        if (!entity.isOnGround()) {
            wingSpeedMultiplier = 20.0f;
        }
        if (entity.prevHorizontalSpeed != entity.horizontalSpeed) {
            wingSpeedMultiplier = 30.0f;
            if (entity.isSprinting()) {
                wingSpeedMultiplier = 15.0f;
            }
        }
        if(entity.isInSneakingPose()) {
            matrices.translate(0.0F, 0.175F, 0.0F);
        }

        float f = this.limbDistance + this.animationProgress / wingSpeedMultiplier;
        matrices.scale(.14F, .14F, .14F);
        matrices.translate(0.0D, -0.3D, 1.1D);
        matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(25.0F));

        float f11 = f * ((float)Math.PI * 2F);
        this.leftWing.pitch = 0.125F - (float)Math.cos(f11) * 0.2F;
        this.leftWing.yaw = -0.25F;
        this.leftWing.roll = -(float)(Math.sin(f11) + 1.125D) * 0.5F;
        this.leftWingTip.roll = ((float)(Math.sin((f11 + 2.0F)) + 0.5D)) * 0.7F;
        this.rightWing.pitch = this.leftWing.pitch;
        this.rightWing.yaw = -this.leftWing.yaw;
        this.rightWing.roll = -this.leftWing.roll;
        this.rightWingTip.roll = -this.leftWingTip.roll;
        this.leftWing.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.rightWing.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.scale(-1.0F, 1.0F, 1.0F);
        matrices.pop();
    }
}
