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

public class SetAnyDamageLootFunction implements LootFunction {

    @Override
    public LootFunctionType getType() {
        return EMILoot.SET_ANY_DAMAGE;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        return ItemStack.EMPTY;
    }

    public static class Serializer implements JsonSerializer<SetAnyDamageLootFunction>{

        @Override
        public void toJson(JsonObject json, SetAnyDamageLootFunction object, JsonSerializationContext context) {
        }

        @Override
        public SetAnyDamageLootFunction fromJson(JsonObject json, JsonDeserializationContext context) {
            return new SetAnyDamageLootFunction();
        }
    }


}
