package me.villagerunknown.flatulenceupdate.feature;

import me.villagerunknown.flatulenceupdate.Flatulenceupdate;
import me.villagerunknown.platform.util.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatFormatter;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class addFlatulenceFeature {
	
	public static final SoundEvent ENTITY_FLATULENCE = RegistryUtil.registerSound( "flatulence", Flatulenceupdate.MOD_ID );
	
	public static final Identifier TOTAL_FLATULENCE_ID = RegistryUtil.registerStat( "total_flatulence", Flatulenceupdate.MOD_ID, StatFormatter.DEFAULT );
	public static final Identifier TOTAL_EXPLOSIVE_FLATULENCE_ID = RegistryUtil.registerStat( "total_explosive_flatulence", Flatulenceupdate.MOD_ID, StatFormatter.DEFAULT );
	
	public static void execute() {
		registerFlatulence();
		registerFlatulenceOnDamage();
		registerFlatulenceOnDeath();
		registerFlatulenceOnRespawn();
	}
	
	private static void registerFlatulence() {
		// # Ambient Flatulence
		ServerTickEvents.START_SERVER_TICK.register( (MinecraftServer server) -> {
			for (ServerWorld world : server.getWorlds()) {
				if( world.isClient() ) {
					continue;
				}
				
				List<LivingEntity> entities = WorldUtil.getEntitiesByType( world, LivingEntity.class );
				
				for (LivingEntity entity : entities) {
					if( entity.isSleeping() ) {
						if( MathUtil.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceWhileSleeping) ) {
							executeFlatulence( entity );
						} // if
					} else if( entity.isAlive() ) {
						if( MathUtil.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulence) ) {
							executeFlatulence( entity );
						} // if
					} // if, else
				} // for
			} // for
		});
	}
	
	private static void registerFlatulenceOnDamage() {
		// # On Damage
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, damageSource, amount) -> {
			if( MathUtil.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnDamage) ) {
				executeFlatulence( entity );
			} // if
			
			return true;
		});
	}
	
	private static void registerFlatulenceOnDeath() {
		// # On Death
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, amount) -> {
			if( MathUtil.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnDeath) ) {
				executeFlatulence( entity );
			} // if
			
			return true;
		});
	}
	
	private static void registerFlatulenceOnRespawn() {
		// # After Respawn
		ServerPlayerEvents.AFTER_RESPAWN.register((entity, damageSource, amount) -> {
			if( MathUtil.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnRespawn) ) {
				executeFlatulence( entity );
			} // if
		});
	}
	
	private static void executeFlatulence(Entity entity) {
		if( Flatulenceupdate.CONFIG.enableFlatulence && !entity.isSpectator() ) {
			List<Block> nearbyBlocks = EntityUtil.getNearbyBlocks( entity, 2 );
			
			if( entity.isPlayer() ) {
				ServerPlayerEntity player = (ServerPlayerEntity) entity;
				player.incrementStat( addFlatulenceFeature.TOTAL_FLATULENCE_ID );
			} // if
			
			playSound(entity);
			spawnParticles(entity,nearbyBlocks);
			applyEffects(entity,nearbyBlocks);
			causeExplosion(entity);
		} // if
	}
	
	private static void playSound(Entity entity) {
		float volume = MathUtil.getRandomWithinRange( 0F, 0.75F );
		float pitch = MathUtil.getRandomWithinRange( 0F, 2.0F );
		
		EntityUtil.playSound( entity, ENTITY_FLATULENCE, SoundCategory.MASTER, volume, pitch, false );
	}
	
	private static void spawnParticles(Entity entity, List<Block> nearbyBlocks) {
		int count = 1;
		float speed = 0.005F;
		SimpleParticleType particle = ParticleTypes.CAMPFIRE_COSY_SMOKE;
		
		// End
		if( entity.getWorld().getRegistryKey().equals(ServerWorld.END ) ) {
			count = 32;
			speed = 0.005F;
			particle = ParticleTypes.PORTAL;
		} // if
		
		// Flames
		if( PositionUtil.isNearFlameSource( nearbyBlocks ) || entity.getWorld().getDimension().ultrawarm() ) {
			count = 6;
			speed = 0.005F;
			if( Flatulenceupdate.CONFIG.chanceForFlammableFlatulence > 0 && MathUtil.hasChance(Flatulenceupdate.CONFIG.chanceForFlammableFlatulence) ) {
				particle = ParticleTypes.FLAME;
				entity.setOnFireFor( 3 );
			} else {
				particle = ParticleTypes.SMALL_FLAME;
			} // if
		} // if
		
		// Soul Flames
		if( nearbyBlocks.contains( Blocks.SOUL_CAMPFIRE )
				|| nearbyBlocks.contains( Blocks.SOUL_FIRE )
				|| nearbyBlocks.contains( Blocks.SOUL_TORCH )
				|| nearbyBlocks.contains( Blocks.SOUL_WALL_TORCH )
		) {
			count = 6;
			speed = 0.005F;
			particle = ParticleTypes.SOUL_FIRE_FLAME;
			if( Flatulenceupdate.CONFIG.chanceForFlammableFlatulence > 0 && MathUtil.hasChance( Flatulenceupdate.CONFIG.chanceForFlammableFlatulence ) ) {
				entity.setOnFireFor( 3 );
			} // if
		} // if
		
		// Bubbles
		if( entity.isSubmergedInWater() ) {
			count = 9;
			speed = 0.005F;
			particle = ParticleTypes.BUBBLE;
		} // if
		
		EntityUtil.spawnParticles( entity, getHeightAdjustment(entity), particle, count, 0.1, 0.1, 0.1, speed );
	}
	
	private static void applyEffects( Entity entity, List<Block> nearbyBlocks ) {
		if( Flatulenceupdate.CONFIG.chanceForEffect > 0 && MathUtil.hasChance( Flatulenceupdate.CONFIG.chanceForEffect ) ) {
			int range = (int) MathUtil.getRandomWithinRange(1,Flatulenceupdate.CONFIG.maxAfflictionRange);
			int duration = (int) MathUtil.getRandomWithinRange(Flatulenceupdate.CONFIG.minStatusEffectDuration,Flatulenceupdate.CONFIG.maxStatusEffectDuration);
			int level = (int) MathUtil.getRandomWithinRange(1,Flatulenceupdate.CONFIG.maxStatusEffectLevel);
			float heightAdjustment = getHeightAdjustment(entity);
			
			List<RegistryEntry> statusEffects = new ArrayList<>();
			statusEffects.add(StatusEffects.NAUSEA);
			statusEffects.add(StatusEffects.WEAKNESS);
			statusEffects.add(StatusEffects.BLINDNESS);
			statusEffects.add(StatusEffects.HUNGER);
			statusEffects.add(StatusEffects.RESISTANCE);
			statusEffects.add(StatusEffects.ABSORPTION);
			
			if( Flatulenceupdate.CONFIG.chanceForHarmfulEffect > 0 && MathUtil.hasChance( Flatulenceupdate.CONFIG.chanceForHarmfulEffect ) ) {
				statusEffects.add(StatusEffects.INSTANT_DAMAGE);
				statusEffects.add(StatusEffects.WITHER);
				statusEffects.add(StatusEffects.POISON);
			} // if
			
			RegistryEntry selectedStatusEffect = statusEffects.get( ThreadLocalRandom.current().nextInt(statusEffects.size()) );
			
			AreaEffectCloudEntity cloudEntity = new AreaEffectCloudEntity(entity.getEntityWorld(),entity.getX(),entity.getY() + heightAdjustment, entity.getZ());
			StatusEffectInstance effectInstance = new StatusEffectInstance(selectedStatusEffect, Flatulenceupdate.CONFIG.minStatusEffectDuration, level );
			
			cloudEntity.addEffect(effectInstance);
			cloudEntity.setDuration(duration);
			cloudEntity.setOwner((LivingEntity) entity);
			cloudEntity.setRadius(range);
			cloudEntity.setOnGround(false);
			
			ParticleEffect particleEffect = ParticleTypes.CAMPFIRE_COSY_SMOKE;
			if( entity.isSubmergedInWater() ) {
				particleEffect = ParticleTypes.BUBBLE;
				cloudEntity.setRadiusGrowth(0.0075F);
				cloudEntity.setNoGravity(true);
			} else if( PositionUtil.isNearFlameSource( nearbyBlocks ) || entity.getEntityWorld().getDimension().ultrawarm() ) {
				particleEffect = ParticleTypes.SMALL_FLAME;
				cloudEntity.setNoGravity(true);
			} else if( !entity.getEntityWorld().getDimension().hasCeiling() && !entity.getEntityWorld().getDimension().bedWorks() ) {
				particleEffect = ParticleTypes.PORTAL;
				cloudEntity.setRadiusGrowth(0.0075F);
				cloudEntity.setNoGravity(false);
			} else {
				cloudEntity.setRadiusGrowth(0F);
				cloudEntity.setNoGravity(false);
			} // if, else
			
			cloudEntity.setParticleType(particleEffect);
			entity.getEntityWorld().spawnEntity( cloudEntity );
			
		} // if
	}
	
	private static void causeExplosion( Entity entity ) {
		if( Flatulenceupdate.CONFIG.chanceForExplosiveFlatulence > 0 && MathUtil.hasChance( Flatulenceupdate.CONFIG.chanceForExplosiveFlatulence ) ) {
			if( entity.isPlayer() ) {
				ServerPlayerEntity player = (ServerPlayerEntity) entity;
				player.incrementStat( addFlatulenceFeature.TOTAL_EXPLOSIVE_FLATULENCE_ID );
			} // if
			
			float power = MathUtil.getRandomWithinRange( Flatulenceupdate.CONFIG.minExplosivePower, Flatulenceupdate.CONFIG.maxExplosivePower );
			EntityUtil.causeExplosion( entity.getEntityWorld(), entity, power, true, Flatulenceupdate.CONFIG.allowExplosionsToBreakBlocks );
		} // if
	}
	
	private static float getHeightAdjustment( Entity entity ) {
		return entity.getStandingEyeHeight() * 0.5F;
	}
	
}
