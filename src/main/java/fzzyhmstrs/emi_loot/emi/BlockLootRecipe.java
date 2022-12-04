package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientBlockLootTable;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import fzzyhmstrs.emi_loot.util.WidgetRowBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BlockLootRecipe implements EmiRecipe {

    //kelp
    //potted plants
    //fire
    //candle cakes

    public BlockLootRecipe(ClientBlockLootTable loot){
        this.loot = loot;
        Identifier blockId = loot.blockId;
        Block block = Registry.BLOCK.get(blockId);
        loot.build(MinecraftClient.getInstance().world, block);
        inputStack = EmiStack.of(block);
        List<EmiStack> list = new LinkedList<>();
        loot.builtItems.forEach((builtPool)-> {
            builtPool.stackMap().forEach((weight, stacks) -> {
                /*if (weight < 100f) {
                    allStacksGuaranteed = false;
                }*/
                list.addAll(stacks.getEmiStacks());
            });
            addWidgetBuilders(builtPool, false);
        });
        outputStacks = list;
    }

    private final ClientBlockLootTable loot;
    private final EmiStack inputStack;
    private final List<EmiStack> outputStacks;
    private final List<WidgetRowBuilder> rowBuilderList = new LinkedList<>();

    private void addWidgetBuilders(ClientBuiltPool newPool, boolean recursive){
        WidgetRowBuilder builder;
        boolean newBuilder = false;
        if (recursive || rowBuilderList.isEmpty()){
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
        return new Identifier("emi", EMILootClient.MOD_ID + "/" + getCategory().id.getPath() + "/" + loot.id.getNamespace() + "/" + loot.id.getPath());
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
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 23 + 29 * (rowBuilderList.size() - 1);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputStack,0,0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 20, 0);
        int x = 46;
        int y = 0;

        for (WidgetRowBuilder builder: rowBuilderList){
            for (ClientBuiltPool pool: builder.getPoolList()){
                IconGroupEmiWidget widget = new IconGroupEmiWidget(x,y,pool);
                widgets.add(widget);
                x += widget.getWidth() + 6;
            }
            y += 29;
            x = 46;
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
