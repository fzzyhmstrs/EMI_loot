package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MobLootTableSender implements LootSender<MobLootPoolBuilder> {

    public MobLootTableSender(Identifier id){
        this.id = id;
    }

    private final Identifier id;
    final List<MobLootPoolBuilder> builderList = new LinkedList<>();
    public static Identifier MOB_SENDER = new Identifier(EMILoot.MOD_ID,"mob_sender");

    @Override
    public void send(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        //start with the loot pool ID and the number of builders to write
        buf.writeIdentifier(id);
        buf.writeByte(builderList.size());
        builderList.forEach((builder)->{
            //start by building the builder
            builder.build();

            //write size of the builders condition set
            buf.writeByte(builder.conditions.size());
            //write the textkey of each of those conditions
            builder.conditions.forEach((lootConditionResult -> lootConditionResult.text().toBuf(buf)));

            //write size of the builders function set
            buf.writeByte(builder.functions.size());
            //write the textkey of the functions
            builder.functions.forEach((lootFunctionResult)-> lootFunctionResult.text().toBuf(buf));
            //write the size of the builtMap of individual chest pools
            Map<List<TextKey>,ChestLootPoolBuilder> lootPoolBuilderMap = builder.builtMap;
            buf.writeByte(lootPoolBuilderMap.size());
            lootPoolBuilderMap.forEach((key,chestBuilder)->{

                //for each functional condition, write the size then list of condition textKeys
                buf.writeByte(key.size());
                key.forEach((textKey)->textKey.toBuf(buf));

                //for each functional condition, write the size of the actual itemstacks
                Map<ItemStack,Float> keyPoolMap = lootPoolBuilderMap.getOrDefault(key,new ChestLootPoolBuilder(1f)).builtMap;
                buf.writeByte(keyPoolMap.size());

                //for each itemstack, write the stack and weight
                keyPoolMap.forEach((stack,weight)->{
                    buf.writeItemStack(stack);
                    buf.writeFloat(weight);
                });
            });

        });
        ServerPlayNetworking.send(player,MOB_SENDER, buf);
    }

    @Override
    public void addBuilder(MobLootPoolBuilder builder) {
        builderList.add(builder);
    }

    @Override
    public List<MobLootPoolBuilder> getBuilders() {
        return builderList;
    }
}
