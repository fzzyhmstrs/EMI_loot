package fzzyhmstrs.emi_loot.forge.events;

import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientLootTables;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class EMILootClientForgeEvents {
    private final ClientLootTables tables = new ClientLootTables();

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        EMILootClient.init();
    }

    @SubscribeEvent
    public void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        tables.getLoots().clear();
    }
}
