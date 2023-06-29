package xfacthd.universalkeyframes.util;

import net.minecraft.core.Direction;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public final class Utils
{
    private static final Direction[] DIRECTIONS = Direction.values();

    /**
     * Apply the given consumer to all possible {@link Direction}s, including {@code null} of desired
     * @param includeNull Whether the consumer should be called with a null direction
     * @param consumer The consumer of the directions
     */
    public static void forAllDirections(boolean includeNull, Consumer<Direction> consumer)
    {
        if (includeNull)
        {
            consumer.accept(null);
        }
        for (Direction dir : DIRECTIONS)
        {
            consumer.accept(dir);
        }
    }

    /**
     * Create a {@link MethodHandle} for the given method in the given class with the given parameter types
     * @param clazz The {@link Class} containing the target method
     * @param srgMethodName The SRG name of the target method
     * @param paramTypes The types of the parameters of the target method
     * @return a method handle for the target method
     */
    public static MethodHandle unreflectMethod(Class<?> clazz, String srgMethodName, Class<?>... paramTypes)
    {
        Method method = ObfuscationReflectionHelper.findMethod(clazz, srgMethodName, paramTypes);
        try
        {
            return MethodHandles.publicLookup().unreflect(method);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect method '%s#%s()'".formatted(clazz.getName(), srgMethodName), e);
        }
    }

    /**
     * Create a {@link MethodHandle} for a getter of the given field in the given class with the given parameter types
     * @param clazz The {@link Class} containing the target field
     * @param srgFieldName The SRG name of the target field
     * @return a method handle for a getter of the target field
     */
    public static MethodHandle unreflectFieldGetter(Class<?> clazz, String srgFieldName)
    {
        Field field = ObfuscationReflectionHelper.findField(clazz, srgFieldName);
        try
        {
            return MethodHandles.publicLookup().unreflectGetter(field);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect field '%s#%s'".formatted(clazz.getName(), srgFieldName), e);
        }
    }

    /**
     * Create a {@link MethodHandle} for a setter of the given field in the given class with the given parameter types
     * @param clazz The {@link Class} containing the target field
     * @param srgFieldName The SRG name of the target field
     * @return a method handle for a setter of the target field
     */
    public static MethodHandle unreflectFieldSetter(Class<?> clazz, String srgFieldName)
    {
        Field field = ObfuscationReflectionHelper.findField(clazz, srgFieldName);
        try
        {
            return MethodHandles.publicLookup().unreflectSetter(field);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect field '%s#%s'".formatted(clazz.getName(), srgFieldName), e);
        }
    }



    private Utils() { }
}
