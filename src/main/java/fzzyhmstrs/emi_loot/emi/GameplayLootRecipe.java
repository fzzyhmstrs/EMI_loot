package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.client.ClientGameplayLootTable;
import fzzyhmstrs.emi_loot.util.ConditionalStack;
import fzzyhmstrs.emi_loot.util.FloatTrimmer;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.SymbolText;
import fzzyhmstrs.emi_loot.util.WidgetRowBuilder;
import me.fzzyhmstrs.fzzy_config.util.FcText;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GameplayLootRecipe implements EmiRecipe {

    public GameplayLootRecipe(ClientGameplayLootTable loot) {
        this.loot = loot;
        loot.build(MinecraftClient.getInstance().world, Blocks.AIR);
        List<EmiStack> list = new LinkedList<>();
        loot.builtItems.forEach((builtPool)-> {
                builtPool.stacks().forEach(stack -> {
                    list.addAll(stack.ingredient());
                });
                addWidgetBuilders(builtPool, false);
            }
        );
        outputStacks = list;
        String key = "emi_loot.gameplay." + loot.id.toString();
        Text text = LText.translatable(key);
        if (Objects.equals(text.getString(), key)) {
            Optional<ModContainer> modNameOpt = FabricLoader.getInstance().getModContainer(loot.id.getNamespace());
            if (modNameOpt.isPresent()) {
                ModContainer modContainer = modNameOpt.get();
                String modName = modContainer.getMetadata().getName();
                name = LText.translatable("emi_loot.gameplay.unknown_gameplay", modName);
            } else {
                Text unknown = LText.translatable("emi_loot.gameplay.unknown");
                name = LText.translatable("emi_loot.gameplay.unknown_gameplay", unknown.getString());
            }
        } else {
            name = text;
        }
    }

    private final ClientGameplayLootTable loot;
    private final List<EmiStack> outputStacks;
    private final Text name;
    private final List<WidgetRowBuilder> rowBuilderList = new LinkedList<>();

    private void addWidgetBuilders(ClientBuiltPool newPool, boolean recursive) {
        if (recursive || rowBuilderList.isEmpty()) {
            rowBuilderList.add(new WidgetRowBuilder(154));
        }
        boolean added = false;
        for (WidgetRowBuilder builder : rowBuilderList) {
            if (builder.canAddPool(newPool)) {
                builder.addAndTrim(newPool);
                added = true;
                break;
            }
        }
        if (!added) {
            Optional<ClientBuiltPool> opt = rowBuilderList.get(rowBuilderList.size() - 1).addAndTrim(newPool);
            opt.ifPresent(clientMobBuiltPool -> addWidgetBuilders(clientMobBuiltPool, true));
        }


    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiClientPlugin.GAMEPLAY_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return new Identifier(EMILootClient.MOD_ID, "/" + getCategory().id.getPath() + "/" + loot.id.getNamespace() + "/" + loot.id.getPath());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return new LinkedList<>();
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
                    return EmiRecipe.super.getCatalysts();
                }

    @Override
    public List<EmiStack> getOutputs() {
        return outputStacks;
    }

    @Override
    public int getDisplayWidth() {
        return EMILoot.config.isTooltipStyle() ? 144 : 154;
    }

    @Override
    public int getDisplayHeight() {
        if (EMILoot.config.isTooltipStyle()) {
            int stacks = outputStacks.size();
            if (stacks <= 8) {
                return 18 + 11;
            } else {
                if (EMILoot.config.isCompact(EMILoot.Type.GAMEPLAY)) {
                    int ingredients = 0;
                    for (WidgetRowBuilder builder: rowBuilderList) {
                        ingredients += builder.ingredientCount();
                    }
                    if (ingredients <= 4) {
                        return 29;
                    } else {
                        return 11 + 18 * (((ingredients - 5) / 8) + 1);
                    }
                } else {
                    return 11 + 18 * ((stacks - 1) / 8);
                }
            }
        } else {
            return rowBuilderList.size() * 29 + 11;
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int x = 0;
        int y = 0;

        //draw the gameplay name
        widgets.addText(name.asOrderedText(), 0, 0, 0x404040, false);


        if (EMILoot.config.isTooltipStyle()) {
            List<ConditionalStack> stacks = (outputStacks.size() <= 4 || !EMILoot.config.isCompact(EMILoot.Type.GAMEPLAY))
                    ?
                    rowBuilderList.stream().map(WidgetRowBuilder::stacks).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll)
                    :
                    rowBuilderList.stream().map(WidgetRowBuilder::ingredients).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
            int i = 0;
            int j = 0;
            for (ConditionalStack stack: stacks) {
                SlotWidget widget = widgets.addSlot(stack.getIngredient(), i * 18, 11 + (18 * j));
                String rounded = FloatTrimmer.trimFloatString(stack.weight());
                widget.appendTooltip(FcText.INSTANCE.translatable("emi_loot.percent_chance", rounded));
                for (Pair<Integer, Text> pair : stack.conditions()) {
                    widget.appendTooltip(SymbolText.of(pair.getLeft(), pair.getRight()));
                }
                ++i;
                if (i > 7) {
                    i = 0;
                    ++j;
                }
            }
        } else {
            y += 11;
            for (WidgetRowBuilder builder : rowBuilderList) {
                for (ClientBuiltPool pool : builder.getPoolList()) {
                    IconGroupEmiWidget widget = new IconGroupEmiWidget(x, y, pool);
                    widgets.add(widget);
                    x += widget.getWidth() + 6;
                }
                y += 29;
                x = 0;
            }
        }
    }

    //may revisit later
    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public boolean hideCraftable() {
        return EmiRecipe.super.hideCraftable();
    }
}