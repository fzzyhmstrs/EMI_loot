package fzzyhmstrs.emi_loot.forge.events;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
