package fzzyhmstrs.emi_loot.server.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.JsonSerializer;

public class OminousBannerLootFunction implements LootFunction {

    @Override
    public LootFunctionType getType() {
        return EMILoot.OMINOUS_BANNER;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        return ItemStack.EMPTY;
    }

    public static class Serializer implements JsonSerializer<OminousBannerLootFunction>{

        @Override
        public void toJson(JsonObject json, OminousBannerLootFunction object, JsonSerializationContext context) {
        }

        @Override
        public OminousBannerLootFunction fromJson(JsonObject json, JsonDeserializationContext context) {
            return new OminousBannerLootFunction();
        }
    }


}
