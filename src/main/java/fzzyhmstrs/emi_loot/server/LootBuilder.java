package fzzyhmstrs.emi_loot.server;

import java.util.List;

public interface LootBuilder{
    void build();
    List<LootTableParser.ItemEntryResult> revert();
}
