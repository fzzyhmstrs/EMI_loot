package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BinomialLootNumberProvider.class)
public interface BinomialLootNumberProviderAccessor {

    @Accessor(value = "n")
    LootNumberProvider getN();

    @Accessor(value = "p")
    LootNumberProvider getP();

}
