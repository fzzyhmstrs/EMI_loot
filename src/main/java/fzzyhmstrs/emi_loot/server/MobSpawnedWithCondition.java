package fzzyhmstrs.emi_loot.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonSerializer;

public class MobSpawnedWithCondition implements LootCondition {
    @Override
    public LootConditionType getType() {
        return EMILoot.SPAWNS_WITH;
    }

    @Override
    public boolean test(LootContext lootContext) {
        return false;
    }

    public static class Serializer implements JsonSerializer<MobSpawnedWithCondition>{

        @Override
        public void toJson(JsonObject json, MobSpawnedWithCondition object, JsonSerializationContext context) {
        }

        @Override
        public MobSpawnedWithCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return new MobSpawnedWithCondition();
        }
    }


}
