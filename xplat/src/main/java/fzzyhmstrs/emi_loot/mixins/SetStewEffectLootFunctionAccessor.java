package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SetStewEffectLootFunction.class)
public interface SetStewEffectLootFunctionAccessor {

    @Accessor(value = "effects")
    Map<StatusEffect, LootNumberProvider> getEffects();

}
