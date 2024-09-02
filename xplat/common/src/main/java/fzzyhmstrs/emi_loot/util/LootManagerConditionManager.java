package fzzyhmstrs.emi_loot.util;


import net.minecraft.loot.LootDataKey;

import java.util.Map;

public interface LootManagerConditionManager {

    Map<LootDataKey<?>, ?> getKeysToValues();

}
