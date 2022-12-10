package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.ExplorationMapLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;

public class ExplorationMapFunctionParser implements FunctionParser {
    
    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        ItemStack mapStack;
        String typeKey = "emi_loot.map.unknown";
        if (!stack.isOf(Items.MAP)){
            mapStack = stack;
        } else {
            MapIcon.Type decoration = ((ExplorationMapLootFunctionAccessor)function).getDecoration();
            TagKey<Structure> destination = ((ExplorationMapLootFunctionAccessor)function).getDestination();
            mapStack = new ItemStack(Items.FILLED_MAP);
            MapState.addDecorationsNbt(mapStack, BlockPos.ORIGIN,"+",decoration);
            typeKey = "emi_loot.map."+ destination.id().getPath();
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.map", LText.translatable(typeKey).getString()), mapStack, conditionTexts);
    }
}
