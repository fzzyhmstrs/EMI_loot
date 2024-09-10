package fzzyhmstrs.emi_loot.neoforge.events;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.client.ClientLootTables;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = EMILoot.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class EMILootClientGameEvents {
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientLootTables.INSTANCE.clearLoots();
    }
}
