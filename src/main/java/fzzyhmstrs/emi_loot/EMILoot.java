package fzzyhmstrs.emi_loot;

import fzzyhmstrs.emi_loot.server.LootTableParser;
import net.fabricmc.api.ModInitializer;

public class EMILoot implements ModInitializer {

    public static String MOD_ID = "emi_loot";
    public static LootTableParser parser = new LootTableParser();

    @Override
    public void onInitialize() {
        parser.registerServer();
    }
}