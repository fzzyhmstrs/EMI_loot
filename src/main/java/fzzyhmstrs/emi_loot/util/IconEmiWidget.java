package fzzyhmstrs.emi_loot.util;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class IconEmiWidget extends Widget {

    public IconEmiWidget(int x, int y, int keyIndex, Text text){
        this.tex = TextKey.getSpriteId(keyIndex);
        this.x = x;
        this.y = y;
        this.bounds = new Bounds(x,y,12,12);
        this.tooltipText = Collections.singletonList(TooltipComponent.of(text.asOrderedText()));
    }

    static final Identifier FRAME_ID = new Identifier(EMILoot.MOD_ID,"textures/gui/icon_frame.png");
    //private static final Identifier SPRITE_ID = new Identifier(EMILoot.MOD_ID,"textures/gui/icon_sprites.png");

    private final Identifier tex;
    private int x, y;
    private final Bounds bounds;
    private final List<TooltipComponent> tooltipText;



    @Override
    public List<TooltipComponent> getTooltip(int mouseX, int mouseY){
        return tooltipText;
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.drawTexture(FRAME_ID, x, y, 12, 12, 0, 0, 12, 12, 64, 16);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.drawTexture(tex, x + 2, y + 2, 8, 8, 0, 0, 8, 8, 8, 8);
    }
}
