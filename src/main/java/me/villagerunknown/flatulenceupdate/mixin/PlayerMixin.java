package me.villagerunknown.flatulenceupdate.mixin;

import me.villagerunknown.flatulenceupdate.Flatulenceupdate;
import me.villagerunknown.flatulenceupdate.feature.AddFlatulenceFeature;
import me.villagerunknown.flatulenceupdate.util.ModRandom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "startAutoSpinAttack", at = @At("RETURN"))
    public void flatulenceupdate$startAutoSpinAttack(int riptideTicks, float spinningPower, ItemStack stack, CallbackInfo ci) {
        if (ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnPoseChange)) {
            Entity entity = (Entity) (Object) this;
            AddFlatulenceFeature.executeFlatulence(entity);
        }
    }
}
