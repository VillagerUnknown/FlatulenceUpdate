package me.villagerunknown.flatulenceupdate.feature;

import me.villagerunknown.flatulenceupdate.Flatulenceupdate;
import me.villagerunknown.flatulenceupdate.util.ModRandom;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class AddFlatulenceFeature {

    private static final List<Block> FLAME_SOURCE_BLOCKS = List.of(
            Blocks.TORCH, Blocks.WALL_TORCH,
            Blocks.FIRE, Blocks.SOUL_FIRE,
            Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE,
            Blocks.LANTERN, Blocks.SOUL_LANTERN,
            Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH,
            Blocks.JACK_O_LANTERN, Blocks.MAGMA_BLOCK,
            Blocks.LAVA, Blocks.LAVA_CAULDRON,
            Blocks.FURNACE, Blocks.SMOKER, Blocks.BLAST_FURNACE,
            Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH
    );

    public static final SoundEvent ENTITY_FLATULENCE = registerSound("flatulence");

    public static final Identifier TOTAL_FLATULENCE_ID =
            registerStat("total_flatulence");
    public static final Identifier TOTAL_EXPLOSIVE_FLATULENCE_ID =
            registerStat("total_explosive_flatulence");

    private AddFlatulenceFeature() {}

    public static void execute() {
        registerAmbientFlatulence();
        registerFlatulenceOnItemUse();
        registerFlatulenceOnDamage();
        registerFlatulenceOnDeath();
        registerFlatulenceOnRespawn();
    }

    private static SoundEvent registerSound(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Flatulenceupdate.MOD_ID, name);
        SoundEvent event = SoundEvent.createVariableRangeEvent(id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, event);
    }

    private static Identifier registerStat(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Flatulenceupdate.MOD_ID, name);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
        Stats.CUSTOM.get(id, net.minecraft.stats.StatFormatter.DEFAULT);
        return id;
    }

    private static void registerAmbientFlatulence() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                for (LivingEntity entity : getLivingEntities(level)) {
                    if (entity.isSleeping()) {
                        if (ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceWhileSleeping)) {
                            executeFlatulence(entity);
                        }
                    } else if (entity.isAlive()) {
                        if (ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulence)) {
                            executeFlatulence(entity);
                        }
                    }
                }
            }
        });
    }

    private static void registerFlatulenceOnItemUse() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnItemUse)) {
                executeFlatulence(player);
            }
            return InteractionResult.PASS;
        });
    }

    private static void registerFlatulenceOnDamage() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, damageSource, amount) -> {
            if (ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnDamage)) {
                executeFlatulence(entity);
            }
            return true;
        });
    }

    private static void registerFlatulenceOnDeath() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, amount) -> {
            if (ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnDeath)) {
                executeFlatulence(entity);
            }
            return true;
        });
    }

    private static void registerFlatulenceOnRespawn() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForFlatulenceOnRespawn)) {
                executeFlatulence(newPlayer);
            }
        });
    }

    public static void executeFlatulence(Entity entity) {
        if (entity.level().isClientSide()) {
            return;
        }
        if (!Flatulenceupdate.CONFIG.enableFlatulence || entity.isSpectator()) {
            return;
        }

        List<Block> nearbyBlocks = getNearbyBlocks(entity, 2);

        if (entity instanceof ServerPlayer player) {
            player.awardStat(TOTAL_FLATULENCE_ID);
        }

        playFlatulenceSound(entity);
        spawnFlatulenceParticles(entity, nearbyBlocks);
        applyFlatulenceEffects(entity, nearbyBlocks);
        causeFlatulenceExplosion(entity);
    }

    private static void playFlatulenceSound(Entity entity) {
        float volume = ModRandom.getRandomWithinRange(0F, 0.75F);
        float pitch = ModRandom.getRandomWithinRange(0F, 2.0F);

        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                ENTITY_FLATULENCE, SoundSource.MASTER, volume, pitch);
    }

    private static void spawnFlatulenceParticles(Entity entity, List<Block> nearbyBlocks) {
        int count = 1;
        float speed = 0.005F;
        SimpleParticleType particle = ParticleTypes.CAMPFIRE_COSY_SMOKE;

        if (entity.level().dimension().equals(Level.END)) {
            count = 32;
            particle = ParticleTypes.PORTAL;
        }

        if (isNearFlameSource(nearbyBlocks) || isUltraWarm(entity.level())) {
            count = 6;
            if (Flatulenceupdate.CONFIG.chanceForFlammableFlatulence > 0
                    && ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForFlammableFlatulence)) {
                particle = ParticleTypes.FLAME;
                entity.igniteForSeconds(3);
            } else {
                particle = ParticleTypes.SMALL_FLAME;
            }
        }

        if (nearbyBlocks.contains(Blocks.SOUL_CAMPFIRE)
                || nearbyBlocks.contains(Blocks.SOUL_FIRE)
                || nearbyBlocks.contains(Blocks.SOUL_TORCH)
                || nearbyBlocks.contains(Blocks.SOUL_WALL_TORCH)) {
            count = 6;
            particle = ParticleTypes.SOUL_FIRE_FLAME;
            if (Flatulenceupdate.CONFIG.chanceForFlammableFlatulence > 0
                    && ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForFlammableFlatulence)) {
                entity.igniteForSeconds(3);
            }
        }

        if (entity.isUnderWater()) {
            count = 9;
            particle = ParticleTypes.BUBBLE;
        }

        ServerLevel level = (ServerLevel) entity.level();
        float yOffset = getHeightAdjustment(entity);
        level.sendParticles(particle,
                entity.getX(),
                entity.getY() + yOffset,
                entity.getZ(),
                count,
                0.1, 0.1, 0.1,
                speed);
    }

    private static void applyFlatulenceEffects(Entity entity, List<Block> nearbyBlocks) {
        if (Flatulenceupdate.CONFIG.chanceForEffect <= 0
                || !ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForEffect)) {
            return;
        }

        int range = (int) ModRandom.getRandomWithinRange(1, Flatulenceupdate.CONFIG.maxAfflictionRange);
        int duration = (int) ModRandom.getRandomWithinRange(
                Flatulenceupdate.CONFIG.minStatusEffectDuration,
                Flatulenceupdate.CONFIG.maxStatusEffectDuration);
        int level = (int) ModRandom.getRandomWithinRange(1, Flatulenceupdate.CONFIG.maxStatusEffectLevel);
        float yOffset = getHeightAdjustment(entity);

        List<Holder<MobEffect>> statusEffects = new ArrayList<>();
        statusEffects.add(MobEffects.NAUSEA);
        statusEffects.add(MobEffects.WEAKNESS);
        statusEffects.add(MobEffects.BLINDNESS);
        statusEffects.add(MobEffects.HUNGER);
        statusEffects.add(MobEffects.RESISTANCE);
        statusEffects.add(MobEffects.ABSORPTION);

        if (Flatulenceupdate.CONFIG.chanceForHarmfulEffect > 0
                && ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForHarmfulEffect)) {
            statusEffects.add(MobEffects.INSTANT_DAMAGE);
            statusEffects.add(MobEffects.WITHER);
            statusEffects.add(MobEffects.POISON);
        }

        Holder<MobEffect> selected = statusEffects.get(ThreadLocalRandom.current().nextInt(statusEffects.size()));

        AreaEffectCloud cloud = new AreaEffectCloud(entity.level(), entity.getX(),
                entity.getY() + yOffset, entity.getZ());
        MobEffectInstance instance = new MobEffectInstance(selected,
                Flatulenceupdate.CONFIG.minStatusEffectDuration, level);

        cloud.addEffect(instance);
        cloud.setDuration(duration);
        if (entity instanceof LivingEntity living) {
            cloud.setOwner(living);
        }
        cloud.setRadius(range);
        cloud.setOnGround(false);

        ParticleOptions cloudParticle = ParticleTypes.CAMPFIRE_COSY_SMOKE;
        if (entity.isUnderWater()) {
            cloudParticle = ParticleTypes.BUBBLE;
            cloud.setRadiusPerTick(0.0075F);
            cloud.setNoGravity(true);
        } else if (isNearFlameSource(nearbyBlocks) || isUltraWarm(entity.level())) {
            cloudParticle = ParticleTypes.SMALL_FLAME;
            cloud.setNoGravity(true);
        } else if (!entity.level().dimensionType().hasCeiling()
                && !bedsWork(entity.level())) {
            cloudParticle = ParticleTypes.PORTAL;
            cloud.setRadiusPerTick(0.0075F);
            cloud.setNoGravity(false);
        } else {
            cloud.setRadiusPerTick(0F);
            cloud.setNoGravity(false);
        }

        cloud.setCustomParticle(cloudParticle);
        entity.level().addFreshEntity(cloud);
    }

    private static void causeFlatulenceExplosion(Entity entity) {
        if (Flatulenceupdate.CONFIG.chanceForExplosiveFlatulence <= 0
                || !ModRandom.hasChance(Flatulenceupdate.CONFIG.chanceForExplosiveFlatulence)) {
            return;
        }

        if (entity instanceof ServerPlayer player) {
            player.awardStat(TOTAL_EXPLOSIVE_FLATULENCE_ID);
        }

        float power = ModRandom.getRandomWithinRange(
                Flatulenceupdate.CONFIG.minExplosivePower,
                Flatulenceupdate.CONFIG.maxExplosivePower);

        Level level = entity.level();
        Level.ExplosionInteraction interaction = Flatulenceupdate.CONFIG.allowExplosionsToBreakBlocks
                ? Level.ExplosionInteraction.MOB
                : Level.ExplosionInteraction.NONE;
        level.explode(entity, entity.getX(), entity.getY(), entity.getZ(),
                4.0F * power, interaction);
    }

    private static float getHeightAdjustment(Entity entity) {
        return entity.getEyeHeight() * 0.5F;
    }

    private static boolean isNearFlameSource(List<Block> blocks) {
        for (Block b : blocks) {
            if (FLAME_SOURCE_BLOCKS.contains(b)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isUltraWarm(Level level) {
        return Boolean.TRUE.equals(level.environmentAttributes()
                .getDimensionValue(EnvironmentAttributes.WATER_EVAPORATES));
    }

    private static boolean bedsWork(Level level) {
        return level.environmentAttributes()
                .getDimensionValue(EnvironmentAttributes.BED_RULE)
                .canSetSpawn(level);
    }

    private static List<Block> getNearbyBlocks(Entity entity, int range) {
        List<Block> result = new ArrayList<>();
        BlockPos center = entity.blockPosition();
        Level level = entity.level();
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    result.add(level.getBlockState(pos).getBlock());
                }
            }
        }
        return result;
    }

    private static List<LivingEntity> getLivingEntities(ServerLevel level) {
        List<LivingEntity> list = new ArrayList<>();
        for (Entity e : level.getAllEntities()) {
            if (e instanceof LivingEntity living) {
                list.add(living);
            }
        }
        return list;
    }
}
