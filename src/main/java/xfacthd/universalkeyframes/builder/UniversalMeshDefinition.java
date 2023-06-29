package xfacthd.universalkeyframes.builder;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.MeshDefinition;

import java.util.List;

public final class UniversalMeshDefinition extends MeshDefinition
{
    public UniversalMeshDefinition()
    {
        root = new UniversalPartDefinition(List.of(), PartPose.ZERO);
    }

    /**
     * {@return the root part of this mesh}
     */
    @Override
    public UniversalPartDefinition getRoot()
    {
        return (UniversalPartDefinition) super.getRoot();
    }
}
