package xfacthd.universalkeyframes.builder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xfacthd.universalkeyframes.model.BakedCube;

import java.util.Set;
import java.util.function.Function;

public final class BakedCubeDefinition implements ICubeDefinition
{
    private final Function<BlockRenderDispatcher, BakedModel> modelGetter;
    @Nullable
    private final BlockState state;
    private final Vector3f origin;
    private final CubeDeformation deformation;
    private final Set<Direction> visibleFaces;

    BakedCubeDefinition(
            Function<BlockRenderDispatcher, BakedModel> modelGetter,
            @Nullable BlockState state,
            Vector3f origin,
            CubeDeformation deformation,
            Set<Direction> visibleFaces
    )
    {
        this.modelGetter = modelGetter;
        this.state = state;
        this.origin = origin;
        this.deformation = deformation;
        this.visibleFaces = visibleFaces;
    }

    /**
     * Bake this cube definition into a {@link ModelPart.Cube}
     * @return the baked cube
     */
    @Override
    public ModelPart.Cube bake(int texWidth, int texHeight)
    {
        BakedModel model = modelGetter.apply(Minecraft.getInstance().getBlockRenderer());
        float growX = deformation.growX;
        float growY = deformation.growY;
        float growZ = deformation.growZ;
        return BakedCube.of(model, state, visibleFaces, growX, growY, growZ, origin);
    }



    /**
     * Create a cube based on the given {@link BlockState}'s {@link BakedModel} with the specified origin,
     * the specified {@link CubeDeformation} and only those faces visible whose normal direction is in the given set
     * @param state The state whose model should be used
     * @param origin The origin point of the cube
     * @param deformation The deformation to be applied to the model's quads
     * @param visibleFaces The normal directions for whom the quads should be collected
     * @return a new cube definition based on the given state
     */
    public static BakedCubeDefinition of(
            BlockState state,
            Vector3f origin,
            CubeDeformation deformation,
            Set<Direction> visibleFaces
    )
    {
        return new BakedCubeDefinition(
                brd -> brd.getBlockModel(state), state, origin, deformation, visibleFaces
        );
    }

    /**
     * Create a cube based on the {@link BakedModel} at the given {@link ModelResourceLocation} with the specified origin,
     * the specified {@link CubeDeformation} and only those faces visible whose normal direction is in the given set
     * @param modelLocation The state whose model should be used
     * @param origin The origin point of the cube
     * @param deformation The deformation to be applied to the model's quads
     * @param visibleFaces The normal directions for whom the quads should be collected
     * @return a new cube definition based on the given state
     */
    public static BakedCubeDefinition of(
            ModelResourceLocation modelLocation,
            Vector3f origin,
            CubeDeformation deformation,
            Set<Direction> visibleFaces
    )
    {
        return new BakedCubeDefinition(
                brd -> brd.getBlockModelShaper().getModelManager().getModel(modelLocation),
                null,
                origin,
                deformation,
                visibleFaces
        );
    }
}
