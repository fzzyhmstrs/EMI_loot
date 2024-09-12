package fzzyhmstrs.emi_loot;

import fzzyhmstrs.emi_loot.client.ClientLootTables;
import fzzyhmstrs.emi_loot.client.ClientLootTablesReceiver;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.server.ArchaeologyLootTableSender;
import fzzyhmstrs.emi_loot.server.BlockLootTableSender;
import fzzyhmstrs.emi_loot.server.ChestLootTableSender;
import fzzyhmstrs.emi_loot.server.GameplayLootTableSender;
import fzzyhmstrs.emi_loot.server.MobLootTableSender;
import fzzyhmstrs.emi_loot.util.SimpleFzzyPayload;
import fzzyhmstrs.emi_loot.util.TextKey;
import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility;
import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.function.LootFunctionType;
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
    public static Supplier<LootFunctionType> SET_ANY_DAMAGE;
    public static Supplier<LootFunctionType> OMINOUS_BANNER;

    public static Enchantment RANDOM = new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.TRIDENT, EquipmentSlot.values()) {
        @Override
        public boolean isAvailableForEnchantedBookOffer() {
            return false;
        }

        public boolean isAvailableForRandomSelection() {
            return false;
        }
    };

    public static Identifier identity(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static void onInitialize() {
        ConfigApi.INSTANCE.network().registerS2C(LootTableParser.CLEAR_LOOTS, SimpleFzzyPayload.class, buf -> new SimpleFzzyPayload(buf, LootTableParser.CLEAR_LOOTS), (payload, ctx) -> ClientLootTables.INSTANCE.clearLoots());
        ConfigApi.INSTANCE.network().registerS2C(ChestLootTableSender.CHEST_SENDER, SimpleFzzyPayload.class, buf -> new SimpleFzzyPayload(buf, ChestLootTableSender.CHEST_SENDER), (payload, ctx) -> ClientLootTables.INSTANCE.receiveChestSender(payload.buf()));
        ConfigApi.INSTANCE.network().registerS2C(BlockLootTableSender.BLOCK_SENDER, SimpleFzzyPayload.class, buf -> new SimpleFzzyPayload(buf, BlockLootTableSender.BLOCK_SENDER), (payload, ctx) -> ClientLootTables.INSTANCE.receiveBlockSender(payload.buf()));
        ConfigApi.INSTANCE.network().registerS2C(MobLootTableSender.MOB_SENDER, SimpleFzzyPayload.class, buf -> new SimpleFzzyPayload(buf, MobLootTableSender.MOB_SENDER), (payload, ctx) -> ClientLootTables.INSTANCE.receiveMobSender(payload.buf()));
        ConfigApi.INSTANCE.network().registerS2C(GameplayLootTableSender.GAMEPLAY_SENDER, SimpleFzzyPayload.class, buf -> new SimpleFzzyPayload(buf, GameplayLootTableSender.GAMEPLAY_SENDER), (payload, ctx) -> ClientLootTables.INSTANCE.receiveGameplaySender(payload.buf()));
        ConfigApi.INSTANCE.network().registerS2C(ArchaeologyLootTableSender.ARCHAEOLOGY_SENDER, SimpleFzzyPayload.class, buf -> new SimpleFzzyPayload(buf, ArchaeologyLootTableSender.ARCHAEOLOGY_SENDER), (payload, ctx) -> ClientLootTables.INSTANCE.receiveArchaeologySender(payload.buf()));

        //Registry.register(Registries.ENCHANTMENT, new Identifier(MOD_ID, "random"), RANDOM);
    }

    @Version(version = 1)
    @IgnoreVisibility
    @ConvertFrom(fileName = "EmiLootConfig_v1.json")
    public static class EmiLootConfig extends Config {

        EmiLootConfig() {
            super(new Identifier(MOD_ID, "emi_loot_config"), "", "");
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