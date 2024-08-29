package fzzyhmstrs.emi_loot.util;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.List;

public record ConditionalStack(List<Pair<Integer, Text>> conditions, float weight, List<EmiStack> ingredient) {

	public EmiIngredient getIngredient() {
		return ingredient.size() == 1 ? ingredient.get(0) : new QuantityListEmiIngredient(ingredient);
	}

}