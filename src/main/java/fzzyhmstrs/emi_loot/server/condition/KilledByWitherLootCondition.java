package fzzyhmstrs.emi_loot.server.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonSerializer;

public class KilledByWitherLootCondition implements LootCondition {
    @Override
    public LootConditionType getType() {
        return EMILoot.WITHER_KILL;
    }

    @Override
    public boolean test(LootContext lootContext) {
        return false;
    }

    public static class Serializer implements JsonSerializer<KilledByWitherLootCondition>{

        @Override
        public void toJson(JsonObject json, KilledByWitherLootCondition object, JsonSerializationContext context) {
        }

        @Override
        public KilledByWitherLootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return new KilledByWitherLootCondition();
        }
    }


}
