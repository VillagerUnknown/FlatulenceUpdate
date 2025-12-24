package me.villagerunknown.flatulenceupdate.mixin;

import me.villagerunknown.flatulenceupdate.Flatulenceupdate;
import me.villagerunknown.flatulenceupdate.feature.addFlatulenceFeature;
import me.villagerunknown.platform.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	
	@Inject(method = "useRiptide", at = @At("RETURN"))
	public void useRiptide(int riptideTicks, float riptideAttackDamage, ItemStack stack, CallbackInfo ci) {
		if( MathUtil.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnPoseChange) ) {
			Entity entity = (Entity) (Object) this;
			addFlatulenceFeature.executeFlatulence( entity );
		} // if
	}
	
}
