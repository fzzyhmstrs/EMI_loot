package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.block.Block;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.predicate.StatePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockStatePropertyLootCondition.class)
public interface BlockStatePropertyLootConditionAccessor{

  @Accessor(value = "block")
  Block getBlock();

  @Accessor(value = "properties")
  StatePredicate getProperties();

}
