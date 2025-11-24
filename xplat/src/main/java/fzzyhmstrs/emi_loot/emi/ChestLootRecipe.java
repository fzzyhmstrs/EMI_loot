package fzzyhmstrs.emi_loot.emi;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ArrayListMultimap;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootAgnos;
import fzzyhmstrs.emi_loot.client.ClientChestLootTable;
import fzzyhmstrs.emi_loot.util.stack.ChestLootEmiStack;
import fzzyhmstrs.emi_loot.util.InteractableTextWidget;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static fzzyhmstrs.emi_loot.util.FloatTrimmer.trimFloatString;

public class ChestLootRecipe implements EmiRecipe {

    public ChestLootRecipe(ChestLootRecipeData data) {
        this.loot = data.loot;
        this.isGuaranteedNonChance = data.guaranteed;

        this.lootStacksSorted = Suppliers.memoize(() -> {
            ArrayListMultimap<Float, EmiStack> map2 = ArrayListMultimap.create();
            for (Iterator<Map.Entry<Float, ItemStack>> it = data.itr; it.hasNext(); ) {
                Map.Entry<Float, ItemStack> entry = it.next();
                EmiStack stack = EmiStack.of(entry.getValue());
                map2.put(entry.getKey(), stack);
            }
            return map2;
        });
        this.lootStacksSortedSize = data.lootStacksSortedSize;
        List<EmiStack> list = new ArrayList<>();
        for (ItemStack stack: data.totalItemList) {
            list.add(EmiStack.of(stack));
        }
        this.outputs = list;
        inputStack = new ChestLootEmiStack(loot.id);
    }

    private final ClientChestLootTable loot;
    private final Supplier<ArrayListMultimap<Float, EmiStack>> lootStacksSorted;
    private final int lootStacksSortedSize;
    private final List<EmiStack> outputs;
    private boolean isGuaranteedNonChance = false;
    private final ChestLootEmiStack inputStack;
    private final float columns = 8f;



    @Override
    public EmiRecipeCategory getCategory() {
        return EmiClientPlugin.CHEST_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return new Identifier(EMILoot.MOD_ID, "/" + getCategory().id.getPath() + "/" + loot.id.getNamespace() + "/" + loot.id.getPath());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return new ArrayList<>();
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return EmiRecipe.super.getCatalysts();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        int titleHeight = 11;
        int boxesHeight = ((int) Math.ceil(lootStacksSortedSize/ columns) * (EMILoot.config.isCompact(EMILoot.Type.CHEST) ? 18 : 19)) - 1;
        return titleHeight + boxesHeight;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int titleSpace;
        final int finalRowHeight;
        if (widgets.getHeight() < getDisplayHeight()) {
            titleSpace = 9;
            finalRowHeight = (widgets.getHeight() - titleSpace) / ((int) Math.ceil(lootStacksSortedSize / 8.0));
        } else {
            titleSpace = 11;
            finalRowHeight =  18;
        }
        widgets.add(new InteractableTextWidget(inputStack, 1, 0, 0x404040, false).recipeContext(this));
        if (EMILootAgnos.isModLoaded(loot.id.getNamespace())) {
            widgets.addTooltip(LText.components(inputStack.getName(), loot.id.getNamespace()), 0, 0, 144, 10);
        } else {
            widgets.addTooltipText(List.of(inputStack.getName()), 0, 0, 144, 10);
        }
        AtomicInteger index = new AtomicInteger(lootStacksSortedSize);
        for (var entry : lootStacksSorted.get().asMap().entrySet()) {
            float weight = entry.getKey();
            Collection<EmiStack> items = entry.getValue();
            if ((loot.items.size() <= 48) && !EMILoot.config.chestLootAlwaysStackSame) {
                for (EmiStack stack : items) {
                    int row = (int) Math.ceil(index.get() / columns) - 1;
                    int column = (index.get() - 1) % (int) columns;
                    index.getAndDecrement();
                    String fTrim = trimFloatString(weight, EMILoot.config.chanceDecimalPlaces.get());
                    SlotWidget slotWidget = new SlotWidget(stack, column * 18, titleSpace + row * finalRowHeight).recipeContext(this);
                    widgets.add(slotWidget.appendTooltip(LText.translatable("emi_loot.percentage", fTrim).formatted(Formatting.GRAY)));
                }
            } else {
                int row = (int) Math.ceil(index.get() / columns) - 1;
                int column = (int)((index.get() - 1) % columns);
                index.getAndDecrement();
                EmiIngredient ingredient = EmiIngredient.of(items.stream().toList());
                String fTrim = trimFloatString(Math.max(weight / 100f, 0.01f), Math.max(EMILoot.config.chanceDecimalPlaces.get() + 1, 2));
                SlotWidget slotWidget = new SlotWidget(ingredient, column * 18, titleSpace + row * finalRowHeight).recipeContext(this);
                widgets.add(slotWidget.appendTooltip(LText.translatable("emi_loot.rolls", fTrim).formatted(Formatting.GRAY)));
            }
        }
    }

    @Override
    public boolean supportsRecipeTree() {
        return EmiRecipe.super.supportsRecipeTree() && isGuaranteedNonChance;
    }

    @Override
    public boolean hideCraftable() {
        return EmiRecipe.super.hideCraftable();
    }

    public record ChestLootRecipeData(ClientChestLootTable loot, Iterator<Map.Entry<Float, ItemStack>> itr, List<ItemStack> totalItemList, boolean guaranteed, int lootStacksSortedSize) {

        public static ChestLootRecipeData of(ClientChestLootTable loot) {
            boolean isGuaranteedNonChance = false;
            if (loot.items.size() == 1) {
                if (loot.items.values().toFloatArray()[0] == 1f) {
                    isGuaranteedNonChance = true;
                }
            }

            List<ItemStack> totalItemList = new ArrayList<>();
            ArrayListMultimap<Float, ItemStack> map2 = ArrayListMultimap.create();
            loot.items.forEach((item, weight) -> {
                map2.put(weight, item);
                totalItemList.add(item);
            });
            for (float key : map2.keySet()) {
                map2.get(key).sort(Comparator.comparingInt(s -> Registries.ITEM.getRawId(s.getItem())));
            }

            Iterator<Map.Entry<Float, ItemStack>> itr = map2.entries().iterator();

            int lootStacksSortedSize;

            if (loot.items.size() > 48 || EMILoot.config.chestLootAlwaysStackSame) {
                lootStacksSortedSize = map2.keySet().size();
            } else {
                lootStacksSortedSize = loot.items.size();
            }

            return new ChestLootRecipeData(loot, itr, totalItemList, isGuaranteedNonChance, lootStacksSortedSize);
        }

    }
}