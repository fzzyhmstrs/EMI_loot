package fzzyhmstrs.emi_loot.util.stack;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.util.Identifier;

public class ArchaeologyLootEmiStack extends EmiStackWithTitle {
    public ArchaeologyLootEmiStack(Identifier id) {
        super(id, "archaeology", "unknown_archaeology", EMILoot.Type.ARCHAEOLOGY, EMILoot.config.isTooltipStyle() ? 138 : 148, EMILoot.config.getLinkFormatting());
    }
}