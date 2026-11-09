package mchorse.bbs_mod.ui.film.utils;

import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.film.UIFilmPanel;
import mchorse.bbs_mod.ui.film.utils.undo.UIUndoHistoryOverlay;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs_mod.ui.utils.icons.Icon;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.Direction;

/**
 * BBSvE: radial quick-tools menu. Opens from the film editor icon bar and
 * puts the most used editor actions in a ring so they are easy to hit on a phone.
 */
public class UIRadialMenuOverlay extends UIOverlayPanel
{
    private static final int COUNT = 10;
    private static final int RADIUS = 80;

    private final UIFilmPanel panel;
    private int index;

    public UIRadialMenuOverlay(UIFilmPanel panel)
    {
        super(IKey.raw("Quick tools"));

        this.panel = panel;

        this.item(Icons.PLAY, "Play / pause", () -> panel.togglePlayback());
        this.item(Icons.FRAME_NEXT, "Next clip", () ->
        {
            if (panel.getData() != null)
            {
                panel.setCursor(panel.getData().camera.findNextTick(panel.getCursor()));
            }
        });
        this.item(Icons.FRUSTUM, "Camera editor", () -> panel.showPanel(panel.cameraEditor));
        this.item(Icons.SCENE, "Replay editor", () -> panel.showPanel(panel.replayEditor));
        this.item(Icons.REDO, "Redo", () -> panel.redo());
        this.item(Icons.LIST, "Undo history", () ->
        {
            if (panel.getUndoHandler() != null)
            {
                UIOverlay.addOverlay(panel.getContext(), new UIUndoHistoryOverlay(panel), 200, 0.6F);
            }
        });
        this.item(Icons.UNDO, "Undo", () -> panel.undo());
        this.item(Icons.REFRESH, "Toggle loop", () -> BBSSettings.editorLoop.set(!BBSSettings.editorLoop.get()));
        this.item(Icons.HELICOPTER, "Toggle flight", () ->
        {
            if (panel.getData() != null)
            {
                panel.toggleFlight();
            }
        });
        this.item(Icons.FRAME_PREV, "Previous clip", () ->
        {
            if (panel.getData() != null)
            {
                panel.setCursor(panel.getData().camera.findPreviousTick(panel.getCursor()));
            }
        });
    }

    private void item(Icon icon, String label, Runnable action)
    {
        double angle = -Math.PI / 2D + (Math.PI * 2D * this.index) / COUNT;
        int dx = (int) Math.round(Math.cos(angle) * RADIUS);
        int dy = (int) Math.round(Math.sin(angle) * RADIUS);

        UIIcon button = new UIIcon(icon, (b) ->
        {
            this.close();
            action.run();
        });

        button.tooltip(IKey.raw(label), Direction.TOP);
        button.relative(this.content).x(0.5F, dx).y(0.5F, dy).wh(28, 28).anchor(0.5F);

        this.content.add(button);
        this.index += 1;
    }
}
