package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetDamageLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class SetDamageFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        LootNumberProvider provider = ((SetDamageLootFunctionAccessor)function).getDurabilityRange();
        float rollAvg = NumberProcessors.getRollAvg(provider);
        boolean add = ((SetDamageLootFunctionAccessor)function).getAdd();
        int md = stack.getMaxDamage();
        float damage;
        if (add){
            int dmg = stack.getDamage();
            damage = MathHelper.clamp(((float )dmg)/md + (rollAvg * md),0,md);
        } else {
            damage = MathHelper.clamp(rollAvg * md,0,md);
        }
        stack.setDamage(MathHelper.floor(damage));
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.damage",Integer.toString((int)(rollAvg*100))), stack, conditionTexts);
    }
}
