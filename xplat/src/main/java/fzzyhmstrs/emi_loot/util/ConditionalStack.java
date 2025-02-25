package fzzyhmstrs.emi_loot.util;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.List;

public record ConditionalStack(List<Pair<Integer, Text>> conditions, float weight, ListProvider ingredient) {

	public ConditionalStack(List<Pair<Integer, Text>> conditions, float weight, List<ItemStack> list) {
		this(conditions, weight, new ListProvider(list));
	}

	public EmiIngredient getIngredient() {
		return ingredient.getList().size() == 1 ? ingredient.getList().get(0) : new QuantityListEmiIngredient(ingredient.getList());
	}

	public List<EmiStack> getStacks() {
		return ingredient.getList();
	}

	public List<ItemStack> getRawStacks() {
		return ingredient.getRawList();
	}


	static class ListProvider {

		public ListProvider(List<ItemStack> list) {
			this.list = list;
		}

		private List<ItemStack> list;
		private List<EmiStack> builtList;

		public List<EmiStack> getList() {
			if (builtList == null) {
				builtList = list.stream().map(EmiStack::of).toList();
				list = null;
			}
			return builtList;
		}

		public List<ItemStack> getRawList() {
			return list == null ? builtList.stream().map(EmiStack::getItemStack).toList() : list;
		}
	}

}