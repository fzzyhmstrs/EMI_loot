package fzzyhmstrs.emi_loot.forge;

import fzzyhmstrs.emi_loot.EMILootClientAgnos;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;

public class EMILootClientAgnosForge extends EMILootClientAgnos {
    static {
        EMILootClientAgnos.delegate = new EMILootClientAgnosForge();
    }

    @Override
    protected void renderBlockAgnos(BlockState blockState, DrawContext draw, int x, int y, float delta) {
        BlockRendererForge.render(blockState, draw, x, y, delta);
    }
}
