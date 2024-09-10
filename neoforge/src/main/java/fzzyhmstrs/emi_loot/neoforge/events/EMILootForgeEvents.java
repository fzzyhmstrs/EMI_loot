package fzzyhmstrs.emi_loot.neoforge.events;

import fzzyhmstrs.emi_loot.EMILoot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

public class EMILootForgeEvents {
//    @SubscribeEvent
//    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getEntity() instanceof ServerPlayerEntity player) {
//            EMILoot.parser.registerServer(player);
//        }
//    }

    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            event.getPlayerList().getPlayerList().forEach(player -> {
                EMILoot.parser.registerServer(player);
            });
        } else {
            EMILoot.parser.registerServer(event.getPlayer());
        }
    }
}
