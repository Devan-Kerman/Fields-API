package net.devtech.fields.v0.api;

import java.util.function.BiFunction;

import net.devtech.fields.impl.ValueFieldImpl;
import net.devtech.fields.v0.api.data.GradientPackedDataFormat;
import net.devtech.fields.v0.api.data.PalettedDataFormat;
import net.devtech.fields.v0.api.value.ValueField;

import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public interface DataFormat<A, F extends ValueField<A>> {
	Registry<DataFormatInitializer.Entry> REGISTRY = FabricRegistryBuilder.createSimple(
			DataFormatInitializer.Entry.class,
			new Identifier("fields-api", "data_formats")).buildAndRegister();

	DataFormatInitializer.Entry<Integer, ValueField.Int> INT_PACKED = register(new Identifier("fields-api", "int_packed"),
			PalettedDataFormat.Int::new,
			ValueFieldImpl.Int::new);
	DataFormatInitializer.Entry<Integer, ValueField.Int> INT_GRADIENT = register(new Identifier("fields-api", "int_gradient"),
			GradientPackedDataFormat::new,
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

	interface Int extends DataFormat<Integer, ValueField.Int> {
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
