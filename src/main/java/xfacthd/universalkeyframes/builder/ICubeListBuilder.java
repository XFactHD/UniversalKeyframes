package xfacthd.universalkeyframes.builder;

import java.util.List;

public interface ICubeListBuilder
{
    /**
     * {@return all cubes added to this builder}
     */
    List<? extends ICubeDefinition> getCubes();
}
