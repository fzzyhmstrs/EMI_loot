package fzzyhmstrs.emi_loot.server.function;

import com.mojang.serialization.Codec;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;

public class OminousBannerLootFunction implements LootFunction {

    public static final OminousBannerLootFunction INSTANCE = new OminousBannerLootFunction();
    public static final Codec<OminousBannerLootFunction> CODEC = Codec.unit(INSTANCE);

    @Override
    public LootFunctionType getType() {
        return EMILoot.OMINOUS_BANNER.get();
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        return ItemStack.EMPTY;
    }

}