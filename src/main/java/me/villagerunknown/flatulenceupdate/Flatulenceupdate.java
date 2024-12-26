package me.villagerunknown.flatulenceupdate;

import me.villagerunknown.flatulenceupdate.feature.addFlatulenceFeature;
import me.villagerunknown.platform.Platform;
import me.villagerunknown.platform.PlatformMod;
import me.villagerunknown.platform.manager.featureManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Flatulenceupdate implements ModInitializer {
	
	public static PlatformMod<FlatulenceupdateConfigData> MOD = null;
	public static String MOD_ID = null;
	public static Logger LOGGER = null;
	public static FlatulenceupdateConfigData CONFIG = null;
	
	@Override
	public void onInitialize() {
		// # Register Mod w/ Platform
		MOD = Platform.register( "flatulenceupdate", Flatulenceupdate.class, FlatulenceupdateConfigData.class );
		
		MOD_ID = MOD.getModId();
		LOGGER = MOD.getLogger();
		CONFIG = MOD.getConfig();
		
		// # Initialize Mod
		init();
	}
	
	private static void init() {
		Platform.init_mod( MOD );
		
		// # Activate Features
		featureManager.addFeature( "addFlatulence", addFlatulenceFeature::execute );
	}
	
}
