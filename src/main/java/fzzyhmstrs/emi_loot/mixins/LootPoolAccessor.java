package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPool.class)
public interface LootPoolAccessor {

    @Accessor(value = "entries")
    LootPoolEntry[] getEntries();

    @Accessor(value = "rolls")
    LootNumberProvider getRolls();

}
