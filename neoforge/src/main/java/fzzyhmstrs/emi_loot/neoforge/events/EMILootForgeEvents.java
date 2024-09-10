package fzzyhmstrs.emi_loot.neoforge.events;

import fzzyhmstrs.emi_loot.EMILoot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@Mod.EventBusSubscriber(modid = EMILoot.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EMILootForgeEvents {
//    @SubscribeEvent
//    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getEntity() instanceof ServerPlayerEntity player) {
//            EMILoot.parser.registerServer(player);
//        }
//    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            event.getPlayerList().getPlayerList().forEach(player -> {
                EMILoot.parser.registerServer(player);
            });
        } else {
            EMILoot.parser.registerServer(event.getPlayer());
        }
    }
}
