package xfacthd.universalkeyframes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@SuppressWarnings("deprecation")
public final class TestRenderer implements BlockEntityRenderer<BarrelBlockEntity>
{
    private final TestModel model;
    private final AnimContext animCtx = new AnimContext(new AnimationState());

    public TestRenderer(BlockEntityRendererProvider.Context ctx)
    {
        this.model = new TestModel(ctx.bakeLayer(TestModel.LAYER_LOCATION));
        animCtx.state.start(0);
    }

    @Override
    public void render(
            BarrelBlockEntity be,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    )
    {
        poseStack.pushPose();
        poseStack.translate(0, 0, 2);

        Direction facing = be.getBlockState().getValue(BlockStateProperties.FACING);
        if (facing.getAxis() != Direction.Axis.Y)
        {
            poseStack.translate(.5, 0, .5);
            poseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()));
            poseStack.translate(-.5, 0, -.5);
        }

        //noinspection ConstantConditions
        model.setupAnimation(be.getLevel(), partialTick, animCtx);
        model.renderToBuffer(
                poseStack,
                buffer.getBuffer(model.renderType(TextureAtlas.LOCATION_BLOCKS)),
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                1F, 1F, 1F, 1F
        );

        poseStack.popPose();
    }



    public record AnimContext(AnimationState state) { }
}
