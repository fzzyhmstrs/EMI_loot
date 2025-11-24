package fzzyhmstrs.emi_loot.util.stack;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.util.Identifier;

public class GameplayLootEmiStack extends EmiStackWithTitle {
    public GameplayLootEmiStack(Identifier id) {
        super(id, "gameplay", "unknown_gameplay", EMILoot.Type.GAMEPLAY, EMILoot.config.isTooltipStyle() ? 138 : 148, EMILoot.config.getLinkFormatting());
    }
}