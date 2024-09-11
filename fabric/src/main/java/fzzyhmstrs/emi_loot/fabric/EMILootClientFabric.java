package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientLootTables;
import fzzyhmstrs.emi_loot.client.ClientResourceData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class EMILootClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EMILootClient.onInitializeClient();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new EntityOffsetsReloadListenerFabric());

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientLootTables.INSTANCE.clearLoots());
    }

    private static class EntityOffsetsReloadListenerFabric extends ClientResourceData.EntityOffsetsReloadListener implements IdentifiableResourceReloadListener {
        @Override
        public Identifier getFabricId() {
            return Identifier.of(EMILoot.MOD_ID,"client_loot_resources");
        }
    }
}
