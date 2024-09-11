package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.SetLootTableLootFunction;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetLootTableLootFunction.class)
public interface SetLootTableLootFunctionAccessor {

    @Accessor(value = "id")
    Identifier getId();

}
