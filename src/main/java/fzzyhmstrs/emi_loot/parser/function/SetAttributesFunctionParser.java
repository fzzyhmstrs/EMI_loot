package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetAttributesLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.text.MutableText;

import java.util.LinkedList;
import java.util.List;

public class SetAttributesFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        List<SetAttributesLootFunction.Attribute> attributes = ((SetAttributesLootFunction)function).attributes;
        List<MutableText> list = new LinkedList<>();
        for (SetAttributesLootFunction.Attribute attribute : attributes) {
            String name = attribute.name;
            LootNumberProvider amount = attribute.amount;
            EntityAttributeModifier.Operation operation = attribute.operation;
            if (operation == EntityAttributeModifier.Operation.ADDITION){
                list.add(LText.translatable("emi_loot.function.set_attributes.add",NumberProcessors.getRollAvg(amount),name));
            } else if (operation == EntityAttributeModifier.Operation.MULTIPLY_BASE){
                list.add(LText.translatable("emi_loot.function.set_attributes.multiply_base",NumberProcessors.getRollAvg(amount) + 1,name));
            } else {
                list.add(LText.translatable("emi_loot.function.set_attributes.multiply",NumberProcessors.getRollAvg(amount) + 1,name));
            }
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_attributes", ListProcessors.buildAndList(list).getString()), ItemStack.EMPTY, conditionTexts);
    }
}
