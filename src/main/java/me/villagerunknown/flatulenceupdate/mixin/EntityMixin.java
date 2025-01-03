package me.villagerunknown.flatulenceupdate.mixin;

import me.villagerunknown.flatulenceupdate.Flatulenceupdate;
import me.villagerunknown.flatulenceupdate.feature.addFlatulenceFeature;
import me.villagerunknown.platform.util.MathUtil;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
	
	@Inject(method = "setSneaking", at = @At("RETURN"))
	public void setSneaking(boolean sneaking, CallbackInfo ci) {
		if( sneaking && MathUtil.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnPoseChange) ) {
			Entity entity = (Entity) (Object) this;
			addFlatulenceFeature.executeFlatulence( entity );
		} // if
	}
	
}
