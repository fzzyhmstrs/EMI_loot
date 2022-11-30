package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NbtPredicate.class)
public interface NbtPredicateAccessor {

    @Accessor(value = "nbt")
    NbtCompound getNbt();

}
