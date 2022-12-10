package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.block.Block;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BlockPredicate.class)
public interface BlockPredicateAccessor {

    @Accessor(value = "tag")
    TagKey<Block> getTag();

    @Accessor(value = "blocks")
    Set<Block> getBlocks();

    @Accessor(value = "state")
    StatePredicate getState();

    @Accessor(value = "nbt")
    NbtPredicate getNbt();

}
