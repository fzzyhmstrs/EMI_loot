package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UniformLootNumberProvider.class)
public interface UniformLootNumberProviderAccessor {

    @Accessor(value = "min")
    LootNumberProvider getMin();

    @Accessor(value = "max")
    LootNumberProvider getMax();

}
