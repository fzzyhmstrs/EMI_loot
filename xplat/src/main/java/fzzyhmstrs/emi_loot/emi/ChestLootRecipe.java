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
import fzzyhmstrs.emi_loot.client.ClientChestLootTable;
import fzzyhmstrs.emi_loot.util.ChestLootEmiStack;
import fzzyhmstrs.emi_loot.util.InteractableTextWidget;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TrimmedTitle;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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
        this.title = data.name;

        outputs = outputsList;
        inputStack = new ChestLootEmiStack(loot.id);
    }

    private final ClientChestLootTable loot;
    //private final Map<EmiStack, Float> lootStacks;
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
        return List.of(inputStack);
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
            widgets.addTooltip(LText.components(title.rawTitle(), loot.id.getNamespace()), 0, 0, 144, 10);
        } else {
            widgets.addTooltipText(List.of(title.rawTitle()), 0, 0, 144, 10);
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

    public record ChestLootRecipeData(ClientChestLootTable loot, Iterator<Map.Entry<Float, ItemStack>> itr, List<ItemStack> totalItemList, boolean guaranteed, TrimmedTitle name, int lootStacksSortedSize) {

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

            String key = "emi_loot.chest." + loot.id.toString();
            MutableText rawTitle;
            if (!I18n.hasTranslation(key)) {
                StringBuilder chestName = new StringBuilder();
                String[] chestPathTokens = loot.id.getPath().split("[/_]");
                for (String str : chestPathTokens) {
                    if (LText.tablePrefixes.contains(str)) continue;
                    if (!chestName.isEmpty()) {
                        chestName.append(" ");
                    }
                    if (str.length() <= 1) {
                        chestName.append(str);
                    } else {
                        chestName.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));
                    }
                }
                if(EMILootAgnos.isModLoaded(loot.id.getNamespace())) {
                    rawTitle = LText.translatable("emi_loot.chest.unknown_chest", chestName.toString());
                } else {
                    Text unknown = LText.translatable("emi_loot.chest.unknown");
                    rawTitle = LText.translatable("emi_loot.chest.unknown_chest", LText.literal(chestName.toString()).append(" ").append(unknown));
                }
                if (EMILoot.config.isLogI18n(EMILoot.Type.CHEST)) {
                    EMILoot.LOGGER.warn("Untranslated chest loot table \"{}\" (key: \"{}\")", loot.id, key);
                }
            } else {
                rawTitle = LText.translatable(key);;
            }
            TrimmedTitle name = TrimmedTitle.of(rawTitle, 138);

            int lootStacksSortedSize;

            if (loot.items.size() > 48 || EMILoot.config.chestLootAlwaysStackSame) {
                lootStacksSortedSize = map2.keySet().size();
            } else {
                lootStacksSortedSize = loot.items.size();
            }

            return new ChestLootRecipeData(loot, itr, totalItemList, isGuaranteedNonChance, name, lootStacksSortedSize);
        }

    }
}