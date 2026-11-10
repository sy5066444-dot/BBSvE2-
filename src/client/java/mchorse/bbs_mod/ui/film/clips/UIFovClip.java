package mchorse.bbs_mod.ui.film.clips;

import mchorse.bbs_mod.camera.clips.modifiers.FovClip;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.film.IUIClipsDelegate;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.context.UIInterpolationContextMenu;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.framework.tooltips.InterpolationTooltip;
import mchorse.bbs_mod.ui.utils.UI;

public class UIFovClip extends UIClip<FovClip>
{
    public UITrackpad from;
    public UITrackpad to;
    public UIButton interp;

    public UIFovClip(FovClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.from = new UITrackpad((value) -> this.clip.from.set(value.floatValue()));
        this.from.tooltip(IKey.raw("FOV at the start"));
        this.to = new UITrackpad((value) -> this.clip.to.set(value.floatValue()));
        this.to.tooltip(IKey.raw("FOV at the end"));

        this.interp = new UIButton(UIKeys.CAMERA_PANELS_INTERPOLATION, (b) ->
        {
            this.getContext().replaceContextMenu(new UIInterpolationContextMenu(this.clip.interp));
        });
        this.interp.tooltip(new InterpolationTooltip(1F, 0.5F, () -> this.clip.interp));
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.column(UIClip.label(UIKeys.C_CLIP.get("bbs:fov")), this.from, this.to, this.interp).marginTop(12));
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.from.setValue(this.clip.from.get());
        this.to.setValue(this.clip.to.get());
    }
}
