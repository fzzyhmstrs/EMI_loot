package fzzyhmstrs.emi_loot.util;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import net.minecraft.util.Formatting;

public class InteractableTextWidget extends TextWidget {
    SlotWidget slotWidget;

    public InteractableTextWidget(EmiStackWithTitle stack, int x, int y, int color, boolean shadow, TrimmedTitle title) {
        super(stack.getRawName().formatted(Formatting.BLUE, Formatting.UNDERLINE).asOrderedText(), x, y, color, shadow);
        slotWidget = new SlotWidget(stack, x, y);
    }

    public InteractableTextWidget recipeContext(EmiRecipe recipe) {
        slotWidget.recipeContext(recipe);
        return this;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return slotWidget.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return slotWidget.keyPressed(keyCode, scanCode, modifiers);
    }
}