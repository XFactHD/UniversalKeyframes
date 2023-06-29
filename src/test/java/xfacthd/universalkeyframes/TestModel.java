package xfacthd.universalkeyframes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;
import xfacthd.universalkeyframes.animation.AnimationHelper;
import xfacthd.universalkeyframes.animation.IAnimatedModel;
import xfacthd.universalkeyframes.builder.*;

public final class TestModel extends Model implements IAnimatedModel<TestRenderer.AnimContext>
{
    private static final ResourceLocation MODEL_LOCATION = new ResourceLocation(UniversalKeyframes.MODID, "test");
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(MODEL_LOCATION, "root");

    private final ModelPart root;
    private final ModelPart leverOff;
    private final ModelPart leverOn;

    public TestModel(ModelPart root)
    {
        super(RenderType::entityCutout);
        this.root = root;
        this.leverOff = root.getChild("lever_off");
        this.leverOn = root.getChild("lever_on");
    }

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer buffer,
            int light, int overlay,
            float red, float green, float blue, float alpha
    )
    {
        root.render(poseStack, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnimation(Level level, float partialTicks, TestRenderer.AnimContext ctx)
    {
        root.getAllParts().forEach(ModelPart::resetPose);

        AnimationState state = ctx.state();
        float time = (float)level.getGameTime() + partialTicks;
        AnimationHelper.animate(this, state, TestAnimation.ANIMATION, time, 1F);

        float animTime = AnimationHelper.getElapsedSeconds(TestAnimation.ANIMATION, state.getAccumulatedTime());
        if (animTime >= 6F && animTime <= 13F)
        {
            leverOff.visible = true;
            leverOn.visible = false;
        }
        else
        {
            leverOff.visible = false;
            leverOn.visible = true;
        }
    }

    @Override
    public ModelPart getRoot()
    {
        return root;
    }



    public static LayerDefinition createRootLayer()
    {
        UniversalMeshDefinition mesh = new UniversalMeshDefinition();

        UniversalPartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "box",
                BakedCubeListBuilder.create().addBox(Blocks.COMPOSTER.defaultBlockState()),
                PartPose.ZERO
        );
        root.addOrReplaceChild(
                "door",
                BakedCubeListBuilder.create().addBox(Blocks.OAK_TRAPDOOR.defaultBlockState()),
                PartPose.offset(0, 16, 0)
        );
        root.addOrReplaceChild(
                "lever_off",
                BakedCubeListBuilder.create().addBox(
                        Blocks.LEVER.defaultBlockState()
                                .setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR)
                ),
                PartPose.offset(0, 16, 16)
        );
        root.addOrReplaceChild(
                "lever_on",
                BakedCubeListBuilder.create().addBox(
                        Blocks.LEVER.defaultBlockState()
                                .setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR)
                                .setValue(BlockStateProperties.POWERED, true)
                ),
                PartPose.offset(0, 16, 16)
        );
        UniversalPartDefinition armOne = root.addOrReplaceChild(
                "arm_one",
                BakedCubeListBuilder.create().addBox(Blocks.LIGHTNING_ROD.defaultBlockState(), new Vector3f(8, 0, 8)),
                PartPose.offsetAndRotation(8, 2, 14, (float) Math.toRadians(-80), 0, 0)
        );
        armOne.addOrReplaceChild(
                "arm_two",
                BakedCubeListBuilder.create().addBox(Blocks.LIGHTNING_ROD.defaultBlockState(), new Vector3f(8, 0, 8)),
                PartPose.offsetAndRotation(0, 14, 1, (float) Math.toRadians(150), 0, 0)
        );

        return LayerDefinition.create(mesh, 0, 0);
    }
}
