package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.VariantPredicateAccessor;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntitySubPredicate;
import net.minecraft.predicate.entity.EntitySubPredicateTypes;
import net.minecraft.predicate.entity.FishingHookPredicate;
import net.minecraft.predicate.entity.LightningBoltPredicate;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.predicate.entity.SlimePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerType;
import net.minecraft.world.GameMode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TypeSpecificPredicateParser {

    public static Text parseTypeSpecificPredicate(EntitySubPredicate predicate) {
        if (predicate instanceof LightningBoltPredicate) {
            return parseLightningBoltPredicate((LightningBoltPredicate)predicate);
        }

        if (predicate instanceof FishingHookPredicate) {
            return parseFishingHookPredicate((FishingHookPredicate)predicate);
        }

        if (predicate instanceof PlayerPredicate) {
            return parsePlayerPredicate((PlayerPredicate)predicate);
        }

        if (predicate instanceof SlimePredicate) {
            return parseSlimePredicate((SlimePredicate)predicate);
        }

        if (predicate instanceof EntitySubPredicateTypes.VariantType<?>.VariantPredicate variantPredicate) {
            if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof CatVariant cat) {
                Identifier id = Registries.CAT_VARIANT.getId(cat);
                if (id != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.cat." + id);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.cat", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof FrogVariant frog) {
                Identifier id = Registries.FROG_VARIANT.getId(frog);
                if (id != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.frog." + id);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.frog", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof AxolotlEntity.Variant axolotl) {
                String name  = axolotl.getName();
                if (name != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.axolotl." + name);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.axolotl", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof BoatEntity.Type boat) {
                String name  = boat.getName();
                if (name != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.boat." + name);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.boat", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof FoxEntity.Type fox) {
                String name  = fox.asString();
                if (name != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.fox." + name);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.fox", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof MooshroomEntity.Type moo) {
                String name  = moo.asString();
                if (name != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.mooshroom." + name);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.mooshroom", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof RabbitEntity.RabbitType rabbit) {
                String name  = rabbit.asString();
                if (name != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.rabbit." + name);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.rabbit", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof HorseColor horse) {
                String name  = horse.asString();
                if (name != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.horse." + name);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.horse", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof LlamaEntity.Variant llama) {
                String name  = llama.asString();
                if (name != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.llama." + name);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.llama", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof VillagerType villager) {
                String name  = villager.toString();
                if (name != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.villager." + name);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.villager", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof ParrotEntity.Variant parrot) {
                String name  = parrot.asString();
                if (name != null) {
                    MutableText v = LText.translatable("emi_loot.entity_predicate.type_specific.parrot." + name);
                    return LText.translatable("emi_loot.entity_predicate.type_specific.parrot", v);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof TropicalFishEntity.Variety tropical_fish) {
                Text name  = tropical_fish.getText();
                if (name != null) {
                    return LText.translatable("emi_loot.entity_predicate.type_specific.tropical_fish", name);
                }
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof PaintingVariant) {
                return LText.translatable("emi_loot.entity_predicate.type_specific.painting");
            } else if (((VariantPredicateAccessor<?>)variantPredicate).getVariant() instanceof WolfVariant) {
                return LText.translatable("emi_loot.entity_predicate.type_specific.wolf");
            }
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Type specific predicate undefined or unparsable. Affects table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }


    public static Text parseLightningBoltPredicate(LightningBoltPredicate predicate) {
        NumberRange.IntRange blocksSetOnFire = predicate.blocksSetOnFire();
        if (!blocksSetOnFire.equals(NumberRange.IntRange.ANY)) {
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.lightning",
                    NumberProcessors.processNumberRange(
                        blocksSetOnFire,
                        "emi_loot.entity_predicate.type_specific.lightning.blocks",
                        "emi_loot.entity_predicate.type_specific.lightning.blocks_2",
                            "emi_loot.entity_predicate.type_specific.lightning.blocks_3",
                            "emi_loot.entity_predicate.type_specific.lightning.blocks_4",
                        ""
                    )
            );
        }

        Optional<EntityPredicate> entityStruck = predicate.entityStruck();
        if(entityStruck.isPresent()) {
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.lightning",
                    LText.translatable(
                        "emi_loot.entity_predicate.type_specific.lightning.struck",
                        EntityPredicateParser.parseEntityPredicate(entityStruck.get()).getString()
                    )
            );
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Lightning bolt predicate empty or unparsable. Affects table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }


    public static Text parseFishingHookPredicate(FishingHookPredicate predicate) {
        Optional<Boolean> inOpenWater = predicate.inOpenWater();
        return (inOpenWater.isPresent() && inOpenWater.get()) ? LText.translatable("emi_loot.entity_predicate.type_specific.fishing_hook_true") : LText.translatable("emi_loot.entity_predicate.type_specific.fishing_hook_false");
    }

    public static Text parsePlayerPredicate(PlayerPredicate predicate) {
        NumberRange.IntRange experienceLevel = predicate.experienceLevel();
        if (!experienceLevel.equals(NumberRange.IntRange.ANY)) {
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.player",
                    NumberProcessors.processNumberRange(
                            experienceLevel,
                            "emi_loot.entity_predicate.type_specific.player.level",
                            "emi_loot.entity_predicate.type_specific.player.level_2",
                            "emi_loot.entity_predicate.type_specific.player.level_3",
                            "emi_loot.entity_predicate.type_specific.player.level_4",
                            ""
                    )
            );
        }

        Optional<GameMode> gameMode = predicate.gameMode();
        if (gameMode.isPresent()) {
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.player",
                    LText.translatable("emi_loot.entity_predicate.type_specific.player.gamemode",gameMode.get().getName()));
        }

        List<PlayerPredicate.StatMatcher<?>> stats = predicate.stats();
        if (!stats.isEmpty()) {
            List<MutableText> list = new LinkedList<>();
            for (PlayerPredicate.StatMatcher<?> stat : stats) {
                String name = stat.stat().get().getName();
                String[] namePieces = name.split(":");
                if (namePieces.length == 2) {
                    String typeId = namePieces[0].replace('.', ':');
                    String valueId = namePieces[1].replace('.', ':');
                    MutableText num = NumberProcessors.processNumberRange(
                            stat.range(),
                            "emi_loot.entity_predicate.type_specific.player.stats.exact",
                            "emi_loot.entity_predicate.type_specific.player.stats.between",
                            "emi_loot.entity_predicate.type_specific.player.stats.at_least",
                            "emi_loot.entity_predicate.type_specific.player.stats.at_most",
                            "emi_loot.entity_predicate.type_specific.player.stats.fallback"
                    );
                    list.add(LText.translatable("emi_loot.entity_predicate.type_specific.player.stats.type." + typeId, valueId, num));
                }
            }
            if (!list.isEmpty()) {
                return LText.translatable(
                        "emi_loot.entity_predicate.type_specific.player",
                        ListProcessors.buildAndList(list)
                );
            }
        }

        Object2BooleanMap<Identifier> recipes = predicate.recipes();
        if (!recipes.isEmpty()) {
            List<MutableText> list = new LinkedList<>();
            for (Object2BooleanMap.Entry<Identifier> entry: recipes.object2BooleanEntrySet()) {
                list.add(
                        entry.getBooleanValue()
                        ?
                        LText.translatable("emi_loot.entity_predicate.type_specific.player.recipe_true", entry.getKey())
                        :
                        LText.translatable("emi_loot.entity_predicate.type_specific.player.recipe_false", entry.getKey())
                );
            }
            if (!list.isEmpty()) {
                return LText.translatable(
                        "emi_loot.entity_predicate.type_specific.player",
                        ListProcessors.buildAndList(list)
                );
            }
        }

        Map<Identifier, PlayerPredicate.AdvancementPredicate> advancements = predicate.advancements();
        if (!advancements.isEmpty()) {
            List<MutableText> list = new LinkedList<>();
            for (Map.Entry<Identifier, PlayerPredicate.AdvancementPredicate> entry: advancements.entrySet()) {
                String idString = entry.getKey().toString();
                PlayerPredicate.AdvancementPredicate advancementPredicate = entry.getValue();
                if (advancementPredicate instanceof PlayerPredicate.CompletedAdvancementPredicate) {
                    boolean done = ((PlayerPredicate.CompletedAdvancementPredicate) advancementPredicate).done();
                    if (done) {
                        list.add(LText.translatable("emi_loot.entity_predicate.type_specific.player.adv.id_true", idString));
                    } else {
                        list.add(LText.translatable("emi_loot.entity_predicate.type_specific.player.adv.id_false", idString));
                    }
                } else if (advancementPredicate instanceof PlayerPredicate.AdvancementCriteriaPredicate) {
                    Object2BooleanMap<String> criteria = ((PlayerPredicate.AdvancementCriteriaPredicate) advancementPredicate).criteria();
                    if (!criteria.isEmpty()) {
                        List<MutableText> list2 = new LinkedList<>();
                        for (Object2BooleanMap.Entry<String> criteriaEntry : criteria.object2BooleanEntrySet()) {
                            if (criteriaEntry.getBooleanValue()) {
                                list2.add(LText.translatable("emi_loot.entity_predicate.type_specific.player.adv.crit_true", criteriaEntry.getKey()));
                            } else {
                                list2.add(LText.translatable("emi_loot.entity_predicate.type_specific.player.adv.crit_false", criteriaEntry.getKey()));
                            }
                        }
                        list.add(LText.translatable("emi_loot.entity_predicate.type_specific.player.adv.crit_base", idString, ListProcessors.buildAndList(list2)));
                    }
                }
            }
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.player", ListProcessors.buildAndList(list));
        }

        Optional<EntityPredicate> entityPredicate = predicate.lookingAt();
        if (entityPredicate.isPresent()) {
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.player",
                    LText.translatable("emi_loot.entity_predicate.type_specific.player.looking", EntityPredicateParser.parseEntityPredicate(entityPredicate.get())));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Lightning bolt predicate empty or unparsable. Affects table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

    public static Text parseSlimePredicate(SlimePredicate predicate) {
        NumberRange.IntRange size = predicate.size();
        if (size.equals(NumberRange.IntRange.ANY)) {
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Undefined slime size predicate in table: "  + LootTableParser.currentTable);
            return LText.translatable("emi_loot.predicate.invalid");
        }
        return NumberProcessors.processNumberRange(
                size,
                "emi_loot.entity_predicate.type_specific.slime",
                "emi_loot.entity_predicate.type_specific.slime_2",
                "emi_loot.entity_predicate.type_specific.slime_3",
                "emi_loot.entity_predicate.type_specific.slime_4",
                ""
        );
    }
}