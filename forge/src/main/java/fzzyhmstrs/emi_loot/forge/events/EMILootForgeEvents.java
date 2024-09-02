package fzzyhmstrs.emi_loot.forge.events;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EMILootForgeEvents {
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
