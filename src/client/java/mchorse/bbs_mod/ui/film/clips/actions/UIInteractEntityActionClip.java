package mchorse.bbs_mod.ui.film.clips.actions;

import mchorse.bbs_mod.actions.types.InteractEntityActionClip;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.film.IUIClipsDelegate;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIToggle;

public class UIInteractEntityActionClip extends UIActionClip<InteractEntityActionClip>
{
    public UIToggle hand;

    public UIInteractEntityActionClip(InteractEntityActionClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.hand = new UIToggle(UIKeys.ACTIONS_ITEM_MAIN_HAND, (b) -> this.editor.editMultiple(this.clip.hand, (hand) -> hand.set(b.getValue())));
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(this.hand);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.hand.setValue(this.clip.hand.get());
    }
}
