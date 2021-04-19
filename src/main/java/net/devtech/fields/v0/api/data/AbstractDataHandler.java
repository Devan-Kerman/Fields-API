package net.devtech.fields.v0.api.data;

import io.netty.buffer.Unpooled;
import net.devtech.fields.impl.DataHandlerSerializer;
import net.devtech.fields.impl.access.ChunkSectionAccess;
import net.devtech.fields.v0.api.DataHandler;
import net.devtech.fields.v0.api.DataFormatInitializer;
import net.devtech.fields.v0.api.value.ValueField;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;

public abstract class AbstractDataHandler<A, F extends ValueField<A>> implements DataHandler<A, F> {
	public static final Identifier SYNC_CHANNEL = new Identifier("fields-api", "sync_channel");
	public final DataFormatInitializer.Entry<A, F> entry;
	public final ChunkSection currentSection;
	public final Identifier valueFieldId;
	protected AbstractDataHandler(DataFormatInitializer.Entry<A, F> entry, ChunkSection section, Identifier id) {
		this.entry = entry;
		this.currentSection = section;
		this.valueFieldId = id;
	}

	/**
	 * syncs the data of the entire subchunk this coordinate is in
	 */
	public void sync(ServerWorld world, int x, int y, int z) {
		int sx = x >> 4, sy = y >> 4, sz = z >> 4;
		world.getChunkManager().threadedAnvilChunkStorage.getPlayersWatchingChunk(new ChunkPos(sx, sz), false).forEach(entity -> {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeString(world.getRegistryKey().getValue().toString());
			buf.writeInt(sx);
			buf.writeInt(sy);
			buf.writeInt(sz);
			buf.writeCompoundTag(DataHandlerSerializer.serialize(this.currentSection, true));
			entity.networkHandler.sendPacket(new CustomPayloadS2CPacket(SYNC_CHANNEL, buf));
		});
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
