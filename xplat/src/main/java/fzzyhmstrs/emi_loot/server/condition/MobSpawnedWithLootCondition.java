package fzzyhmstrs.emi_loot.server.condition;

import com.mojang.serialization.MapCodec;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;

public class MobSpawnedWithLootCondition implements LootCondition {

    public static final MobSpawnedWithLootCondition INSTANCE = new MobSpawnedWithLootCondition();
    public static final MapCodec<MobSpawnedWithLootCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public LootConditionType getType() {
        return EMILoot.SPAWNS_WITH.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return false;
    }

}