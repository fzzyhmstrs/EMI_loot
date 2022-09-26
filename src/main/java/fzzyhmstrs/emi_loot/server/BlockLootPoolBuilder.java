package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockLootPoolBuilder implements LootBuilder {

    public BlockLootPoolBuilder(float rollWeight, List<LootTableParser.LootConditionResult> conditions){
        this.rollWeight = rollWeight;
        this.conditions = conditions;
    }

    private final HashMap<TextKey, Map<ItemStack,Integer>> map = new HashMap<>();
    private final float rollWeight;
    private final List<LootTableParser.LootConditionResult> conditions;
    private Integer totalWeight = 0;
    HashMap<ItemStack, Float> builtMap = new HashMap<>();
    public static Identifier BLOCK_SENDER = new Identifier(EMILoot.MOD_ID,"block_sender");

    public void addItem(LootTableParser.ItemEntryResult result){

    }

    @Override
    public void build() {

    }
}
