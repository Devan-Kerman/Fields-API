package net.devtech.fields.impl.access;

import net.devtech.fields.v0.api.DataHandler;
import net.devtech.fields.v0.api.DataFormatInitializer;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.WorldChunk;

public interface ChunkSectionAccess {
	DataHandler<?, ?> getOrCreate(Identifier identifier, @Nullable DataFormatInitializer.Entry<?, ?> entry);
	DataHandler<?, ?> get(Identifier identifier);

	Iterable<Identifier> fields_getStored();

	void fields_delete(Identifier entry);

	void fields_handleTick(ServerWorld world, WorldChunk chunk);
}
