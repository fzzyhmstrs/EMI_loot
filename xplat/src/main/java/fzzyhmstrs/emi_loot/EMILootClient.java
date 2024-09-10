package fzzyhmstrs.emi_loot;

import fzzyhmstrs.emi_loot.client.ClientLootTables;

public class EMILootClient {

    public static String MOD_ID = "emi_loot";
    public static ClientLootTables tables = new ClientLootTables();

    public static void onInitializeClient() {
        tables.registerClient();
    }
}
