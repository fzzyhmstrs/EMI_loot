package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetCountLootFunction.class)
public interface SetCountLootFunctionAccessor {

    @Accessor(value = "countRange")
    LootNumberProvider getCountRange();

    @Accessor(value = "add")
    boolean getAdd();

}
