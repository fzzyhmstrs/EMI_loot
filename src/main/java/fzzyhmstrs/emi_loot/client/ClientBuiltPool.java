package fzzyhmstrs.emi_loot.client;

import dev.emi.emi.api.stack.EmiIngredient;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.List;

public record ClientBuiltPool(List<Pair<Integer, Text>> list, Float2ObjectMap<EmiIngredient> stackMap){}
