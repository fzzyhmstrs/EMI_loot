package fzzyhmstrs.emi_loot.fabric.util;

import dev.emi.emi.api.recipe.EmiRecipe;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import net.minecraft.client.gui.DrawContext;

public class IconGroupEmiWidgetImpl extends IconGroupEmiWidget {
    public IconGroupEmiWidgetImpl(int x, int y, ClientBuiltPool pool, EmiRecipe recipe) {
        super(x, y, pool, recipe);
    }

    /**
     * This is the method that fails to remap
     * @see IconGroupEmiWidget#renderInternal(DrawContext, int, int, float)
     */
    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        renderInternal(matrices, mouseX, mouseY, delta);
    }
}