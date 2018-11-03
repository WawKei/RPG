<?php

namespace EntityManager\entity;

use pocketmine\block\Block;
use pocketmine\level\Level;
use pocketmine\math\AxisAlignedBB;
use pocketmine\math\Vector3;
use pocketmine\utils\Random;

use pocketmine\entity\Attribute;
use pocketmine\entity\AttributeMap;

use EntityManager\utils\DamageSource;
use EntityManager\utils\MathHelper;
use pocketmine\network\mcpe\protocol\EntityEventPacket;

use EntityManager\Channel;

abstract class EntityLivingBase extends Entity{

	private $previousEquipment = [];

	public $isSwingInProgress;
	public $swingProgressInt;
	public $arrowHitTimer;
	public $hurtTime;
	public $maxHurtTime;
	public $attackedAtYaw;
	public $deathTime;
	public $prevSwingProgress;
	public $swingProgress;
	public $prevLimbSwingAmount;
	public $limbSwingAmount;
	public $limbSwing;
	public $maxHurtResistantTime = 20;
	public $renderYawOffset;
	public $prevRenderYawOffset;
	public $rotationYawHead;
	public $prevRotationYawHead;
	public $jumpMovementFactor = 0.02;
	protected $attackingPlayer;
	protected $recentlyHit;
	protected $dead;
	protected $entityAge;
	protected $prevOnGroundSpeedFactor;
	protected $onGroundSpeedFactor;
	protected $movedDistance;
	protected $prevMovedDistance;
	protected $field_70741_aB;
	protected $scoreValue;
	protected $lastDamage;
	protected $isJumping;
	public $moveStrafing;
	public $moveForward;
	protected $randomYawVelocity;
	protected $newPosRotationIncrements;
	protected $newPosX;
	protected $newPosY;
	protected $newPosZ;
	protected $newRotationYaw;
	protected $newRotationPitch;
	private $potionsNeedUpdate = true;
	private $entityLivingToAttack;
	private $revengeTimer = 0;
	private $lastAttacker;
	private $lastAttackerTime;
	private $landMovementFactor = 0;
	private $jumpTicks;
	private $absorptionAmount = 0;

	public function __construct(Level $worldIn, Channel $channel){
		parent::__construct($worldIn, $channel);
		$this->setHealth($this->getMaxHealth());
		$this->preventEntitySpawning = true;
		$this->field_70770_ap = (float)(((rand(1, 100) / 100) + 1.0) * 0.009999999776482582);
		$this->setPosition($this->posX, $this->posY, $this->posZ);
		$this->field_70769_ao = (rand(1, 100) / 100) * 12398.0;
		$this->rotationYaw = (float)((rand(1, 100) / 100) * M_PI * 2.0);
		$this->rotationYawHead = $this->rotationYaw;
		$this->stepHeight = 0.6;
	}

	protected function entityInit() : void{
		$this->attributeMap->addAttribute(Attribute::getAttribute(Attribute::HEALTH));
		$this->attributeMap->addAttribute(Attribute::getAttribute(Attribute::KNOCKBACK_RESISTANCE));
		$this->attributeMap->addAttribute(Attribute::getAttribute(Attribute::MOVEMENT_SPEED));
	}

	protected function applyEntityAttributes() : void{
		$this->attributeMap->addAttribute(Attribute::getAttribute(Attribute::HEALTH));
		$this->attributeMap->addAttribute(Attribute::getAttribute(Attribute::KNOCKBACK_RESISTANCE));
		$this->attributeMap->addAttribute(Attribute::getAttribute(Attribute::MOVEMENT_SPEED));
	}

	public function getAttributeMap(){
		return $this->attributeMap;
	}

	public function canBreatheUnderwater() : bool{
		return false;
	}

	public function onEntityUpdate() : void{
		$this->prevSwingProgress = $this->swingProgress;
		parent::onEntityUpdate();

		//if($this->isEntityAlive()){
		//	if($this->isEntityInsideOpaqueBlock()){
		//		this.attackEntityFrom(DamageSource.inWall, 1.0F);
		//	}
		//}

		if($this->isImmuneToFire()){
			$this->extinguish();
		}

		if($this->isEntityAlive() && $this->isWet()){
			$this->extinguish();
		}

		if($this->hurtTime > 0){
			--$this->hurtTime;
		}

		if($this->hurtResistantTime > 0){
			--$this->hurtResistantTime;
		}

		if($this->getHealth() <= 0.0){
			$this->onDeathUpdate();
		}

		if($this->recentlyHit > 0){
			--$this->recentlyHit;
		}else{
			$this->attackingPlayer = null;
		}

		if($this->lastAttacker != null && !$this->lastAttacker->isEntityAlive()){
			$this->lastAttacker = null;
		}

		if($this->entityLivingToAttack != null){
			if(!$this->entityLivingToAttack->isEntityAlive()){
				$this->setRevengeTarget(null);
			}else if($this->ticksExisted - $this->revengeTimer > 100){
				$this->setRevengeTarget(null);
			}
		}

		$this->prevMovedDistance = $this->movedDistance;
		$this->prevRenderYawOffset = $this->renderYawOffset;
		$this->prevRotationYawHead = $this->rotationYawHead;
		$this->prevRotationYaw = $this->rotationYaw;
		$this->prevRotationPitch = $this->rotationPitch;
	}

	public function isChild() : bool{
		return false;
	}

	protected function onDeathUpdate() : void{
		++$this->deathTime;

		if($this->deathTime == 20){
			$this->setDead();
			$this->despawnFromAll();
		}
	}

	public function getAITarget() : ?EntityLivingBase{
		return $this->entityLivingToAttack;
	}

	public function getRevengeTimer() : int{
		return $this->revengeTimer;
	}

	public function setRevengeTarget(?EntityLivingBase $livingBase) : void{
		$this->entityLivingToAttack = $livingBase;
		$this->revengeTimer = $this->ticksExisted;
	}

	public function getLastAttacker() : EntityLivingBase{
		return $this->lastAttacker;
	}

	public function getLastAttackerTime() : int{
		return $this->lastAttackerTime;
	}

	public function setLastAttacker(Entity $entityIn) : void{
		if($entityIn instanceof EntityLivingBase){
			$this->lastAttacker = $entityIn;
		}else{
			$this->lastAttacker = null;
		}

		$this->lastAttackerTime = $this->ticksExisted;
	}

	public function getAge() : int{
		return $this->entityAge;
	}

	public function heal(float $healAmount) : void{
		$f = $this->getHealth();

		if($f > 0.0){
			$this->setHealth($f + $healAmount);
		}
	}

	public final function getHealth() : float{
		return $this->attributeMap->getAttribute(Attribute::HEALTH)->getValue();
	}

	public function setHealth(float $health) : void{
		$this->attributeMap->getAttribute(Attribute::HEALTH)->setValue(max(0, $health));
	}

	public function getMaxHealth() : float{
		return $this->attributeMap->getAttribute(Attribute::HEALTH)->getMaxValue();
	}

	public function setMaxHealth(float $health) : void{
		$this->attributeMap->getAttribute(Attribute::HEALTH)->setMaxValue($health);
	}

	public function attackEntityFrom(DamageSource $source, float $amount) : bool{
		$this->entityAge = 0;

		if($this->getHealth() <= 0.0){
			return false;
		}/*else if($source->isFireDamage() && this.isPotionActive(Potion.fireResistance)){
			return false;
		}*/else{
			$this->limbSwingAmount = 1.5;
			$flag = true;

			if($this->hurtResistantTime > $this->maxHurtResistantTime / 2.0){
				if($amount <= $this->lastDamage){
					return false;
				}

				$this->damageEntity($source, $amount - $this->lastDamage);
				$this->lastDamage = $amount;
				$flag = false;
			}else{
				$this->lastDamage = $amount;
				$this->hurtResistantTime = $this->maxHurtResistantTime;
				$this->damageEntity($source, $amount);
				$this->hurtTime = $this->maxHurtTime = 10;
			}

			$this->attackedAtYaw = 0.0;
			$entity = $source->getEntity();

			if($entity !== null){
				if($entity instanceof EntityLivingBase){
					$this->setRevengeTarget($entity);
				}

				if($entity instanceof EntityPlayer){
					$this->recentlyHit = 100;
					$this->attackingPlayer = $entity;
				}/*else if(entity instanceof EntityWolf){
					$entitywolf = $entity;

					if($entitywolf->isTamed()){
						$this->recentlyHit = 100;
						$this->attackingPlayer = null;
					}
				}*/
			}

			if($flag){
				if($source->getCause() !== DamageSource::CAUSE_DROWNING){
					$this->setBeenAttacked();
				}

				if($entity !== null){
					$d1 = $entity->posX - $this->posX;

					for($d0 = $entity->posZ - $this->posZ;$d1 * $d1 + $d0 * $d0 < 1.0E-4;$d0 = ((rand(1, 100) / 100) - (rand(1, 100) / 100)) * 0.01){
						$d1 = ((rand(1, 100) / 100) - (rand(1, 100) / 100)) * 0.01;
					}

					$this->attackedAtYaw = (float)(atan2($d0, $d1) * 180.0 / M_PI - $this->rotationYaw);
					$this->knockBack($entity, $amount, $d1, $d0);
				}else{
					$this->attackedAtYaw = (float)((int)((rand(1, 100) / 100) * 2.0) * 180);
				}
			}

			if($this->getHealth() <= 0.0){
				//deathsound

				$this->onDeath($source);
			}else{
				//hurtsound
			}

			return true;
		}
	}

	public function knockBack(Entity $entityIn, float $p_70653_2_, float $p_70653_3_, float $p_70653_5_) : void{
		//if($this->rand->nextFloat() >= $this->attributeMap->getAttribute(Attribute::KNOCKBACK_RESISTANCE)->getValue()){
			$this->isAirBorne = true;
			$f = sqrt($p_70653_3_ * $p_70653_3_ + $p_70653_5_ * $p_70653_5_);
			$f1 = 0.4;
			$this->motionX /= 2.0;
			$this->motionY /= 2.0;
			$this->motionZ /= 2.0;
			$this->motionX -= $p_70653_3_ / $f * $f1;
			$this->motionY += $f1;
			$this->motionZ -= $p_70653_5_ / $f * $f1;

			if($this->motionY > 0.4000000059604645){
				$this->motionY = 0.4000000059604645;
			}
		//}
	}

	protected function damageEntity(DamageSource $damageSrc, float $damageAmount) : void{
		//if(!$this->isEntityInvulnerable($damageSrc)){
			//$damageAmount = $this->applyArmorCalculations($damageSrc, $damageAmount);
			//$damageAmount = $this->applyPotionDamageCalculations($damageSrc, $damageAmount);
			$f = $damageAmount;
			$damageAmount = max($damageAmount - $this->getAbsorptionAmount(), 0.0);
			$this->setAbsorptionAmount($this->getAbsorptionAmount() - ($f - $damageAmount));

			if($damageAmount != 0.0){
				$f1 = $this->getHealth();
				$this->setHealth($f1 - $damageAmount);
				$pk = new EntityEventPacket();
				$pk->entityRuntimeId = $this->entityId;
				$pk->event = EntityEventPacket::HURT_ANIMATION;

				foreach($this->hasSpawned as $player){
					$player->dataPacket($pk);
				}
				//$this->getCombatTracker()->trackDamage($damageSrc, $f1, $damageAmount);
				$this->setAbsorptionAmount($this->getAbsorptionAmount() - $damageAmount);
			}
		//}
	}

	public function onDeath(DamageSource $source) : void{
		$pk = new EntityEventPacket();
		$pk->entityRuntimeId = $this->entityId;
		$pk->event = EntityEventPacket::DEATH_ANIMATION;

		foreach($this->hasSpawned as $player){
			$player->dataPacket($pk);
		}
		//drop;
	}

	public function isOnLadder() : bool{
		$i = MathHelper::floor_float($this->posX);
		$j = MathHelper::floor_float($this->getEntityBoundingBox()->minY);
		$k = MathHelper::floor_float($this->posZ);
		$block = $this->worldObj->getBlock(new Vector3($i, $j, $k));
		return ($block->getId() == Block::LADDER || $block->getId() == Block::VINE) && (!($this instanceof EntityPlayer) || !$this->isSpectator());
	}

	public function isEntityAlive() : bool{
		return !$this->isDead && $this->getHealth() > 0.0;
	}

	public function fall(float $distance, float $damageMultiplier) : void{
		parent::fall($distance, $damageMultiplier);
		//$potioneffect = $this->getActivePotionEffect(Potion::jump);
		//$f = $potioneffect != null ? (float)($potioneffect->getAmplifier() + 1) : 0.0;
		$f = 0;
		$i = MathHelper::ceiling_float_int(($distance - 3.0 - $f) * $damageMultiplier);

		if($i > 0){
			//fallsound
			$this->attackEntityFrom(new DamageSource(DamageSource::CAUSE_FALL), (float)$i);
		}
	}

	protected function getJumpUpwardsMotion() : float{
		return 0.42;
	}

	protected function jump() : void{
		$this->motionY = $this->getJumpUpwardsMotion();

		//if($this->isPotionActive(Potion::jump)){
		//	$this->motionY += ((float)($this->getActivePotionEffect(Potion::jump)->getAmplifier() + 1) * 0.1);
		//}

		//if($this->isSprinting()){
		//	$f = $this->rotationYaw * 0.017453292;
		///	$this->motionX -= (sin($f) * 0.2);
		//	$this->motionZ += (cos($f) * 0.2);
		//}

		$this->isAirBorne = true;
	}

	protected function updateAITick() : void{
		$this->motionY += 0.03999999910593033;
	}

	protected function handleJumpLava() : void{
		$this->motionY += 0.03999999910593033;
	}

	public function moveEntityWithHeading(float $strafe, float $forward) : void{
		if(!$this->isInWater()){
			if(!$this->isInLava()){
				$f4 = 0.91;

				if($this->onGround){
					$f4 = 0.6 * 0.91;
					//$f4 = $this->worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
				}

				$f = 0.16277136 / ($f4 * $f4 * $f4);

				if($this->onGround){
					$f5 = $this->getAIMoveSpeed() * $f;
				}else{
					$f5 = $this->jumpMovementFactor;
				}

				$this->moveFlying($strafe, $forward, $f5);
				$f4 = 0.91;

				if($this->onGround){
					$f4 = 0.6 * 0.91;
					//$f4 = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
				}

				if($this->isOnLadder()){
					$f6 = 0.15;
					$this->motionX = MathHelper::clamp($this->motionX, (-$f6), $f6);
					$this->motionZ = MathHelper::clamp($this->motionZ, (-$f6), $f6);
					$this->fallDistance = 0.0;

					if($this->motionY < -0.15){
						$this->motionY = -0.15;
					}

					$flag = false;// $this->isSneaking() && $this instanceof EntityPlayer;

					if($flag && $this->motionY < 0.0){
						$this->motionY = 0.0;
					}
				}

				$this->moveEntity($this->motionX, $this->motionY, $this->motionZ);

				if($this->isCollidedHorizontally && $this->isOnLadder()){
					$this->motionY = 0.2;
				}

				$this->motionY -= 0.08;

				$this->motionY *= 0.9800000190734863;
				$this->motionX *= $f4;
				$this->motionZ *= $f4;
			}else{
				$d1 = $this->posY;
				$this->moveFlying($strafe, $forward, 0.02);
				$this->moveEntity($this->motionX, $this->motionY, $this->motionZ);
				$this->motionX *= 0.5;
				$this->motionY *= 0.5;
				$this->motionZ *= 0.5;
				$this->motionY -= 0.02;

				if ($this->isCollidedHorizontally && $this->isOffsetPositionInLiquid($this->motionX, $this->motionY + 0.6000000238418579 - $this->posY + $d1, $this->motionZ)){
					$this->motionY = 0.30000001192092896;
				}
			}
		}else{
			$d0 = $this->posY;
			$f1 = 0.8;
			$f2 = 0.02;
			$f3 = 0;//(float)EnchantmentHelper::getDepthStriderModifier($this);

			if($f3 > 3.0){
				$f3 = 3.0;
			}

			if(!$this->onGround){
				$f3 *= 0.5;
			}

			if($f3 > 0.0){
				$f1 += (0.54600006 - $f1) * $f3 / 3.0;
				$f2 += ($this->getAIMoveSpeed() * 1.0 - $f2) * $f3 / 3.0;
			}

			$this->moveFlying($strafe, $forward, $f2);
			$this->moveEntity($this->motionX, $this->motionY, $this->motionZ);
			$this->motionX *= $f1;
			$this->motionY *= 0.800000011920929;
			$this->motionZ *= $f1;
			$this->motionY -= 0.02;

			//if($this->isCollidedHorizontally && $this->isOffsetPositionInLiquid($this->motionX, $this->motionY + 0.6000000238418579 - $this->posY + $d0, $this->motionZ)){
			//	$this->motionY = 0.30000001192092896;
			//}
		}

		$this->prevLimbSwingAmount = $this->limbSwingAmount;
		$d2 = $this->posX - $this->prevPosX;
		$d3 = $this->posZ - $this->prevPosZ;
		$f7 = sqrt($d2 * $d2 + $d3 * $d3) * 4.0;

		if($f7 > 1.0){
			$f7 = 1.0;
		}

		$this->limbSwingAmount += ($f7 - $this->limbSwingAmount) * 0.4;
		$this->limbSwing += $this->limbSwingAmount;
	}

	public function getAIMoveSpeed() : float{
		return $this->landMovementFactor;
	}

	public function setAIMoveSpeed(float $speedIn) : void{
		$this->landMovementFactor = $speedIn;
	}

	public function attackEntityAsMob(Entity $entityIn) : bool{
		$this->setLastAttacker($entityIn);
		return false;
	}

	public function onUpdate() : void{
		parent::onUpdate();

		/*$i = $this->getArrowCountInEntity();

		if (i > 0){
			if (this.arrowHitTimer <= 0){
				$this->arrowHitTimer = 20 * (30 - i);
			}

			--$this->arrowHitTimer;

			if (this.arrowHitTimer <= 0){
				$this->setArrowCountInEntity(i - 1);
			}
		}*/

		$this->onLivingUpdate();
		$d0 = $this->posX - $this->prevPosX;
		$d1 = $this->posZ - $this->prevPosZ;
		$f = (float)($d0 * $d0 + $d1 * $d1);
		$f1 = $this->renderYawOffset;
		$f2 = 0.0;
		$this->prevOnGroundSpeedFactor = $this->onGroundSpeedFactor;
		$f3 = 0.0;

		if($f > 0.0025000002){
			$f3 = 1.0;
			$f2 = (float)sqrt($f) * 3.0;
			$f1 = (float)atan2($d1, $d0) * 180.0 / (float)M_PI - 90.0;
		}

		if($this->swingProgress > 0.0){
			$f1 = $this->rotationYaw;
		}

		if(!$this->onGround){
			$f3 = 0.0;
		}

		$this->onGroundSpeedFactor += ($f3 - $this->onGroundSpeedFactor) * 0.3;
		$f2 = $this->func_110146_f((float)$f1, (float)$f2);

		while($this->rotationYaw - $this->prevRotationYaw < -180.0){
			$this->prevRotationYaw -= 360.0;
		}

		while($this->rotationYaw - $this->prevRotationYaw >= 180.0){
			$this->prevRotationYaw += 360.0;
		}

		while($this->renderYawOffset - $this->prevRenderYawOffset < -180.0){
			$this->prevRenderYawOffset -= 360.0;
		}

		while($this->renderYawOffset - $this->prevRenderYawOffset >= 180.0){
			$this->prevRenderYawOffset += 360.0;
		}

		while($this->rotationPitch - $this->prevRotationPitch < -180.0){
			$this->prevRotationPitch -= 360.0;
		}

		while($this->rotationPitch - $this->prevRotationPitch >= 180.0){
			$this->prevRotationPitch += 360.0;
		}

		while($this->rotationYawHead - $this->prevRotationYawHead < -180.0){
			$this->prevRotationYawHead -= 360.0;
		}

		while($this->rotationYawHead - $this->prevRotationYawHead >= 180.0){
			$this->prevRotationYawHead += 360.0;
		}

		$this->movedDistance += $f2;
	}

	protected function func_110146_f(float $p_110146_1_, float $p_110146_2_) : float{
		$f = MathHelper::wrapAngleTo180($p_110146_1_ - $this->renderYawOffset);
		$this->renderYawOffset += $f * 0.3;
		$f1 = MathHelper::wrapAngleTo180($this->rotationYaw - $this->renderYawOffset);
		$flag = $f1 < -90.0 || $f1 >= 90.0;

		if($f1 < -75.0){
			$f1 = -75.0;
		}

		if($f1 >= 75.0){
			$f1 = 75.0;
		}

		$this->renderYawOffset = $this->rotationYaw - $f1;

		if($f1 * $f1 > 2500.0){
			$this->renderYawOffset += $f1 * 0.2;
		}

		if($flag){
			$p_110146_2_ *= -1.0;
		}

		return $p_110146_2_;
	}

	public function onLivingUpdate() : void{
		if($this->jumpTicks > 0){
			--$this->jumpTicks;
		}

		if($this->newPosRotationIncrements > 0){
			$d0 = $this->posX + ($this->newPosX - $this->posX) / $this->newPosRotationIncrements;
			$d1 = $this->posY + ($this->newPosY - $this->posY) / $this->newPosRotationIncrements;
			$d2 = $this->posZ + ($this->newPosZ - $this->posZ) / $this->newPosRotationIncrements;
			$d3 = MathHelper::wrapAngleTo180($this->newRotationYaw - $this->rotationYaw);
			$this->rotationYaw = (float)($this->rotationYaw + $d3 /$this->newPosRotationIncrements);
			$this->rotationPitch = (float)($this->rotationPitch + ($this->newRotationPitch - $this->rotationPitch) / $this->newPosRotationIncrements);
			--$this->newPosRotationIncrements;
			$this->setPosition($d0, $d1, $d2);
			$this->setRotation($this->rotationYaw, $this->rotationPitch);
		}

		if(abs($this->motionX) < 0.005){
			$this->motionX = 0.0;
		}

		if(abs($this->motionY) < 0.005){
			$this->motionY = 0.0;
		}

		if(abs($this->motionZ) < 0.005){
			$this->motionZ = 0.0;
		}

		/*if (this.isMovementBlocked()){
			this.isJumping = false;
			this.moveStrafing = 0.0F;
			this.moveForward = 0.0F;
			this.randomYawVelocity = 0.0F;
		}*/


		if($this->isJumping){
			if ($this->isInWater()){
				$this->updateAITick();
			}else if ($this->isInLava()){
				$this->handleJumpLava();
			}else if($this->onGround && $this->jumpTicks == 0){
				$this->jump();
				$this->jumpTicks = 10;
			}
		}else{
			$this->jumpTicks = 0;
		}

		$this->moveStrafing *= 0.98;
		$this->moveForward *= 0.98;
		$this->randomYawVelocity *= 0.9;
		$this->moveEntityWithHeading($this->moveStrafing, $this->moveForward);

		$this->collideWithNearbyEntities();
	}

	protected function collideWithNearbyEntities() : void{
		$list = $this->channel->getEntitiesWithinAABB($this, $this->getEntityBoundingBox()->grow(0.20000000298023224, 0.0, 0.20000000298023224));

		if(count($list) > 0){
			for($i = 0;$i < count($list);++$i){
				$entity = $list[$i];
				if(!$entity->canBePushed()) continue;
				$this->collideWithEntity($entity);
			}
		}
	}

	protected function collideWithEntity(Entity $p_82167_1_) : void{
		$p_82167_1_->applyEntityCollision($this);
	}

	public function mountEntity(Entity $entityIn) : void{
		if($this->ridingEntity != null && $entityIn == null){
			$this->dismountEntity($this->ridingEntity);

			if($this->ridingEntity != null){
				$this->ridingEntity->riddenByEntity = null;
			}

			$this->ridingEntity = null;
		}else{
			parent::mountEntity($entityIn);
		}
	}

	public function updateRidden() : void{
		parent::updateRidden();
		$this->prevOnGroundSpeedFactor = $this->onGroundSpeedFactor;
		$this->onGroundSpeedFactor = 0.0;
		$this->fallDistance = 0.0;
	}

	public function setJumping(bool $p_70637_1_) : void{
		$this->isJumping = $p_70637_1_;
	}

	public function getLookVec() : Vector3{
		return $this->getLook(1.0);
	}

	public function getLook(float $partialTicks) : Vector3{
		if($partialTicks == 1.0){
			return $this->getVectorForRotation($this->rotationPitch, $this->rotationYawHead);
		}else{
			$f = $this->prevRotationPitch + ($this->rotationPitch - $this->prevRotationPitch) * $partialTicks;
			$f1 = $this->prevRotationYawHead + ($this->rotationYawHead - $this->prevRotationYawHead) * $partialTicks;
			return $this->getVectorForRotation($f, $f1);
		}
	}

	public function canBeCollidedWith() : bool{
		return !$this->isDead;
	}

	public function canBePushed() : bool{
		return !$this->isDead;
	}

	protected function setBeenAttacked() : void{
		$this->velocityChanged = $this->rand->nextFloat() >= $this->attributeMap->getAttribute(Attribute::KNOCKBACK_RESISTANCE)->getValue();
	}

	public function getRotationYawHead() : float{
		return $this->rotationYawHead;
	}

	public function setRotationYawHead(float $rotation) : void{
		$this->rotationYawHead = $rotation;
	}

	public function func_181013_g(float $p_181013_1_) : void{
		$this->renderYawOffset = $p_181013_1_;
	}

	public function getAbsorptionAmount() : float{
		return $this->absorptionAmount;
	}

	public function setAbsorptionAmount(float $amount) : void{
		if($amount < 0.0){
			$amount = 0.0;
		}

		$this->absorptionAmount = $amount;
	}
}