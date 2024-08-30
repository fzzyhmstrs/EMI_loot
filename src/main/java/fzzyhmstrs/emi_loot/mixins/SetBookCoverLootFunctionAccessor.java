package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.loot.function.SetBookCoverLootFunction;
import net.minecraft.text.RawFilteredPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(SetBookCoverLootFunction.class)
public interface SetBookCoverLootFunctionAccessor {
	@Accessor
	Optional<String> getAuthor();

	@Accessor
	Optional<RawFilteredPair<String>> getTitle();

	@Accessor
	Optional<Integer> getGeneration();

	@Invoker
	WrittenBookContentComponent callApply(WrittenBookContentComponent current);
}