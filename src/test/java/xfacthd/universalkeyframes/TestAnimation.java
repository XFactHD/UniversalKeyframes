package xfacthd.universalkeyframes;

import net.minecraft.client.animation.*;
import xfacthd.universalkeyframes.animation.CustomInterpolations;

public final class TestAnimation
{
    public static final AnimationDefinition ANIMATION = AnimationDefinition.Builder.withLength(14F)
            .looping()
            .addAnimation(
                    "door",
                    new AnimationChannel(
                            AnimationChannel.Targets.ROTATION,
                            new Keyframe( 0F, KeyframeAnimations.degreeVec(0, 0, 0), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe( 2F, KeyframeAnimations.degreeVec(-90, 0, 0), CustomInterpolations.PARABOLIC),
                            new Keyframe(10F, KeyframeAnimations.degreeVec(-90, 0, 0), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(12F, KeyframeAnimations.degreeVec(0, 0, 0), CustomInterpolations.PARABOLIC)
                    )
            )
            .addAnimation(
                    "arm_one",
                    new AnimationChannel(
                            AnimationChannel.Targets.ROTATION,
                            new Keyframe(3F, KeyframeAnimations.degreeVec(0, 0, 0), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(6F, KeyframeAnimations.degreeVec(60, 0, 0), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(9F, KeyframeAnimations.degreeVec(0, 0, 0), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .addAnimation(
                    "arm_two",
                    new AnimationChannel(
                            AnimationChannel.Targets.ROTATION,
                            new Keyframe(3F, KeyframeAnimations.degreeVec(0, 0, 0), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(6F, KeyframeAnimations.degreeVec(-70, 0, 0), AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(9F, KeyframeAnimations.degreeVec(0, 0, 0), AnimationChannel.Interpolations.LINEAR)
                    )
            )
            .build();



    private TestAnimation() { }
}
