package xfacthd.universalkeyframes.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xfacthd.universalkeyframes.util.*;

import java.lang.invoke.MethodHandle;
import java.util.Set;

public final class BakedCube extends ModelPart.Cube
{
    private static final MethodHandle MTH_POLYGON_ARRAY = Utils.unreflectFieldSetter(ModelPart.Cube.class, "f_104341_");

    private final BakedQuad[] quads;
    private final Vector3f origin;

    private BakedCube(BakedQuad[] quads, float x, float y, float z, float sx, float sy, float sz, Vector3f origin)
    {
        super(0, 0, x, y, z, sx, sy, sz, 0F, 0F, 0F, false, 1F, 1F, Set.of());
        this.quads = quads;
        this.origin = origin;
        deletePolygons(this);
    }

    @Override
    public void compile(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int light, int overlay,
            float red, float green, float blue, float alpha
    )
    {
        for (BakedQuad quad : quads)
        {
            ModelUtils.drawQuad(pose, consumer, quad, origin, red, green, blue, alpha, light, overlay);
        }
    }



    /**
     * Create a {@link BakedCube} from the given {@link BakedModel} with the given optional {@link BlockState} and
     * origin point, containing all {@link BakedQuad}s from the given model whose normal directions are in the given set,
     * grown by the given absolute inflation values
     *
     * @param model The source model
     * @param state The optional state to use for quad collection
     * @param faces The normal directions for whom the quads should be collected
     * @param growX The amount to grow the quad by on the X axis
     * @param growY The amount to grow the quad by on the Y axis
     * @param growZ The amount to grow the quad by on the Z axis
     * @param origin The origin point of the cube
     * @return the baked cube containing the relevant quads of the given model
     */
    public static BakedCube of(
            BakedModel model,
            @Nullable BlockState state,
            Set<Direction> faces,
            float growX, float growY, float growZ,
            Vector3f origin
    )
    {
        BakedQuad[] quads = ModelUtils.collectVisibleQuads(model, state, faces);

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        float maxZ = Float.MIN_VALUE;
        float[][] pos = new float[4][3];
        boolean grow = growX != 0F || growY != 0F || growZ != 0F;
        for (int i = 0; i < quads.length; i++)
        {
            BakedQuad quad = quads[i];
            ModelUtils.unpackPosition(quad.getVertices(), pos);
            for (int vert = 0; vert < 4; vert++)
            {
                minX = Math.min(minX, pos[vert][0]);
                maxX = Math.max(maxX, pos[vert][0]);
                minY = Math.min(minY, pos[vert][1]);
                maxY = Math.max(maxY, pos[vert][1]);
                minZ = Math.min(minZ, pos[vert][2]);
                maxZ = Math.max(maxZ, pos[vert][2]);
            }

            if (grow)
            {
                quads[i] = ModelUtils.growQuad(quad, pos, growX / 16F, growY / 16F, growZ / 16F);
            }
        }

        origin = origin.div(16F, new Vector3f());
        return new BakedCube(
                quads,
                minX - origin.x, minY - origin.y, minZ - origin.z,
                maxX - minX, maxY - minY, maxZ - minZ,
                origin
        );
    }

    private static void deletePolygons(BakedCube cube)
    {
        try
        {
            MTH_POLYGON_ARRAY.invokeExact((ModelPart.Cube) cube, (ModelPart.Polygon[]) null);
        }
        catch (Throwable e)
        {
            throw new RuntimeException("Failed to delete polygon array in ModelPart.Cube", e);
        }
    }
}
