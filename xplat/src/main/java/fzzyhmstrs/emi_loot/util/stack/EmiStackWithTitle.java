package fzzyhmstrs.emi_loot.util.stack;

import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootAgnos;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TrimmedTitle;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.ComponentChanges;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class EmiStackWithTitle extends EmiStack {
    protected final Identifier id;
    private final TrimmedTitle name;
    private final MutableText rawName;

    public EmiStackWithTitle(Identifier id, String unknownNamespace, String unknownPath, EMILoot.Type type, int width, Formatting ... formatting) {
        this.id = id;
        this.rawName = getRawTitle(id, unknownNamespace, unknownPath, type);
        this.name = TrimmedTitle.of(rawName.copy().formatted(formatting), width);
    }

    protected MutableText getRawTitle(Identifier id, String unknownNamespace, String unknownPath, EMILoot.Type type) {
        String key = String.join(".", "emi_loot", unknownNamespace, id.toString());
        MutableText rawTitle;
        if (!I18n.hasTranslation(key)) {
            StringBuilder chestName = new StringBuilder();
            String[] chestPathTokens = id.getPath().split("[/_]");

            for (String str : chestPathTokens) {
                if (!LText.tablePrefixes.contains(str)) {
                    if (!chestName.isEmpty()) {
                        chestName.append(" ");
                    }

                    if (str.length() <= 1) {
                        chestName.append(str);
                    } else {
                        chestName.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));
                    }
                }
            }

            if (EMILootAgnos.isModLoaded(id.getNamespace())) {
                rawTitle = LText.translatable(getUnknownModdedKey(unknownNamespace, unknownPath), chestName.toString());
            } else {
                Text unknown = LText.translatable(getUnknownKey(unknownNamespace));
                rawTitle = LText.translatable(getUnknownModdedKey(unknownNamespace, unknownPath), LText.literal(chestName.toString()).append(" ").append(unknown));
            }
            if (EMILoot.config.isLogI18n(type)) {
                EMILoot.LOGGER.warn("Untranslated " + unknownNamespace + " loot table \"" + id + "\" (key: \"" + key + "\")");
            }
        } else {
            rawTitle = LText.translatable(key);
        }
        return rawTitle;
    }

    private String getUnknownModdedKey(String namespace, String path) {
        return String.join(".", "emi_loot", namespace, path);
    }

    private String getUnknownKey(String namespace) {
        return String.join(".", "emi_loot", namespace, "unknown");
    }

    @Override
    public void render(DrawContext draw, int x, int y, float delta, int flags) {}

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ComponentChanges getComponentChanges() {
        return ComponentChanges.EMPTY;
    }

    @Override
    public Object getKey() {
        return getId().toString();
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public List<Text> getTooltipText() {
        return List.of();
    }

    @Override
    public Text getName() {
        return rawName.copy();
    }

    public OrderedText getTrimmedName() {
        return this.name.title();
    }

    public boolean isTrimmed() {
        return name.trimmed();
    }
}