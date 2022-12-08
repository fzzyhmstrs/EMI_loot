package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.CopyNameLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CopyNameLootFunction.class)
public interface CopyNameLootFunctionAccessor {

    @Accessor(value = "source")
    CopyNameLootFunction.Source getSource();

}
