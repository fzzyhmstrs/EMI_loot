package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MobLootTableSender implements LootSender<MobLootPoolBuilder> {

    public MobLootTableSender(Identifier id, Identifier mobId){
        this.idToSend = LootSender.getIdToSend(id);
        this.mobIdToSend = LootSender.getIdToSend(mobId);
    }

    private final String idToSend;
    private final String mobIdToSend;
    final List<MobLootPoolBuilder> builderList = new LinkedList<>();
    public static Identifier MOB_SENDER = new Identifier("e_l","m_s");
    boolean isEmpty = true;

    @Override
    public void build() {
        builderList.forEach((builder)->{
            builder.build();
            if (!builder.isEmpty){
                isEmpty = false;
            }
        });
    }

    @Override
    public String getId() {
        return idToSend;
    }

    @Override
    public void send(ServerPlayerEntity player) {
        //pre-build the builders to do empty checks
        if (isEmpty){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("avoiding empty mob: " + idToSend);
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        //start with the loot pool ID and the number of builders to write
        buf.writeString(idToSend);
        buf.writeString(mobIdToSend);

        if (builderList.size() == 1 && builderList.get(0).isSimple) {
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("sending simple mob: " + idToSend);
            buf.writeByte(-1);
            buf.writeRegistryValue(Registries.ITEM, builderList.get(0).simpleStack.getItem());
            ServerPlayNetworking.send(player, MOB_SENDER, buf);
            return;
        } else if (builderList.isEmpty()){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("avoiding empty mob: " + idToSend);
            return;
        }

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
