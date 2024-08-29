package fzzyhmstrs.emi_loot.server.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;

public class OminousBannerLootFunction implements LootFunction {

    public static final OminousBannerLootFunction INSTANCE = new OminousBannerLootFunction();
    public static final MapCodec<OminousBannerLootFunction> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public LootFunctionType getType() {
        return EMILoot.OMINOUS_BANNER;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        return ItemStack.EMPTY;
    }

}