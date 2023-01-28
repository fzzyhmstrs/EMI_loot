package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.client.ClientGameplayLootTable;
import fzzyhmstrs.emi_loot.client.ClientMobLootTable;
import fzzyhmstrs.emi_loot.client.ClientResourceData;
import fzzyhmstrs.emi_loot.util.EntityEmiStack;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.WidgetRowBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GameplayLootRecipe implements EmiRecipe {

    public GameplayLootRecipe(ClientGameplayLootTable loot){
        this.loot = loot;
        loot.build(MinecraftClient.getInstance().world, Blocks.AIR);
        List<EmiStack> list = new LinkedList<>();
        loot.builtItems.forEach((builtPool)-> {
                builtPool.stackMap().forEach((weight, stacks) -> {
                    list.addAll(stacks.getEmiStacks());
                });
                addWidgetBuilders(builtPool,false);
            }
        );
        outputStacks = list;
        String key = "emi_loot.gameplay." + loot.id.toString();
        Text text = LText.translatable(key);
        if (Objects.equals(text.getString(), key)){
            Optional<ModContainer> modNameOpt = FabricLoader.getInstance().getModContainer(loot.id.getNamespace());
            if (modNameOpt.isPresent()){
                ModContainer modContainer = modNameOpt.get();
                String modName = modContainer.getMetadata().getName();
                name = LText.translatable("emi_loot.gameplay.unknown_gameplay",modName);
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

    private void addWidgetBuilders(ClientBuiltPool newPool, boolean recursive){
        if (recursive || rowBuilderList.isEmpty()){
            rowBuilderList.add(new WidgetRowBuilder(154));
        }
        boolean added = false;
        for (WidgetRowBuilder builder : rowBuilderList){
            if (builder.canAddPool(newPool)){
                builder.addAndTrim(newPool);
                added = true;
                break;
            }
        }
        if (!added){
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
        return 154;
    }

    @Override
    public int getDisplayHeight() {
        return rowBuilderList.size() * 29 + 11;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int x = 0;
        int y = 0;

        //draw the gameplay name
        widgets.addText(name.asOrderedText(),0,0,0x404040,false);

        y += 11;
        for (WidgetRowBuilder builder: rowBuilderList){
            for (ClientBuiltPool pool: builder.getPoolList()){
                IconGroupEmiWidget widget = new IconGroupEmiWidget(x,y,pool);
                widgets.add(widget);
                x += widget.getWidth() + 6;
            }
            y += 29;
            x = 0;
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
