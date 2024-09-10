package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.EMILootClientAgnos;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.fabric.util.BlockRendererImpl;
import fzzyhmstrs.emi_loot.fabric.util.IconEmiWidgetImpl;
import fzzyhmstrs.emi_loot.fabric.util.IconGroupEmiWidgetImpl;
import fzzyhmstrs.emi_loot.util.IconEmiWidget;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class EMILootClientAgnosFabric extends EMILootClientAgnos {
    static {
        EMILootClientAgnos.delegate = new EMILootClientAgnosFabric();
    }

    @Override
    protected IconEmiWidget createIconEmiWidgetAgnos(int x, int y, int keyIndex, Text text) {
        return new IconEmiWidgetImpl(x, y, keyIndex, text);
    }

    @Override
    protected IconGroupEmiWidget createIconGroupEmiWidgetAgnos(int x, int y, ClientBuiltPool pool) {
        return new IconGroupEmiWidgetImpl(x, y, pool);
    }

    @Override
    protected void renderBlockAgnos(BlockState blockState, DrawContext draw, int x, int y, float delta) {
        BlockRendererImpl.render(blockState, draw, x, y, delta);
    }
}
