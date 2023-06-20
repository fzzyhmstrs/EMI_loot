package fzzyhmstrs.emi_loot.emi;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.render.EmiRenderable;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class LootSimplifiedRenderer implements EmiRenderable {

    private final int u;
    private final int v;
    private final Identifier SPRITE_SHEET = new Identifier(EMILoot.MOD_ID,"textures/gui/emi_recipe_textures.png");

    public LootSimplifiedRenderer(int u, int v){
        this.u = u;
        this.v = v;
    }


    @Override
    public void render(DrawContext draw, int x, int y, float delta) {
        draw.drawTexture(SPRITE_SHEET, x, y, u, v, 16, 16, 32, 16);
    }
}
