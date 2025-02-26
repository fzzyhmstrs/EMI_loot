package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.networking.GameplayLootPayload;
import fzzyhmstrs.emi_loot.util.TextKey;
import io.netty.buffer.Unpooled;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class GameplayLootTableSender implements LootSender<ComplexLootPoolBuilder> {

    public GameplayLootTableSender(Identifier id) {
        this.idToSend = LootSender.getIdToSend(id);
    }

    private final String idToSend;
    final List<ComplexLootPoolBuilder> builderList = new LinkedList<>();
    boolean isEmpty = true;

    @Override
    public String getId() {
        return idToSend;
    }

    @Override
    public void build() {
        builderList.forEach((builder)-> {
            builder.build();
            if (!builder.isEmpty) {
                isEmpty = false;
            }
        });
    }

    @Override
    public void send(ServerPlayerEntity player) {
        if (!ConfigApi.INSTANCE.network().canSend(GameplayLootPayload.TYPE.id(), player)) return;
        if (isEmpty) {
            if (EMILoot.config.isDebug(EMILoot.Type.GAMEPLAY)) EMILoot.LOGGER.info("avoiding empty gameplay table: " + idToSend);
            return;
        }
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        //start with the loot pool ID and the number of builders to write check a few special conditions to send compressed shortcut packets
        buf.writeString(idToSend);

        if (builderList.size() == 1 && builderList.get(0).isSimple) {
            if (EMILoot.config.isDebug(EMILoot.Type.GAMEPLAY)) EMILoot.LOGGER.info("sending simple gameplay table: " + idToSend);
            buf.writeShort(-1);
            buf.writeRegistryKey(builderList.get(0).simpleStack.getItem().getRegistryEntry().registryKey());
            ConfigApi.INSTANCE.network().send(new GameplayLootPayload(buf), player);
            return;
        } else if (builderList.isEmpty()) {
            return;
        }
        buf.writeShort(builderList.size());
        builderList.forEach((builder)-> {

            List<TextKey> totalConditions = new ArrayList<>();
            builder.conditions.forEach((lootConditionResult -> totalConditions.add(lootConditionResult.text())));
            builder.functions.forEach((lootFunctionResult)-> totalConditions.addAll(lootFunctionResult.conditions()));

            //write size of the builders condition set
            buf.writeShort(totalConditions.size());
            //write the textkey of each of those conditions
            totalConditions.forEach((lootConditionResult -> lootConditionResult.toBuf(buf)));

            //write size of the builders function set
            buf.writeShort(builder.functions.size());
            //write the textkey of the functions
            builder.functions.forEach((lootFunctionResult)-> lootFunctionResult.text().toBuf(buf));
            //write the size of the builtMap of individual chest pools
            Map<List<TextKey>, SimpleLootPoolBuilder> lootPoolBuilderMap = builder.builtMap;
            buf.writeShort(lootPoolBuilderMap.size());
            lootPoolBuilderMap.forEach((key, chestBuilder)-> {

                //for each functional condition, write the size then list of condition textKeys
                buf.writeShort(key.size());
                key.forEach((textKey)->textKey.toBuf(buf));

                //for each functional condition, write the size of the actual itemstacks
                Map<ItemStack, Float> keyPoolMap = lootPoolBuilderMap.getOrDefault(key, new SimpleLootPoolBuilder(1f)).builtMap;
                buf.writeShort(keyPoolMap.size());

                //for each itemstack, write the stack and weight
                keyPoolMap.forEach((stack, weight)-> {
                    writeItemStack(buf, stack, player.getServerWorld());
                    buf.writeFloat(weight);
                });
            });

        });
        ConfigApi.INSTANCE.network().send(new GameplayLootPayload(buf), player);
    }

    @Override
    public void addBuilder(ComplexLootPoolBuilder builder) {
        builderList.add(builder);
    }

    @Override
    public List<ComplexLootPoolBuilder> getBuilders() {
        return builderList;
    }

}