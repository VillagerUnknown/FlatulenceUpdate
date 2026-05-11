package me.villagerunknown.flatulenceupdate.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfigClient;
import me.villagerunknown.flatulenceupdate.FlatulenceupdateConfigData;

public class ModMenuIntegration implements ModMenuApi {
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfigClient.getConfigScreen(FlatulenceupdateConfigData.class, parent).get();
	}
	
}
