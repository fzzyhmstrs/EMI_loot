package fzzyhmstrs.emi_loot;

import com.google.gson.JsonElement;
import fzzyhmstrs.emi_loot.client.ClientLootTables;
import fzzyhmstrs.emi_loot.networking.ArchaeologyLootPayload;
import fzzyhmstrs.emi_loot.networking.BlockLootPayload;
import fzzyhmstrs.emi_loot.networking.ChestLootPayload;
import fzzyhmstrs.emi_loot.networking.ClearPayload;
import fzzyhmstrs.emi_loot.networking.GameplayLootPayload;
import fzzyhmstrs.emi_loot.networking.MobLootPayload;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.server.ServerResourceData;
import fzzyhmstrs.emi_loot.util.TextKey;
import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility;
import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresRestart;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.FcText;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class EMILoot {

    public static final String MOD_ID = "emi_loot";
    public static final Logger LOGGER = LoggerFactory.getLogger("emi_loot");
    public static Random emiLootRandom = new LocalRandom(System.currentTimeMillis());
    public static LootTableParser parser = new LootTableParser();
    public static EmiLootConfig config = ConfigApiJava.registerAndLoadConfig(EmiLootConfig::new, RegisterType.BOTH);
    public static boolean DEBUG = config.debugMode;

    //conditions & functions will be used in Lootify also, copying the identifier here so both mods can serialize the same conditions separately
    public static Supplier<LootConditionType> WITHER_KILL;
    public static Supplier<LootConditionType> SPAWNS_WITH;
    public static Supplier<LootConditionType> CREEPER;
    public static Supplier<LootFunctionType<?>> SET_ANY_DAMAGE;
    public static Supplier<LootFunctionType<?>> OMINOUS_BANNER;

    public static Identifier identity(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static void onInitialize() {
        ConfigApi.INSTANCE.network().registerS2C(ClearPayload.TYPE, ClearPayload.CODEC, (payload, ctx) -> ClientLootTables.INSTANCE.clearLoots());
        ConfigApi.INSTANCE.network().registerS2C(ChestLootPayload.TYPE, ChestLootPayload.CODEC, (payload, ctx) -> ClientLootTables.INSTANCE.receiveChestSender(payload, ctx));
        ConfigApi.INSTANCE.network().registerS2C(BlockLootPayload.TYPE, BlockLootPayload.CODEC, (payload, ctx) -> ClientLootTables.INSTANCE.receiveBlockSender(payload, ctx));
        ConfigApi.INSTANCE.network().registerS2C(MobLootPayload.TYPE, MobLootPayload.CODEC, (payload, ctx) -> ClientLootTables.INSTANCE.receiveMobSender(payload, ctx));
        ConfigApi.INSTANCE.network().registerS2C(GameplayLootPayload.TYPE, GameplayLootPayload.CODEC, (payload, ctx) -> ClientLootTables.INSTANCE.receiveGameplaySender(payload, ctx));
        ConfigApi.INSTANCE.network().registerS2C(ArchaeologyLootPayload.TYPE, ArchaeologyLootPayload.CODEC, (payload, ctx) -> ClientLootTables.INSTANCE.receiveArchaeologySender(payload, ctx));
    }

    public static void parseTables(ResourceManager resourceManager, Registry<LootTable> lootManager, RegistryOps<JsonElement> ops) {
        LootTableParser.registryOps = ops;
        ServerResourceData.loadDirectTables(resourceManager, ops);
        LootTableParser.parseLootTables(lootManager);
    }

    @Version(version = 1)
    @IgnoreVisibility
    @ConvertFrom(fileName = "EmiLootConfig_v1.json")
    public static class EmiLootConfig extends Config {

        EmiLootConfig() {
            super(Identifier.of(MOD_ID, "emi_loot_config"), "", "");
        }

        @RequiresAction(action = Action.RELOAD_DATA)
        public boolean debugMode = false;

        @RequiresAction(action = Action.RELOAD_DATA)
        @SuppressWarnings("FieldMayBeFinal")
        private ValidatedAny<DebugMode> debugModes = new ValidatedAny<>(new DebugMode());

        @RequiresAction(action = Action.RELOAD_DATA)
        public boolean parseChestLoot = true;

        @RequiresAction(action = Action.RELOAD_DATA)
        public boolean parseBlockLoot = true;

        @RequiresAction(action = Action.RELOAD_DATA)
        public boolean parseMobLoot = true;

        @RequiresAction(action = Action.RELOAD_DATA)
        public boolean parseGameplayLoot = true;

        @RequiresAction(action = Action.RELOAD_DATA)
        public boolean parseArchaeologyLoot = true;

        @RequiresAction(action = Action.RELOAD_DATA)
        public Set<String> skippedKeys = new ValidatedSet<>(TextKey.defaultSkips, ValidatedString.fromList(TextKey.keys().stream().toList()));

        @NonSync
        @SuppressWarnings("FieldMayBeFinal")
        private ValidatedAny<CompactLoot> compactLoot = new ValidatedAny<>(new CompactLoot());

        @NonSync
        @SuppressWarnings("FieldMayBeFinal")
        private ValidatedAny<LogUntranslatedTables> logUnstranslatedTables = new ValidatedAny<>(new LogUntranslatedTables());

        @NonSync
        public boolean chestLootAlwaysStackSame = false;

        @NonSync
        public boolean mobLootIncludeDirectDrops = true;

		@NonSync
        @SuppressWarnings("FieldMayBeFinal")
        private ValidatedChoice<String> conditionStyle = EMILootAgnos.isModLoaded("symbols_n_stuff")
                ?
            new ValidatedChoice<>(List.of("tooltip", "plain", "default"), new ValidatedString(), (t, u) -> FcText.INSTANCE.translate(u + "." + t), (t, u) -> FcText.INSTANCE.translate(u + "." + t), ValidatedChoice.WidgetType.CYCLING)
				:
            new ValidatedChoice<>(List.of("default", "tooltip", "plain"), new ValidatedString(), (t, u) -> FcText.INSTANCE.translate(u + "." + t + ".sns"), (t, u) -> FcText.INSTANCE.translate(u + "." + t + ".sns"), ValidatedChoice.WidgetType.CYCLING);

        public boolean isTooltipStyle() {
            return Objects.equals(conditionStyle.get(), "tooltip") || Objects.equals(conditionStyle.get(), "plain");
        }

        public boolean isNotPlain() {
            return !((Objects.equals(conditionStyle.get(), "tooltip") && !EMILootAgnos.isModLoaded("symbols_n_stuff")) || Objects.equals(conditionStyle.get(), "plain"));
        }

        public boolean isCompact(Type type) {
            return type.compactLootSupplier.getAsBoolean();
        }

        public boolean isDebug(Type type) {
            return type.debugModeSupplier.getAsBoolean();
        }

        public boolean isLogI18n(Type type) {
            return type.logUntranslatedTablesSupplier.getAsBoolean();
        }
	}

    @IgnoreVisibility
    private static class CompactLoot {
        public boolean block = true;

        public boolean chest = true;

        public boolean mob = true;

        public boolean gameplay = true;

        public boolean archaeology = true;
    }

    @IgnoreVisibility
    private static class DebugMode {
        public boolean block = false;

        public boolean chest = false;

        public boolean mob = false;

        public boolean gameplay = false;

        public boolean archaeology = false;
    }

    @IgnoreVisibility
    private static class LogUntranslatedTables {
        public boolean chest = EMILootAgnos.isDevelopmentEnvironment();

        public boolean gameplay = EMILootAgnos.isDevelopmentEnvironment();

        public boolean archaeology = EMILootAgnos.isDevelopmentEnvironment();
    }

    public enum Type {
        BLOCK(() -> EMILoot.config.compactLoot.get().block, () -> EMILoot.config.debugModes.get().block, () -> false),
        CHEST(() -> EMILoot.config.compactLoot.get().chest, () -> EMILoot.config.debugModes.get().chest, () -> EMILoot.config.logUnstranslatedTables.get().chest),
        MOB(() -> EMILoot.config.compactLoot.get().mob, () -> EMILoot.config.debugModes.get().mob, () -> false),
        GAMEPLAY(() -> EMILoot.config.compactLoot.get().gameplay, () -> EMILoot.config.debugModes.get().gameplay, () -> EMILoot.config.logUnstranslatedTables.get().gameplay),
        ARCHAEOLOGY(() -> EMILoot.config.compactLoot.get().archaeology, () -> EMILoot.config.debugModes.get().archaeology, () -> EMILoot.config.logUnstranslatedTables.get().archaeology);

        final BooleanSupplier compactLootSupplier;
        final BooleanSupplier debugModeSupplier;
        final BooleanSupplier logUntranslatedTablesSupplier;

        Type(BooleanSupplier compactLootSupplier, BooleanSupplier debugModeSupplier, BooleanSupplier logUntranslatedTablesSupplier) {
            this.compactLootSupplier = compactLootSupplier;
            this.debugModeSupplier = debugModeSupplier;
            this.logUntranslatedTablesSupplier = logUntranslatedTablesSupplier;
        }
    }
}
