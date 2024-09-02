package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetDamageLootFunction.class)
public interface SetDamageLootFunctionAccessor {

    @Accessor(value = "durabilityRange")
    LootNumberProvider getDurabilityRange();

    @Accessor(value = "add")
    boolean getAdd();

}
