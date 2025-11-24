package fzzyhmstrs.emi_loot;

import dev.emi.emi.api.recipe.EmiRecipe;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.util.IconEmiWidget;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public abstract class EMILootClientAgnos {
    public static EMILootClientAgnos delegate;

    static {
        try {
            Class.forName("fzzyhmstrs.emi_loot.fabric.EMILootClientAgnosFabric");
        } catch (Throwable t) {
        }
        try {
            Class.forName("fzzyhmstrs.emi_loot.forge.EMILootClientAgnosForge");
        } catch (Throwable t) {
        }
    }

    public static IconEmiWidget createIconEmiWidget(int x, int y, int keyIndex, Text text) {
        return delegate.createIconEmiWidgetAgnos(x, y, keyIndex, text);
    }

    protected abstract IconEmiWidget createIconEmiWidgetAgnos(int x, int y, int keyIndex, Text text);

    public static IconGroupEmiWidget createIconGroupEmiWidget(int x, int y, ClientBuiltPool pool, EmiRecipe recipe) {
        return delegate.createIconGroupEmiWidgetAgnos(x, y, pool, recipe);
    }

    protected abstract IconGroupEmiWidget createIconGroupEmiWidgetAgnos(int x, int y, ClientBuiltPool pool, EmiRecipe recipe);

    public static void renderBlock(BlockState blockState, DrawContext draw, int x, int y, float delta) {
        delegate.renderBlockAgnos(blockState, draw, x, y, delta);
    }

    protected abstract void renderBlockAgnos(BlockState blockState, DrawContext draw, int x, int y, float delta);
}