package fzzyhmstrs.emi_loot.parser;

import com.google.gson.JsonElement;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EntityTypePredicateParser{

    public static Text parseEntityTypePredicate(EntityTypePredicate predicate){
        JsonElement element = predicate.toJson();
        String jsonString = element.getAsString();
        if (jsonString != null){
            if (jsonString.startsWith("#")) {
                return LText.translatable("emi_loot.entity_predicate.type_tag", jsonString);
            } else {
                EntityType<?> type = Registries.ENTITY_TYPE.get(new Identifier(jsonString));
                return LText.translatable("emi_loot.entity_predicate.type_single", type.getName().getString());
            }
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable entity type predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }
}
