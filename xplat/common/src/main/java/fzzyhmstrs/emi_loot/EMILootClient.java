package fzzyhmstrs.emi_loot;

import fzzyhmstrs.emi_loot.client.ClientLootTables;
import fzzyhmstrs.emi_loot.client.ClientResourceData;
import net.fabricmc.api.ClientModInitializer;

public class EMILootClient implements ClientModInitializer {

    public static String MOD_ID = "emi_loot";
    public static ClientLootTables tables = new ClientLootTables();

    @Override
    public void onInitializeClient() {
        tables.registerClient();
        ClientResourceData.register();
    }
}
