package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class ClientLootTables {
    public static final ClientLootTables INSTANCE = new ClientLootTables();

    public final List<LootReceiver> chestLoots = new Vector<>();
    public final List<LootReceiver> blockLoots = new Vector<>();
    public final List<LootReceiver> mobLoots = new Vector<>();
    public final List<LootReceiver> gameplayLoots = new Vector<>();
    public final List<LootReceiver> archaeologyLoots = new Vector<>();

    public void clearLoots() {
        chestLoots.clear();
        blockLoots.clear();
        mobLoots.clear();
        gameplayLoots.clear();
        archaeologyLoots.clear();
    }

    void receiveChestSender(PacketByteBuf buf) {
        try {
            LootReceiver table = ClientChestLootTable.INSTANCE.fromBuf(buf);
            chestLoots.add(table);
            if (EMILoot.config.isDebug(EMILoot.Type.CHEST)) EMILoot.LOGGER.info("received chest {}", table.getId());
        } catch (Throwable e) {
            EMILoot.LOGGER.error("Critical error encountered while receiving Chest Loot Packet");
            EMILoot.LOGGER.error("Thrown Error: ", e);
        }
    }

    void receiveBlockSender(PacketByteBuf buf) {
        try {
            LootReceiver table = ClientBlockLootTable.INSTANCE.fromBuf(buf);
            blockLoots.add(table);
            if (EMILoot.config.isDebug(EMILoot.Type.BLOCK)) EMILoot.LOGGER.info("received block {}", table.getId());
        } catch (Throwable e) {
            EMILoot.LOGGER.error("Critical error encountered while receiving Block Loot Packet");
            EMILoot.LOGGER.error("Thrown Error: ", e);
        }
    }

    void receiveMobSender(PacketByteBuf buf) {
        try {
            LootReceiver table = ClientMobLootTable.INSTANCE.fromBuf(buf);
            mobLoots.add(table);
            if (EMILoot.config.isDebug(EMILoot.Type.MOB)) EMILoot.LOGGER.info("received mob {}", table.getId());
        } catch (Throwable e) {
            EMILoot.LOGGER.error("Critical error encountered while receiving Mob Loot Packet");
            EMILoot.LOGGER.error("Thrown Error: ", e);
        }
    }

    void receiveGameplaySender(PacketByteBuf buf) {
        try {
            LootReceiver table = ClientGameplayLootTable.INSTANCE.fromBuf(buf);
            gameplayLoots.add(table);
            if (EMILoot.config.isDebug(EMILoot.Type.GAMEPLAY)) EMILoot.LOGGER.info("received gameplay loot: {}", table.getId());
        } catch (Throwable e) {
            EMILoot.LOGGER.error("Critical error encountered while receiving Gameplay Loot Packet");
            EMILoot.LOGGER.error("Thrown Error: ", e);
        }
    }

    void receiveArchaeologySender(PacketByteBuf buf) {
        try {
            LootReceiver table = ClientArchaeologyLootTable.INSTANCE.fromBuf(buf);
            archaeologyLoots.add(table);
            if (EMILoot.config.isDebug(EMILoot.Type.ARCHAEOLOGY)) EMILoot.LOGGER.info("received archaeology loot: {}", table.getId());
        } catch (Throwable e) {
            EMILoot.LOGGER.error("Critical error encountered while receiving Archaeology Loot Packet");
            EMILoot.LOGGER.error("Thrown Error: ", e);
        }
    }

}