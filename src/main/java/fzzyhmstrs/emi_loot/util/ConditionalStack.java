package fzzyhmstrs.emi_loot.util;

import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.List;

public record ConditionalStack(List<Pair<Integer, Text>> conditions, float weight, EmiIngredient ingredient) {
}