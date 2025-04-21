package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;
import java.util.Optional;

public class LocationPredicateParser {

    public static Text parseLocationPredicate(LocationPredicate predicate) {
        Optional<LocationPredicate.PositionRange> position = predicate.position();
        if (position.isPresent()) {
            NumberRange.DoubleRange x = position.get().x();
            if (!x.equals(NumberRange.DoubleRange.ANY)) {
                return NumberProcessors.processNumberRange(x,
                        "emi_loot.location_predicate.x.exact",
                        "emi_loot.location_predicate.x",
                        "emi_loot.location_predicate.x.at_least",
                        "emi_loot.location_predicate.x.at_most",
                        "Unknown X coordinate");
            }

            NumberRange.DoubleRange y = position.get().y();
            if (!y.equals(NumberRange.DoubleRange.ANY)) {
                return NumberProcessors.processNumberRange(y,
                        "emi_loot.location_predicate.y.exact",
                        "emi_loot.location_predicate.y",
                        "emi_loot.location_predicate.y.at_least",
                        "emi_loot.location_predicate.y.at_most",
                        "Unknown Y coordinate");
            }

            NumberRange.DoubleRange z = position.get().z();
            if (!z.equals(NumberRange.DoubleRange.ANY)) {
                return NumberProcessors.processNumberRange(z,
                        "emi_loot.location_predicate.z.exact",
                        "emi_loot.location_predicate.z",
                        "emi_loot.location_predicate.z.at_least",
                        "emi_loot.location_predicate.z.at_most",
                        "Unknown Z coordinate");
            }
        }

        Optional<RegistryKey<World>> dim = predicate.dimension();
        if (dim.isPresent()) {
            return LText.translatable("emi_loot.location_predicate.dim", dim.get().getValue().toString());
        }

        Optional<RegistryEntryList<Biome>> biome = predicate.biomes();
        if (biome.isPresent() && biome.get().getTagKey().isPresent()) {
            return LText.translatable("emi_loot.location_predicate.biome.tag", biome.get().getTagKey().get().id().toString());
        } else if (biome.isPresent() && biome.get().size() > 0) {
            List<MutableText> list = biome.get().stream().map(b -> LText.translatable(b.getKey().map(key -> Util.createTranslationKey("biome", key.getValue())).orElse(b.getIdAsString()))).toList();
            return LText.translatable("emi_loot.location_predicate.biome.list", ListProcessors.buildOrList(list));
        }

        Optional<RegistryEntryList<Structure>> structure = predicate.structures();
        if (structure.isPresent() && structure.get().getTagKey().isPresent()) {
            return LText.translatable("emi_loot.location_predicate.structure.tag", structure.get().getTagKey().get().id().toString());
        } else if (structure.isPresent() && structure.get().size() > 0) {
            List<MutableText> list = structure.get().stream().map(b -> {
                String id = b.getKey().map(key -> Util.createTranslationKey("structure", key.getValue())).orElse(b.getIdAsString());
                if (I18n.hasTranslation(id))
                    return LText.translatable(id);
                else
                    return LText.literal(b.getIdAsString());
            }).toList();
            return LText.translatable("emi_loot.location_predicate.structure.list", ListProcessors.buildOrList(list));
        }

        Optional<Boolean> smokey = predicate.smokey();
        if (smokey.isPresent()) {
            if (smokey.get()) {
                return LText.translatable("emi_loot.location_predicate.smoke_true");
            } else {
                return LText.translatable("emi_loot.location_predicate.smoke_false");
            }
        }

        Optional<LightPredicate> light = predicate.light();
        if (light.isPresent()) {
            return LightPredicateParser.parseLightPredicate(light.get());
        }

        Optional<BlockPredicate> block = predicate.block();
        if (block.isPresent()) {
            return BlockPredicateParser.parseBlockPredicate(block.get());
        }

        Optional<FluidPredicate> fluid = predicate.fluid();
        if (fluid.isPresent()) {
            return FluidPredicateParser.parseFluidPredicate(fluid.get());
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable location predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}