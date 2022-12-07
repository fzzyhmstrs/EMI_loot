package fzzyhmstrs.emi_loot.parser.condition;

package fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public interface FunctionParser{
    public static FunctionParser EMPTY = new FunctionParser(){
        @Override
        public LootTableParser.LootFunctionResult parseFunction(LootFunction function){
            return LootTableParser.LootFunctionResult.EMPTY;
        }
    }
    
    LootTableParser.LootFunctionResult parseFunction(LootFunction function);
}
