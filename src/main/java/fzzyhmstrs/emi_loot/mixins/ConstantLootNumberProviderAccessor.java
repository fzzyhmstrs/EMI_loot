package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConstantLootNumberProvider.class)
public interface ConstantLootNumberProviderAccessor {

    @Accessor(value = "value")
    float getValue();

}
