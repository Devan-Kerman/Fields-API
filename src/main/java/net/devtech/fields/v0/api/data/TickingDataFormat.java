package net.devtech.fields.v0.api.data;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

public interface TickingDataFormat {
	void onTick(ServerWorld world, WorldChunk chunk);
}
