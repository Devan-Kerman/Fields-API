package net.devtech.fields.mixin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.devtech.fields.impl.access.ChunkSectionAccess;
import net.devtech.fields.v0.api.DataFormat;
import net.devtech.fields.v0.api.DataFormatInitializer;
import net.devtech.fields.v0.api.data.TickingDataFormat;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ChunkSection.class)
public class ChunkSectionMixin_ChunkSectionAccess implements ChunkSectionAccess {
	public Map<Identifier, DataFormat<?, ?>> formats;
	public List<TickingDataFormat> ticking;

	@Override
	public void fields_handleTick(ServerWorld world, WorldChunk chunk) {
		if(this.ticking != null) {
			for (TickingDataFormat format : this.ticking) {
				format.onTick(world, chunk);
			}
		}
	}

	@Override
	public DataFormat<?, ?> getOrCreate(Identifier values, DataFormatInitializer.Entry<?, ?> entry) {
		if(this.formats == null) {
			this.formats = new HashMap<>();
		}

		return this.formats.computeIfAbsent(values, i -> {
			DataFormat<?, ?> format = entry.initializer.apply(entry, (ChunkSection) (Object) this, values);
			if(format instanceof TickingDataFormat) {
				if(this.ticking == null) {
					this.ticking = new ArrayList<>();
				}
				this.ticking.add((TickingDataFormat) format);
			}
			return format;
		});
	}

	@Override
	public DataFormat<?, ?> get(Identifier identifier) {
		if(this.formats == null) {
			return null;
		}
		return this.formats.get(identifier);
	}

	@Override
	public Iterable<Identifier> fields_getStored() {
		return this.formats == null ? Collections.emptyList() : this.formats.keySet();
	}

	@Override
	public void fields_delete(Identifier valueId) {
		if(this.formats == null || this.formats.remove(valueId) == null) {
			throw new IllegalArgumentException(valueId + " is not present in subchunk");
		}
	}
}
