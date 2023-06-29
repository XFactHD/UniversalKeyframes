package xfacthd.universalkeyframes.builder;

import net.minecraft.client.model.geom.ModelPart;

public interface ICubeDefinition
{
    /**
     * Bake this cube definition into a {@link ModelPart.Cube}
     * @return the baked cube
     */
    ModelPart.Cube bake(int texWidth, int texHeight);
}
