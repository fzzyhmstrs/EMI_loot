package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public class SetAnyDamageFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        if (EMILoot.DEBUG) EMILoot.LOGGER.info("Parsing an any-damage function");
        stack.setDamage((1 + stack.getMaxDamage())/2);
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_any_damage"), stack, conditionTexts);
    }
}
