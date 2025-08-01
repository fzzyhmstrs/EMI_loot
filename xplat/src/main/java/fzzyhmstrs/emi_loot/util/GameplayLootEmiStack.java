package fzzyhmstrs.emi_loot.util;

import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootAgnos;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class GameplayLootEmiStack extends EmiStackWithTitle {
    public GameplayLootEmiStack(Identifier id) {
        super(id, "gameplay", "unknown_gameplay", EMILoot.Type.GAMEPLAY, EMILoot.config.isTooltipStyle() ? 138 : 148);
    }
}
