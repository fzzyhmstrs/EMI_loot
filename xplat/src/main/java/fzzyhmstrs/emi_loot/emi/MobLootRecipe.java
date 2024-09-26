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
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.client.ClientMobLootTable;
import fzzyhmstrs.emi_loot.client.ClientResourceData;
import fzzyhmstrs.emi_loot.util.ConditionalStack;
import fzzyhmstrs.emi_loot.util.EntityEmiStack;
import fzzyhmstrs.emi_loot.util.FloatTrimmer;
import fzzyhmstrs.emi_loot.util.IconGroupEmiWidget;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.SymbolText;
import fzzyhmstrs.emi_loot.util.TrimmedTitle;
import fzzyhmstrs.emi_loot.util.WidgetRowBuilder;
import me.fzzyhmstrs.fzzy_config.util.FcText;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MobLootRecipe implements EmiRecipe {

    //private final static Map<EntityType<?>, Integer> needsElevating;
    private static final Identifier ARROW_ID = Identifier.of(EMILoot.MOD_ID, "textures/gui/downturn_arrow.png");

    public MobLootRecipe(ClientMobLootTable loot) {
        this.lootId = loot.id;
        loot.build(MinecraftClient.getInstance().world, Blocks.AIR);
        Identifier mobId = loot.mobId;
        EntityType<?> type = Registries.ENTITY_TYPE.get(mobId);
        this.type = type;
        SpawnEggItem eggItem = SpawnEggItem.forEntity(type);
        this.egg = eggItem != null ? EmiStack.of(eggItem) : null;
        MinecraftClient client = MinecraftClient.getInstance();
        Entity entity = type.create(client.world);
        Text rawTitle;
        if (entity != null) {
            Box box = entity.getBoundingBox();
            double len = box.getAverageSideLength();
            if (len > 1.05) {
                len = (len + Math.sqrt(len))/2.0;
            }
            if (entity instanceof SlimeEntity) {
                ((SlimeEntity)entity).setSize(5, false);
            }
            if (entity instanceof SheepEntity && !Objects.equals(loot.color, "")) {
                DyeColor color = DyeColor.byName(loot.color, DyeColor.WHITE);
                MutableText colorName = LText.translatable("color.minecraft." + color.getName());
                rawTitle = LText.translatable("emi_loot.color_name", colorName.getString(), entity.getName().getString());
                ((SheepEntity)entity).setColor(color);

            } else {
                rawTitle = entity.getName();
            }
            double scale = 1.05 / len * 8.0;
            if (ClientResourceData.MOB_SCALES.containsKey(type)) {
                scale *= ClientResourceData.MOB_SCALES.getOrDefault(type, 1.0f);
            }
            inputStack = EntityEmiStack.ofScaled(entity, scale);
        } else {
            inputStack = EmiStack.EMPTY;
            rawTitle = LText.translatable("emi_loot.missing_entity");
        }
        this.name = TrimmedTitle.of(rawTitle, (EMILoot.config.isTooltipStyle() ? 138 : 158) - ((egg != null) ? 49 : 30));
        List<EmiStack> list = new LinkedList<>();
        //System.out.println(getId());
        loot.builtItems.forEach((builtPool)-> {
                builtPool.stacks().forEach(cs -> {
                    list.addAll(cs.ingredient());
                });
                addWidgetBuilders(builtPool, false);
            }
        );
        outputStacks = list;
    }

    private final Identifier lootId;
    private final EmiStack inputStack;
    private final List<EmiStack> outputStacks;
    private final TrimmedTitle name;
    private final EntityType<?> type;
    @Nullable
    private final EmiStack egg;
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
        return EmiClientPlugin.MOB_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return Identifier.of(EMILoot.MOD_ID, "/" + getCategory().id.getPath() + "/" + lootId.getNamespace() + "/" + lootId.getPath());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return egg != null ? List.of(egg) : Collections.emptyList();
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
            if (stacks <= 4) {
                return 29;
            } else {
                if (EMILoot.config.isCompact(EMILoot.Type.MOB)) {
                    int ingredients = 0;
                    for (WidgetRowBuilder builder: rowBuilderList) {
                        ingredients += builder.ingredientCount();
                    }
                    if (ingredients <= 4) {
                        return 29;
                    } else {
                        return 29 + 18 * (((ingredients - 5) / 8) + 1);
                    }
                } else {
                    return 29 + 18 * (((stacks - 5) / 8) + 1);
                }
            }
        } else {
            if (rowBuilderList.size() > 1 || rowBuilderList.get(0).getWidth() > 94) {
                return 28 + 23 + 29 * (rowBuilderList.size() - 1);
            } else {
                return 34;
            }
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int x = 0;
        int y = 0;
        //draw the mob
        if (!ClientResourceData.MOB_OFFSETS.containsKey(type)) {
            widgets.addSlot(inputStack, x, y).large(true);
        } else {
            int offset = ClientResourceData.MOB_OFFSETS.getOrDefault(type, 0);
            widgets.addTexture(EmiTexture.LARGE_SLOT, x, y);
            widgets.addDrawable(x, y, 16, 16, (matrices, mx, my, delta) -> inputStack.render(matrices, 5, 6 + offset, delta));
            widgets.addTooltip(inputStack.getTooltip(), x, y, 24, 24);
        }

        //draw the name, moved over if the spawn egg is available
        if (egg == null) {
            widgets.addText(name.title(), 30, 0, 0x404040, false);
            if (name.trimmed()) {
                widgets.addTooltipText(List.of(name.rawTitle()), 30, 0, EMILoot.config.isTooltipStyle() ? 108 : 118, 10);
            }
        } else {
            widgets.addText(name.title(), 49, 0, 0x404040, false);
            if (name.trimmed()) {
                widgets.addTooltipText(List.of(name.rawTitle()), 30, 0, EMILoot.config.isTooltipStyle() ? 89 : 99, 10);
            }
            widgets.addSlot(egg, 28, 0);
        }

        //draw the items
        if (EMILoot.config.isTooltipStyle()) {
            if (egg == null) {
                widgets.addTexture(new EmiTexture(ARROW_ID, 0, 16, 28, 15, 28, 15, 64, 32), 30, 10);
            } else {
                widgets.addTexture(new EmiTexture(ARROW_ID, 32, 16, 28, 15, 28, 15, 64, 32), 28, 15);
            }
            List<ConditionalStack> stacks = (outputStacks.size() <= 4 || !EMILoot.config.isCompact(EMILoot.Type.MOB))
                    ?
                rowBuilderList.stream().map(WidgetRowBuilder::stacks).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll)
                    :
                rowBuilderList.stream().map(WidgetRowBuilder::ingredients).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
            int i = 4;
            int j = 0;
            for (ConditionalStack stack: stacks) {
                SlotWidget widget = widgets.addSlot(stack.getIngredient(), i * 18, 11 + (18 * j));
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
            if (rowBuilderList.size() == 1 && rowBuilderList.get(0).getWidth() <= 94) {
                if (egg == null) {
                    widgets.addTexture(new EmiTexture(ARROW_ID, 0, 16, 27, 15, 27, 15, 64, 32), 30, 10);
                } else {
                    widgets.addTexture(new EmiTexture(ARROW_ID, 32, 16, 28, 15, 28, 15, 64, 32), 28, 15);
                }
                x = 60;
                y = 11;
                WidgetRowBuilder builder = rowBuilderList.get(0);
                for (ClientBuiltPool pool: builder.getPoolList()) {
                    IconGroupEmiWidget widget = EMILootClientAgnos.createIconGroupEmiWidget(x, y, pool);
                    widgets.add(widget);
                    x += widget.getWidth() + 6;
                }
            } else {
                if (egg == null) {
                    widgets.addTexture(new EmiTexture(ARROW_ID, 0, 0, 39, 15, 39, 15, 64, 32), 30, 10);
                } else {
                    widgets.addTexture(new EmiTexture(ARROW_ID, 0, 0, 39, 15, 39, 15, 64, 32), 49, 10);
                }
                y += 28;
                for (WidgetRowBuilder builder: rowBuilderList) {
                    for (ClientBuiltPool pool: builder.getPoolList()) {
                        IconGroupEmiWidget widget = EMILootClientAgnos.createIconGroupEmiWidget(x, y, pool);
                        widgets.add(widget);
                        x += widget.getWidth() + 6;
                    }
                    y += 29;
                    x = 0;
                }
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