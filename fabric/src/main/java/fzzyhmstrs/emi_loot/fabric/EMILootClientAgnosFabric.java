package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.EMILootClientAgnos;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;

public class EMILootClientAgnosFabric extends EMILootClientAgnos {
    static {
        EMILootClientAgnos.delegate = new EMILootClientAgnosFabric();
    }

    @Override
    protected void renderBlockAgnos(BlockState blockState, DrawContext draw, int x, int y, float delta) {
        BlockRendererFabric.render(blockState, draw, x, y, delta);
    }
}
