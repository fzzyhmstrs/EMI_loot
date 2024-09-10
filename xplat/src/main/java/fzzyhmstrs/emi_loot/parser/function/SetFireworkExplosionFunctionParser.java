package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetFireworkExplosionLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetFireworkExplosionLootFunction;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SetFireworkExplosionFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        List<MutableText> texts = new ArrayList<>();
        Optional<FireworkExplosionComponent.Type> shape = ((SetFireworkExplosionLootFunctionAccessor) function).getShape();
        if (shape.isPresent()) {
            Text s = shape.get().getName();
            texts.add(LText.translatable("emi_loot.function.firework_explosion.shape", s));
        }
        Optional<IntList> colors = ((SetFireworkExplosionLootFunctionAccessor) function).getColors();
        if (colors.isPresent()) {
            List<MutableText> cs = colors.get().intStream().mapToObj(i -> LText.literal(Integer.toString(i))).toList();
            texts.add(LText.translatable("emi_loot.function.firework_explosion.colors", ListProcessors.buildAndList(cs)));
        }
        Optional<IntList> fadeColors = ((SetFireworkExplosionLootFunctionAccessor) function).getFadeColors();
        if (fadeColors.isPresent()) {
            List<MutableText> cs = fadeColors.get().intStream().mapToObj(i -> LText.literal(Integer.toString(i))).toList();
            texts.add(LText.translatable("emi_loot.function.firework_explosion.fade_colors", ListProcessors.buildAndList(cs)));
        }
        Optional<Boolean> trail = ((SetFireworkExplosionLootFunctionAccessor) function).getTrail();
        if (trail.isPresent()) {
            if (trail.get()) {
                texts.add(LText.translatable("emi_loot.function.firework_explosion.trail"));
            } else {
                texts.add(LText.translatable("emi_loot.function.firework_explosion.no_trail"));
            }
        }
        Optional<Boolean> twinkle = ((SetFireworkExplosionLootFunctionAccessor) function).getTwinkle();
        if (twinkle.isPresent()) {
            if (twinkle.get()) {
                texts.add(LText.translatable("emi_loot.function.firework_explosion.twinkle"));
            } else {
                texts.add(LText.translatable("emi_loot.function.firework_explosion.no_twinkle"));
            }
        }

        if (!stack.isEmpty())
            stack.apply(DataComponentTypes.FIREWORK_EXPLOSION, SetFireworkExplosionLootFunction.DEFAULT_EXPLOSION, ((SetFireworkExplosionLootFunctionAccessor) function)::callApply);

        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.firework_explosion", ListProcessors.buildAndList(texts).getString()), ItemStack.EMPTY, conditionTexts);
    }
}