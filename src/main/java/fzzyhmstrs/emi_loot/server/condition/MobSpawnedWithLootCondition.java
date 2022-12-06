package fzzyhmstrs.emi_loot.server.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonSerializer;

public class MobSpawnedWithLootCondition implements LootCondition {
    @Override
    public LootConditionType getType() {
        return EMILoot.SPAWNS_WITH;
    }

    @Override
    public boolean test(LootContext lootContext) {
        return false;
    }

    public static class Serializer implements JsonSerializer<MobSpawnedWithLootCondition>{

        @Override
        public void toJson(JsonObject json, MobSpawnedWithLootCondition object, JsonSerializationContext context) {
        }

        @Override
        public MobSpawnedWithLootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return new MobSpawnedWithLootCondition();
        }
    }


}
