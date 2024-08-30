package fzzyhmstrs.emi_loot;

import com.google.gson.JsonElement;
import fzzyhmstrs.emi_loot.networking.ArchaeologyBufCustomPayload;
import fzzyhmstrs.emi_loot.networking.BlockBufCustomPayload;
import fzzyhmstrs.emi_loot.networking.ChestBufCustomPayload;
import fzzyhmstrs.emi_loot.networking.ClearLootCustomPayload;
import fzzyhmstrs.emi_loot.networking.GameplayBufCustomPayload;
import fzzyhmstrs.emi_loot.networking.MobBufCustomPayload;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.server.ServerResourceData;
import fzzyhmstrs.emi_loot.server.condition.BlownUpByCreeperLootCondition;
import fzzyhmstrs.emi_loot.server.condition.KilledByWitherLootCondition;
import fzzyhmstrs.emi_loot.server.condition.MobSpawnedWithLootCondition;
import fzzyhmstrs.emi_loot.server.function.OminousBannerLootFunction;
import fzzyhmstrs.emi_loot.server.function.SetAnyDamageLootFunction;
import fzzyhmstrs.emi_loot.util.TextKey;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility;
import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresRestart;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.FcText;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
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

public class EMILoot implements ModInitializer {

    public static final String MOD_ID = "emi_loot";
    public static final Logger LOGGER = LoggerFactory.getLogger("emi_loot");
    public static Random emiLootRandom = new LocalRandom(System.currentTimeMillis());
    public static LootTableParser parser = new LootTableParser();
    public static EmiLootConfig config = ConfigApiJava.registerAndLoadConfig(EmiLootConfig::new, RegisterType.BOTH);
    public static boolean DEBUG = config.debugMode;

    //conditions & functions will be used in Lootify also, copying the identifier here so both mods can serialize the same conditions separately
    public static LootConditionType WITHER_KILL = Registry.register(Registries.LOOT_CONDITION_TYPE, "lootify:wither_kill", new LootConditionType(KilledByWitherLootCondition.CODEC));
    public static LootConditionType SPAWNS_WITH = Registry.register(Registries.LOOT_CONDITION_TYPE, "lootify:spawns_with", new LootConditionType(MobSpawnedWithLootCondition.CODEC));
    public static LootConditionType CREEPER = Registry.register(Registries.LOOT_CONDITION_TYPE, "lootify:creeper", new LootConditionType(BlownUpByCreeperLootCondition.CODEC));
    public static LootFunctionType<SetAnyDamageLootFunction> SET_ANY_DAMAGE = Registry.register(Registries.LOOT_FUNCTION_TYPE, "lootify:set_any_damage", new LootFunctionType<>(SetAnyDamageLootFunction.CODEC));
    public static LootFunctionType<OminousBannerLootFunction> OMINOUS_BANNER = Registry.register(Registries.LOOT_FUNCTION_TYPE, "lootify:ominous_banner", new LootFunctionType<>(OminousBannerLootFunction.CODEC));


    public static Identifier identity(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        parser.registerServer();

        PayloadTypeRegistry.playS2C().register(ClearLootCustomPayload.TYPE, ClearLootCustomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BlockBufCustomPayload.TYPE, BlockBufCustomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ChestBufCustomPayload.TYPE, ChestBufCustomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MobBufCustomPayload.TYPE, MobBufCustomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GameplayBufCustomPayload.TYPE, GameplayBufCustomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ArchaeologyBufCustomPayload.TYPE, ArchaeologyBufCustomPayload.CODEC);
    }

    public static void parseTables(ResourceManager resourceManager, Registry<LootTable> lootManager, RegistryOps<JsonElement> ops) {
        LootTableParser.registryOps = ops;
        ServerResourceData.loadDirectTables(resourceManager, ops);
        LootTableParser.parseLootTables(lootManager);
    }

    @IgnoreVisibility
    @ConvertFrom(fileName = "EmiLootConfig_v1.json")
    public static class EmiLootConfig extends Config {

        EmiLootConfig() {
            super(Identifier.of(MOD_ID, "emi_loot_config"), "", "");
        }

        @RequiresRestart
        public boolean debugMode = false;

        @RequiresRestart
        public boolean parseChestLoot = true;

        @RequiresRestart
        public boolean parseBlockLoot = true;

        @RequiresRestart
        public boolean parseMobLoot = true;

        @RequiresRestart
        public boolean parseGameplayLoot = true;

        @RequiresRestart
        public boolean parseArchaeologyLoot = true;

        @RequiresRestart
        public Set<String> skippedKeys = new ValidatedSet<>(TextKey.defaultSkips, ValidatedString.fromList(TextKey.keys().stream().toList()));

        @NonSync
        @SuppressWarnings("FieldMayBeFinal")
        private ValidatedAny<CompactLoot> compactLoot = new ValidatedAny<>(new CompactLoot());

        @NonSync
        public boolean chestLootAlwaysStackSame = false;

        @NonSync
        public boolean mobLootIncludeDirectDrops = true;

		@NonSync
        @SuppressWarnings("FieldMayBeFinal")
        private ValidatedChoice<String> conditionStyle = FabricLoader.getInstance().isModLoaded("symbols_n_stuff")
                ?
            new ValidatedChoice<>(List.of("default", "tooltip", "plain"), new ValidatedString(), (t, u) -> FcText.INSTANCE.translate(u + "." + t), (t, u) -> FcText.INSTANCE.translate(u + "." + t), ValidatedChoice.WidgetType.CYCLING)
				:
            new ValidatedChoice<>(List.of("default", "tooltip", "plain"), new ValidatedString(), (t, u) -> FcText.INSTANCE.translate(u + "." + t + ".sns"), (t, u) -> FcText.INSTANCE.translate(u + "." + t + ".sns"), ValidatedChoice.WidgetType.CYCLING);

        public boolean isTooltipStyle() {
            return Objects.equals(conditionStyle.get(), "tooltip") || Objects.equals(conditionStyle.get(), "plain");
        }

        public boolean isNotPlain() {
            return !((Objects.equals(conditionStyle.get(), "tooltip") && !FabricLoader.getInstance().isModLoaded("symbols_n_stuff")) || Objects.equals(conditionStyle.get(), "plain"));
        }

        public boolean isCompact(Type type) {
            return type.supplier.getAsBoolean();
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

    public enum Type {
        BLOCK(() -> EMILoot.config.compactLoot.get().block),
        CHEST(() -> EMILoot.config.compactLoot.get().chest),
        MOB(() -> EMILoot.config.compactLoot.get().mob),
        GAMEPLAY(() -> EMILoot.config.compactLoot.get().gameplay),
        ARCHAEOLOGY(() -> EMILoot.config.compactLoot.get().archaeology);

        final BooleanSupplier supplier;

        Type(BooleanSupplier supplier) {
            this.supplier = supplier;
        }
    }
}