package fzzyhmstrs.emi_loot.forge.util;

import fzzyhmstrs.emi_loot.util.IconEmiWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class IconEmiWidgetImpl extends IconEmiWidget {
    public IconEmiWidgetImpl(int x, int y, int keyIndex, Text text) {
        super(x, y, keyIndex, text);
    }

    /**
     * This is the method that fails to remap
     * @see IconEmiWidget#renderInternal(DrawContext, int, int, float)
     */
    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        renderInternal(matrices, mouseX, mouseY, delta);
    }
}
