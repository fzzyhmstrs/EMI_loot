package fzzyhmstrs.emi_loot.parser.condition;

package fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public class SimpleFunctionParser{
    
    private final TextKey key;
  
    public SimpleFunctionParser(String key){
      this.key = TextKey.of(key);
    }
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, List<TextKey> conditionTexts){
        return new LootFunctionResult(key,ItemStack.EMPTY,conditionTexts)
    }
}
