package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;

import java.util.Optional;

public class EntityTypePredicateParser {

    public static Text parseEntityTypePredicate(EntityTypePredicate predicate){
        RegistryEntryList<EntityType<?>> registryEntryList = predicate.types();
        Optional<TagKey<EntityType<?>>> tag = registryEntryList.getTagKey();
        if (tag.isPresent()) {
            return LText.translatable("emi_loot.entity_predicate.type_tag", tag.get().id());
        } else if (registryEntryList.size() == 1) {
            return LText.translatable("emi_loot.entity_predicate.type_single", registryEntryList.get(0).value().getName().getString());
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable entity type predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }
}