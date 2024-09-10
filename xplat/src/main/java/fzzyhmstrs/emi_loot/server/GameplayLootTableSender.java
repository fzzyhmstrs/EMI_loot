package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.SimpleCustomPayload;
import fzzyhmstrs.emi_loot.util.TextKey;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class GameplayLootTableSender implements LootSender<GameplayLootPoolBuilder> {

    public GameplayLootTableSender(Identifier id) {
        this.idToSend = LootSender.getIdToSend(id);
    }

    private final String idToSend;
    final List<GameplayLootPoolBuilder> builderList = new LinkedList<>();
    public static Identifier GAMEPLAY_SENDER = new Identifier("e_l", "g_s");
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
        if (!ConfigApi.INSTANCE.network().canSend(GAMEPLAY_SENDER, player)) return;
        if (isEmpty) {
            if (EMILoot.config.isDebug(EMILoot.Type.GAMEPLAY)) EMILoot.LOGGER.info("avoiding empty gameplay table: " + idToSend);
            return;
        }
        PacketByteBuf buf = ConfigApi.INSTANCE.network().buf();
        //start with the loot pool ID and the number of builders to write check a few special conditions to send compressed shortcut packets
        buf.writeString(idToSend);

        if (builderList.size() == 1 && builderList.get(0).isSimple) {
            if (EMILoot.config.isDebug(EMILoot.Type.GAMEPLAY)) EMILoot.LOGGER.info("sending simple gameplay table: " + idToSend);
            buf.writeShort(-1);
            buf.writeRegistryValue(Registries.ITEM, builderList.get(0).simpleStack.getItem());
            ConfigApi.INSTANCE.network().send(new SimpleCustomPayload(buf, GAMEPLAY_SENDER), player);
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
            Map<List<TextKey>, ChestLootPoolBuilder> lootPoolBuilderMap = builder.builtMap;
            buf.writeShort(lootPoolBuilderMap.size());
            lootPoolBuilderMap.forEach((key, chestBuilder)-> {

                //for each functional condition, write the size then list of condition textKeys
                buf.writeShort(key.size());
                key.forEach((textKey)->textKey.toBuf(buf));

                //for each functional condition, write the size of the actual itemstacks
                Map<ItemStack, Float> keyPoolMap = lootPoolBuilderMap.getOrDefault(key, new ChestLootPoolBuilder(1f)).builtMap;
                buf.writeShort(keyPoolMap.size());

                //for each itemstack, write the stack and weight
                keyPoolMap.forEach((stack, weight)-> {
                    buf.writeItemStack(stack);
                    buf.writeFloat(weight);
                });
            });

        });
        ConfigApi.INSTANCE.network().send(new SimpleCustomPayload(buf, GAMEPLAY_SENDER), player);
    }

    @Override
    public void addBuilder(GameplayLootPoolBuilder builder) {
        builderList.add(builder);
    }

    @Override
    public List<GameplayLootPoolBuilder> getBuilders() {
        return builderList;
    }

}