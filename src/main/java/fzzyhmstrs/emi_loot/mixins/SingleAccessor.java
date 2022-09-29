package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "net/minecraft/predicate/entity/EntityTypePredicate$Single")
public interface SingleAccessor {

    @Accessor(value = "type")
    EntityType<?> getType();

}
