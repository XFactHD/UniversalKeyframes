package xfacthd.universalkeyframes.animation;

import net.minecraft.client.animation.AnimationChannel;
import org.joml.Vector3f;

public final class CustomInterpolations
{
    public static final AnimationChannel.Interpolation ACCELERATE_PARABOLIC = (vecCache, delta, frames, currIdx, nextIdx, scale) ->
    {
        Vector3f currTarget = frames[currIdx].target();
        Vector3f nextTarget = frames[nextIdx].target();

        delta = delta * delta * delta;
        return currTarget.lerp(nextTarget, delta, vecCache).mul(scale);
    };

    public static final AnimationChannel.Interpolation DECELERATE_PARABOLIC = (vecCache, delta, frames, currIdx, nextIdx, scale) ->
    {
        Vector3f currTarget = frames[currIdx].target();
        Vector3f nextTarget = frames[nextIdx].target();

        delta = 1F - delta;
        delta = 1F - delta * delta * delta;
        return currTarget.lerp(nextTarget, delta, vecCache).mul(scale);
    };

    public static final AnimationChannel.Interpolation PARABOLIC = (vecCache, delta, frames, currIdx, nextIdx, scale) ->
    {
        Vector3f currTarget = frames[currIdx].target();
        Vector3f nextTarget = frames[nextIdx].target();
        if (delta < .5F)
        {
            delta *= 2F;
            delta = (delta * delta * delta) / 2F;
        }
        else
        {
            delta = (1F - delta) * 2F;
            delta = (1F - delta * delta * delta) / 2F + .5F;
        }
        return currTarget.lerp(nextTarget, delta, vecCache).mul(scale);
    };



    private CustomInterpolations() { }
}
