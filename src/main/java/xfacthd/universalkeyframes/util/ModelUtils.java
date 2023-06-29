package xfacthd.universalkeyframes.util;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

public final class ModelUtils
{
    /**
     * Unpack the vertex positions from the given vertex data into the given position array
     * @param vertexData The {@link BakedQuad}'s packed vertex data
     * @param pos The array of vertex positions
     */
    public static void unpackPosition(int[] vertexData, float[][] pos)
    {
        for (int vert = 0; vert < 4; vert++)
        {
            int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
            pos[vert][0] = Float.intBitsToFloat(vertexData[offset]);
            pos[vert][1] = Float.intBitsToFloat(vertexData[offset + 1]);
            pos[vert][2] = Float.intBitsToFloat(vertexData[offset + 2]);
        }
    }

    /**
     * Pack the vertex positions from the given position array into the given vertex data
     * @param pos The array of vertex positions
     * @param vertexData The {@link BakedQuad}'s packed vertex data
     */
    public static void packPosition(float[][] pos, int[] vertexData)
    {
        for (int vert = 0; vert < 4; vert++)
        {
            int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
            vertexData[offset    ] = Float.floatToRawIntBits(pos[vert][0]);
            vertexData[offset + 1] = Float.floatToRawIntBits(pos[vert][1]);
            vertexData[offset + 2] = Float.floatToRawIntBits(pos[vert][2]);
        }
    }

    /**
     * Apply the given absolute inflation values to the given vertex positions
     * @param quad The {@link BakedQuad} being modified
     * @param pos The given quad's vertex positions
     * @param gx The amount to grow the quad by on the X axis
     * @param gy The amount to grow the quad by on the Y axis
     * @param gz The amount to grow the quad by on the Z axis
     * @return the modified copy of the given quads
     */
    public static BakedQuad growQuad(BakedQuad quad, float[][] pos, float gx, float gy, float gz)
    {
        int[] vertices = quad.getVertices();
        int[] newVertices = new int[vertices.length];
        System.arraycopy(vertices, 0, newVertices, 0, vertices.length);

        switch (quad.getDirection())
        {
            case DOWN ->
            {
                applyToAll(pos, 1, -gy);
                applyToTopBottom(pos, gx, gz, false);
            }
            case UP ->
            {
                applyToAll(pos, 1, gy);
                applyToTopBottom(pos, gx, gz, true);
            }
            case NORTH ->
            {
                applyToAll(pos, 2, -gz);
                applyToVert(pos, gy);
                applyToHor(pos, gx, Direction.NORTH);
            }
            case SOUTH ->
            {
                applyToAll(pos, 2, gz);
                applyToVert(pos, gy);
                applyToHor(pos, gx, Direction.SOUTH);
            }
            case WEST ->
            {
                applyToAll(pos, 0, -gx);
                applyToVert(pos, gy);
                applyToHor(pos, gz, Direction.WEST);
            }
            case EAST ->
            {
                applyToAll(pos, 0, gx);
                applyToVert(pos, gy);
                applyToHor(pos, gz, Direction.EAST);
            }
        }
        ModelUtils.packPosition(pos, newVertices);

        return new BakedQuad(
                newVertices,
                quad.getTintIndex(),
                quad.getDirection(),
                quad.getSprite(),
                quad.isShade(),
                quad.hasAmbientOcclusion()
        );
    }

    private static void applyToAll(float[][] pos, int idx, float grow)
    {
        for (int vert = 0; vert < 4; vert++)
        {
            pos[vert][idx] += grow;
        }
    }

    private static void applyToVert(float[][] pos, float grow)
    {
        pos[0][1] += grow;
        pos[1][1] -= grow;
        pos[2][1] -= grow;
        pos[3][1] += grow;
    }

    private static void applyToHor(float[][] pos, float grow, Direction side)
    {
        int idx = side.getAxis() == Direction.Axis.X ? 2 : 0;
        if (side == Direction.NORTH || side == Direction.EAST)
        {
            grow *= -1F;
        }
        pos[0][idx] -= grow;
        pos[1][idx] -= grow;
        pos[2][idx] += grow;
        pos[3][idx] += grow;
    }

    private static void applyToTopBottom(float[][] pos, float growX, float growZ, boolean up)
    {
        pos[0][0] -= growX;
        pos[1][0] -= growX;
        pos[2][0] += growX;
        pos[3][0] += growX;

        if (!up)
        {
            growZ *= -1F;
        }
        pos[0][2] -= growZ;
        pos[1][2] += growZ;
        pos[2][2] += growZ;
        pos[3][2] -= growZ;
    }

    /**
     * Collect all {@link BakedQuad}s from the given {@link BakedModel} whose normal direction is
     * in the given set of {@link Direction}s
     * @param model The model whose quads to collect
     * @param state The optional {@link BlockState} to use with the model
     * @param faces The set of directions for which the quads should be collected
     * @return the array of quads whose normal direction is in the given set
     */
    public static BakedQuad[] collectVisibleQuads(BakedModel model, @Nullable BlockState state, Set<Direction> faces)
    {
        RandomSource rand = new SingleThreadedRandomSource(42);
        List<BakedQuad> quads = new ArrayList<>();
        Utils.forAllDirections(true, dir -> quads.addAll(model.getQuads(state, dir, rand, ModelData.EMPTY, null)));
        quads.removeIf(q -> !faces.contains(q.getDirection()));
        return quads.toArray(BakedQuad[]::new);
    }

    /**
     * Draw the given {@link BakedQuad} to the given {@link VertexConsumer} based on the given parameters
     * @param poseEntry The {@link PoseStack} entry containing the transformations
     * @param consumer The consumer to be drawn to
     * @param quad The quad to be drawn
     * @param origin The origin position the quad should be drawn relative to
     * @param red The red color multiplier
     * @param green The green color multiplier
     * @param blue The blue color multiplier
     * @param alpha The opacity multiplier
     * @param light The packed light texture coordinates
     * @param overlay The packed overlay texture coordinates
     */
    public static void drawQuad(
            PoseStack.Pose poseEntry,
            VertexConsumer consumer,
            BakedQuad quad,
            Vector3f origin,
            float red, float green, float blue, float alpha,
            int light, int overlay
    )
    {
        int[] vertices = quad.getVertices();
        Vec3i dirNormal = quad.getDirection().getNormal();
        Matrix4f pose = poseEntry.pose();
        Vector3f normal = poseEntry.normal().transform(
                new Vector3f((float)dirNormal.getX(), (float)dirNormal.getY(), (float)dirNormal.getZ())
        );
        int vertCount = vertices.length / IQuadTransformer.STRIDE;

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            ByteBuffer byteBuffer = stack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();

            for(int vert = 0; vert < vertCount; vert++)
            {
                intBuffer.clear();
                intBuffer.put(vertices, vert * IQuadTransformer.STRIDE, IQuadTransformer.STRIDE);

                float x = byteBuffer.getFloat(0) - origin.x;
                float y = byteBuffer.getFloat(4) - origin.y;
                float z = byteBuffer.getFloat(8) - origin.z;
                light = consumer.applyBakedLighting(light, byteBuffer);
                float u = byteBuffer.getFloat(16);
                float v = byteBuffer.getFloat(20);
                Vector4f pos = pose.transform(new Vector4f(x, y, z, 1.0F));
                consumer.applyBakedNormals(normal, byteBuffer, poseEntry.normal());
                consumer.vertex(
                        pos.x(), pos.y(), pos.z(),
                        red, green, blue, alpha,
                        u, v,
                        overlay, light,
                        normal.x(), normal.y(), normal.z()
                );
            }
        }
    }



    private ModelUtils() { }
}
