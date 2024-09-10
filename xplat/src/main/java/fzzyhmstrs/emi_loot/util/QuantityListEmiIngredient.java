package fzzyhmstrs.emi_loot.util;

import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class QuantityListEmiIngredient implements EmiIngredient {
	private final List<EmiStack> ingredients;

	public QuantityListEmiIngredient(List<EmiStack> ingredients) {
		this.ingredients = ingredients;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof QuantityListEmiIngredient other) {
			return other.getEmiStacks().equals(this.getEmiStacks());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ingredients.hashCode();
	}

	@Override
	public EmiIngredient copy() {
		return new QuantityListEmiIngredient(ingredients);
	}

	@Override
	public String toString() {
		return "Ingredient" + getEmiStacks();
	}

	@Override
	public List<EmiStack> getEmiStacks() {
		return ingredients;
	}

	@Override
	public long getAmount() {
		return 1;
	}

	@Override
	public EmiIngredient setAmount(long amount) {
		return this;
	}

	@Override
	public float getChance() {
		return 1f;
	}

	@Override
	public EmiIngredient setChance(float chance) {
		return this;
	}

	@Override
	public void render(DrawContext draw, int x, int y, float delta, int flags) {
		int item = (int) (System.currentTimeMillis() / 1000 % ingredients.size());
		EmiIngredient current = ingredients.get(item);
		if ((flags & RENDER_ICON) != 0) {
			current.render(draw, x, y, delta, ~RENDER_AMOUNT);
		}
		if ((flags & RENDER_AMOUNT) != 0) {
			current.render(draw, x, y, delta, RENDER_AMOUNT);
		}
		if ((flags & RENDER_INGREDIENT) != 0) {
			EmiRender.renderIngredientIcon(this, draw, x, y);
		}
	}

	@Override
	public List<TooltipComponent> getTooltip() {
		List<TooltipComponent> tooltip = Lists.newArrayList();
		tooltip.add(TooltipComponent.of(LText.translatable("tooltip.emi.accepts").asOrderedText()));
		tooltip.add(EmiTooltipComponents.getIngredientTooltipComponent(ingredients));
		int item = (int) (System.currentTimeMillis() / 1000 % ingredients.size());
		tooltip.addAll(ingredients.get(item).getTooltip());
		return tooltip;
	}
}