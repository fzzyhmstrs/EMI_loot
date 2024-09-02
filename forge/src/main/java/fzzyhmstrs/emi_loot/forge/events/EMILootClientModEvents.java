package fzzyhmstrs.emi_loot.forge.events;

import fzzyhmstrs.emi_loot.client.ClientResourceData;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EMILootClientModEvents {
    @SubscribeEvent
    public void onResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ClientResourceData.EntityOffsetsReloadListener());
    }
}
