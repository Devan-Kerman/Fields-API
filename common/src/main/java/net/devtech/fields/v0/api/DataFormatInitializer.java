package net.devtech.fields.v0.api;

import java.util.function.BiFunction;

import net.devtech.fields.v0.api.value.ValueField;

import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.ChunkSection;

public interface DataFormatInitializer<A, F extends ValueField<A>> {
	DataHandler<A, F> apply(DataFormatInitializer.Entry entry, ChunkSection currentSection, Identifier identifier);

	final class Entry<A, F extends ValueField<A>> {
		public final DataFormatInitializer<A, F> initializer;
		public final BiFunction<Entry, Identifier, F> fieldCreator;
		public final Identifier id;

		/**
		 * @see DataHandler#register(Identifier, DataFormatInitializer, BiFunction)
		 */
		Entry(DataFormatInitializer<A, F> initializer, BiFunction<Entry, Identifier, F> creator, Identifier id) {
			this.initializer = initializer;
			this.fieldCreator = creator;
			this.id = id;
		}
	}
}
