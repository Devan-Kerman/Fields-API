package net.devtech.fields.mixin;

import net.devtech.fields.impl.DataHandlerSerializer;
import net.devtech.fields.v0.api.data.AbstractDataHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

@Mixin (ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin_InterceptSyncPacket {
	@Inject (method = "onCustomPayload",
			at = @At (value = "INVOKE",
					target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getData()Lnet/minecraft/network/PacketByteBuf;"), cancellable = true)
	public void onSyncPacket(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if(AbstractDataHandler.SYNC_CHANNEL.equals(packet.getChannel())) {
			PacketByteBuf buf = packet.getData();
			String id = buf.readString();
			ClientWorld world = MinecraftClient.getInstance().world;

			if(world != null && world.getRegistryKey().getValue().toString().equals(id)) {
				int sx = buf.readInt(), sy = buf.readInt(), sz = buf.readInt();
				CompoundTag tag = buf.readCompoundTag();
				WorldChunk chunk = world.getChunk(sx, sz);
				DataHandlerSerializer.deserialize(chunk.getSectionArray()[sy], tag);
			}
			ci.cancel();
		}
	}
}
