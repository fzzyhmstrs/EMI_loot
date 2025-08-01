package fzzyhmstrs.emi_loot.util;

import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootAgnos;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class EmiStackWithTitle extends EmiStack {
    private final Identifier id;
    private final TrimmedTitle name;

    public EmiStackWithTitle(Identifier id,String unknownNamespace, String unknownPath, EMILoot.Type type, int width) {
        this.id = id;
        this.name = TrimmedTitle.of(getRawTitle(id, unknownNamespace, unknownPath, type), width);
    }

    public Text getRawTitle(Identifier id, String unknownNamespace, String unknownPath, EMILoot.Type type) {
        String key = String.join(".", "emi_loot", unknownNamespace, id.toString());
        Text rawTitle;
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

    public String getUnknownModdedKey(String namespace, String path) {
        return String.join(".", "emi_loot", namespace, path);
    }

    public String getUnknownKey(String namespace) {
        return String.join(".", "emi_loot", namespace, "unknown");
    }

    @Override
    public EmiStack copy() {
        return new ChestLootEmiStack(this.id);
    }

    @Override
    public void render(DrawContext draw, int x, int y, float delta, int flags) {}

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public NbtCompound getNbt() {
        return null;
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
        return name.rawTitle();
    }

    public TrimmedTitle getTrimmedTitle() {
        return this.name;
    }
}
