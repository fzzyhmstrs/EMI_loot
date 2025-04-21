package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootClientAgnos;
import fzzyhmstrs.emi_loot.client.ClientBlockLootTable;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.client.InitializedSupplier;
import fzzyhmstrs.emi_loot.util.BlockStateEmiStack;
import fzzyhmstrs.emi_loot.util.ConditionalStack;
import fzzyhmstrs.emi_loot.util.FloatTrimmer;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import fzzyhmstrs.emi_loot.util.SymbolText;
import fzzyhmstrs.emi_loot.util.WidgetRowBuilder;
import me.fzzyhmstrs.fzzy_config.util.FcText;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BlockLootRecipe implements EmiRecipe {

    public BlockLootRecipe(BlockLootRecipeData data) {
        this.id = data.id;
        this.inputStack = data.inputStack;
        this.outputStacks = data.outputStacks;
        this.rowBuilderList = data.rowBuilderList;
        this.isSimple = data.guaranteed;
    }

    private final Identifier id;
    private final InitializedSupplier<EmiStack> inputStack;
    private final InitializedSupplier<List<EmiStack>> outputStacks;
    private final List<WidgetRowBuilder> rowBuilderList;
    private final boolean isSimple;
    private List<EmiIngredient> inputStacks;

    public void init() {
        inputStack.init();
        outputStacks.init();
        inputStacks = inputStack.get().getItemStack().isEmpty() ? Collections.emptyList() : List.of(inputStack.get());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiClientPlugin.BLOCK_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputStacks;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return EmiRecipe.super.getCatalysts();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputStacks.get();
    }

    @Override
    public int getDisplayWidth() {
        return EMILoot.config.isTooltipStyle() ? 144 : 160;
    }

    @Override
    public int getDisplayHeight() {
        if (EMILoot.config.isTooltipStyle()) {
            int stacks = outputStacks.get().size();
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
        widgets.addSlot(inputStack.get(), 0, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 20, 0);
        int x = 46;
        int y = 0;

        if (EMILoot.config.isTooltipStyle()) {
            List<ConditionalStack> stacks = (outputStacks.get().size() <= 4 || !EMILoot.config.isCompact(EMILoot.Type.BLOCK))
                ?
                rowBuilderList.stream().map(WidgetRowBuilder::stacks).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll)
                :
                rowBuilderList.stream().map(WidgetRowBuilder::ingredients).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
            int i = 3;
            int j = 0;
            for (ConditionalStack stack: stacks) {
                SlotWidget widget = widgets.addSlot(stack.getIngredient(), i * 18, 18 * j);
                String rounded = FloatTrimmer.trimFloatString(Math.max(stack.weight() / 100f, 0.01f), EMILoot.config.chanceDecimalPlaces.get());
                widget.appendTooltip(FcText.INSTANCE.translatable("emi_loot.rolls", rounded).formatted(Formatting.GRAY));
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
                    IconGroupEmiWidget widget = EMILootClientAgnos.createIconGroupEmiWidget(x, y, pool);
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
        return isSimple;
    }

    @Override
    public boolean hideCraftable() {
        return EmiRecipe.super.hideCraftable();
    }

    public record BlockLootRecipeData(Identifier id, List<WidgetRowBuilder> rowBuilderList, boolean guaranteed, InitializedSupplier<EmiStack> inputStack, InitializedSupplier<List<EmiStack>> outputStacks) {

        public static BlockLootRecipeData of(ClientBlockLootTable loot) {
            List<ItemStack> itemStackList = new ArrayList<>();
            boolean allStacksGuaranteed = true;
            List<WidgetRowBuilder> rowBuilderList = new ArrayList<>();
            Identifier blockId = loot.blockId;
            Block block = Registries.BLOCK.get(blockId);
            loot.build(MinecraftClient.getInstance().world, block);
            for (ClientBuiltPool builtPool : loot.builtItems) {
                for (ConditionalStack stack : builtPool.stacks()) {
                    if (stack.weight() < 100f) {
                        allStacksGuaranteed = false;
                    }
                    itemStackList.addAll(stack.getRawStacks());
                }
                if (loot.isSimple) {
                    addSimpleWidgetBuilder(rowBuilderList, builtPool);
                } else {
                    addWidgetBuilders(rowBuilderList, builtPool, false);
                }
            }
            Identifier id = Identifier.of(EMILoot.MOD_ID, "/" + EmiClientPlugin.BLOCK_CATEGORY.id.getPath() + "/" + loot.id.getNamespace() + "/" + loot.id.getPath());;

            InitializedSupplier<List<EmiStack>> outputStacks = new InitializedSupplier<>(() -> {
                List<EmiStack> list = new ArrayList<>();
                for (ItemStack stack : itemStackList) {
                    list.add(EmiStack.of(stack));
                }
                return list;
            });

            InitializedSupplier<EmiStack> inputStack = new InitializedSupplier<>(() -> block.asItem() == Items.AIR ? new BlockStateEmiStack(block.getDefaultState(), blockId) : EmiStack.of(block));

            return new BlockLootRecipeData(id, rowBuilderList, allStacksGuaranteed || loot.isSimple, inputStack, outputStacks);
        }

        private static void addSimpleWidgetBuilder(List<WidgetRowBuilder> rowBuilderList, ClientBuiltPool newPool) {
            WidgetRowBuilder builder =  new WidgetRowBuilder(115);
            builder.addSimple(newPool);
            rowBuilderList.add(builder);
        }

        private static void addWidgetBuilders(List<WidgetRowBuilder> rowBuilderList, ClientBuiltPool newPool, boolean recursive) {
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
            opt.ifPresent(clientMobBuiltPool -> addWidgetBuilders(rowBuilderList, clientMobBuiltPool, true));
        }

    }
}