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

public class ArchaeologyLootEmiStack extends EmiStackWithTitle {
    public ArchaeologyLootEmiStack(Identifier id) {
        super(id, "archaeology", "unknown_archaeology", EMILoot.Type.ARCHAEOLOGY, EMILoot.config.isTooltipStyle() ? 138 : 148);
    }
}
