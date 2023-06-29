package xfacthd.universalkeyframes.builder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.*;
import java.util.stream.Collectors;

public final class UniversalPartDefinition extends PartDefinition
{
    private final List<? extends ICubeDefinition> cubes;
    private final PartPose partPose;
    private final Map<String, UniversalPartDefinition> children = Maps.newHashMap();

    UniversalPartDefinition(List<? extends ICubeDefinition> cubes, PartPose pose)
    {
        super(List.of(), PartPose.ZERO);
        this.cubes = cubes;
        this.partPose = pose;
    }

    /**
     * Add a vanilla {@link CubeListBuilder} with the given name and initial {@link PartPose} to this part
     */
    @Override
    public PartDefinition addOrReplaceChild(String name, CubeListBuilder cubes, PartPose pose)
    {
        return addOrReplaceChild(name, new VanillaCubeListBuilder(cubes), pose);
    }

    /**
     * Add a custom {@link ICubeListBuilder} with the given name and initial {@link PartPose} to this part
     */
    public UniversalPartDefinition addOrReplaceChild(String name, ICubeListBuilder cubes, PartPose pose)
    {
        UniversalPartDefinition def = new UniversalPartDefinition(cubes.getCubes(), pose);
        UniversalPartDefinition prevDef = children.put(name, def);
        if (prevDef != null)
        {
            def.children.putAll(prevDef.children);
        }
        return def;
    }

    /**
     * Bake this part definition into a {@link ModelPart} with the given texture size
     * @return the baked part
     */
    @Override
    public ModelPart bake(int texWidth, int texHeight)
    {
        Object2ObjectArrayMap<String, ModelPart> bakedChildren = children.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().bake(texWidth, texHeight),
                        (first, second) -> first, Object2ObjectArrayMap::new
                ));
        List<ModelPart.Cube> bakedCubes = cubes.stream()
                .map(cube -> cube.bake(texWidth, texHeight))
                .collect(ImmutableList.toImmutableList());

        ModelPart part = new ModelPart(bakedCubes, bakedChildren);
        part.setInitialPose(partPose);
        part.loadPose(partPose);
        return part;
    }

    /**
     * {@return the child of this part with the given name}
     */
    @Override
    public UniversalPartDefinition getChild(String name)
    {
        return children.get(name);
    }



    private record VanillaCubeListBuilder(CubeListBuilder builder) implements ICubeListBuilder
    {
        @Override
        public List<VanillaCubeDefinition> getCubes()
        {
            return builder.getCubes().stream().map(VanillaCubeDefinition::new).toList();
        }
    }

    private record VanillaCubeDefinition(CubeDefinition def) implements ICubeDefinition
    {
        @Override
        public ModelPart.Cube bake(int texWidth, int texHeight)
        {
            return def.bake(texWidth, texHeight);
        }
    }
}
