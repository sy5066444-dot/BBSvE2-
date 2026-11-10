package mchorse.bbs_mod.camera.clips.modifiers;

import mchorse.bbs_mod.camera.clips.CameraClip;
import mchorse.bbs_mod.camera.data.Position;
import mchorse.bbs_mod.settings.values.numeric.ValueFloat;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.clips.ClipContext;
import mchorse.bbs_mod.utils.interps.IInterp;
import mchorse.bbs_mod.utils.interps.Interpolation;
import mchorse.bbs_mod.utils.interps.Interpolations;

/**
 * BBSvE: FOV clip
 *
 * Changes the field of view of whatever camera is running underneath it,
 * going from "from" FOV to "to" FOV over the duration of the clip.
 */
public class FovClip extends CameraClip
{
    public final ValueFloat from = new ValueFloat("from", 70F);
    public final ValueFloat to = new ValueFloat("to", 40F);
    public final Interpolation interp = new Interpolation("interp", Interpolations.MAP, Interpolations.LINEAR);

    public FovClip()
    {
        super();

        this.add(this.from);
        this.add(this.to);
        this.add(this.interp);
    }

    @Override
    protected void applyClip(ClipContext context, Position position)
    {
        float progress = (context.relativeTick + context.transition) / (float) this.duration.get();

        progress = MathUtils.clamp(progress, 0F, 1F);

        double fov = this.interp.interpolate(IInterp.context.set(this.from.get(), this.to.get(), progress));

        position.angle.fov = (float) fov;
    }

    @Override
    public CameraClip create()
    {
        return new FovClip();
    }
}
