package fzzyhmstrs.emi_loot.util;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public record TextKey(int index, List<String> args) {

    static final Map<String, Integer> keyMap = new HashMap<>();
    static final Map<Integer, String> keyReverseMap = new HashMap<>();
    static final Map<Integer, Function<TextKey, Text>> keyTextBuilderMap = new HashMap<>();
    static final Map<Integer, BiFunction<ItemStack, World, List<ItemStack>>> processorMap = new HashMap<>();
    static final Map<Integer, Identifier> keySpriteIdMap = new HashMap<>();
    static final Function<TextKey, Text> DEFAULT_FUNCTION = (key)-> LText.translatable("emi_loot.missing_key");
    static final BiFunction<ItemStack, World, List<ItemStack>> DEFAULT_PROCESSOR = (stack, world) -> List.of(stack);
    static final Identifier EMPTY = Identifier.of(EMILoot.MOD_ID, "textures/gui/empty.png");
    static int curDynamicIndex = 1000;

    public static final Set<String> defaultSkips = Set.of("emi_loot.function.set_count_add", "emi_loot.function.set_count_set", "emi_loot.function.fill_player_head", "emi_loot.function.limit_count", "emi_loot.no_conditions");

    static {
        BiFunction<ItemStack, World, List<ItemStack>> smeltProcessor = (stack, world) -> {
            List<ItemStack> finalStacks = new LinkedList<>();
            //finalStacks.add(stack);
            if (world != null) {
                Optional<RecipeEntry<SmeltingRecipe>> opt = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SingleStackRecipeInput(stack), world);
                if (opt.isPresent()) {
                    ItemStack tempStack = opt.get().value().getResult(world.getRegistryManager());
                    if (!tempStack.isEmpty()) {
                        //System.out.println(tempStack);
                        finalStacks.add(tempStack.copy());
                    }
                } else {
                    finalStacks.add(stack);
                }
            } else {
                finalStacks.add(stack);
            }
            return finalStacks;
        };

        BiFunction<ItemStack, World, List<ItemStack>> ominousProcessor = (stack, world) -> List.of(Raid.getOminousBanner(world.getRegistryManager().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN)));

        BiFunction<ItemStack, World, List<ItemStack>> glintProcessor = (stack, world) -> {
            stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            return List.of(stack);
        };

        mapBuilder(0, "emi_loot.function.empty", (key)->LText.empty(), EMPTY);
        mapBuilder(1, "emi_loot.function.bonus", (key)-> getOneArgText(1, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/bonus.png"));
        mapBuilder(2, "emi_loot.function.potion", (key) -> getOneArgText(2, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/potion.png"));
        mapBuilder(3, "emi_loot.function.set_count_add", (key)-> getBasicText(3), Identifier.of(EMILoot.MOD_ID, "textures/gui/set_count_add.png"));
        mapBuilder(4, "emi_loot.function.set_count_set", (key)-> getBasicText(4), Identifier.of(EMILoot.MOD_ID, "textures/gui/set_count_add.png"));
        mapBuilder(5, "emi_loot.function.randomly_enchanted_book", (key)-> getBasicText(5), Identifier.of(EMILoot.MOD_ID, "textures/gui/random_book.png"), glintProcessor);
        mapBuilder(6, "emi_loot.function.randomly_enchanted_item", (key)-> getBasicText(6), Identifier.of(EMILoot.MOD_ID, "textures/gui/random_item.png"), glintProcessor);
        mapBuilder(7, "emi_loot.function.set_enchant_book", (key)-> getBasicText(7), Identifier.of(EMILoot.MOD_ID, "textures/gui/set_book.png"));
        mapBuilder(8, "emi_loot.function.set_enchant_item", (key)-> getBasicText(8), Identifier.of(EMILoot.MOD_ID, "textures/gui/set_item.png"));
        mapBuilder(9, "emi_loot.function.smelt", (key)-> getBasicText(9), Identifier.of(EMILoot.MOD_ID, "textures/gui/smelt.png"), smeltProcessor);
        mapBuilder(10, "emi_loot.function.looting", (key)-> getBasicText(10), Identifier.of(EMILoot.MOD_ID, "textures/gui/looting.png"));
        mapBuilder(11, "emi_loot.function.map", (key) -> getOneArgText(11, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/map.png"));
        mapBuilder(12, "emi_loot.function.set_contents", (key)-> getBasicText(12), Identifier.of(EMILoot.MOD_ID, "textures/gui/set_contents.png"));
        mapBuilder(13, "emi_loot.function.damage", (key)-> getOneArgText(13, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/damage.png"));
        mapBuilder(14, "emi_loot.function.copy_state", (key)-> getBasicText(14), Identifier.of(EMILoot.MOD_ID, "textures/gui/copy.png"));
        mapBuilder(15, "emi_loot.function.copy_name", (key)-> getOneArgText(15, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/copy.png"));
        mapBuilder(16, "emi_loot.function.copy_nbt", (key)-> getBasicText(16), Identifier.of(EMILoot.MOD_ID, "textures/gui/nbt.png"));
        mapBuilder(17, "emi_loot.function.decay", (key)-> getBasicText(17), Identifier.of(EMILoot.MOD_ID, "textures/gui/tnt.png"));
        mapBuilder(18, "emi_loot.function.fill_player_head", (key)-> getBasicText(18), Identifier.of(EMILoot.MOD_ID, "textures/gui/fzzy.png"));
        mapBuilder(19, "emi_loot.function.limit_count", (key)-> getOneArgText(19, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/limit_count.png"));
        mapBuilder(20, "emi_loot.function.set_attributes", (key)-> getOneArgText(20, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/set_attributes.png"));
        mapBuilder(21, "emi_loot.function.banner", (key)-> getBasicText(21), Identifier.of(EMILoot.MOD_ID, "textures/gui/banner.png"));
        mapBuilder(22, "emi_loot.function.lore", (key)-> getBasicText(22), Identifier.of(EMILoot.MOD_ID, "textures/gui/lore.png"));
        mapBuilder(23, "emi_loot.function.set_stew", (key)-> getOneArgText(23, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/stew.png"));
        mapBuilder(24, "emi_loot.function.set_nbt", (key)-> getBasicText(24), Identifier.of(EMILoot.MOD_ID, "textures/gui/nbt.png"));
        mapBuilder(25, "emi_loot.function.set_loot_table", (key)-> getOneArgText(25, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/chest.png"));
        mapBuilder(26, "emi_loot.function.reference", (key)-> getOneArgText(26, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/reference.png"));
        mapBuilder(27, "emi_loot.function.sequence", (key)-> getOneArgText(27, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/sequence.png"));
        mapBuilder(28, "emi_loot.function.set_item", (key)-> getOneArgText(28, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/set_item.png"));
        mapBuilder(29, "emi_loot.function.modify_contents", (key)-> getOneArgText(29, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/modify_contents.png"));
        mapBuilder(30, "emi_loot.function.filtered", (key)-> getTwoArgText(30, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/filtered.png"));
        mapBuilder(31, "emi_loot.function.fireworks", (key)-> getBasicText(31), Identifier.of(EMILoot.MOD_ID, "textures/gui/firework.png"));
        mapBuilder(32, "emi_loot.function.firework_explosion", (key)-> getOneArgText(32, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/firework.png"));
        mapBuilder(33, "emi_loot.function.set_book_cover", (key)-> getOneArgText(33, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/set_book.png"));

        mapBuilder(34, "emi_loot.condition.survives_explosion", (key)-> getBasicText(34), Identifier.of(EMILoot.MOD_ID, "textures/gui/tnt.png"));
        mapBuilder(35, "emi_loot.condition.blockstate", (key)-> getOneArgText(35, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/blockstate.png"));
        mapBuilder(36, "emi_loot.condition.table_bonus", (key)-> getOneArgText(36, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/percent.png"));
        mapBuilder(37, "emi_loot.condition.invert", (key)-> getInvertedText(37, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/invert.png"));
        //mapBuilder(38, "emi_loot.condition.alternates", TextKey::getAnyOfText, Identifier.of(EMILoot.MOD_ID, "textures/gui/or.png"));
        mapBuilder(39, "emi_loot.condition.any_of", TextKey::getAnyOfText, Identifier.of(EMILoot.MOD_ID, "textures/gui/or.png"));
        mapBuilder(40, "emi_loot.condition.all_of", TextKey::getAllOfText, Identifier.of(EMILoot.MOD_ID, "textures/gui/or.png"));
        mapBuilder(41, "emi_loot.condition.killed_player", (key)-> getBasicText(41), Identifier.of(EMILoot.MOD_ID, "textures/gui/steve.png"));
        mapBuilder(42, "emi_loot.condition.chance", (key)-> getOneArgText(42, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/percent.png"));
        mapBuilder(43, "emi_loot.condition.chance_looting", (key)-> getThreeArgText(43, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/chance_looting.png"));
        mapBuilder(44, "emi_loot.condition.damage_source", (key)-> getOneArgText(44, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/tiny_cactus.png"));
        mapBuilder(45, "emi_loot.condition.location", (key)-> getOneArgText(45, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/location.png"));
        mapBuilder(46, "emi_loot.condition.entity_props", (key)-> getOneArgText(46, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/entity_props.png"));
        mapBuilder(47, "emi_loot.condition.match_tool", (key)-> getOneArgText(47, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/match_tool.png"));
        mapBuilder(48, "emi_loot.condition.entity_scores", (key)-> getBasicText(48), Identifier.of(EMILoot.MOD_ID, "textures/gui/score.png"));
        mapBuilder(49, "emi_loot.condition.reference", (key)-> getOneArgText(49, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/reference.png"));
        mapBuilder(50, "emi_loot.condition.time_check", (key)-> getOneArgText(50, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/time.png"));
        mapBuilder(51, "emi_loot.condition.value_check", (key)-> getTwoArgText(51, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/value.png"));
        mapBuilder(52, "emi_loot.condition.raining_true", (key)-> getBasicText(52), Identifier.of(EMILoot.MOD_ID, "textures/gui/raining.png"));
        mapBuilder(53, "emi_loot.condition.raining_false", (key)-> getBasicText(53), Identifier.of(EMILoot.MOD_ID, "textures/gui/sunny.png"));
        mapBuilder(54, "emi_loot.condition.thundering_true", (key)-> getBasicText(54), Identifier.of(EMILoot.MOD_ID, "textures/gui/thundering.png"));
        mapBuilder(55, "emi_loot.condition.thundering_false", (key)-> getBasicText(55), Identifier.of(EMILoot.MOD_ID, "textures/gui/not_thundering.png"));


        //tool tag textkeys
        //pickaxe
        mapBuilder(62, "emi_loot.pickaxe.wood", (key)-> getBasicText(62), Identifier.of(EMILoot.MOD_ID, "textures/gui/pickaxe_wood.png"));
        mapBuilder(63, "emi_loot.pickaxe.stone", (key)-> getBasicText(63), Identifier.of(EMILoot.MOD_ID, "textures/gui/pickaxe_stone.png"));
        mapBuilder(64, "emi_loot.pickaxe.iron", (key)-> getBasicText(64), Identifier.of(EMILoot.MOD_ID, "textures/gui/pickaxe_iron.png"));
        mapBuilder(65, "emi_loot.pickaxe.diamond", (key)-> getBasicText(65), Identifier.of(EMILoot.MOD_ID, "textures/gui/pickaxe_diamond.png"));
        mapBuilder(66, "emi_loot.pickaxe.netherite", (key)-> getBasicText(66), Identifier.of(EMILoot.MOD_ID, "textures/gui/pickaxe_netherite.png"));
        //axe
        mapBuilder(77, "emi_loot.axe.wood", (key)-> getBasicText(77), Identifier.of(EMILoot.MOD_ID, "textures/gui/axe_wood.png"));
        mapBuilder(78, "emi_loot.axe.stone", (key)-> getBasicText(78), Identifier.of(EMILoot.MOD_ID, "textures/gui/axe_stone.png"));
        mapBuilder(79, "emi_loot.axe.iron", (key)-> getBasicText(79), Identifier.of(EMILoot.MOD_ID, "textures/gui/axe_iron.png"));
        mapBuilder(80, "emi_loot.axe.diamond", (key)-> getBasicText(80), Identifier.of(EMILoot.MOD_ID, "textures/gui/axe_diamond.png"));
        mapBuilder(81, "emi_loot.axe.netherite", (key)-> getBasicText(81), Identifier.of(EMILoot.MOD_ID, "textures/gui/axe_netherite.png"));
        //shovel
        mapBuilder(92, "emi_loot.shovel.wood", (key)-> getBasicText(92), Identifier.of(EMILoot.MOD_ID, "textures/gui/shovel_wood.png"));
        mapBuilder(93, "emi_loot.shovel.stone", (key)-> getBasicText(93), Identifier.of(EMILoot.MOD_ID, "textures/gui/shovel_stone.png"));
        mapBuilder(94, "emi_loot.shovel.iron", (key)-> getBasicText(94), Identifier.of(EMILoot.MOD_ID, "textures/gui/shovel_iron.png"));
        mapBuilder(95, "emi_loot.shovel.diamond", (key)-> getBasicText(95), Identifier.of(EMILoot.MOD_ID, "textures/gui/shovel_diamond.png"));
        mapBuilder(96, "emi_loot.shovel.netherite", (key)-> getBasicText(96), Identifier.of(EMILoot.MOD_ID, "textures/gui/shovel_netherite.png"));
        //hoe
        mapBuilder(107, "emi_loot.hoe.wood", (key)-> getBasicText(107), Identifier.of(EMILoot.MOD_ID, "textures/gui/hoe_wood.png"));
        mapBuilder(108, "emi_loot.hoe.stone", (key)-> getBasicText(108), Identifier.of(EMILoot.MOD_ID, "textures/gui/hoe_stone.png"));
        mapBuilder(109, "emi_loot.hoe.iron", (key)-> getBasicText(109), Identifier.of(EMILoot.MOD_ID, "textures/gui/hoe_iron.png"));
        mapBuilder(110, "emi_loot.hoe.diamond", (key)-> getBasicText(110), Identifier.of(EMILoot.MOD_ID, "textures/gui/hoe_diamond.png"));
        mapBuilder(111, "emi_loot.hoe.netherite", (key)-> getBasicText(111), Identifier.of(EMILoot.MOD_ID, "textures/gui/hoe_netherite.png"));
        //Direct drops and builtin/lootify conditions
        mapBuilder(124, "emi_loot.condition.sequence", (key)-> getBasicText(124), Identifier.of(EMILoot.MOD_ID, "textures/gui/sequence.png"));
        mapBuilder(125, "emi_loot.condition.direct_drop", (key)-> getBasicText(125), Identifier.of(EMILoot.MOD_ID, "textures/gui/direct_drops.png"));
        mapBuilder(126, "emi_loot.condition.spawns_with", (key)-> getBasicText(126), Identifier.of(EMILoot.MOD_ID, "textures/gui/spawns_with.png"));
        mapBuilder(127, "emi_loot.condition.creeper", (key)-> getBasicText(127), Identifier.of(EMILoot.MOD_ID, "textures/gui/creeper.png"));
        mapBuilder(128, "emi_loot.condition.wither_kill", (key)-> getBasicText(128), Identifier.of(EMILoot.MOD_ID, "textures/gui/wither.png"));
        mapBuilder(129, "emi_loot.function.set_any_damage", (key)-> getBasicText(129), Identifier.of(EMILoot.MOD_ID, "textures/gui/damage.png"));
        mapBuilder(130, "emi_loot.function.ominous_banner", (key)-> getBasicText(130), Identifier.of(EMILoot.MOD_ID, "textures/gui/ominous.png"), ominousProcessor);
        mapBuilder(131, "emi_loot.function.set_pages", (key)-> getBasicText(131), Identifier.of(EMILoot.MOD_ID, "textures/gui/set_book.png"));
        mapBuilder(132, "emi_loot.function.ominous_bottle", (key)-> getOneArgText(132, key), Identifier.of(EMILoot.MOD_ID, "textures/gui/potion.png"));
        mapBuilder(133, "emi_loot.function.custom_model_data", (key)-> getBasicText(133), Identifier.of(EMILoot.MOD_ID, "textures/gui/random_item.png"));
        mapBuilder(134, "emi_loot.function.toggle_tooltip", (key)-> getBasicText(134), Identifier.of(EMILoot.MOD_ID, "textures/gui/tooltip.png"));

        //No conditions
        mapBuilder(150, "emi_loot.no_conditions", (key)-> getBasicText(150), EMPTY);
    }

    private static void mapBuilder(int index, String key, Function<TextKey, Text> function, Identifier spriteId) {
        keyMap.put(key, index);
        keyReverseMap.put(index, key);
        keyTextBuilderMap.put(index, function);
        keySpriteIdMap.put(index, spriteId);
    }

    private static void mapBuilder(int index, String key, Function<TextKey, Text> function, Identifier spriteId, BiFunction<ItemStack, World, List<ItemStack>> processor) {
        keyMap.put(key, index);
        keyReverseMap.put(index, key);
        keyTextBuilderMap.put(index, function);
        keySpriteIdMap.put(index, spriteId);
        processorMap.put(index, processor);
    }

    public static void register(String key, int args, Identifier sprite, BiFunction<ItemStack, World, List<ItemStack>> processor) {
        if (keyMap.containsKey(key)) throw new IllegalArgumentException("Text key [" + key + "] already registered!");
        if (!sprite.toString().contains(".png")) throw new IllegalArgumentException("Text key [" + key + "] registered with sprite identifier [" + sprite + "] that isn't a png!)");
        int index = curDynamicIndex;
        curDynamicIndex++;
        switch (args) {
            case 0 -> mapBuilder(index, key, (tk) -> getBasicText(index), sprite, processor);
            case 1 -> mapBuilder(index, key, (tk) -> getOneArgText(index, tk), sprite, processor);
            case 2 -> mapBuilder(index, key, (tk) -> getTwoArgText(index, tk), sprite, processor);
            default -> mapBuilder(index, key, TextKey::getAnyOfText, sprite, processor);
        }
    }

    public static void register(String key, int args, Identifier sprite) {
        if (keyMap.containsKey(key)) throw new IllegalArgumentException("Text key [" + key + "] already registered!");
        if (!sprite.toString().contains(".png")) throw new IllegalArgumentException("Text key [" + key + "] registered with sprite identifier [" + sprite + "] that isn't a png!)");
        int index = curDynamicIndex;
        curDynamicIndex++;
        switch (args) {
            case 0 -> mapBuilder(index, key, (tk) -> getBasicText(index), sprite);
            case 1 -> mapBuilder(index, key, (tk) -> getOneArgText(index, tk), sprite);
            case 2 -> mapBuilder(index, key, (tk) -> getTwoArgText(index, tk), sprite);
            default -> mapBuilder(index, key, TextKey::getAnyOfText, sprite);
        }
    }

    private static Text getBasicText(int index) {
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        return LText.translatable(translationKey);
    }

    private static Text getOneArgText(int index, TextKey key) {
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        String arg;
        try {
            arg = key.args.get(0);
        } catch(Exception e) {
            EMILoot.LOGGER.error("Couldn't get one-arg text");
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
            arg = "Missing";
        }
        return LText.translatable(translationKey, arg);
    }

    private static Text getTwoArgText(int index, TextKey key) {
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        String arg1;
        String arg2;
        try {
            arg1 = key.args.get(0);
        } catch(Exception e) {
            EMILoot.LOGGER.error("Couldn't get first arg of two-arg text");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            arg1 = "Missing";
        }
        try {
            arg2 = key.args.get(1);
        } catch(Exception e) {
            EMILoot.LOGGER.error("Couldn't get second arg of two-arg text");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            arg2 = "Missing";
        }
        return LText.translatable(translationKey, arg1, arg2);
    }

    private static Text getThreeArgText(int index, TextKey key) {
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        String arg1;
        String arg2;
        String arg3;
        try {
            arg1 = key.args.get(0);
        } catch(Exception e) {
            EMILoot.LOGGER.error("Couldn't get first arg of two-arg text");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            arg1 = "Missing";
        }
        try {
            arg2 = key.args.get(1);
        } catch(Exception e) {
            EMILoot.LOGGER.error("Couldn't get second arg of three-arg text");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            arg2 = "Missing";
        }
        try {
            arg3 = key.args.get(2);
        } catch(Exception e) {
            EMILoot.LOGGER.error("Couldn't get third arg of three-arg text");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            arg3 = "Missing";
        }
        return LText.translatable(translationKey, arg1, arg2, arg3);
    }

    private static Text getAnyOfText(TextKey key) {
        List<String> args = key.args;
        int size = args.size();
        if (size == 1) {
            return LText.translatable("emi_loot.condition.any_of", args.get(0));
        } else if (size == 2) {
            return LText.translatable("emi_loot.condition.any_of_2", args.get(0), args.get(1));
        } else {
            MutableText finalText = LText.empty();
            for (int i = 0; i < size; i++) {
                String arg = args.get(i);
                if (i == (size - 2)) {
                    finalText.append(LText.translatable("emi_loot.condition.any_of_3a", arg));
                } else if (i == (size - 1)) {
                    finalText.append(LText.translatable("emi_loot.condition.any_of", arg));
                } else {
                    finalText.append(LText.translatable("emi_loot.condition.any_of_3", arg));
                }
            }
            return finalText;
        }
    }

    private static Text getAllOfText(TextKey key) {
        List<String> args = key.args;
        int size = args.size();
        if (size == 1) {
            return LText.translatable("emi_loot.condition.all_of", args.get(0));
        } else if (size == 2) {
            return LText.translatable("emi_loot.condition.all_of_2", args.get(0), args.get(1));
        } else {
            MutableText finalText = LText.empty();
            for (int i = 0; i < size; i++) {
                String arg = args.get(i);
                if (i == (size - 2)) {
                    finalText.append(LText.translatable("emi_loot.condition.all_of_3a", arg));
                } else if (i == (size - 1)) {
                    finalText.append(LText.translatable("emi_loot.condition.all_of", arg));
                } else {
                    finalText.append(LText.translatable("emi_loot.condition.all_of_3", arg));
                }
            }
            return finalText;
        }
    }

    private static Text getInvertedText(int index, TextKey key) {
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        String arg;
        try {
            arg = key.args.get(0);
        } catch(Exception e) {
            EMILoot.LOGGER.error("Couldn't get inverted text");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            arg = "Missing";
        }
        return LText.translatable(translationKey, arg).formatted(Formatting.RED);
    }

    public boolean isNotEmpty() {
        return index != 0;
    }

    public static Set<String> keys() {
        return keyMap.keySet();
    }

    public static int getIndex(String key) {
        return keyMap.getOrDefault(key, -1);
    }

    public static String getKey(int index) {
        return keyReverseMap.getOrDefault(index, "emi_loot.function.empty");
    }

    public static Identifier getSpriteId(int index) {
        return keySpriteIdMap.getOrDefault(index, EMPTY);
    }

    public static TextKey empty() {
        return new TextKey(0, new LinkedList<>());
    }

    public static TextKey of(String key, String ... args) {
        if (keyMap.containsKey(key)) {
            return new TextKey(keyMap.get(key), Arrays.stream(args).toList());
        } else {
            return new TextKey(0, new LinkedList<>());
        }
    }

    public static TextKey of(String key, List<String> args) {
        if (keyMap.containsKey(key)) {
           return new TextKey(keyMap.get(key), args);
        } else {
			EMILoot.LOGGER.error("Couldn't parse TextKey with key: {}and args: {}", key, args);
            return new TextKey(0, new LinkedList<>());
        }
    }

    public static TextKey of(String key) {
        return TextKey.of(key, new LinkedList<>());
    }

    public static TextKey of (String key, String arg) {
        return TextKey.of(key, Collections.singletonList(arg));
    }

    public static String symbolKey(int index) {
        return String.valueOf((char)(0xe700 + index));
    }

    public TextKeyResult process(ItemStack stack, @Nullable World world) {
        BiFunction<ItemStack, World, List<ItemStack>> processor = processorMap.getOrDefault(this.index, DEFAULT_PROCESSOR);
        List<ItemStack> finalStacks = processor.apply(stack, world);
        Text text = keyTextBuilderMap.getOrDefault(this.index, DEFAULT_FUNCTION).apply(this);
        return new TextKeyResult(text, finalStacks);
    }

    public Text asText() {
        return keyTextBuilderMap.getOrDefault(this.index, DEFAULT_FUNCTION).apply(this);
    }

    public boolean skip() {
        return EMILoot.config.skippedKeys.contains(getKey(this.index));
    }

    public record TextKeyResult(Text text, List<ItemStack> stacks){}

    public static TextKey fromBuf(PacketByteBuf buf) {
        int key = buf.readVarInt();
        int size = buf.readByte();
        List<String> args = new LinkedList<>();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                args.add(buf.readString());
            }
        }
        return new TextKey(key, args);
    }

    public void toBuf(PacketByteBuf buf) {
        buf.writeVarInt(this.index);
        if (args.isEmpty()) {
            buf.writeByte(0);
        } else {
            int argSize = Math.min(127, args.size());
            buf.writeByte(args.size());
            for (int i = 0; i< argSize; i++) {
                String string = args.get(i);
                buf.writeString(string);
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextKey textKey = (TextKey) o;
        return index == textKey.index && args.equals(textKey.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, args);
    }
}