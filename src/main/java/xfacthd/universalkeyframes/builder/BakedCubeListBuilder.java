package xfacthd.universalkeyframes.builder;

import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import java.util.*;

public final class BakedCubeListBuilder implements ICubeListBuilder
{
    private static final Set<Direction> ALL_FACES = EnumSet.allOf(Direction.class);
    private static final Vector3f ZERO = new Vector3f();

    private final List<BakedCubeDefinition> cubes = new ArrayList<>();

    private BakedCubeListBuilder() { }

    /**
     * Create a new {@link BakedCubeListBuilder}
     */
    public static BakedCubeListBuilder create()
    {
        return new BakedCubeListBuilder();
    }

    /**
     * Add a cube based on the given {@link BlockState}'s model with a zero origin, no {@link CubeDeformation}
     * and all faces visible
     * @param state The state whose model to use
     */
    public BakedCubeListBuilder addBox(BlockState state)
    {
        cubes.add(BakedCubeDefinition.of(state, ZERO, CubeDeformation.NONE, ALL_FACES));
        return this;
    }

    /**
     * Add a cube based on the given {@link BlockState}'s {@link BakedModel} with the specified origin,
     * no {@link CubeDeformation} and all faces visible
     * @param state The state whose model to use
     * @param origin The origin of the resulting cube
     */
    public BakedCubeListBuilder addBox(BlockState state, Vector3f origin)
    {
        cubes.add(BakedCubeDefinition.of(state, origin, CubeDeformation.NONE, ALL_FACES));
        return this;
    }

    /**
     * Add a cube based on the given {@link BlockState}'s {@link BakedModel} with a zero origin,
     * the specified {@link CubeDeformation} and all faces visible
     * @param state The state whose model to use
     * @param deformation The deformation to be applied to the model's quads
     */
    public BakedCubeListBuilder addBox(BlockState state, CubeDeformation deformation)
    {
        cubes.add(BakedCubeDefinition.of(state, ZERO, deformation, ALL_FACES));
        return this;
    }

    /**
     * Add a cube based on the given {@link BlockState}'s {@link BakedModel} with the specified origin,
     * the specified {@link CubeDeformation} and all faces visible
     * @param state The state whose model to use
     * @param origin The origin of the resulting cube
     * @param deformation The deformation to be applied to the model's quads
     */
    public BakedCubeListBuilder addBox(BlockState state, Vector3f origin, CubeDeformation deformation)
    {
        cubes.add(BakedCubeDefinition.of(state, origin, deformation, ALL_FACES));
        return this;
    }

    /**
     * Add a cube based on the given {@link BlockState}'s {@link BakedModel} with the specified origin,
     * the specified {@link CubeDeformation} and only those faces visible whose normal direction is in the given set
     * @param state The state whose model to use
     * @param origin The origin of the resulting cube
     * @param deformation The deformation to be applied to the model's quads
     * @param visibleFaces The set of normal directions for whom the quads should be collected
     */
    public BakedCubeListBuilder addBox(
            BlockState state, Vector3f origin, CubeDeformation deformation, Set<Direction> visibleFaces
    )
    {
        cubes.add(BakedCubeDefinition.of(state, origin, deformation, visibleFaces));
        return this;
    }

    /**
     * Add a cube based on the {@link BakedModel} at the given {@link ModelResourceLocation} with a zero origin,
     * no {@link CubeDeformation} and all faces visible
     * @param model The location of the model to use
     */
    public BakedCubeListBuilder addBox(ModelResourceLocation model)
    {
        cubes.add(BakedCubeDefinition.of(model, ZERO, CubeDeformation.NONE, ALL_FACES));
        return this;
    }

    /**
     * Add a cube based on the {@link BakedModel} at the given {@link ModelResourceLocation} with the specified origin,
     * no {@link CubeDeformation} and all faces visible
     * @param model The location of the model to use
     * @param origin The origin of the resulting cube
     */
    public BakedCubeListBuilder addBox(ModelResourceLocation model, Vector3f origin)
    {
        cubes.add(BakedCubeDefinition.of(model, origin, CubeDeformation.NONE, ALL_FACES));
        return this;
    }

    /**
     * Add a cube based on the {@link BakedModel} at the given {@link ModelResourceLocation} with a zero origin,
     * the specified {@link CubeDeformation} and all faces visible
     * @param model The location of the model to use
     * @param deformation The deformation to be applied to the model's quads
     */
    public BakedCubeListBuilder addBox(ModelResourceLocation model, CubeDeformation deformation)
    {
        cubes.add(BakedCubeDefinition.of(model, ZERO, deformation, ALL_FACES));
        return this;
    }

    /**
     * Add a cube based on the {@link BakedModel} at the given {@link ModelResourceLocation} with the specified origin,
     * the specified {@link CubeDeformation} and all faces visible
     * @param model The location of the model to use
     * @param origin The origin of the resulting cube
     * @param deformation The deformation to be applied to the model's quads
     */
    public BakedCubeListBuilder addBox(ModelResourceLocation model, Vector3f origin, CubeDeformation deformation)
    {
        cubes.add(BakedCubeDefinition.of(model, origin, deformation, ALL_FACES));
        return this;
    }

    /**
     * Add a cube based on the {@link BakedModel} at the given {@link ModelResourceLocation} with the specified origin,
     * the specified {@link CubeDeformation} and only those faces visible whose normal direction is in the given set
     * @param model The location of the model to use
     * @param origin The origin of the resulting cube
     * @param deformation The deformation to be applied to the model's quads
     * @param visibleFaces The set of normal directions for whom the quads should be collected
     */
    public BakedCubeListBuilder addBox(
            ModelResourceLocation model, Vector3f origin, CubeDeformation deformation, Set<Direction> visibleFaces
    )
    {
        cubes.add(BakedCubeDefinition.of(model, origin, deformation, visibleFaces));
        return this;
    }

    /**
     * {@return all cubes added to this builder}
     */
    @Override
    public List<BakedCubeDefinition> getCubes()
    {
        return List.copyOf(cubes);
    }
}
