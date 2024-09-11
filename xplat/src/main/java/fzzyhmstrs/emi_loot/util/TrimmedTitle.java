package fzzyhmstrs.emi_loot.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public record TrimmedTitle(boolean trimmed, OrderedText title, Text rawTitle) {

	public static TrimmedTitle of(Text text, int width) {
		OrderedText title;
		boolean trimmed;
		if (MinecraftClient.getInstance().textRenderer.getWidth(text) > width) {
			title = LText.trim(text, width);
			trimmed = true;
		} else {
			title = text.asOrderedText();
			trimmed = false;
		}
		return new TrimmedTitle(trimmed, title, text);
	}

}