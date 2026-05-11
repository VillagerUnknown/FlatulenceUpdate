package me.villagerunknown.flatulenceupdate;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.villagerunknown.flatulenceupdate.feature.AddFlatulenceFeature;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Flatulenceupdate implements ModInitializer {

    public static final String MOD_ID = "villagerunknown-flatulenceupdate";
    public static final Logger LOGGER = LoggerFactory.getLogger(Flatulenceupdate.class);
    public static FlatulenceupdateConfigData CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = AutoConfig.register(FlatulenceupdateConfigData.class, GsonConfigSerializer::new)
                .getConfig();

        AddFlatulenceFeature.execute();
    }
}
