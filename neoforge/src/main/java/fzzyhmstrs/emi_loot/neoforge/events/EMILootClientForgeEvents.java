package fzzyhmstrs.emi_loot.neoforge.events;

import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientLootTables;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

public class EMILootClientForgeEvents {
    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        EMILootClient.onInitializeClient();
    }

    @SubscribeEvent
    public void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientLootTables.INSTANCE.clearLoots();
    }
}
