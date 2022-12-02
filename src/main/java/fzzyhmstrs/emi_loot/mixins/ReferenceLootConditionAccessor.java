package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.loot.condition.ReferenceLootCondition;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ReferenceLootCondition.class)
public interface ReferenceLootConditionAccessor {

    @Accessor(value = "id")
    Identifier getId();

}
