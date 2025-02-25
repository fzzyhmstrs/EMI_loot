package fzzyhmstrs.emi_loot.client;

import java.util.function.Supplier;

public final class InitializedSupplier<T> implements Supplier<T> {

	public InitializedSupplier(Supplier<T> delegate) {
		this.delegate = delegate;
	}

	private final Supplier<T> delegate;
	private T value;

	public void init() {
		this.value = delegate.get();
	}

	@Override
	public T get() {
		return value;
	}
}