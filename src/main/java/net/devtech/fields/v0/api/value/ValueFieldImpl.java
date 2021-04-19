package net.devtech.fields.v0.api.value;

import net.devtech.fields.impl.access.ChunkSectionAccess;
import net.devtech.fields.v0.api.data.AbstractDataFormat;
import net.devtech.fields.v0.api.DataFormatInitializer;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class ValueFieldImpl<T> implements ValueField<T> {
	public final DataFormatInitializer.Entry<T, ?> initializer;
	public final Identifier id;

	public ValueFieldImpl(DataFormatInitializer.Entry<T, ?> initializer, Identifier id) {
		this.initializer = initializer;
		this.id = id;
	}

	@Override
	public T get(WorldChunk chunk, BlockPos pos) {
		int subchunkX = pos.getX() & 15, subchunkY = pos.getY() & 15, subchunkZ = pos.getZ() & 15;
		return this.getFormat(chunk, pos).get(subchunkX, subchunkY, subchunkZ);
	}

	@Override
	public void set(WorldChunk chunk, BlockPos pos, T value) {
		int subchunkX = pos.getX() & 15, subchunkY = pos.getY() & 15, subchunkZ = pos.getZ() & 15;
		this.getFormat(chunk, pos).set(subchunkX, subchunkY, subchunkZ, value);
	}

	public AbstractDataFormat<T, ?> getFormat(WorldChunk chunk, BlockPos pos) {
		ChunkSection[] section = chunk.getSectionArray();
		int sectionIndex = pos.getY() >> 4;
		ChunkSection target = section[sectionIndex];
		if(target == null) {
			section[sectionIndex] = target = new ChunkSection(sectionIndex);
		}
		return (AbstractDataFormat<T, ?>) ((ChunkSectionAccess)target).getOrCreate(this.id, this.initializer);
	}

	public static final class Int extends ValueFieldImpl<Integer> implements ValueField.Int {
		public Int(DataFormatInitializer.Entry<Integer, ?> initializer, Identifier id) {
			super(initializer, id);
		}

		@Override
		public int getInt(WorldChunk chunk, BlockPos pos) {
			return ((AbstractDataFormat.Int)this.getFormat(chunk, pos)).getInt(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
		}

		@Override
		public void setInt(WorldChunk chunk, BlockPos pos, int val) {
			((AbstractDataFormat.Int)this.getFormat(chunk, pos)).setInt(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, val);
		}
	}
}
