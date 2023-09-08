package com.github.alexthe666.iceandfire.core;

import com.github.alexthe666.iceandfire.IceAndFireConfig;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.enums.EnumHippogryphTypes;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.List;

public class ModEntities {

	public static void init() {
		if (IceAndFireConfig.ENTITY_SPAWNING.spawnHippogryphs) {
			for (EnumHippogryphTypes type : EnumHippogryphTypes.values()) {
				if (!type.developer) {
					for (Biome biome : Biome.REGISTRY) {
						if (biome != null && BiomeDictionary.hasType(biome, BiomeDictionary.Type.HILLS)) {
							List<Biome.SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.CREATURE);
							spawnList.add(new Biome.SpawnListEntry(EntityHippogryph.class, IceAndFireConfig.ENTITY_SPAWNING.hippogryphSpawnRate, 1, 1));
						}
					}
				}
			}
		}
		if (IceAndFireConfig.ENTITY_SPAWNING.spawnDeathWorm) {
			for (Biome biome : Biome.REGISTRY) {
				if (biome != null && BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY) && BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY) && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH) && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.MESA)) {
					List<Biome.SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.CREATURE);
					spawnList.add(new Biome.SpawnListEntry(EntityDeathWorm.class, IceAndFireConfig.ENTITY_SPAWNING.deathWormSpawnRate, 1, 3));
				}
			}
		}
		if (IceAndFireConfig.ENTITY_SPAWNING.spawnTrolls) {
			for (EnumTroll type : EnumTroll.values()) {
				for (Biome biome : Biome.REGISTRY) {
					if (biome != null && BiomeDictionary.hasType(biome, type.spawnBiome)) {
						List<Biome.SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.MONSTER);
						spawnList.add(new Biome.SpawnListEntry(EntityTroll.class, IceAndFireConfig.ENTITY_SPAWNING.trollSpawnRate, 1, 1));
					}
				}
			}
		}
		if (IceAndFireConfig.ENTITY_SPAWNING.spawnCockatrices) {
			for (Biome biome : Biome.REGISTRY) {
				if (biome != null && BiomeDictionary.hasType(biome, BiomeDictionary.Type.SAVANNA) && BiomeDictionary.hasType(biome, BiomeDictionary.Type.SPARSE)) {
					List<Biome.SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.CREATURE);
					spawnList.add(new Biome.SpawnListEntry(EntityCockatrice.class, IceAndFireConfig.ENTITY_SPAWNING.cockatriceSpawnRate, 1, 2));
				}
			}
		}
		if (IceAndFireConfig.ENTITY_SPAWNING.spawnAmphitheres) {
			for (Biome biome : Biome.REGISTRY) {
				if (biome != null && BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE)) {
					List<Biome.SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.CREATURE);
					spawnList.add(new Biome.SpawnListEntry(EntityAmphithere.class, IceAndFireConfig.ENTITY_SPAWNING.amphithereSpawnRate, 1, 3));
				}
			}
		}
	}
}
