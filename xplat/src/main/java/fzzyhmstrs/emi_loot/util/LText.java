package fzzyhmstrs.emi_loot.util;

import dev.emi.emi.api.render.EmiTooltipComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LText {

    public static HashSet<String> tablePrefixes = new HashSet<>(List.of(
            "spawners",
            "spawner",
            "shearing",
            "pots",
            "pot",
            "gameplay",
            "equipment",
            "entities",
            "entity",
            "dispensers",
            "dispenser",
            "chests",
            "chest",
            "blocks",
            "block",
            "archaeology"
    ));

    public static List<TooltipComponent> components(Text title, String namespace) {
        ArrayList<TooltipComponent> components = new ArrayList<>();
        components.add(EmiTooltipComponents.of(title));
        EmiTooltipComponents.appendModName(components, namespace);
        return components;
    }

    public static MutableText translatable(String key) {
        return Text.translatable(key);
    }

    public static MutableText translatable(String key, Object ... args) {
        return Text.translatable(key, args);
    }

    public static MutableText literal(String msg) {
        return Text.literal(msg);
    }

    public static MutableText empty() {
        return Text.empty();
    }

    public static OrderedText trim(Text text, int width) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        StringVisitable stringVisitable = textRenderer.trimToWidth(text, width - textRenderer.getWidth(ScreenTexts.ELLIPSIS));
        return Language.getInstance().reorder(StringVisitable.concat(stringVisitable, ScreenTexts.ELLIPSIS));
    }

    public static MutableText enchant(RegistryEntry<Enchantment> entry) {
        MutableText mutableText = entry.value().description().copy();
        if (entry.isIn(EnchantmentTags.CURSE)) {
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.RED));
        } else {
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
        }
        return mutableText;
    }

}