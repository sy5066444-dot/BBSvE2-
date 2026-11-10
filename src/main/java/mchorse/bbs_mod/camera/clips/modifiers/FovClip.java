package mchorse.bbs_mod.camera.clips.modifiers;

import mchorse.bbs_mod.camera.clips.CameraClip;
import mchorse.bbs_mod.camera.data.Position;
import mchorse.bbs_mod.film.replays.ReplayKeyframes;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.settings.values.numeric.ValueFloat;
import mchorse.bbs_mod.utils.MathUtils;
import mchorse.bbs_mod.utils.clips.ClipContext;
import mchorse.bbs_mod.utils.interps.IInterp;
import mchorse.bbs_mod.utils.interps.Interpolation;
import mchorse.bbs_mod.utils.interps.Interpolations;

import java.util.List;

/**
 * BBSvE: FOV clip
 *
 * Pick an actor and the camera FOV follows that actor's recorded first
 * person FOV (the fov track of its replay). Without an actor (or when the
 * actor has no recorded FOV) it zooms from "from" to "to" over the clip.
 */
public class FovClip extends EntityClip
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
        List<IEntity> entities = this.getEntities(context);

        if (!entities.isEmpty() && entities.get(0) != null)
        {
            Double recorded = ReplayKeyframes.entityFov.get(entities.get(0));

            if (recorded != null)
            {
                position.angle.fov = recorded.floatValue();

                return;
            }
        }

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
