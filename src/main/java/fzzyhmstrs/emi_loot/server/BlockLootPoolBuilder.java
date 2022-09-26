package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class BlockLootPoolBuilder implements LootBuilder {

    public BlockLootPoolBuilder(float rollWeight){
        this.rollWeight = rollWeight;
    }

    private final HashMap<ItemStack, Integer> map = new HashMap<>();
    private final float rollWeight;
    private Integer totalWeight = 0;
    HashMap<ItemStack, Float> builtMap = new HashMap<>();
    public static Identifier BLOCK_SENDER = new Identifier(EMILoot.MOD_ID,"block_sender");

    public void addItem(LootTableParser.ItemEntryResult result){

    }

    @Override
    public void build() {

    }
}
