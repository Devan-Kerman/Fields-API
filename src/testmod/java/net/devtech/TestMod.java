package net.devtech;

import net.devtech.fields.impl.ValueFieldImpl;
import net.devtech.fields.v0.api.DataFormat;
import net.devtech.fields.v0.api.DataFormatInitializer;
import net.devtech.fields.v0.api.value.ValueField;
import net.devtech.heat.HeatThing;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

public class TestMod implements ModInitializer {
	public static final DataFormatInitializer.Entry<Integer, ValueField.Int> HEAT_FORMAT = DataFormat.register(new Identifier("testmod", "heat"), HeatThing::new, ValueFieldImpl.Int::new);
	public static final ValueField.Int HEAT = ValueField.create(HEAT_FORMAT, new Identifier("testmod", "heat"));

	@Override
	public void onInitialize() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			HEAT.set(world, pos, 100);
		});
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity entity : server.getPlayerManager().getPlayerList()) {
				entity.sendMessage(new LiteralText("EE: " + HEAT.get(entity.world, entity.getBlockPos())), false);
			}
		});
	}
}
