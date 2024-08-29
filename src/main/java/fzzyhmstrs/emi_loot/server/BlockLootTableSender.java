package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BlockLootTableSender implements LootSender<BlockLootPoolBuilder> {

    public BlockLootTableSender(Identifier id) {
        this.idToSend = LootSender.getIdToSend(id);
    }

    private final String idToSend;
    final List<BlockLootPoolBuilder> builderList = new LinkedList<>();
    public static Identifier BLOCK_SENDER = new Identifier("e_l", "b_s");
    boolean isEmpty = true;


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
    public String getId() {
        return idToSend;
    }

    @Override
    public void send(ServerPlayerEntity player) {
        if (!ServerPlayNetworking.canSend(player, BLOCK_SENDER)) return;
        if (isEmpty) {
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("avoiding empty block: " + idToSend);
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        //start with the loot pool ID and the number of builders to write check a few special conditions to send compressed shortcut packets
        buf.writeString(idToSend);
        //pre-build the builders to do empty checks
        if (builderList.size() == 1 && builderList.get(0).isSimple) {
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("sending simple block: " + idToSend);
            buf.writeShort(-1);
            buf.writeRegistryValue(Registries.ITEM, builderList.get(0).simpleStack.getItem());
            ServerPlayNetworking.send(player, BLOCK_SENDER, buf);
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
            totalConditions.forEach((textKey -> textKey.toBuf(buf)));

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
        ServerPlayNetworking.send(player, BLOCK_SENDER, buf);
    }

    @Override
    public void addBuilder(BlockLootPoolBuilder builder) {
        builderList.add(builder);
    }

    @Override
    public List<BlockLootPoolBuilder> getBuilders() {
        return builderList;
    }
}