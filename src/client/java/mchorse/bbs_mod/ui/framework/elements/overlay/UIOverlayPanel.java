package mchorse.bbs_mod.ui.framework.elements.overlay;

import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.graphics.window.Window;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.Keys;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.events.UIOverlayCloseEvent;
import mchorse.bbs_mod.ui.framework.elements.utils.EventPropagation;
import mchorse.bbs_mod.ui.framework.elements.utils.UILabel;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.colors.Colors;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class UIOverlayPanel extends UIElement
{
    public UILabel title;
    public UIElement icons;
    public UIIcon close;
    public UIElement content;

    private boolean moving;
    private int lastX;
    private int lastY;

    private int initialOffsetX;
    private int initialOffsetY;

    public UIOverlayPanel(IKey title)
    {
        super();

        this.title = UI.label(title);
        this.close = new UIIcon(Icons.CLOSE, (b) -> this.close());
        this.content = new UIElement();
        this.icons = new UIElement();

        this.title.labelAnchor(0, 0.5F).relative(this).xy(6, 0).w(0.6F).h(20);
        this.icons.relative(this).x(1F, -20).y(0).w(20).h(1F).column(0).stretch();
        this.content.relative(this).xy(0, 20).w(1F, -20).h(1F, -20);

        this.icons.add(this.close);

        this.add(this.title, this.icons, this.content);

        this.mouseEventPropagataion(EventPropagation.BLOCK_INSIDE);
    }

    public void setInitialOffset(int x, int y)
    {
        this.initialOffsetX = x;
        this.initialOffsetY = y;
    }

    public void onClose(Consumer<UIOverlayCloseEvent> callback)
    {
        this.events.register(UIOverlayCloseEvent.class, callback);
    }

    public void close()
    {
        UIElement parent = this.getParent();

        if (parent instanceof UIOverlay)
        {
            ((UIOverlay) parent).closeItself();
        }
    }

    public void confirm()
    {}

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.title.area.isInside(context))
        {
            if (Window.isCtrlPressed())
            {
                this.flex.x.offset = this.initialOffsetX;
                this.flex.y.offset = this.initialOffsetY;

                this.getParent().resize();

                return true;
            }

            this.moving = true;
            this.lastX = context.mouseX;
            this.lastY = context.mouseY;

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.moving = super.subMouseReleased(context);

        return false;
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (!context.isFocused())
        {
            if (context.isPressed(Keys.CLOSE))
            {
                this.close();

                return true;
            }
            else if (context.isPressed(Keys.CONFIRM))
            {
                this.confirm();

                return true;
            }
        }

        return super.subKeyPressed(context);
    }

    @Override
    public void render(UIContext context)
    {
        if (this.moving && (context.mouseX != this.lastX || context.mouseY != this.lastY))
        {
            int dx = context.mouseX - this.lastX;
            int dy = context.mouseY - this.lastY;
            int lastX = this.area.x;
            int lastY = this.area.y;

            this.flex.x.offset += dx;
            this.flex.y.offset += dy;

            this.getParent().resize();

            if (lastX == this.area.x) this.flex.x.offset -= dx;
            if (lastY == this.area.y) this.flex.y.offset -= dy;

            this.lastX = context.mouseX;
            this.lastY = context.mouseY;
        }

        this.renderBackground(context);

        super.render(context);
    }

    protected void renderBackground(UIContext context)
    {
        int color = BBSSettings.primaryColor.get();

        if (BBSSettings.glassUi != null && BBSSettings.glassUi.get())
        {
            this.renderGlass(context, color);
        }
        else
        {
            context.batcher.dropShadow(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 10, Colors.A25 | color, color);
            this.area.render(context.batcher, Colors.mulRGB(color | Colors.A100, 0.1F));

            this.icons.area.render(context.batcher, Colors.CONTROL_BAR);
        }

        if (this.close.area.isInside(context))
        {
            this.close.area.render(context.batcher, Colors.RED | Colors.A100);
        }

        if (this.title.area.isInside(context))
        {
            context.batcher.icon(Icons.ALL_DIRECTIONS, Colors.GRAY, this.area.mx(), this.title.area.my(), 0.5F, 0.5F);
        }
    }

    public void onClose()
    {
        this.events.emit(new UIOverlayCloseEvent(this));
    }

    /* BBSvE: frosted glass look, every part adjustable in Settings > glass */
    private void renderGlass(UIContext context, int color)
    {
        int x1 = this.area.x;
        int y1 = this.area.y;
        int x2 = this.area.ex();
        int y2 = this.area.ey();
        int h = y2 - y1;
        int r = Math.max(0, Math.min(BBSSettings.glassRadius.get(), Math.min(x2 - x1, h) / 2));
        int rgb = color & 0xffffff;
        int top = (pct(BBSSettings.glassOpacityTop.get()) << 24) | lerpRGB(rgb, 0xffffff, 1F - BBSSettings.glassTint.get() / 100F);
        int bottom = (pct(BBSSettings.glassOpacityBottom.get()) << 24) | lerpRGB(rgb, 0x000000, BBSSettings.glassDarkness.get() / 100F);

        if (BBSSettings.glassFlip.get())
        {
            int swap = top;

            top = bottom;
            bottom = swap;
        }

        if (BBSSettings.glassShadow.get())
        {
            context.batcher.dropShadow(x1, y1, x2, y2, BBSSettings.glassShadowSize.get(), (pct(BBSSettings.glassGlow.get()) << 24) | rgb, rgb);
        }

        /* Middle section in one gradient, rounded corner rows one by one */
        float span = Math.max(1, h - 1);

        context.batcher.gradientVBox(x1, y1 + r, x2, y2 - r, lerpARGB(top, bottom, r / span), lerpARGB(top, bottom, (h - r) / span));

        for (int i = 0; i < r; i++)
        {
            int inset = cornerInset(r, i);

            context.batcher.box(x1 + inset, y1 + i, x2 - inset, y1 + i + 1, lerpARGB(top, bottom, i / span));
            context.batcher.box(x1 + inset, y2 - i - 1, x2 - inset, y2 - i, lerpARGB(top, bottom, (h - 1 - i) / span));
        }

        /* Soft shine on the upper part */
        int shine = pct(BBSSettings.glassShine.get());

        if (shine > 0)
        {
            context.batcher.gradientVBox(x1 + 2, y1 + r, x2 - 2, y1 + Math.max(r + 1, h / 3), (shine << 24) | 0xffffff, 0x00ffffff);
        }

        /* Darker strip behind the title */
        if (BBSSettings.glassTitleStrip.get() && y1 + 20 < y2 - r)
        {
            context.batcher.box(x1 + 1, y1 + r, x2 - 1, y1 + 20, 0x30000000);
        }

        /* Bright rim: strongest on top, fainter on the sides and bottom */
        if (BBSSettings.glassRim.get())
        {
            int base = rgb;

            if (!BBSSettings.glassRimAccent.get())
            {
                base = 0xffffff;
            }

            int strong = pct(BBSSettings.glassRimBrightness.get());
            int hi = (strong << 24) | base;
            int mid = ((strong * 55 / 100) << 24) | base;
            int low = BBSSettings.glassRimBottom.get() ? (((strong * 33 / 100) << 24) | base) : 0;

            context.batcher.box(x1 + r, y1, x2 - r, y1 + 1, hi);
            context.batcher.box(x1, y1 + r, x1 + 1, y2 - r, mid);
            context.batcher.box(x2 - 1, y1 + r, x2, y2 - r, mid);

            if (low != 0)
            {
                context.batcher.box(x1 + r, y2 - 1, x2 - r, y2, low);
            }

            for (int i = 0; i < r; i++)
            {
                int inset = cornerInset(r, i);

                context.batcher.box(x1 + inset, y1 + i, x1 + inset + 1, y1 + i + 1, hi);
                context.batcher.box(x2 - inset - 1, y1 + i, x2 - inset, y1 + i + 1, hi);

                if (low != 0)
                {
                    context.batcher.box(x1 + inset, y2 - i - 1, x1 + inset + 1, y2 - i, low);
                    context.batcher.box(x2 - inset - 1, y2 - i - 1, x2 - inset, y2 - i, low);
                }
            }
        }
    }

    private static int pct(int value)
    {
        return Math.max(0, Math.min(100, value)) * 255 / 100;
    }

    private static int cornerInset(int r, int row)
    {
        double dy = r - row - 0.5D;

        return (int) Math.ceil(r - Math.sqrt(Math.max(0D, r * r - dy * dy)));
    }

    private static int lerpRGB(int a, int b, float t)
    {
        int red = (int) (((a >> 16) & 255) * (1F - t) + ((b >> 16) & 255) * t);
        int green = (int) (((a >> 8) & 255) * (1F - t) + ((b >> 8) & 255) * t);
        int blue = (int) ((a & 255) * (1F - t) + (b & 255) * t);

        return (red << 16) | (green << 8) | blue;
    }

    private static int lerpARGB(int a, int b, float t)
    {
        int alpha = (int) (((a >>> 24) & 255) * (1F - t) + ((b >>> 24) & 255) * t);

        return (alpha << 24) | lerpRGB(a, b, t);
    }
}