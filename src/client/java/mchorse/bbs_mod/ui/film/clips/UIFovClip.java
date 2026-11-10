package mchorse.bbs_mod.ui.film.clips;

import mchorse.bbs_mod.camera.clips.modifiers.FovClip;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.film.IUIClipsDelegate;
import mchorse.bbs_mod.ui.film.UIFilmPanel;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.factories.UIAnchorKeyframeFactory;
import mchorse.bbs_mod.ui.utils.UI;

public class UIFovClip extends UIClip<FovClip>
{
    public UIButton selector;
    public UITrackpad from;
    public UITrackpad to;

    public UIFovClip(FovClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.selector = new UIButton(UIKeys.CAMERA_PANELS_TARGET_TITLE, (b) ->
        {
            UIFilmPanel panel = this.getParent(UIFilmPanel.class);

            if (panel != null)
            {
                UIAnchorKeyframeFactory.displayActors(this.getContext(), panel.getController().getEntities(), this.clip.selector.get(), (i) -> this.clip.selector.set(i));
            }
        });
        this.selector.tooltip(UIKeys.CAMERA_PANELS_TARGET_TOOLTIP);

        this.from = new UITrackpad((value) -> this.clip.from.set(value.floatValue()));
        this.from.tooltip(IKey.raw("FOV at the start (used when no actor recorded FOV)"));
        this.to = new UITrackpad((value) -> this.clip.to.set(value.floatValue()));
        this.to.tooltip(IKey.raw("FOV at the end (used when no actor recorded FOV)"));
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UI.column(UIClip.label(UIKeys.CAMERA_PANELS_TARGET), this.selector).marginTop(12));
        this.panels.add(UI.column(UIClip.label(UIKeys.C_CLIP.get("bbs:fov")), this.from, this.to).marginTop(12));
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.from.setValue(this.clip.from.get());
        this.to.setValue(this.clip.to.get());
    }
}
