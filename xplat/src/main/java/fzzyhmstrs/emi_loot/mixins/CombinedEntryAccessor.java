package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CombinedEntry.class)
public interface CombinedEntryAccessor {

    @Accessor(value = "children")
    LootPoolEntry[] getChildren();

}
