package net.devtech.fields.v0.api;

import java.util.function.BiFunction;

import net.devtech.fields.v0.api.data.TickingDataHandler;
import net.devtech.fields.v0.api.value.ValueFieldImpl;
import net.devtech.fields.v0.api.data.GradientPackedDataHandler;
import net.devtech.fields.v0.api.data.PalettedDataHandler;
import net.devtech.fields.v0.api.value.ValueField;

import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

/**
 * the format and handler for data in a given subchunk
 * @see TickingDataHandler
 */
public interface DataHandler<A, F extends ValueField<A>> {
	Registry<DataFormatInitializer.Entry> REGISTRY = FabricRegistryBuilder.createSimple(
			DataFormatInitializer.Entry.class,
			new Identifier("fields-api", "data_handlers")).buildAndRegister();

	/**
	 * a data format that stores ints in a pallet, optimized for data that varies wildly (eg. block ids)
	 */
	DataFormatInitializer.Entry<Integer, ValueField.Int> INT_PACKED = register(new Identifier("fields-api", "int_packed"),
			PalettedDataHandler.Int::new,
			ValueFieldImpl.Int::new);

	/**
	 * optimized for values that don't differ much from block to block. Eg. Heat, Gasses, Pollution
	 */
	DataFormatInitializer.Entry<Integer, ValueField.Int> INT_GRADIENT = register(new Identifier("fields-api", "int_gradient"),
			GradientPackedDataHandler::new,
			ValueFieldImpl.Int::new);

	static <A, B extends ValueField<A>> DataFormatInitializer.Entry<A, B> register(Identifier identifier,
			DataFormatInitializer<A, B> initializer,
			BiFunction<DataFormatInitializer.Entry, Identifier, B> function) {
		return Registry.register(REGISTRY, identifier, new DataFormatInitializer.Entry<>(initializer, function, identifier));
	}

	Tag toTag();

	void fromTag(Tag tag);

	A get(int subchunkX, int subchunkY, int subchunkZ);

	void set(int subchunkX, int subchunkY, int subchunkZ, A a);

	DataFormatInitializer.Entry<A, F> getEntry();

	default boolean shouldSync() {
		return false;
	}

	interface Int extends DataHandler<Integer, ValueField.Int> {
		@Override
		default Integer get(int subchunkX, int subchunkY, int subchunkZ) {
			return this.getInt(subchunkX, subchunkY, subchunkZ);
		}

		@Override
		default void set(int subchunkX, int subchunkY, int subchunkZ, Integer integer) {
			this.setInt(subchunkX, subchunkY, subchunkZ, integer);
		}

		int getInt(int subchunkX, int subchunkY, int subchunkZ);

		boolean setInt(int subchunkX, int subchunkY, int subchunkZ, int val);
	}
}
