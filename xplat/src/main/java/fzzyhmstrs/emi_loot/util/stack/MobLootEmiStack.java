package fzzyhmstrs.emi_loot.util.stack;

import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;

public class MobLootEmiStack extends EmiStackWithTitle {
    public MobLootEmiStack(Identifier id) {
        super(id, "mob", "unknown_mob", EMILoot.Type.CHEST, 138, EMILoot.config.getLinkFormatting());
    }

    @Override
    public EmiStack copy() {
        return new MobLootEmiStack(this.id);
    }

    @Override
    protected MutableText getRawTitle(Identifier id, String unknownNamespace, String unknownPath, EMILoot.Type type) {
        return LText.empty();
    }
}