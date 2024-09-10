package fzzyhmstrs.emi_loot;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;

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

    public static void renderBlock(BlockState blockState, DrawContext draw, int x, int y, float delta) {
        delegate.renderBlockAgnos(blockState, draw, x, y, delta);
    }

    protected abstract void renderBlockAgnos(BlockState blockState, DrawContext draw, int x, int y, float delta);
}
