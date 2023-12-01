package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.function.SetStewEffectLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SetStewEffectLootFunction.class)
public interface SetStewEffectLootFunctionAccessor {

    @Accessor(value = "stewEffects")
    List<SetStewEffectLootFunction.StewEffect> getEffects();

}
