package fzzyhmstrs.emi_loot.util;

import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.ComponentChanges;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class BlockStateEmiStack extends EmiStack {

	public BlockStateEmiStack(BlockState state, Identifier id) {
		this.state = state;
		this.id = id;
	}

	private final BlockState state;
	private final Identifier id;

	@Override
	public EmiStack copy() {
		return new BlockStateEmiStack(state, id);
	}

	@Override
	public void render(DrawContext draw, int x, int y, float delta, int flags) {
		BlockRenderer.render(state, draw, x, y, delta);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ComponentChanges getComponentChanges() {
		return null;
	}

	@Override
	public Object getKey() {
		return state;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public List<Text> getTooltipText() {
		return List.of(Registries.BLOCK.get(id).getName());
	}

	@Override
	public Text getName() {
		return Registries.BLOCK.get(id).getName();
	}
}