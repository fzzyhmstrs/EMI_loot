package fzzyhmstrs.emi_loot.server.condition;

import com.mojang.serialization.MapCodec;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;

public class BlownUpByCreeperLootCondition implements LootCondition {

    public static final BlownUpByCreeperLootCondition INSTANCE = new BlownUpByCreeperLootCondition();
    public static final MapCodec<BlownUpByCreeperLootCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public LootConditionType getType() {
        return EMILoot.CREEPER.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return false;
    }

}