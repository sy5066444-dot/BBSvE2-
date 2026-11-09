package mchorse.bbs_mod.actions.types;

import mchorse.bbs_mod.actions.SuperFakePlayer;
import mchorse.bbs_mod.film.Film;
import mchorse.bbs_mod.film.replays.Replay;
import mchorse.bbs_mod.settings.values.numeric.ValueBoolean;
import mchorse.bbs_mod.utils.clips.Clip;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * BBSvE: right click on an entity (villager trade, mount, name tag, etc.)
 */
public class InteractEntityActionClip extends ActionClip
{
    public final ValueBoolean hand = new ValueBoolean("hand", true);

    public InteractEntityActionClip()
    {
        super();

        this.add(this.hand);
    }

    @Override
    public void applyAction(LivingEntity actor, SuperFakePlayer player, Film film, Replay replay, int tick)
    {
        this.applyPositionRotation(player, replay, tick);

        double distance = 4D;
        HitResult blockHit = player.raycast(distance, 1F, false);
        Vec3d origin = player.getCameraPosVec(1F);
        Vec3d rotation = player.getRotationVec(1F);
        Vec3d direction = origin.add(rotation.x * distance, rotation.y * distance, rotation.z * distance);

        double newDistance = blockHit != null ? blockHit.getPos().squaredDistanceTo(origin) : distance * distance;
        Box box = player.getBoundingBox().stretch(rotation.multiply(distance)).expand(1, 1, 1);
        EntityHitResult entityHit = ProjectileUtil.raycast(actor == null ? player : actor, origin, direction, box, entity -> !entity.isSpectator() && entity.canHit(), newDistance);

        if (entityHit != null)
        {
            Entity entity = entityHit.getEntity();

            if (entity != null)
            {
                entity.interact(player, this.hand.get() ? Hand.MAIN_HAND : Hand.OFF_HAND);
            }
        }
    }

    @Override
    protected Clip create()
    {
        return new InteractEntityActionClip();
    }
}
