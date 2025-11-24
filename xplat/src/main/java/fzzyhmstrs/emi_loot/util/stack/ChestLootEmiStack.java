package fzzyhmstrs.emi_loot.util.stack;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.util.Identifier;

public class ChestLootEmiStack extends EmiStackWithTitle {
    public ChestLootEmiStack(Identifier id) {
        super(id, "chest", "unknown_chest", EMILoot.Type.CHEST, 138, EMILoot.config.getLinkFormatting());
    }
}