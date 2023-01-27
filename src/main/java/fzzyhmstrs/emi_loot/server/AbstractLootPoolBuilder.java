package fzzyhmstrs.emi_loot.server;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.entry.LootPoolEntry;

import java.util.Collection;

abstract public class AbstractLootPoolBuilder implements LootBuilder {

    public AbstractLootPoolBuilder(float rollWeight){
        this.rollWeight = rollWeight;
    }

    final float rollWeight;
    boolean isSimple = false;
    boolean isEmpty = false;
    ItemStack simpleStack = ItemStack.EMPTY;
    private final Multimap<LootTableParser.PostProcessor,LootPoolEntry> map = ArrayListMultimap.create();

    @Override
    public void addEntryForPostProcessing(LootTableParser.PostProcessor process, LootPoolEntry entry) {
        map.put(process,entry);
    }

    @Override
    public Collection<LootPoolEntry> getEntriesToPostProcess(LootTableParser.PostProcessor process) {
        return map.get(process);
    }
}
