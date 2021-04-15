package net.devtech.fields.impl.access;

import net.devtech.fields.v0.api.DataFormat;
import net.devtech.fields.v0.api.DataFormatInitializer;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.WorldChunk;

public interface ChunkSectionAccess {
	DataFormat<?, ?> getOrCreate(Identifier identifier, @Nullable DataFormatInitializer.Entry<?, ?> entry);
	DataFormat<?, ?> get(Identifier identifier);

	Iterable<Identifier> fields_getStored();

	void fields_delete(Identifier entry);

	void fields_handleTick(ServerWorld world, WorldChunk chunk);
}
