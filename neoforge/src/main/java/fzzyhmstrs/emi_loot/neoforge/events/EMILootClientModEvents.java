package fzzyhmstrs.emi_loot.neoforge.events;

import fzzyhmstrs.emi_loot.client.ClientResourceData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public class EMILootClientModEvents {
    @SubscribeEvent
    public void onResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ClientResourceData.EntityOffsetsReloadListener());
    }
}
