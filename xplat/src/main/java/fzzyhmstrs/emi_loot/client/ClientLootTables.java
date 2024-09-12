package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.networking.ArchaeologyLootPayload;
import fzzyhmstrs.emi_loot.networking.BlockLootPayload;
import fzzyhmstrs.emi_loot.networking.ChestLootPayload;
import fzzyhmstrs.emi_loot.networking.ClearPayload;
import fzzyhmstrs.emi_loot.networking.GameplayLootPayload;
import fzzyhmstrs.emi_loot.networking.MobLootPayload;
import me.fzzyhmstrs.fzzy_config.networking.api.ClientPlayNetworkContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class ClientLootTables {
    public static final ClientLootTables INSTANCE = new ClientLootTables();

    private final List<LootReceiver> loots = new LinkedList<>();

    public List<LootReceiver> getLoots() {
        return loots;
    }

    public void clearLoots() {
        loots.clear();
    }

    public void receiveChestSender(payload: ChestLootPayload, context: ClientPlayNetworkContext) {
        receiveChestSender(payload.buf(), ctx.player().clientWorld)
    }
    
    private void receiveChestSender(PacketByteBuf buf, World world) {
        LootReceiver table = ClientChestLootTable.INSTANCE.fromBuf(buf, world);
        loots.add(table);
        if (EMILoot.config.isDebug(EMILoot.Type.CHEST)) EMILoot.LOGGER.info("received chest " + table.getId());
    }

    public void receiveBlockSender(payload: ChestLootPayload, context: ClientPlayNetworkContext) {
        receiveBlockSender(payload.buf(), ctx.player().clientWorld)
    }

    private void receiveBlockSender(PacketByteBuf buf, World world) {
        LootReceiver table = ClientBlockLootTable.INSTANCE.fromBuf(buf, world);
        loots.add(table);
        if (EMILoot.config.isDebug(EMILoot.Type.BLOCK)) EMILoot.LOGGER.info("received block " + table.getId());
    }

    public void receiveMobSender(payload: ChestLootPayload, context: ClientPlayNetworkContext) {
        receiveMobSender(payload.buf(), ctx.player().clientWorld)
    }

    private void receiveMobSender(PacketByteBuf buf, World world) {
        LootReceiver table = ClientMobLootTable.INSTANCE.fromBuf(buf, world);
        loots.add(table);
        if (EMILoot.config.isDebug(EMILoot.Type.MOB)) EMILoot.LOGGER.info("received mob " + table.getId());
    }

    public void receiveGameplaySender(payload: ChestLootPayload, context: ClientPlayNetworkContext) {
        receiveGameplaySender(payload.buf(), ctx.player().clientWorld)
    }

    private void receiveGameplaySender(PacketByteBuf buf, World world) {
        LootReceiver table = ClientGameplayLootTable.INSTANCE.fromBuf(buf, world);
        loots.add(table);
        if (EMILoot.config.isDebug(EMILoot.Type.GAMEPLAY)) EMILoot.LOGGER.info("received gameplay loot: " + table.getId());
    }

    public void receiveArchaeologySender(payload: ChestLootPayload, context: ClientPlayNetworkContext) {
        receiveArchaeologySender(payload.buf(), ctx.player().clientWorld)
    }
    
    private void receiveArchaeologySender(PacketByteBuf buf, World world) {
        LootReceiver table = ClientArchaeologyLootTable.INSTANCE.fromBuf(buf, world);
        loots.add(table);
        if (EMILoot.config.isDebug(EMILoot.Type.ARCHAEOLOGY)) EMILoot.LOGGER.info("received archaeology loot: " + table.getId());
    }

}
