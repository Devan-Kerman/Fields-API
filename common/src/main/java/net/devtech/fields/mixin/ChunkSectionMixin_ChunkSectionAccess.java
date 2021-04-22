package net.devtech.fields.mixin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.devtech.fields.impl.DataHandlerSerializer;
import net.devtech.fields.impl.access.ChunkSectionAccess;
import net.devtech.fields.v0.api.DataHandler;
import net.devtech.fields.v0.api.DataFormatInitializer;
import net.devtech.fields.v0.api.data.TickingDataHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Mixin(ChunkSection.class)
public class ChunkSectionMixin_ChunkSectionAccess implements ChunkSectionAccess {
	public Map<Identifier, DataHandler<?, ?>> formats;
	public List<TickingDataHandler> ticking;

	@Override
	public void fields_handleTick(ServerWorld world, WorldChunk chunk) {
		if(this.ticking != null) {
			for (TickingDataHandler format : this.ticking) {
				format.onTick(world, chunk);
			}
		}
	}

	@Override
	public DataHandler<?, ?> getOrCreate(Identifier values, DataFormatInitializer.Entry<?, ?> entry) {
		if(this.formats == null) {
			this.formats = new HashMap<>();
		}

		return this.formats.computeIfAbsent(values, i -> {
			DataHandler<?, ?> format = entry.initializer.apply(entry, (ChunkSection) (Object) this, values);
			if(format instanceof TickingDataHandler) {
				if(this.ticking == null) {
					this.ticking = new ArrayList<>();
				}
				this.ticking.add((TickingDataHandler) format);
			}
			return format;
		});
	}

	@Override
	public DataHandler<?, ?> get(Identifier identifier) {
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

	@Environment(EnvType.CLIENT)
	@Inject(method = "fromPacket", at = @At("RETURN"))
	public void fromPacket(PacketByteBuf packetByteBuf, CallbackInfo ci) {
		DataHandlerSerializer.deserialize((ChunkSection) (Object) this, packetByteBuf.readCompoundTag());
	}

	@Inject(method = "toPacket", at = @At("RETURN"))
	public void toPacket(PacketByteBuf packetByteBuf, CallbackInfo ci) {
		packetByteBuf.writeCompoundTag(DataHandlerSerializer.serialize((ChunkSection) (Object) this, true));
	}
}
