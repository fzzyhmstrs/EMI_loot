package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.render.EmiRenderable;
import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class LootSimplifiedRenderer implements EmiRenderable {

    private int u;
    private int v;
    private Identifier SPRITE_SHEET = new Identifier(EMILoot.MOD_ID,"textures/gui/emi_recipe_textures.png");

    public LootSimplifiedRenderer(int u, int v){
        this.u = u;
        this.v = v;
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float delta) {

    }
}
