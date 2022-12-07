package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.parser.LootTableParser;

import java.util.List;

public interface LootBuilder{
    void addItem(LootTableParser.ItemEntryResult result);
    void build();
    List<LootTableParser.ItemEntryResult> revert();
}
