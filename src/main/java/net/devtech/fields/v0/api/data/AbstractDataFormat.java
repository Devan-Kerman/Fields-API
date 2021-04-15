package net.devtech.fields.v0.api.data;

import net.devtech.fields.impl.access.ChunkSectionAccess;
import net.devtech.fields.v0.api.DataFormat;
import net.devtech.fields.v0.api.DataFormatInitializer;
import net.devtech.fields.v0.api.value.ValueField;

import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.ChunkSection;

public abstract class AbstractDataFormat<A, F extends ValueField<A>> implements DataFormat<A, F> {
	public final DataFormatInitializer.Entry<A, F> entry;
	public final ChunkSection currentSection;
	public final Identifier valueFieldId;
	protected AbstractDataFormat(DataFormatInitializer.Entry<A, F> entry, ChunkSection section, Identifier id) {
		this.entry = entry;
		this.currentSection = section;
		this.valueFieldId = id;
	}

	@Override
	public DataFormatInitializer.Entry<A, F> getEntry() {
		return this.entry;
	}

	/**
	 * removes the current data format instance from the chunk section
	 */
	public void delete() {
		((ChunkSectionAccess) this.currentSection).fields_delete(this.valueFieldId);
	}
}
