package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.loot.entry.LootPoolEntry;

import java.util.Collection;
import java.util.List;

public interface LootBuilder{
    void addEntryForPostProcessing(LootTableParser.PostProcessor process, LootPoolEntry entry);
    Collection<LootPoolEntry> getEntriesToPostProcess(LootTableParser.PostProcessor process);
    void addItem(LootTableParser.ItemEntryResult result);
    void build();
    List<LootTableParser.ItemEntryResult> revert();
}
