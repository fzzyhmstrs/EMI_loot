package fzzyhmstrs.emi_loot.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootAgnos;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.util.*;
import java.util.regex.Pattern;

public class ServerResourceData {

    private static final Multimap<TableChecker, LootTable> DIRECT_DROPS = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
    public static final List<Identifier> SHEEP_TABLES;
    public static final List<Identifier> TABLE_EXCLUSIONS = new LinkedList<>();
	private static final int DIRECT_DROPS_PATH_LENGTH = "direct_drops/".length();
    private static final int FILE_SUFFIX_LENGTH = ".json".length();

    public static void loadDirectTables(ResourceManager resourceManager, RegistryOps<JsonElement> ops) {
        //if (LootTableParser.registryManager != null) {
            DIRECT_DROPS.clear();
            resourceManager.findResources("direct_drops", path -> path.getPath().endsWith(".json")).forEach((id, resource) -> loadDirectTable(id, resource, ops));
            resourceManager.findResources("emi_loot_data", path -> path.getPath().endsWith(".json")).forEach(ServerResourceData::loadTableExclusion);
        //}
    }

    private static void loadDirectTable(Identifier id, Resource resource, RegistryOps<JsonElement> ops) {
		if (EMILoot.DEBUG) EMILoot.LOGGER.info("Reading direct drop table from file: {}", id.toString());
        String path = id.getPath();
        Identifier id2 = Identifier.of(id.getNamespace(), path.substring(DIRECT_DROPS_PATH_LENGTH, path.length() - FILE_SUFFIX_LENGTH));
        String path2 = id2.getPath();
        if (!(path2.startsWith("blocks/") || path2.startsWith("entities/") || path2.startsWith("chests/"))) {
			EMILoot.LOGGER.error("File path for [{}] not correct; needs a 'blocks', 'entities' or 'chests' subfolder. Skipping.", id);
            EMILoot.LOGGER.error("Example: [./data/mod_id/direct_drops/blocks/cobblestone.json] is a valid block direct drop table path for a block added by [mod_id].");
            return;
        }
        try {
            BufferedReader reader = resource.getReader();
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            TableChecker checker = loadTableChecker(id2, json);
            LootTable lootTable = EMILootAgnos.loadLootTable(id2, LootTable.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(IllegalStateException::new));
            if (lootTable != null) {
                DIRECT_DROPS.put(checker, lootTable);
            } else {
				EMILoot.LOGGER.error("Loot table in file [{}] is empty!", id);
            }

        } catch(Exception e) {
			EMILoot.LOGGER.error("Failed to open or read direct drops loot table file: {}", id);
        }
    }

    /**
     * match_tables: {
     *     "regex": "minecraft:",
     *     "ids": [
     *          "minecraft:id1",
     *          "minecraft:id2",
     *     ],
     *     "type": "minecraft:chest"
     * }
     *
     */

    private static TableChecker loadTableChecker(Identifier id, JsonObject jsonObject) {
        if (!jsonObject.has("match_tables")) return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
        JsonElement matchesElement = jsonObject.get("match_tables");
        if (!matchesElement.isJsonObject()) {
            EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher, needs to be an object", id);
            return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
        }
        JsonObject matchesObject = matchesElement.getAsJsonObject();
        Optional<Pattern> regexCheck = Optional.empty();
        if (matchesObject.has("regex")) {
            JsonElement regexElement = matchesObject.get("regex");
            if (!regexElement.isJsonPrimitive()) {
                EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher. 'regex' key needs a primitive string value", id);
                return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
            }
            JsonPrimitive regexPrimitive = regexElement.getAsJsonPrimitive();
            if (!regexPrimitive.isString()) {
                EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher. 'regex' key needs a string value", id);
                return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
            }
            regexCheck = Optional.of(Pattern.compile(regexPrimitive.getAsString()));
        }

        Optional<List<Identifier>> idsCheck = Optional.empty();
        if (matchesObject.has("ids")) {
            JsonElement idsElement = matchesObject.get("ids");
            if (!idsElement.isJsonArray()) {
                EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher. 'ids' key needs to be an array of strings", id);
                return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
            }
            JsonArray idsArray = idsElement.getAsJsonArray();
            List<Identifier> ids = new ArrayList<>();
            for (int i = 0; i < idsArray.size(); ++i) {
                JsonElement idElement = idsArray.get(i);
                if (!idElement.isJsonPrimitive()) {
                    EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher. 'id' key has non-primitive-string value at index {}", id, i);
                    return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
                }
                JsonPrimitive idPrimitive = idElement.getAsJsonPrimitive();
                if (!idPrimitive.isString()) {
                    EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher. 'id' key has non-string value at index {}", id, i);
                    return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
                }
                Identifier idsId = Identifier.tryParse(idPrimitive.getAsString());
                if (idsId == null) {
                    EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher. 'id' key has unparsable identifier {} at index {}", id, idPrimitive.getAsString(), i);
                    return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
                }
                ids.add(idsId);
            }
            idsCheck = Optional.of(ids);
        }

        Optional<LootContextType> typeCheck = Optional.empty();
        if (matchesObject.has("type")) {
            JsonElement typeElement = matchesObject.get("type");
            if (!typeElement.isJsonPrimitive()) {
                EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher. 'type' key needs a primitive string value", id);
                return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
            }
            JsonPrimitive typePrimitive = typeElement.getAsJsonPrimitive();
            if (!typePrimitive.isString()) {
                EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher. 'type' key needs a string value", id);
                return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
            }
            Identifier typeId = Identifier.tryParse(typePrimitive.getAsString());
            if (typeId == null) {
                EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher. 'type' key has unparsable identifier {}", id, typePrimitive.getAsString());
                return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
            }
            LootContextType type = LootContextTypes.get(typeId);
            if (type == null) {
                EMILoot.LOGGER.error("Direct drops table {} has malformed table matcher. 'type' key has unregistered context type {}", id, typeId);
                return new TableChecker(id, Optional.empty(), Optional.empty(), Optional.empty());
            }
            typeCheck = Optional.of(type);
        }
        return new TableChecker(id, regexCheck, idsCheck, typeCheck);
    }

    private static void loadTableExclusion(Identifier id, Resource resource) {
        if (EMILoot.DEBUG) EMILoot.LOGGER.info("Reading exclusion table from file: {}", id.toString());
        try {
            BufferedReader reader = resource.getReader();
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement list = json.get("exclusions");
            if (list != null && list.isJsonArray()) {
                for (JsonElement element : list.getAsJsonArray()) {
                    if (element.isJsonPrimitive()) {
                        Identifier identifier = new Identifier(element.getAsString());
                        if (EMILoot.DEBUG) EMILoot.LOGGER.info("Adding exclusion: {}", identifier);
                        TABLE_EXCLUSIONS.add(identifier);
                    } else {
						EMILoot.LOGGER.error("Exclusion element not properly formatted: {}", element);
                    }
                }
            } else {
				EMILoot.LOGGER.error("Exclusions in file: {} not readable.", id);
            }
        } catch(Exception e) {
			EMILoot.LOGGER.error("Failed to open or read table exclusions file: {}", id);
        }
    }

    public static boolean skipTable(Identifier id) {
        return TABLE_EXCLUSIONS.contains(id);
    }

    public static List<LootTable> getDirectTables(LootTable table, Identifier id, boolean getDirect) {
        if (!getDirect) return List.of();
        List<LootTable> tables = new ArrayList<>();
        for (TableChecker checker : DIRECT_DROPS.keySet()) {
            if (checker.check(table, id)) {
                Collection<LootTable> checkedTables = DIRECT_DROPS.get(checker);
                tables.addAll(checkedTables);
            }
        }
        return tables;
    }

    public static Multimap<Identifier, LootTable> getMissedDirectDrops(List<Identifier> parsedList) {
        Multimap<Identifier, LootTable> missedDrops = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
        for (Map.Entry<TableChecker, LootTable> entry : DIRECT_DROPS.entries()) {
            if (!parsedList.contains(entry.getKey().idCheck())) {
                missedDrops.put(entry.getKey().idCheck(), entry.getValue());
            }
        }
        return missedDrops;
    }

    static {
        Identifier[] ids = {
                LootTables.WHITE_SHEEP_ENTITY.getValue(),
                LootTables.ORANGE_SHEEP_ENTITY.getValue(),
                LootTables.MAGENTA_SHEEP_ENTITY.getValue(),
                LootTables.LIGHT_BLUE_SHEEP_ENTITY.getValue(),
                LootTables.YELLOW_SHEEP_ENTITY.getValue(),
                LootTables.LIME_SHEEP_ENTITY.getValue(),
                LootTables.PINK_SHEEP_ENTITY.getValue(),
                LootTables.GRAY_SHEEP_ENTITY.getValue(),
                LootTables.LIGHT_GRAY_SHEEP_ENTITY.getValue(),
                LootTables.CYAN_SHEEP_ENTITY.getValue(),
                LootTables.PURPLE_SHEEP_ENTITY.getValue(),
                LootTables.BLUE_SHEEP_ENTITY.getValue(),
                LootTables.BROWN_SHEEP_ENTITY.getValue(),
                LootTables.GREEN_SHEEP_ENTITY.getValue(),
                LootTables.RED_SHEEP_ENTITY.getValue(),
                LootTables.BLACK_SHEEP_ENTITY.getValue()
        };
        SHEEP_TABLES = Arrays.stream(ids).toList();
    }

    private record TableChecker(Identifier idCheck, Optional<Pattern> regexCheck, Optional<List<Identifier>> idsCheck, Optional<LootContextType> typeCheck) {

        boolean check(LootTable table, Identifier id) {
            if(idCheck.equals(id)) return true;
            if(regexCheck.map(regex -> regex.matcher(id.toString()).find()).orElse(false)) return true;
            if(idsCheck.map(ids -> ids.contains(id)).orElse(false)) return true;
			return typeCheck.map(type -> type.equals(table.getType())).orElse(false);
		}
    }
}