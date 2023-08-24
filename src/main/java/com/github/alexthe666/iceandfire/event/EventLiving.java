package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.entity.ai.EntitySheepAIFollowCyclops;
import com.github.alexthe666.iceandfire.entity.ai.VillagerAIFearUntamed;
import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.item.ItemSeaSerpentArmor;
import com.github.alexthe666.iceandfire.item.ItemTrollArmor;
import com.github.alexthe666.iceandfire.message.MessagePlayerHitMultipart;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EventLiving {

	@SubscribeEvent
	public void onArrowCollide(ProjectileImpactEvent event){
		if(event.getEntity() instanceof EntityArrow && ((EntityArrow) event.getEntity()).shootingEntity != null){
			if(event.getRayTraceResult() != null && event.getRayTraceResult().entityHit != null){
				Entity shootingEntity = ((EntityArrow) event.getEntity()).shootingEntity;
				Entity shotEntity = event.getRayTraceResult().entityHit;
				if(shootingEntity instanceof EntityLivingBase && shootingEntity.isRidingOrBeingRiddenBy(shotEntity)){
					if(shotEntity instanceof EntityTameable && ((EntityTameable) shotEntity).isTamed() && shotEntity.isOnSameTeam(shootingEntity)){
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerAttackMob(AttackEntityEvent event) {
		if(event.getTarget() instanceof EntityMutlipartPart && event.getEntity() instanceof EntityPlayer){
			event.setCanceled(true);
			EntityLivingBase parent = ((EntityMutlipartPart)event.getTarget()).getParent();
			((EntityPlayer)event.getEntity()).attackTargetEntityWithCurrentItem(parent);
			IceAndFire.NETWORK_WRAPPER.sendToServer(new MessagePlayerHitMultipart(parent.getEntityId()));
		}
	}

	@SubscribeEvent
	public void onGatherCollisionBoxes(GetCollisionBoxesEvent event) {
		if(event.getEntity() != null && event.getEntity() instanceof IPhasesThroughBlock){
			Iterator<AxisAlignedBB> itr = event.getCollisionBoxesList().iterator();
			while (itr.hasNext()) {
				AxisAlignedBB aabb = itr.next();
				BlockPos pos = new BlockPos(aabb.minX, aabb.minY, aabb.minZ);
				if(((IPhasesThroughBlock) event.getEntity()).canPhaseThroughBlock(event.getWorld(), pos)){
					itr.remove();
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityMount(EntityMountEvent event) {
		if(event.getEntityBeingMounted() instanceof EntityDragonBase){
			EntityDragonBase dragon = (EntityDragonBase)event.getEntityBeingMounted();
			if(event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && !event.getEntityMounting().world.isRemote){
				EntityPlayer player = (EntityPlayer)event.getEntityMounting();
				if(dragon.isOwner((EntityPlayer)event.getEntityMounting())){
					dragon.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
					player.fallDistance = -dragon.height;
				} else{
					dragon.renderYawOffset = dragon.rotationYaw;
					float modTick_0 = dragon.getAnimationTick() - 25;
					float modTick_1 = dragon.getAnimationTick() > 25 && dragon.getAnimationTick() < 55 ? 8 * MathHelper.clamp(MathHelper.sin((float) (Math.PI + modTick_0 * 0.25)), -0.8F, 0.8F) : 0;
					float modTick_2 = dragon.getAnimationTick() > 30 ? 10 : Math.max(0, dragon.getAnimationTick() - 20);
					float radius = 0.75F * (0.6F * dragon.getRenderSize() / 3) * -3;
					float angle = (0.01745329251F * dragon.renderYawOffset) + 3.15F + (modTick_1 *2F) * 0.015F;
					double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle)));
					double extraZ = (double) (radius * MathHelper.cos(angle));
					double extraY = modTick_2 == 0 ? 0 : 0.035F * ((dragon.getRenderSize() / 3) + (modTick_2 * 0.5 * (dragon.getRenderSize() / 3)));
					player.setPosition(dragon.posX + extraX, dragon.posY + extraY, dragon.posZ + extraZ);
				}
			}

		}
		if(event.getEntityBeingMounted() instanceof EntityHippogryph){
			EntityHippogryph hippogryph = (EntityHippogryph)event.getEntityBeingMounted();
			if(event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && !event.getEntityMounting().world.isRemote && hippogryph.isOwner((EntityPlayer)event.getEntityMounting())){
				EntityPlayer player = (EntityPlayer)event.getEntityMounting();
				hippogryph.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
			}
		}
	}

	@SubscribeEvent
	public void onEntityDamage(LivingHurtEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(event.getSource().isProjectile()){
			float multi = 1;
			if (entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemTrollArmor) {
				multi -= 0.1;
			}
			if (entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemTrollArmor) {
				multi -= 0.3;
			}
			if (entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemTrollArmor) {
				multi -= 0.2;
			}
			if (entity.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemTrollArmor) {
				multi -= 0.1;
			}
			event.setAmount(event.getAmount() * multi);
		}

	}

	@SubscribeEvent
	public void onEntityDrop(LivingDropsEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity instanceof EntityWitherSkeleton) {
			entity.dropItem(ModItems.witherbone, entity.getRNG().nextInt(2));
		}
		if (entity instanceof EntityLiving) {
			EntityEffectProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, EntityEffectProperties.class);
			if (properties != null && properties.isStone()) {
				event.setCanceled(true);
			}
		}

	}

	@SubscribeEvent
	public void onEntityDespawn(LivingSpawnEvent.AllowDespawn event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity instanceof EntityLiving) {
			EntityEffectProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, EntityEffectProperties.class);
			if (properties != null && properties.isStone()) {
				event.setResult(Event.Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public void onLivingAttacked(LivingAttackEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(event.getSource().getTrueSource() != null) {
			Entity attacker = event.getSource().getTrueSource();
			if (isAnimaniaChicken(entity) && attacker instanceof EntityLivingBase) {
				signalChickenAlarm(entity, (EntityLivingBase) attacker);
			}
			if(DragonUtils.isVillager(entity) && attacker instanceof EntityLivingBase){
				signalAmphithereAlarm(entity, (EntityLivingBase) attacker);
			}
		}
	}

	@SubscribeEvent
	public void onLivingSetTarget(LivingSetAttackTargetEvent event) {
		if(event.getTarget() != null){
			EntityLivingBase attacker = event.getEntityLiving();
			if (isAnimaniaChicken(event.getTarget())) {
				signalChickenAlarm(event.getTarget(), attacker);
			}
			if(DragonUtils.isVillager(event.getTarget())){
				signalAmphithereAlarm(event.getTarget(), attacker);
			}
		}
	}

	private static void signalChickenAlarm(EntityLivingBase chicken, EntityLivingBase attacker){
		float d0 = IceAndFire.CONFIG.cockatriceChickenSearchLength;
		List<Entity> list = chicken.world.getEntitiesWithinAABB(EntityCockatrice.class, (new AxisAlignedBB(chicken.posX, chicken.posY, chicken.posZ, chicken.posX + 1.0D, chicken.posY + 1.0D, chicken.posZ + 1.0D)).grow(d0, 10.0D, d0));
		list.sort(new EntityAINearestAttackableTarget.Sorter(attacker));
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (entity instanceof EntityCockatrice && !(attacker instanceof EntityCockatrice)) {
					EntityCockatrice cockatrice = (EntityCockatrice) entity;
					if (!DragonUtils.hasSameOwner(cockatrice, attacker)) {
						if (attacker instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) attacker;
							if (!player.isCreative() && !cockatrice.isOwner(player)) {
								cockatrice.setAttackTarget(player);
							}
						} else {
							cockatrice.setAttackTarget(attacker);
						}
					}
				}
			}
		}
	}

	private static void signalAmphithereAlarm(EntityLivingBase villager, EntityLivingBase attacker){
		float d0 = IceAndFire.CONFIG.amphithereVillagerSearchLength;
		List<Entity> list = villager.world.getEntitiesWithinAABB(EntityAmphithere.class, (new AxisAlignedBB(villager.posX - 1.0D, villager.posY - 1.0D, villager.posZ - 1.0D, villager.posX + 1.0D, villager.posY + 1.0D, villager.posZ + 1.0D)).grow(d0, d0, d0));
		list.sort(new EntityAINearestAttackableTarget.Sorter(attacker));
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (entity instanceof EntityAmphithere && !(attacker instanceof EntityAmphithere)) {
					EntityAmphithere amphithere = (EntityAmphithere) entity;
					if (!DragonUtils.hasSameOwner(amphithere, attacker)) {
						if (attacker instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) attacker;
							if (!player.isCreative() && !amphithere.isOwner(player)) {
								amphithere.setAttackTarget(player);
							}
						} else {
							amphithere.setAttackTarget(attacker);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerAttack(AttackEntityEvent event) {
		if (event.getTarget() != null && isAnimaniaSheep(event.getTarget())) {
			float dist = IceAndFire.CONFIG.cyclopesSheepSearchLength;
			List<Entity> list = event.getTarget().world.getEntitiesWithinAABBExcludingEntity(event.getEntityPlayer(), event.getEntityPlayer().getEntityBoundingBox().expand(dist, dist, dist));
			list.sort(new EntityAINearestAttackableTarget.Sorter(event.getEntityPlayer()));
			if (!list.isEmpty()) {
				for (Entity entity : list) {
					if (entity instanceof EntityCyclops) {
						EntityCyclops cyclops = (EntityCyclops) entity;
						if (!cyclops.isBlinded() && !event.getEntityPlayer().capabilities.isCreativeMode) {
							cyclops.setAttackTarget(event.getEntityPlayer());
						}
					}
				}
			}
		}
		if (event.getTarget() instanceof EntityLiving) {
			boolean stonePlayer = event.getTarget() instanceof EntityStoneStatue;
			EntityEffectProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(event.getTarget(), EntityEffectProperties.class);
			if (properties != null && properties.isStone() || stonePlayer) {
				((EntityLiving) event.getTarget()).setHealth(((EntityLiving) event.getTarget()).getMaxHealth());
				if (event.getEntityPlayer() != null) {
					ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
					if (stack.getItem() != null && (stack.getItem().canHarvestBlock(Blocks.STONE.getDefaultState()) || stack.getItem().getTranslationKey().contains("pickaxe"))) {
						boolean silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
						boolean ready = false;
						if (properties != null && !stonePlayer) {
							properties.effectData++;
							ready = properties.effectData > 9;
						}
						if (stonePlayer) {
							EntityStoneStatue statue = (EntityStoneStatue) event.getTarget();
							statue.setCrackAmount(statue.getCrackAmount() + 1);
							ready = statue.getCrackAmount() > 9;
						}
						if (ready) {
							event.getTarget().setDead();
							if (silkTouch) {
								ItemStack statuette = new ItemStack(ModItems.stone_statue);
								statuette.setTagCompound(new NBTTagCompound());
								statuette.getTagCompound().setBoolean("IAFStoneStatueEntityPlayer", stonePlayer);
								statuette.getTagCompound().setInteger("IAFStoneStatueEntityID", stonePlayer ? 90 : EntityList.getID(event.getTarget().getClass()));
								((EntityLiving) event.getTarget()).writeEntityToNBT(statuette.getTagCompound());
								if (!event.getTarget().world.isRemote) {
									event.getTarget().entityDropItem(statuette, 1);
								}
							} else {
								if (!(event.getTarget()).world.isRemote) {
									event.getTarget().dropItem(Item.getItemFromBlock(Blocks.COBBLESTONE), 2 + event.getEntityLiving().getRNG().nextInt(4));
								}
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityUseItem(PlayerInteractEvent.RightClickItem event){
		EntityLivingBase entity = event.getEntityLiving();
		if (entity instanceof EntityPlayer && entity.rotationPitch > 87 && entity.getRidingEntity() != null && entity.getRidingEntity() instanceof EntityDragonBase){
			((EntityDragonBase) entity.getRidingEntity()).processInteract((EntityPlayer)entity, event.getHand());
		}
	}

	Random rand = new Random();

	@SubscribeEvent
	public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if (entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemSeaSerpentArmor || entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemSeaSerpentArmor || entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemSeaSerpentArmor || entity.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemSeaSerpentArmor) {
			entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 50, 0, false, false));
			if(entity.isWet()){
				int headMod = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
				int chestMod = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
				int legMod = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
				int footMod = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 50, headMod + chestMod + legMod + footMod - 1, false, false));

			}
		}
		if (IceAndFire.CONFIG.chickensLayRottenEggs && !entity.world.isRemote && isAnimaniaChicken(entity) && !entity.isChild() && entity instanceof EntityAnimal) {
			if (entity.ticksExisted > 30 && entity.getRNG().nextInt(IceAndFire.CONFIG.cockatriceEggChance * 6000) == 0) {
				entity.playSound(SoundEvents.ENTITY_CHICKEN_HURT, 2.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
				entity.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
				entity.dropItem(ModItems.rotten_egg, 1);
			}
		}
		EntityEffectProperties effectProperties = EntityPropertiesHandler.INSTANCE.getProperties(entity, EntityEffectProperties.class);
		if (effectProperties != null) {
			boolean prevFrozen = effectProperties.isFrozen();
			if (entity instanceof EntityIceDragon && prevFrozen) {
				effectProperties.reset();
			}
			if (!entity.world.isRemote) {
				if (effectProperties.isFrozen()) {
					if (entity.isBurning()) {
						effectProperties.reset();
						entity.extinguish();
					} else if (entity.deathTime > 0 && effectProperties.isFrozen()) {
						effectProperties.reset();
					} else if (effectProperties.effectData > 0 && effectProperties.isFrozen()) {
						effectProperties.effectData--;
					} else if (effectProperties.isFrozen()) {
						effectProperties.reset();
					}
				}
			}

			if(!(entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative())){
				if (effectProperties.isFrozen()) {
					entity.motionX *= 0.25;
					entity.motionZ *= 0.25;
					if (!(entity instanceof EntityDragon)) {
						entity.motionY -= 0.1D;
					}
				}
			}

			if (prevFrozen != effectProperties.isFrozen()) {
				for (int i = 0; i < 15; i++) {
					entity.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, entity.posX + ((rand.nextDouble() - 0.5D) * entity.width), entity.posY + ((rand.nextDouble()) * entity.height), entity.posZ + ((rand.nextDouble() - 0.5D) * entity.width), 0, 0, 0, new int[]{Block.getIdFromBlock(ModBlocks.dragon_ice)});
				}
				entity.playSound(SoundEvents.BLOCK_GLASS_BREAK, 3, 1);
			}
		}
		if (entity instanceof EntityPlayer || entity instanceof EntityVillager || entity instanceof IHearsSiren) {
			if (effectProperties != null && effectProperties.isCharmed() && effectProperties.effectData != 0) {
				EntitySiren closestSiren = effectProperties.getSiren(entity.world);
				if (closestSiren != null && closestSiren.isActuallySinging()) {
					if (EntitySiren.isWearingEarplugs(entity) || effectProperties.additionalData > IceAndFire.CONFIG.sirenMaxSingTime) {
						effectProperties.reset();
						closestSiren.singCooldown = IceAndFire.CONFIG.sirenTimeBetweenSongs;
					} else {
						effectProperties.additionalData++;
						if (rand.nextInt(7) == 0) {
							for (int i = 0; i < 5; i++) {
								entity.world.spawnParticle(EnumParticleTypes.HEART, entity.posX + ((rand.nextDouble() - 0.5D) * 3), entity.posY + ((rand.nextDouble() - 0.5D) * 3), entity.posZ + ((rand.nextDouble() - 0.5D) * 3), 0, 0, 0);
							}
						}
						if(entity.collidedHorizontally){
							if(entity instanceof EntityLiving){
								((EntityLiving) entity).getJumpHelper().setJumping();
							}else if(entity.onGround){
								entity.motionY = 0.42F;
							}
						}
						entity.motionX += (Math.signum(closestSiren.posX - entity.posX) * 0.5D - entity.motionX) * 0.100000000372529;
						entity.motionY += (Math.signum(closestSiren.posY - entity.posY + 1) * 0.5D - entity.motionY) * 0.100000000372529;
						entity.motionZ += (Math.signum(closestSiren.posZ - entity.posZ) * 0.5D - entity.motionZ) * 0.100000000372529;
						double d0 = closestSiren.posX - entity.posX;
						double d2 = closestSiren.posZ - entity.posZ;
						double d1 = closestSiren.posY - 1 - entity.posY;
						if (entity.isRiding()) {
							entity.dismountRidingEntity();
						}
						double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
						float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
						float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
						entity.rotationPitch = updateRotation(entity.rotationPitch, f1, 30F);
						entity.rotationYaw = updateRotation(entity.rotationYaw, f, 30F);
						if (entity.getDistance(closestSiren) < 5D) {
							effectProperties.reset();
							closestSiren.singCooldown = IceAndFire.CONFIG.sirenTimeBetweenSongs;
							closestSiren.setSinging(false);
							closestSiren.setAttackTarget(entity);
							closestSiren.setAggressive(true);
							closestSiren.triggerOtherSirens(entity);
						}
						if (closestSiren.isDead || entity.getDistance(closestSiren) > EntitySiren.SEARCH_RANGE * 2 || effectProperties.getSiren(entity.world) == null || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
							effectProperties.reset();
						}
					}
				}
			}
		}

		if (entity instanceof EntityLiving) {
			boolean stonePlayer = entity instanceof EntityStoneStatue;
			if (effectProperties != null && effectProperties.isStone() || stonePlayer) {
				EntityLiving living = ((EntityLiving) entity);
				if (!living.getPassengers().isEmpty()) {
					for (Entity e : living.getPassengers()) {
						e.dismountRidingEntity();
					}
				}
				living.motionX *= 0D;
				living.motionZ *= 0D;
				living.motionY -= 0.1D;
				living.swingProgress = 0;
				living.limbSwing = 0;
				living.setInvisible(!stonePlayer);
				living.livingSoundTime = 0;
				living.hurtTime = 0;
				living.hurtResistantTime = living.maxHurtResistantTime - 1;
				living.extinguish();
				if (living instanceof EntityAnimal) {
					((EntityAnimal) living).resetInLove();
				}
				if (!living.isAIDisabled()) {
					living.setNoAI(true);
				}
				if (living.getAttackTarget() != null) {
					living.setAttackTarget(null);
				}
				if (living instanceof EntityHorse) {
					EntityHorse horse = (EntityHorse) living;
					horse.tailCounter = 0;
					horse.setEatingHaystack(false);
				}
			}
		}
	}

	public static float updateRotation(float angle, float targetAngle, float maxIncrease) {
		float f = MathHelper.wrapDegrees(targetAngle - angle);
		if (f > maxIncrease) {
			f = maxIncrease;
		}
		if (f < -maxIncrease) {
			f = -maxIncrease;
		}
		return angle + f;
	}

	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity instanceof EntityLiving) {
			EntityEffectProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, EntityEffectProperties.class);
			if (properties != null && properties.isStone()) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity instanceof EntityLiving) {
			EntityEffectProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, EntityEffectProperties.class);
			if (properties != null && properties.isStone()) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
		if (event.getEntityPlayer() != null && (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockChest)) {
			float dist = IceAndFire.CONFIG.dragonGoldSearchLength;
			List<Entity> list = event.getWorld().getEntitiesWithinAABBExcludingEntity(event.getEntityPlayer(), event.getEntityPlayer().getEntityBoundingBox().expand(dist, dist, dist));
			list.sort(new EntityAINearestAttackableTarget.Sorter(event.getEntityPlayer()));
			if (!list.isEmpty()) {
				for (Entity entity : list) {
					if (entity instanceof EntityDragonBase) {
						EntityDragonBase dragon = (EntityDragonBase) entity;
						if (!dragon.isTamed() && !dragon.isModelDead() && !dragon.isOwner(event.getEntityPlayer()) && !event.getEntityPlayer().capabilities.isCreativeMode) {
							dragon.setSleeping(false);
							dragon.setSitting(false);
							dragon.setAttackTarget(event.getEntityPlayer());
						}
					}
				}
			}
		}
	}


	@SubscribeEvent
	public void onBreakBlock(BlockEvent.BreakEvent event) {
		if (event.getPlayer() != null && (event.getState().getBlock() == ModBlocks.goldPile || event.getState().getBlock() == ModBlocks.silverPile)) {
			float dist = IceAndFire.CONFIG.dragonGoldSearchLength;
			List<Entity> list = event.getWorld().getEntitiesWithinAABBExcludingEntity(event.getPlayer(), event.getPlayer().getEntityBoundingBox().expand(dist, dist, dist));
			list.sort(new EntityAINearestAttackableTarget.Sorter(event.getPlayer()));
			if (!list.isEmpty()) {
				for (Entity entity : list) {
					if (entity instanceof EntityDragonBase) {
						EntityDragonBase dragon = (EntityDragonBase) entity;
						if (!dragon.isTamed() && !dragon.isModelDead() && !dragon.isOwner(event.getPlayer()) && !event.getPlayer().capabilities.isCreativeMode) {
							dragon.setSleeping(false);
							dragon.setSitting(false);
							dragon.setAttackTarget(event.getPlayer());
						}
					}
				}
			}
		}
	}


	@SubscribeEvent
	public void onChestGenerated(LootTableLoadEvent event) {
		if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
			final LootPool pool1 = event.getTable().getPool("pool1");
			if (pool1 != null) {
				pool1.addEntry(new LootEntryItem(ModItems.manuscript, 10, 5, new LootFunction[0], new LootCondition[0], "iceandfire:manuscript"));
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLeaveEvent(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.player != null) {
			if (!event.player.getPassengers().isEmpty()) {
				if (event.player.isRiding()) {
					event.player.dismountRidingEntity();
				}
				for (Entity entity : event.player.getPassengers()) {
					entity.dismountRidingEntity();
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.getEntity() != null && isAnimaniaSheep(event.getEntity()) && event.getEntity() instanceof EntityAnimal) {
			EntityAnimal animal = (EntityAnimal) event.getEntity();
			animal.tasks.addTask(8, new EntitySheepAIFollowCyclops(animal, 1.2D));
		}
		if (event.getEntity() != null && DragonUtils.isVillager(event.getEntity()) && event.getEntity() instanceof EntityCreature && IceAndFire.CONFIG.villagersFearDragons) {
			EntityCreature villager = (EntityCreature) event.getEntity();
			villager.tasks.addTask(1, new VillagerAIFearUntamed(villager, EntityLivingBase.class, (entity) -> entity instanceof IVillagerFear, 12.0F, 0.8D, 0.8D));
		}
		if (event.getEntity() != null && DragonUtils.isLivestock(event.getEntity()) && event.getEntity() instanceof EntityCreature && IceAndFire.CONFIG.animalsFearDragons) {
			EntityCreature animal = (EntityCreature) event.getEntity();
			animal.tasks.addTask(1, new VillagerAIFearUntamed(animal, EntityLivingBase.class, (entity) -> entity instanceof IAnimalFear && ((IAnimalFear) entity).shouldAnimalsFear(animal), 12.0F, 1.2D, 1.5D));
		}
	}

	public static boolean isAnimaniaSheep(Entity entity){
		String className = entity.getClass().getName();
		return className.contains("sheep") || entity instanceof EntitySheep;
	}

	public static boolean isAnimaniaChicken(Entity entity){
		String className = entity.getClass().getName();
		return (className.contains("chicken") || entity instanceof EntityChicken) && entity instanceof EntityLiving && !entity.isCreatureType(EnumCreatureType.MONSTER, false);
	}

	public static boolean isAnimaniaFerret(Entity entity){
		String className = entity.getClass().getName();
		return className.contains("ferret") || className.contains("polecat");
	}
}
