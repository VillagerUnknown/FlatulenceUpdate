package me.villagerunknown.flatulenceupdate.mixin;

import me.villagerunknown.flatulenceupdate.Flatulenceupdate;
import me.villagerunknown.flatulenceupdate.feature.addFlatulenceFeature;
import me.villagerunknown.platform.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	
	@Inject(method = "jump", at = @At("HEAD"))
	public void jump(CallbackInfo ci) {
		if( MathUtil.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnPoseChange) ) {
			Entity entity = (Entity) (Object) this;
			addFlatulenceFeature.executeFlatulence( entity );
		} // if
	}
	
	@Inject(method = "useRiptide", at = @At("HEAD"))
	public void useRiptide(int riptideTicks, float riptideAttackDamage, ItemStack stack, CallbackInfo ci) {
		if( MathUtil.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnPoseChange) ) {
			Entity entity = (Entity) (Object) this;
			addFlatulenceFeature.executeFlatulence( entity );
		} // if
	}
	
}
