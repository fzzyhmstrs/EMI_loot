package fzzyhmstrs.emi_loot.mixins;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.loot.function.SetFireworkExplosionLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(SetFireworkExplosionLootFunction.class)
public interface SetFireworkExplosionLootFunctionAccessor {
	@Accessor
	Optional<FireworkExplosionComponent.Type> getShape();

	@Accessor
	Optional<IntList> getColors();

	@Accessor
	Optional<IntList> getFadeColors();

	@Accessor
	Optional<Boolean> getTrail();

	@Accessor
	Optional<Boolean> getTwinkle();

	@Invoker
	FireworkExplosionComponent callApply(FireworkExplosionComponent current);
}