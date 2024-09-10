package fzzyhmstrs.emi_loot.server.function;

import com.mojang.serialization.MapCodec;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;

public class SetAnyDamageLootFunction implements LootFunction {

    public static final SetAnyDamageLootFunction INSTANCE = new SetAnyDamageLootFunction();
    public static final MapCodec<SetAnyDamageLootFunction> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public LootFunctionType getType() {
        return EMILoot.SET_ANY_DAMAGE.get();
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        return ItemStack.EMPTY;
    }

}