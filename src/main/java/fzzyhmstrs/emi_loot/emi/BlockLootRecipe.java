package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientBlockLootTable;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.util.BlockStateEmiStack;
import fzzyhmstrs.emi_loot.util.ConditionalStack;
import fzzyhmstrs.emi_loot.util.FloatTrimmer;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import fzzyhmstrs.emi_loot.util.SymbolText;
import fzzyhmstrs.emi_loot.util.WidgetRowBuilder;
import me.fzzyhmstrs.fzzy_config.util.FcText;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BlockLootRecipe implements EmiRecipe {

    public BlockLootRecipe(ClientBlockLootTable loot) {
        this.loot = loot;
        Identifier blockId = loot.blockId;
        Block block = Registries.BLOCK.get(blockId);
        loot.build(MinecraftClient.getInstance().world, block);
        inputStack = block.asItem() == Items.AIR ? new BlockStateEmiStack(block.getDefaultState(), blockId) : EmiStack.of(block);
        List<EmiStack> list = new LinkedList<>();
        loot.builtItems.forEach((builtPool)-> {
            builtPool.stacks().forEach(stack -> {
                /*if (weight < 100f) {
                    allStacksGuaranteed = false;
                }*/
                list.addAll(stack.ingredient());
            });
            addWidgetBuilders(builtPool, false);
        });
        outputStacks = list;
    }

    private final ClientBlockLootTable loot;
    private final EmiStack inputStack;
    private final List<EmiStack> outputStacks;
    private final List<WidgetRowBuilder> rowBuilderList = new LinkedList<>();

    private void addWidgetBuilders(ClientBuiltPool newPool, boolean recursive) {
        WidgetRowBuilder builder;
        boolean newBuilder = false;
        if (recursive || rowBuilderList.isEmpty()) {
            builder = new WidgetRowBuilder(115);
            newBuilder = true;
        } else {
            builder = rowBuilderList.get(rowBuilderList.size() - 1);
        }
        Optional<ClientBuiltPool> opt = builder.addAndTrim(newPool);
        if (newBuilder) rowBuilderList.add(builder);
        opt.ifPresent(clientMobBuiltPool -> addWidgetBuilders(clientMobBuiltPool, true));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiClientPlugin.BLOCK_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return new Identifier(EMILootClient.MOD_ID, "/" + getCategory().id.getPath() + "/" + loot.id.getNamespace() + "/" + loot.id.getPath());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputStack.getItemStack().isEmpty() ? Collections.emptyList() : List.of(inputStack);
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
        return EMILoot.config.isTooltipStyle() ? 144 : 160;
    }

    @Override
    public int getDisplayHeight() {
        if (EMILoot.config.isTooltipStyle()) {
            int stacks = outputStacks.size();
            if (stacks <= 5) {
                return 18;
            } else {
                if (EMILoot.config.isCompact(EMILoot.Type.BLOCK)) {
                    int ingredients = 0;
                    for (WidgetRowBuilder builder: rowBuilderList) {
                        ingredients += builder.ingredientCount();
                    }
                    if (ingredients <= 4) {
                        return 29;
                    } else {
                        return 18 + 18 * (((ingredients - 5) / 8) + 1);
                    }
                } else {
                    return 18 + 18 * (((stacks - 6) / 8) + 1);
                }
            }
        } else {
            return 23 + 29 * (rowBuilderList.size() - 1);
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputStack, 0, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 20, 0);
        int x = 46;
        int y = 0;

        if (EMILoot.config.isTooltipStyle()) {
            List<ConditionalStack> stacks = (outputStacks.size() <= 4 || !EMILoot.config.isCompact(EMILoot.Type.BLOCK))
                ?
                rowBuilderList.stream().map(WidgetRowBuilder::stacks).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll)
                :
                rowBuilderList.stream().map(WidgetRowBuilder::ingredients).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
            int i = 3;
            int j = 0;
            for (ConditionalStack stack: stacks) {
                SlotWidget widget = widgets.addSlot(stack.getIngredient(), i * 18, 18 * j);
                String rounded = FloatTrimmer.trimFloatString(stack.weight());
                widget.appendTooltip(FcText.INSTANCE.translatable("emi_loot.percent_chance", rounded));
                if (EMILoot.config.isNotPlain()) {
                    for (Pair<Integer, Text> pair : stack.conditions()) {
                        widget.appendTooltip(SymbolText.of(pair.getLeft(), pair.getRight()));
                    }
                }
                ++i;
                if (i > 7) {
                    i = 0;
                    ++j;
                }
            }
        } else {
            for (WidgetRowBuilder builder : rowBuilderList) {
                for (ClientBuiltPool pool : builder.getPoolList()) {
                    IconGroupEmiWidget widget = new IconGroupEmiWidget(x, y, pool);
                    widgets.add(widget);
                    x += widget.getWidth() + 6;
                }
                y += 29;
                x = 46;
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