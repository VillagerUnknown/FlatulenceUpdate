package me.villagerunknown.flatulenceupdate;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "villagerunknown-flatulenceupdate")
public class FlatulenceupdateConfigData implements me.shedaniel.autoconfig.ConfigData {
	
	/**
	 * General
	 */
	
	@ConfigEntry.Category("General")
	public boolean enableFlatulence = true;
	
	@ConfigEntry.Category("General")
	public float chanceForFlatulence = 0.00005F;
	
	@ConfigEntry.Category("General")
	public float chanceForFlatulenceWhileSleeping = 0.0005F;
	
	@ConfigEntry.Category("General")
	public float chanceForFlatulenceOnDamage = 0.05F;
	
	@ConfigEntry.Category("General")
	public float chanceForFlatulenceOnDeath = 0.05F;
	
	@ConfigEntry.Category("General")
	public float chanceForFlatulenceOnRespawn = 0.05F;
	
	@ConfigEntry.Category("General")
	public float chanceForFlammableFlatulence = 0F;
	
	@ConfigEntry.Category("General")
	public float chanceForExplosiveFlatulence = 0F;
	
	/**
	 * Effects
	 */
	
	@ConfigEntry.Category("Effects")
	public float chanceForEffect = 0.10F;
	
	@ConfigEntry.Category("Effects")
	public float chanceForHarmfulEffect = 0F;
	
	@ConfigEntry.Category("Effects")
	public int maxAfflictionRange = 2;
	
	@ConfigEntry.Category("Effects")
	public int maxStatusEffectLevel = 3;
	
	@ConfigEntry.Category("Effects")
	public int minStatusEffectDuration = 60;
	
	@ConfigEntry.Category("Effects")
	public int maxStatusEffectDuration = 120;
	
	/**
	 * Explosions
	 */
	
	@ConfigEntry.Category("Explosions")
	public float minExplosivePower = 1.0F;
	
	@ConfigEntry.Category("Explosions")
	public float maxExplosivePower = 4.0F;
	
	@ConfigEntry.Category("Explosions")
	public boolean allowExplosionsToBreakBlocks = false;
	
}
