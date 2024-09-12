package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.networking.ArchaeologyLootPayload;
import fzzyhmstrs.emi_loot.networking.BlockLootPayload;
import fzzyhmstrs.emi_loot.networking.ChestLootPayload;
import fzzyhmstrs.emi_loot.networking.GameplayLootPayload;
import fzzyhmstrs.emi_loot.networking.MobLootPayload;
import me.fzzyhmstrs.fzzy_config.networking.api.ClientPlayNetworkContext;

public class ClientLootTablesReceiver {

	public static void receiveChestSender(ChestLootPayload payload, ClientPlayNetworkContext ctx) {
		ClientLootTables.INSTANCE.receiveChestSender(payload.buf(), ctx.player().getWorld());
	}

	public static void receiveBlockSender(BlockLootPayload payload, ClientPlayNetworkContext ctx) {
		ClientLootTables.INSTANCE.receiveBlockSender(payload.buf(), ctx.player().getWorld());
	}

	public static void receiveMobSender(MobLootPayload payload, ClientPlayNetworkContext ctx) {
		ClientLootTables.INSTANCE.receiveMobSender(payload.buf(), ctx.player().getWorld());
	}

	public static void receiveGameplaySender(GameplayLootPayload payload, ClientPlayNetworkContext ctx) {
		ClientLootTables.INSTANCE.receiveGameplaySender(payload.buf(), ctx.player().getWorld());
	}

	public static void receiveArchaeologySender(ArchaeologyLootPayload payload, ClientPlayNetworkContext ctx) {
		ClientLootTables.INSTANCE.receiveArchaeologySender(payload.buf(), ctx.player().getWorld());
	}

}