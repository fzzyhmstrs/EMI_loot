package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.WeatherCheckLootCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeatherCheckLootCondition.class)
public interface WeatherCheckLootConditionAccessor {

    @Accessor(value = "raining")
    Boolean getRaining();

    @Accessor(value = "thundering")
    Boolean getThundering();

}
