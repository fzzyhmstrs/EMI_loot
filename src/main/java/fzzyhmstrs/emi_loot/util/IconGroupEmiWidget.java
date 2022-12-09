package fzzyhmstrs.emi_loot.util;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static fzzyhmstrs.emi_loot.util.IconEmiWidget.FRAME_ID;

public class IconGroupEmiWidget extends Widget {

    public IconGroupEmiWidget(int x, int y, ClientBuiltPool pool){
        this.x = x;
        this.y = y;
        List<IconEmiWidget> list = new LinkedList<>();
        for (int i = 0; i < pool.list().size(); i++){
            Pair<Integer, Text> pair = pool.list().get(i);
            int xOffset = i / 2 * 11;
            int yOffset = i % 2 * 11;
            list.add(new IconEmiWidget(x + xOffset, y + yOffset,pair.getLeft(),pair.getRight()));
        }
        this.icons = list;
        this.iconsWidth = 12 + (((icons.size() - 1)/2) * 11);
        this.itemsWidth = 2 + pool.stackMap().size() * 20;
        this.width = iconsWidth + this.itemsWidth;
        this.bounds = new Bounds(x,y, width,23);
        List<SlotWidget> list2 = new LinkedList<>();
        int itemXOffset = iconsWidth + 2;
        for(Map.Entry<Float, EmiIngredient> entry: pool.stackMap().float2ObjectEntrySet()){
            SlotWidget widget = new SlotWidget(entry.getValue(),itemXOffset + x,y + 3).appendTooltip(LText.translatable("emi_loot.percent_chance",entry.getKey()));
            itemXOffset +=20;
            list2.add(widget);
        }
        items = list2;
    }

    private final List<IconEmiWidget> icons;
    private final List<SlotWidget> items;
    private final int x,y;
    private final int iconsWidth;
    private final int itemsWidth;
    private final int width;
    private final Bounds bounds;

    public int getIconsWidth(){
        return iconsWidth;
    }
    public int getWidth(){
        return width;
    }

    @Override
    public List<TooltipComponent> getTooltip(int mouseX, int mouseY){
        for (IconEmiWidget icon : icons){
            if (icon.getBounds().contains(mouseX,mouseY)) return icon.getTooltip(mouseX, mouseY);
        }
        for (SlotWidget slot: items){
            if (slot.getBounds().contains(mouseX, mouseY)) return slot.getTooltip(mouseX, mouseY);
        }
        return List.of();
    }
    
    @Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
        for (SlotWidget slot: items){
            if (slot.getBounds().contains(mouseX, mouseY)){
                if(slot.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }
        return super.mouseClicked(mouseX,mouseY,button);
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0,FRAME_ID);
        int widthRemaining = itemsWidth;
        int xNew = x + iconsWidth;
        do{
            int newWidth = Math.min(64,widthRemaining);
            DrawableHelper.drawTexture(matrices, xNew, y, newWidth, 1, 0, 0, newWidth, 1, 64, 16);
            xNew += newWidth;
            widthRemaining -= newWidth;
        } while (widthRemaining > 0);
        DrawableHelper.fill(matrices,x,y,x + width,y+1,0x555555);
        for (IconEmiWidget icon: icons){
            icon.render(matrices, mouseX, mouseY, delta);
        }
        for (SlotWidget slot: items){
            slot.render(matrices, mouseX, mouseY, delta);
        }
    }
}
