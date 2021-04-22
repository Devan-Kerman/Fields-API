# Fields-API
Value fields for Minecraft Worlds (eg. Heat, Gas)

```groovy
repositories {
    maven {
        url 'https://storage.googleapis.com/devan-maven/'
    }
}

dependencies {
    ...
    modImplementation 'net.devtech:fields-api-fabric:2.0.0'
    // there is a forge version as well now, fields-api-forge
}
```

```java
ValueField.Int pollution = ValueField.create(INT_GRADIENT, new Identifier("mymod", "pollution")); // store in a static final field somewhere
int pollution = pollution.getInt(world, pos); // get pollution at point
pollution.setInt(world, pos, 0); // delete pollution

// idk do something
ItemStack stack = new ItemStack(MyItems.SLUDGE, pollution);
```


## ValueFields
ValueField is just an accessor basically, it's an interface to translate global calls (eg. world + pos) to local ones (eg. chunksection). Feel free to make your own for other primitives

## DataHandlers
DataHandler is the format in which the data is stored in memory, and can tick `TickingDataHandler`. Overriding the `shouldSync` method and returning true allows 

```java
package net.devtech.heat;

import java.util.Arrays;
import java.util.Collections;

import net.devtech.fields.v0.api.DataFormatInitializer;
import net.devtech.fields.v0.api.data.GradientPackedDataHandler;
import net.devtech.fields.v0.api.data.TickingDataHandler;
import net.devtech.fields.v0.api.value.ValueField;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

// this is kinda broken cus it doesn't actually spread heat to nearby subchunks, so heat will just say within the same subchunk
// a proper implementation is an excersize left up to the reader :wink:
public class HeatDataHandler extends GradientPackedDataHandler implements TickingDataHandler {
	public static final Direction[] DIRECTIONS = Direction.values();
	static {
		ServerTickEvents.START_SERVER_TICK.register(server -> Collections.shuffle(Arrays.asList(DIRECTIONS)));
	}

	public HeatDataHandler(DataFormatInitializer.Entry<Integer, ValueField.Int> entry, ChunkSection section, Identifier id) {
		super(entry, section, id);
	}

	@Override
	public void onTick(ServerWorld world, WorldChunk chunk) {
		if(this.max == 0) return;
		int disequalibrium = 100 / Math.max(Math.abs(this.min), this.max);
		if (world.getTime() % disequalibrium == 0) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					int surface = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE).get(x, z);
					for (int y = 0; y < 16; y++) {
						int i = this.getInt(x, y, z);
						if(y >= surface) { // if exposed to sky
							if(i < 0) {
								// radiate excess heat into space
								this.setInt(x, y, z, i--);
							} else if(i > 0) {
								// get heated up by the sun
								this.setInt(x, y, z, i++);
							}
						}
						// spread heat
						for (Direction direction : DIRECTIONS) {
							int a = this.getInt(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ());
							if (i - a > 2) {
								int avg = (i + a) / 2;
								i = avg;
								a = ((i - a & 1) == 0 ? 0 : 1) + avg;
								if (this.setInt(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ(), a)) {
									this.setInt(x, y, z, i);
								}
							}
						}
					}
				}
			}
		}
	}
}
```
