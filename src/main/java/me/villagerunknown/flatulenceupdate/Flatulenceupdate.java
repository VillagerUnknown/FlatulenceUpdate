package me.villagerunknown.flatulenceupdate;

import me.villagerunknown.flatulenceupdate.feature.addFlatulenceFeature;
import me.villagerunknown.platform.Platform;
import me.villagerunknown.platform.PlatformMod;
import me.villagerunknown.platform.manager.featureManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Flatulenceupdate implements ModInitializer {
	
	public static PlatformMod<FlatulenceupdateConfigData> MOD = Platform.register( "flatulenceupdate", Flatulenceupdate.class, FlatulenceupdateConfigData.class );
	public static String MOD_ID = MOD.getModId();
	public static Logger LOGGER = MOD.getLogger();
	public static FlatulenceupdateConfigData CONFIG = MOD.getConfig();
	
	@Override
	public void onInitialize() {
		// # Register Mod w/ Platform
		Platform.init_mod( MOD );
		
		// # Activate Features
		featureManager.addFeature( "add-flatulence", addFlatulenceFeature::execute );
		
		// # Load Features
		featureManager.loadFeatures();
	}
	
	
}
