package net.devtech.heat;

import net.devtech.fields.v0.api.DataFormatInitializer;
import net.devtech.fields.v0.api.data.GradientPackedDataFormat;
import net.devtech.fields.v0.api.data.TickingDataFormat;
import net.devtech.fields.v0.api.value.ValueField;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class HeatThing extends GradientPackedDataFormat implements TickingDataFormat {
	private static final Direction[] DIRECTIONS = Direction.values();

	public HeatThing(DataFormatInitializer.Entry<Integer, ValueField.Int> entry, ChunkSection section, Identifier id) {
		super(entry, section, id);
	}

	@Override
	public void onTick(ServerWorld world, WorldChunk chunk) {
		int disequalibrium = 100 / this.max;
		if (world.getTime() % disequalibrium == 0) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					int surface = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE).get(x, z);
					for (int y = 0; y < 16; y++) {
						int i = this.getInt(x, y, z);
						if(y >= surface) {
							if(i < 0) {
								this.setInt(x, y, z, i--);
							} else if(i > 0) {
								this.setInt(x, y, z, i++);
							}
						}
						for (Direction direction : DIRECTIONS) {
							int a = this.getInt(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ());
							if(i - a > 2) {
								int avg = (i + a) / 2;
								i = avg;
								a = ((i - a & 1) == 0 ? 0 : 1) + avg;
								this.setInt(x, y, z, i);
								this.setInt(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ(), a);
							}
						}
					}
				}
			}
		}
	}
}
