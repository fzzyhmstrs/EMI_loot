package fzzyhmstrs.emi_loot.mixins;

import com.google.common.collect.BiMap;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootContextTypes.class)
public interface LootContextTypesAccessor {
	@Accessor
	static BiMap<Identifier, LootContextType> getMAP() {
		throw new UnsupportedOperationException();
	}
}