<?php

namespace EntityManager\entity;

use pocketmine\Player;
use pocketmine\block\Block;
use pocketmine\entity\DataPropertyManager;
use pocketmine\entity\Entity as PocketmineEntity;
use pocketmine\math\AxisAlignedBB;
use pocketmine\math\Vector3;
use pocketmine\level\Level;
use pocketmine\utils\Random;

use pocketmine\entity\Attribute;
use pocketmine\entity\AttributeMap;

use EntityManager\utils\MathHelper;
use EntityManager\Channel;

use EntityManager\utils\DamageSource;

use pocketmine\network\mcpe\protocol\AddEntityPacket;
use pocketmine\network\mcpe\protocol\EntityEventPacket;
use pocketmine\network\mcpe\protocol\MoveEntityPacket;
use pocketmine\network\mcpe\protocol\RemoveEntityPacket;
use pocketmine\network\mcpe\protocol\SetEntityDataPacket;
use pocketmine\network\mcpe\protocol\SetEntityMotionPacket;
use pocketmine\network\mcpe\protocol\SetEntityLinkPacket;
use pocketmine\network\mcpe\protocol\types\EntityLink;

abstract class Entity{

	public $attributeMap;

	protected $entityId;

	public $channel;

	public $riddenByEntity = null;
	public $ridingEntity = null;

	public $forceSpawn;

	public $worldObj;

	public $prevPosX = 0;
	public $prevPosY = 0;
	public $prevPosZ = 0;

	public $posX = 0;
	public $posY = 0;
	public $posZ = 0;

	public $motionX = 0;
	public $motionY = 0;
	public $motionZ = 0;

	public $rotationYaw = 0;
	public $rotationPitch = 0;

	public $prevRotationYaw = 0;
	public $prevRotationPitch = 0;

	private $boundingBox;

	public $prevOnGround;
	public $onGround;
	public $isCollidedHorizontally;
	public $isCollidedVertically;
	public $isCollided;

	public $velocityChanged;

	protected $isInWeb;

	public $isDead;

	public $width;
	public $height;

	public $prevDistanceWalkedModified;

	public $distanceWalkedModified;
	public $distanceWalkedOnStepModified;

	public $fallDistance;
	private $nextStepDistance;

	public $lastTickPosX;
	public $lastTickPosY;
	public $lastTickPosZ;

	public $stepHeight = 0;

	public $noClip;

	public $entityCollisionReduction;

	protected $rand;

	public $ticksExisted = 0;
	public $fireResistance;

	private $fire;

	protected $inWater;

	public $hurtResistantTime;
	protected $firstUpdate;
	protected $isImmuneToFire = false;

	private $entityRiderPitchDelta;
	private $entityRiderYawDelta;

	protected $propertyManager;

	public function getEntityId() : int{
		return $this->entityId;
	}

	public function setEntityId(int $id) : void{
		$this->entityId = $id;
	}

	public function __construct(Level $worldIn, Channel $channel){
		$this->channel = $channel;

		$this->entityId = PocketmineEntity::$entityCount++;
		$this->renderDistanceWeight = 1.0;
		$this->boundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
		$this->width = 0.6;
		$this->height = 1.8;
		$this->nextStepDistance = 1;
		$this->rand = new Random();
		$this->fireResistance = 1;
		$this->firstUpdate = true;
		$this->worldObj = $worldIn;
		$this->setPosition(0.0, 0.0, 0.0);

		$this->propertyManager = new DataPropertyManager();

		$this->propertyManager->setLong(self::DATA_FLAGS, 0);
		$this->propertyManager->setShort(self::DATA_MAX_AIR, 400);
		$this->propertyManager->setString(self::DATA_NAMETAG, "");
		$this->propertyManager->setLong(self::DATA_LEAD_HOLDER_EID, -1);
		$this->propertyManager->setFloat(self::DATA_SCALE, 1);
		$this->attributeMap = new AttributeMap();

		$this->entityInit();
	}

	protected abstract function entityInit() : void;

	public function setDead() : void{
		$this->isDead = true;
	}

	public function getAttributeMap(){
		return $this->attributeMap;
	}

	protected function setSize(float $width, float $height) : void{
		if($width != $this->width || $height != $this->height){
			$f = $this->width;
			$this->width = $width;
			$this->height = $height;
			$this->setEntityBoundingBox(new AxisAlignedBB($this->getEntityBoundingBox()->minX, $this->getEntityBoundingBox()->minY, $this->getEntityBoundingBox()->minZ, $this->getEntityBoundingBox()->minX + $this->width,$this->getEntityBoundingBox()->minY + $this->height, $this->getEntityBoundingBox()->minZ + $this->width));

			if($this->width > $f && !$this->firstUpdate){
				$this->moveEntity(($f - $this->width), 0.0, ($f - $this->width));
			}
			$this->propertyManager->setFloat(self::DATA_BOUNDING_BOX_WIDTH, $width);
			$this->propertyManager->setFloat(self::DATA_BOUNDING_BOX_HEIGHT, $height);
		}
	}

	protected function setRotation(float $yaw, float $pitch) : void{
		$this->rotationYaw = $yaw % 360.0;
		$this->rotationPitch = $pitch % 360.0;
	}

	public function setPosition(float $x, float $y, float $z) : void{
		$this->posX = $x;
		$this->posY = $y;
		$this->posZ = $z;
		$f = $this->width / 2.0;
		$f1 = $this->height;
		$this->setEntityBoundingBox(new AxisAlignedBB($x - $f, $y, $z - $f, $x + $f, $y + $f1, $z + $f));
	}

	public function onUpdate() : void{
		$this->onEntityUpdate();
	}

	public function onEntityUpdate() : void{
		if($this->ridingEntity != null && $this->ridingEntity->isDead){
			$this->ridingEntity = null;
		}

		$this->prevDistanceWalkedModified = $this->distanceWalkedModified;
		$this->prevPosX = $this->posX;
		$this->prevPosY = $this->posY;
		$this->prevPosZ = $this->posZ;
		$this->prevRotationYaw = $this->rotationYaw;
		$this->prevRotationPitch = $this->rotationPitch;

		//$this->spawnRunningParticles();
		$this->handleWaterMovement();

		if($this->fire > 0){
			if($this->isImmuneToFire){
				$this->fire -= 4;

				if($this->fire < 0){
					$ths->fire = 0;
				}
			}else{
				if($this->fire % 20 == 0){
					//$this->attackEntityFrom(fire, 1.0);
				}

				$this->fire--;
			}
		}

		//if($this->isInLava()){
		//	$this->setOnFireFromLava();
		//	$this->fallDistance *= 0.5;
		//}

		if($this->posY < -64.0){
			$this->kill();
		}

		//setFlag onfire

		$this->firstUpdate = false;
	}

	protected function setOnFireFromLava() : void{
		if(!$this->isImmuneToFire){
			//$this->attackEntityFrom(lava, 4.0);
			$this->setFire(15);
		}
	}

	public function setFire(int $seconds) : void{
		$i = $seconds * 20;
		//enchantment

		if($this->fire < $i){
			$this->fire = $i;
		}
	}

	public function extinguish() : void{
		$this->fire = 0;
	}

	protected function kill() : void{
		$this->setDead();
	}

	public function moveEntity(float $x, float $y, float $z) : void{
		$d0 = $this->posX;
		$d1 = $this->posY;
		$d2 = $this->posZ;

		if($this->isInWeb){
			$this->isInWeb = false;
			$x *= 0.25;
			$y *= 0.05000000074505806;
			$z *= 0.25;
			$this->motionX = 0.0;
			$this->motionY = 0.0;
			$this->motionZ = 0.0;
		}

		$d3 = $x;
		$d4 = $y;
		$d5 = $z;

		$list1 = $this->channel->getCollidingBoundingBoxes($this, $this->getEntityBoundingBox()->addCoord($x, $y, $z));
		$axisalignedbb = $this->getEntityBoundingBox();

		foreach($list1 as $axisalignedbb1){
			$y = $axisalignedbb1->calculateYOffset($this->getEntityBoundingBox(), $y);
		}

		$this->setEntityBoundingBox($this->getEntityBoundingBox()->offset(0.0, $y, 0.0));
		$flag1 = $this->onGround || $d4 != $y && $d4 < 0.0;

		foreach($list1 as $axisalignedbb2){
			$x = $axisalignedbb2->calculateXOffset($this->getEntityBoundingBox(), $x);
		}

		$this->setEntityBoundingBox($this->getEntityBoundingBox()->offset($x, 0.0, 0.0));

		foreach($list1 as $axisalignedbb13){
			$z = $axisalignedbb13->calculateZOffset($this->getEntityBoundingBox(), $z);
		}

		$this->setEntityBoundingBox($this->getEntityBoundingBox()->offset(0.0, 0.0, $z));

		if($this->stepHeight > 0.0 && $flag1 && ($d3 != $x || $d5 != $z)){
			$d11 = $x;
			$d7 = $y;
			$d8 = $z;
			$axisalignedbb3 = $this->getEntityBoundingBox();
			$this->setEntityBoundingBox($axisalignedbb);
			$y = $this->stepHeight;
			$list = $this->channel->getCollidingBoundingBoxes($this, $this->getEntityBoundingBox()->addCoord($d3, $y, $d5));
			$axisalignedbb4 = $this->getEntityBoundingBox();
			$axisalignedbb5 = $axisalignedbb4->addCoord($d3, 0.0, $d5);
			$d9 = $y;

			foreach($list as $axisalignedbb6){
				$d9 = $axisalignedbb6->calculateYOffset($axisalignedbb5, $d9);
			}

			$axisalignedbb4 = $axisalignedbb4->offset(0.0, $d9, 0.0);
			$d15 = $d3;

			foreach($list as $axisalignedbb7){
				$d15 = $axisalignedbb7->calculateXOffset($axisalignedbb4, $d15);
			}

			$axisalignedbb4 = $axisalignedbb4->offset($d15, 0.0, 0.0);
			$d16 = $d5;

			foreach($list as $axisalignedbb8){
				$d16 = $axisalignedbb8->calculateZOffset($axisalignedbb4, $d16);
			}

			$axisalignedbb4 = $axisalignedbb4->offset(0.0, 0.0, $d16);
			$axisalignedbb14 = $this->getEntityBoundingBox();
			$d17 = $y;

			foreach($list as $axisalignedbb9){
				$d17 = $axisalignedbb9->calculateYOffset($axisalignedbb14, $d17);
			}

			$axisalignedbb14 = $axisalignedbb14->offset(0.0, $d17, 0.0);
			$d18 = $d3;

			foreach($list as $axisalignedbb10){
				$d18 = $axisalignedbb10->calculateXOffset($axisalignedbb14, $d18);
			}

			$axisalignedbb14 = $axisalignedbb14->offset($d18, 0.0, 0.0);
			$d19 = $d5;

			foreach($list as $axisalignedbb11){
				$d19 = $axisalignedbb11->calculateZOffset($axisalignedbb14, $d19);
			}

			$axisalignedbb14 = $axisalignedbb14->offset(0.0, 0.0, $d19);
			$d20 = $d15 * $d15 + $d16 * $d16;
			$d10 = $d18 * $d18 + $d19 * $d19;

			if($d20 > $d10){
				$x = $d15;
				$z = $d16;
				$y = -$d9;
				$this->setEntityBoundingBox($axisalignedbb4);
			}else{
				$x = $d18;
				$z = $d19;
				$y = -$d17;
				$this->setEntityBoundingBox($axisalignedbb14);
			}

			foreach($list as $axisalignedbb12){
				$y = $axisalignedbb12->calculateYOffset($this->getEntityBoundingBox(), $y);
			}

			$this->setEntityBoundingBox($this->getEntityBoundingBox()->offset(0.0, $y, 0.0));

			if($d11 * $d11 + $d8 * $d8 >= $x * $x + $z * $z){
				$x = $d11;
				$y = $d7;
				$z = $d8;
				$this->setEntityBoundingBox($axisalignedbb3);
			}
		}

		$this->resetPositionToBB();
		$this->isCollidedHorizontally = $d3 != $x || $d5 != $z;
		$this->isCollidedVertically = $d4 != $y;
		$this->prevOnGround = $this->onGround;
		$this->onGround = $this->isCollidedVertically && $d4 < 0.0;
		if($this->onGround == $this->prevOnGround){
			/////
		}
		$this->isCollided = $this->isCollidedHorizontally || $this->isCollidedVertically;
		$i = MathHelper::floor_float($this->posX);
		$j = MathHelper::floor_float($this->posY - 0.20000000298023224);
		$k = MathHelper::floor_float($this->posZ);
		$blockpos = new Vector3($i, $j, $k);
		$block1 = $this->worldObj->getBlock($blockpos);

		if($block1->getId() == Block::AIR){
			$block = $this->worldObj->getBlock($blockpos->getSide(Vector3::SIDE_DOWN));

			if($block instanceof Fence || $block instanceof CobblestoneWall || $block instanceof FenceGate){
				$block1 = $block;
				$blockpos = $blockpos->getSide(Vector3::SIDE_DOWN);
			}
		}

		$this->updateFallState($y, $this->onGround, $block1, $blockpos);

		if($d3 != $x){
			$this->motionX = 0.0;
		}

		if($d5 != $z){
			$this->motionZ = 0.0;
		}

		if($d4 != $y){
			//$block1->onLanded($this->worldObj, $this);
		}

		$flag = false;

		if($this->canTriggerWalking() && !$flag && $this->ridingEntity == null){
			$d12 = $this->posX - $d0;
			$d13 = $this->posY - $d1;
			$d14 = $this->posZ - $d2;

			if($block1->getId() == Block::LADDER){
				$d13 = 0.0;
			}

			if($block1 != null && $this->onGround){
				//$block1->onEntityCollidedWithBlock($this->worldObj, $blockpos, $this);
			}

			$this->distanceWalkedModified = ($this->distanceWalkedModified + sqrt($d12 * $d12 + $d14 * $d14) * 0.6);
			$this->distanceWalkedOnStepModified = ($this->distanceWalkedOnStepModified + sqrt($d12 * $d12 + $d13 * $d13 + $d14 * $d14) * 0.6);

			if($this->distanceWalkedOnStepModified > $this->nextStepDistance && $block1->getId() == Block::AIR){
				$this->nextStepDistance = (int)$this->distanceWalkedOnStepModified + 1;

				if($this->isInWater()){
					$f = sqrt($this->motionX * $this->motionX * 0.20000000298023224 + $this->motionY * $this->motionY + $this->motionZ * $this->motionZ * 0.20000000298023224) * 0.35;

					if($f > 1.0){
						$f = 1.0;
					}

					//swimsound
				}

				//stepsound
			}
		}

		$this->doBlockCollisions();

		$flag2 = $this->isWet();

		/*if(//infire){
			$this->dealFireDamage(1);

			if(!$flag2){
				++$this->fire;

				if($this->fire == 0){
					$this->setFire(8);
				}
			}
		}else if($this->fire <= 0){
			$thi->fire = -$this->fireResistance;
		}

		if($flag2 && $this->fire > 0){
			//fizzsound
			$this->fire = -$this->fireResistance;
		}*/
	}

	public function interactFirst(EntityPlayer $playerIn) : bool{
		return false;
	}

	private function resetPositionToBB() : void{
		$this->posX = ($this->getEntityBoundingBox()->minX + $this->getEntityBoundingBox()->maxX) / 2.0;
		$this->posY = $this->getEntityBoundingBox()->minY;
		$this->posZ = ($this->getEntityBoundingBox()->minZ + $this->getEntityBoundingBox()->maxZ) / 2.0;
	}

	protected function doBlockCollisions() : void{
	}

	protected function canTriggerWalking() : bool{
		return true;
	}

	protected function updateFallState(float $y, bool $onGroundIn, Block $blockIn, Vector3 $pos) : void{
		if($onGroundIn){
			if($this->fallDistance > 0.0){
				//if($blockIn != null){
					//onFallenUpon
				//}else{
					$this->fall($this->fallDistance, 1.0);
				//}

				$this->fallDistance = 0.0;
			}
		}else if($y < 0.0){
			$this->fallDistance = ($this->fallDistance - $y);
		}
	}

	public function getCollisionBoundingBox() : AxisAlignedBB{
		return null;
	}

	protected function dealFireDamage(int $amount) : void{
		if(!$this->isImmuneToFire){
			//$this->attackEntityFrom(fire, $amount);
		}
	}

	public function isImmuneToFire() : bool{
		return $this->isImmuneToFire;
	}

	public function fall(float $distance, float $damageMultiplier) : void{
		if($this->riddenByEntity != null){
			$this->riddenByEntity->fall($distance, $damageMultiplier);
		}
	}

	public function isWet() : bool{
		return $this->inWater;
	}

	public function isInWater() : bool{
		return $this->inWater;
	}

	public function handleWaterMovement() : bool{
		if($this->channel->handleMaterialAcceleration($this->getEntityBoundingBox()->grow(0.0, -0.4000000059604645, 0.0)->contract(0.001, 0.001, 0.001), [Block::WATER, Block::STILL_WATER], $this, $this->worldObj)){
			if(!$this->inWater && !$this->firstUpdate){
				//$this->resetHeight();
			}

			$this->fallDistance = 0.0;
			$this->inWater = true;
			$this->fire = 0;
		}else{
			$this->inWater = false;
		}

		return $this->inWater;
	}

	public function isInLava() : bool{
		return false;//TODO 
	}

	public function moveFlying(float $strafe, float $forward, float $friction) : void{
		$f = $strafe * $strafe + $forward * $forward;

		if($f >= 1.0E-4){
			$f = sqrt($f);

			if($f < 1.0){
				$f = 1.0;
			}

			$f = $friction / $f;
			$strafe = $strafe * $f;
			$forward = $forward * $f;
			$f1 = sin($this->rotationYaw * M_PI / 180.0);
			$f2 = cos($this->rotationYaw * M_PI / 180.0);
			$this->motionX += ($strafe * $f2 - $forward * $f1);
			$this->motionZ += ($forward * $f2 + $strafe * $f1);
		}
	}

	public function setPositionAndRotation(float $x, float $y, float $z, float $yaw, float $pitch) : void{
		$this->prevPosX = $this->posX = $x;
		$this->prevPosY = $this->posY = $y;
		$this->prevPosZ = $this->posZ = $z;
		$this->prevRotationYaw = $this->rotationYaw = $yaw;
		$this->prevRotationPitch = $this->rotationPitch = $pitch;
		$d0 = ($this->prevRotationYaw - $yaw);

		if($d0 < -180.0){
			$this->prevRotationYaw += 360.0;
		}

		if($d0 >= 180.0){
			$thi->prevRotationYaw -= 360.0;
		}

		$this->setPosition($this->posX, $this->posY, $this->posZ);
		$this->setRotation($yaw, $pitch);
	}

	public function moveToBlockPosAndAngles(Vector3 $pos, float $rotationYawIn, float $rotationPitchIn) : void{
		$this->setLocationAndAngles($pos->getX() + 0.5, $pos->getY(), $pos->getZ() + 0.5, $rotationYawIn, $rotationPitchIn);
	}

	public function setLocationAndAngles(float $x, float $y, float $z, float $yaw, float $pitch) : void{
		$this->lastTickPosX = $this->prevPosX = $this->posX = $x;
		$this->lastTickPosY = $this->prevPosY = $this->posY = $y;
		$this->lastTickPosZ = $this->prevPosZ = $this->posZ = $z;
		$this->rotationYaw = $yaw;
		$this->rotationPitch = $pitch;
		$this->setPosition($this->posX, $this->posY, $this->posZ);
	}

	public function getDistanceToEntity(Entity $entityIn) : float{
		$f = (float)($this->posX - $entityIn->posX);
		$f1 = (float)($this->posY - $entityIn->posY);
		$f2 = (float)($this->posZ - $entityIn->posZ);
		return sqrt($f * $f + $f1 * $f1 + $f2 * $f2);
	}

	public function getDistanceSq(float $x, float $y, float $z) : float{
		$d0 = $this->posX - $x;
		$d1 = $this->posY - $y;
		$d2 = $this->posZ - $z;
		return $d0 * $d0 + $d1 * $d1 + $d2 * $d2;
	}

	public function getDistance(float $x, float $y, float $z) : float{
		$d0 = $this->posX - $x;
		$d1 = $this->posY - $y;
		$d2 = $this->posZ - $z;
		return sqrt($d0 * $d0 + $d1 * $d1 + $d2 * $d2);
	}

	public function getDistanceSqToVector3(Vector3 $vec) : float{
		$d0 = $this->posX - $vec->x;
		$d1 = $this->posY - $vec->y;
		$d2 = $this->posZ - $vec->z;
		return $d0 * $d0 + $d1 * $d1 + $d2 * $d2;
	}

	public function getDistanceToVector3(Vector3 $vec) : float{
		$d0 = $this->posX - $vec->x;
		$d1 = $this->posY - $vec->y;
		$d2 = $this->posZ - $vec->z;
		return sqrt($d0 * $d0 + $d1 * $d1 + $d2 * $d2);
	}

	public function getDistanceSqToEntity(Entity $entityIn) : float{
		$d0 = $this->posX - $entityIn->posX;
		$d1 = $this->posY - $entityIn->posY;
		$d2 = $this->posZ - $entityIn->posZ;
		return $d0 * $d0 + $d1 * $d1 + $d2 * $d2;
	}

	public function applyEntityCollision(Entity $entityIn) : void{
		if($entityIn->riddenByEntity != $this && $entityIn->ridingEntity != $this){
			if(!$entityIn->noClip && !$this->noClip){
				$d0 = $entityIn->posX - $this->posX;
				$d1 = $entityIn->posZ - $this->posZ;
				$d2 = max(abs($d0), abs($d1));

				if($d2 >= 0.009999999776482582){
					$d2 = sqrt($d2);
					$d0 = $d0 / $d2;
					$d1 = $d1 / $d2;
					$d3 = 1.0 / $d2;

					if($d3 > 1.0){
						$d3 = 1.0;
					}

					$d0 = $d0 * $d3;
					$d1 = $d1 * $d3;
					$d0 = $d0 * 0.05000000074505806;
					$d1 = $d1 * 0.05000000074505806;
					$d0 = $d0 * (1.0 - $this->entityCollisionReduction);
					$d1 = $d1 * (1.0 - $this->entityCollisionReduction);

					if($this->riddenByEntity == null){
						$this->addVelocity(-$d0, 0.0, -$d1);
					}

					if($entityIn->riddenByEntity == null){
						$entityIn->addVelocity($d0, 0.0, $d1);
					}
				}
			}
		}
	}

	public function addVelocity(float $x, float $y, float $z){
		$this->motionX += $x;
		$this->motionY += $y;
		$this->motionZ += $z;
		$this->isAirBorne = true;
	}

	protected function setBeenAttacked() : void{
		$this->velocityChanged = true;
	}

	public function attackEntityFrom(DamageSource $source, float $amount) : bool{
	//	if ($this->isEntityInvulnerable(source)){
	//		return false;
	//	}else{
			$this->setBeenAttacked();
			return false;
	//	}
	}

	public function getLook(float $partialTicks) : Vector3{
		if($partialTicks == 1.0){
			return $this->getVectorForRotation($this->rotationPitch, $this->rotationYaw);
		}else{
			$f = $this->prevRotationPitch + ($this->rotationPitch - $this->prevRotationPitch) * $partialTicks;
			$f1 = $this->prevRotationYaw + ($this->rotationYaw - $this->prevRotationYaw) * $partialTicks;
			return $this->getVectorForRotation($f, $f1);
		}
	}

	protected final function getVectorForRotation(float $pitch, float $yaw) : Vector3{
		$f = cos(-$yaw * 0.017453292 - M_PI);
		$f1 = sin(-$yaw * 0.017453292 - M_PI);
		$f2 = -cos(-$pitch * 0.017453292);
		$f3 = sin(-$pitch * 0.017453292);
		return new Vector3(($f1 * $f2), $f3, ($f * $f2));
	}

	public function canBeCollidedWith() : bool{
		return false;
	}

	public function canBePushed() : bool{
		return false;
	}

	public function isEntityAlive() : bool{
		return !$this->isDead;
	}

	public function mountEntity(Entity $entityIn) : void{
		$this->entityRiderPitchDelta = 0.0;
		$this->entityRiderYawDelta = 0.0;

		if($entityIn == null){
			if($this->ridingEntity != null){
				$this->setLocationAndAngles($this->ridingEntity->posX, $this->ridingEntity->getEntityBoundingBox()->minY + $this->ridingEntity->height, $this->ridingEntity->posZ, $this->rotationYaw, $this->rotationPitch);
				$this->ridingEntity->riddenByEntity = null;
			}

			$this->ridingEntity = null;
			$pk = new SetEntityLinkPacket();
			$pk->link = new EntityLink($entityIn->entityId, $this->entityId, EntityLink::TYPE_REMOVE, false);
			foreach($this->hasSpawned as $player){
				$player->dataPacket($pk);
			}
			foreach($entityIn->hasSpawned as $player){
				$player->dataPacket($pk);
			}
			$this->setGenericFlag(self::DATA_FLAG_RIDING, false);
		}else{
			if($this->ridingEntity !== null){
				$this->ridingEntity->riddenByEntity = null;
			}

			if($entityIn != null){
				for($entity = $entityIn->ridingEntity;$entity != null;$entity = $entity->ridingEntity){
					if($entity == $this){
						return;
					}
				}
			}

			$this->ridingEntity = $entityIn;
			$entityIn->riddenByEntity = $this;
			$pk = new SetEntityLinkPacket();
			$pk->link = new EntityLink($entityIn->entityId, $this->entityId, EntityLink::TYPE_RIDE, false);
			foreach($this->hasSpawned as $player){
				$player->dataPacket($pk);
			}
			foreach($entityIn->hasSpawned as $player){
				$player->dataPacket($pk);
			}
			$this->setGenericFlag(self::DATA_FLAG_RIDING);
		}
	}

	public function getEntityBoundingBox() : AxisAlignedBB{
		return $this->boundingBox;
	}

	public function setEntityBoundingBox(AxisAlignedBB $bb) : void{
		$this->boundingBox = $bb;
	}

	public function getEyeHeight() : float{
		return $this->height * 0.85;
	}

	protected $hasSpawned = [];

	public function spawnTo(Player $player){
		if(!isset($this->hasSpawned[$player->getLoaderId()])){
			$this->hasSpawned[$player->getLoaderId()] = $player;
			$pk = new AddEntityPacket();
			$pk->entityRuntimeId = $this->entityId;
			$pk->type = static::NETWORK_ID;
			$pk->position = new Vector3($this->posX, $this->posY, $this->posZ);
			$pk->motion = new Vector3($this->motionX, $this->motionY, $this->motionZ);
			$pk->yaw = $this->rotationYaw;
			$pk->pitch = $this->rotationPitch;
			$pk->attributes = $this->attributeMap->getAll();
			$pk->metadata = $this->propertyManager->getAll();

			$player->dataPacket($pk);
		}
	}

	public function despawnFrom(Player $player, bool $send = true){
		if(isset($this->hasSpawned[$player->getLoaderId()])){
			if($send){
				$pk = new RemoveEntityPacket();
				$pk->entityUniqueId = $this->entityId;
				$player->dataPacket($pk);
			}
			unset($this->hasSpawned[$player->getLoaderId()]);
		}
	}

	public function despawnFromAll(){
		foreach($this->hasSpawned as $player){
			$this->despawnFrom($player);
		}
	}

	public function sendDataAll(array $data = null){
		$pk = new SetEntityDataPacket();
		$pk->entityRuntimeId = $this->entityId;
		$pk->metadata = $data ?? $this->dataProperties;

		foreach($this->hasSpawned as $player){
			$player->dataPacket(clone $pk);
		}
	}

	public function sendData($player, array $data = null){
		if(!is_array($player)){
			$player = [$player];
		}

		$pk = new SetEntityDataPacket();
		$pk->entityRuntimeId = $this->entityId;
		$pk->metadata = $data ?? $this->dataProperties;

		foreach($player as $p){
			if($p === $this){
				continue;
			}
			$p->dataPacket(clone $pk);
		}

		if($this instanceof Player){
			$this->dataPacket($pk);
		}
	}

	public function setDataProperty(int $id, int $type, $value, bool $send = true) : bool{
		if($this->getDataProperty($id) !== $value){
			$this->dataProperties[$id] = [$type, $value];
			if($send){
				$this->changedDataProperties[$id] = $this->dataProperties[$id]; //This will be sent on the next tick
			}

			return true;
		}

		return false;
	}

	public function getDataProperty(int $id){
		return isset($this->dataProperties[$id]) ? $this->dataProperties[$id][1] : null;
	}

	public function removeDataProperty(int $id){
		unset($this->dataProperties[$id]);
	}

	public function getDataPropertyType(int $id){
		return isset($this->dataProperties[$id]) ? $this->dataProperties[$id][0] : null;
	}

	public function setDataFlag(int $propertyId, int $id, bool $value = true, int $type = self::DATA_TYPE_LONG){
		if($this->getDataFlag($propertyId, $id) !== $value){
			$flags = (int) $this->getDataProperty($propertyId);
			$flags ^= 1 << $id;
			$this->setDataProperty($propertyId, $type, $flags);
		}
	}

	public function getDataFlag(int $propertyId, int $id){
		return (((int) $this->getDataProperty($propertyId)) & (1 << $id)) > 0;
	}
	/**
	 * Wrapper around {@link Entity#getDataFlag} for generic data flag reading.
	 *
	 * @param int $flagId
	 * @return bool
	 */
	public function getGenericFlag(int $flagId) : bool{
		return $this->getDataFlag(self::DATA_FLAGS, $flagId);
	}

	/**
	 * Wrapper around {@link Entity#setDataFlag} for generic data flag setting.
	 *
	 * @param int  $flagId
	 * @param bool $value
	 */
	public function setGenericFlag(int $flagId, bool $value = true){
		$this->setDataFlag(self::DATA_FLAGS, $flagId, $value, self::DATA_TYPE_LONG);
	}

	public const DATA_TYPE_BYTE = 0;
	public const DATA_TYPE_SHORT = 1;
	public const DATA_TYPE_INT = 2;
	public const DATA_TYPE_FLOAT = 3;
	public const DATA_TYPE_STRING = 4;
	public const DATA_TYPE_SLOT = 5;
	public const DATA_TYPE_POS = 6;
	public const DATA_TYPE_LONG = 7;
	public const DATA_TYPE_VECTOR3F = 8;

	public const DATA_FLAGS = 0;
	public const DATA_HEALTH = 1; //int (minecart/boat)
	public const DATA_VARIANT = 2; //int
	public const DATA_COLOR = 3, DATA_COLOUR = 3; //byte
	public const DATA_NAMETAG = 4; //string
	public const DATA_OWNER_EID = 5; //long
	public const DATA_TARGET_EID = 6; //long
	public const DATA_AIR = 7; //short
	public const DATA_POTION_COLOR = 8; //int (ARGB!)
	public const DATA_POTION_AMBIENT = 9; //byte
	/* 10 (byte) */
	public const DATA_HURT_TIME = 11; //int (minecart/boat)
	public const DATA_HURT_DIRECTION = 12; //int (minecart/boat)
	public const DATA_PADDLE_TIME_LEFT = 13; //float
	public const DATA_PADDLE_TIME_RIGHT = 14; //float
	public const DATA_EXPERIENCE_VALUE = 15; //int (xp orb)
	public const DATA_DISPLAY_ITEM = 16; //int (id | (data << 16))
	public const DATA_DISPLAY_OFFSET = 17; //int
	public const DATA_HAS_DISPLAY = 18; //byte (must be 1 for minecart to show block inside)

	//TODO: add more properties

	public const DATA_ENDERMAN_HELD_ITEM_ID = 23; //short
	public const DATA_ENTITY_AGE = 24; //short

	/* 26 (byte) player-specific flags
	 * 27 (int) player "index"?
	 * 28 (block coords) bed position */
	public const DATA_FIREBALL_POWER_X = 29; //float
	public const DATA_FIREBALL_POWER_Y = 30;
	public const DATA_FIREBALL_POWER_Z = 31;
	/* 32 (unknown)
	 * 33 (float) fishing bobber
	 * 34 (float) fishing bobber
	 * 35 (float) fishing bobber */
	public const DATA_POTION_AUX_VALUE = 36; //short
	public const DATA_LEAD_HOLDER_EID = 37; //long
	public const DATA_SCALE = 38; //float
	public const DATA_INTERACTIVE_TAG = 39; //string (button text)
	public const DATA_NPC_SKIN_ID = 40; //string
	public const DATA_URL_TAG = 41; //string
	public const DATA_MAX_AIR = 42; //short
	public const DATA_MARK_VARIANT = 43; //int
	/* 44 (byte) container stuff
	 * 45 (int) container stuff
	 * 46 (int) container stuff */
	public const DATA_BLOCK_TARGET = 47; //block coords (ender crystal)
	public const DATA_WITHER_INVULNERABLE_TICKS = 48; //int
	public const DATA_WITHER_TARGET_1 = 49; //long
	public const DATA_WITHER_TARGET_2 = 50; //long
	public const DATA_WITHER_TARGET_3 = 51; //long
	/* 52 (short) */
	public const DATA_BOUNDING_BOX_WIDTH = 53; //float
	public const DATA_BOUNDING_BOX_HEIGHT = 54; //float
	public const DATA_FUSE_LENGTH = 55; //int
	public const DATA_RIDER_SEAT_POSITION = 56; //vector3f
	public const DATA_RIDER_ROTATION_LOCKED = 57; //byte
	public const DATA_RIDER_MAX_ROTATION = 58; //float
	public const DATA_RIDER_MIN_ROTATION = 59; //float
	public const DATA_AREA_EFFECT_CLOUD_RADIUS = 60; //float
	public const DATA_AREA_EFFECT_CLOUD_WAITING = 61; //int
	public const DATA_AREA_EFFECT_CLOUD_PARTICLE_ID = 62; //int
	/* 63 (int) shulker-related */
	public const DATA_SHULKER_ATTACH_FACE = 64; //byte
	/* 65 (short) shulker-related */
	public const DATA_SHULKER_ATTACH_POS = 66; //block coords
	public const DATA_TRADING_PLAYER_EID = 67; //long

	/* 69 (byte) command-block */
	public const DATA_COMMAND_BLOCK_COMMAND = 70; //string
	public const DATA_COMMAND_BLOCK_LAST_OUTPUT = 71; //string
	public const DATA_COMMAND_BLOCK_TRACK_OUTPUT = 72; //byte
	public const DATA_CONTROLLING_RIDER_SEAT_NUMBER = 73; //byte
	public const DATA_STRENGTH = 74; //int
	public const DATA_MAX_STRENGTH = 75; //int
	/* 76 (int) */
	public const DATA_LIMITED_LIFE = 77;
	public const DATA_ARMOR_STAND_POSE_INDEX = 78; //int
	public const DATA_ENDER_CRYSTAL_TIME_OFFSET = 79; //int
	/* 80 (byte) something to do with nametag visibility? */


	public const DATA_FLAG_ONFIRE = 0;
	public const DATA_FLAG_SNEAKING = 1;
	public const DATA_FLAG_RIDING = 2;
	public const DATA_FLAG_SPRINTING = 3;
	public const DATA_FLAG_ACTION = 4;
	public const DATA_FLAG_INVISIBLE = 5;
	public const DATA_FLAG_TEMPTED = 6;
	public const DATA_FLAG_INLOVE = 7;
	public const DATA_FLAG_SADDLED = 8;
	public const DATA_FLAG_POWERED = 9;
	public const DATA_FLAG_IGNITED = 10;
	public const DATA_FLAG_BABY = 11;
	public const DATA_FLAG_CONVERTING = 12;
	public const DATA_FLAG_CRITICAL = 13;
	public const DATA_FLAG_CAN_SHOW_NAMETAG = 14;
	public const DATA_FLAG_ALWAYS_SHOW_NAMETAG = 15;
	public const DATA_FLAG_IMMOBILE = 16, DATA_FLAG_NO_AI = 16;
	public const DATA_FLAG_SILENT = 17;
	public const DATA_FLAG_WALLCLIMBING = 18;
	public const DATA_FLAG_CAN_CLIMB = 19;
	public const DATA_FLAG_SWIMMER = 20;
	public const DATA_FLAG_CAN_FLY = 21;
	public const DATA_FLAG_WALKER = 22;
	public const DATA_FLAG_RESTING = 23;
	public const DATA_FLAG_SITTING = 24;
	public const DATA_FLAG_ANGRY = 25;
	public const DATA_FLAG_INTERESTED = 26;
	public const DATA_FLAG_CHARGED = 27;
	public const DATA_FLAG_TAMED = 28;
	public const DATA_FLAG_LEASHED = 29;
	public const DATA_FLAG_SHEARED = 30;
	public const DATA_FLAG_GLIDING = 31;
	public const DATA_FLAG_ELDER = 32;
	public const DATA_FLAG_MOVING = 33;
	public const DATA_FLAG_BREATHING = 34;
	public const DATA_FLAG_CHESTED = 35;
	public const DATA_FLAG_STACKABLE = 36;
	public const DATA_FLAG_SHOWBASE = 37;
	public const DATA_FLAG_REARING = 38;
	public const DATA_FLAG_VIBRATING = 39;
	public const DATA_FLAG_IDLING = 40;
	public const DATA_FLAG_EVOKER_SPELL = 41;
	public const DATA_FLAG_CHARGE_ATTACK = 42;
	public const DATA_FLAG_WASD_CONTROLLED = 43;
	public const DATA_FLAG_CAN_POWER_JUMP = 44;
	public const DATA_FLAG_LINGER = 45;
	public const DATA_FLAG_HAS_COLLISION = 46;
	public const DATA_FLAG_AFFECTED_BY_GRAVITY = 47;
	public const DATA_FLAG_FIRE_IMMUNE = 48;
	public const DATA_FLAG_DANCING = 49;
	public const DATA_FLAG_ENCHANTED = 50;

}