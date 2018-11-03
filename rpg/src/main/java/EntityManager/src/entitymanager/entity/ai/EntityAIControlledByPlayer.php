<?php

namespace EntityManager\entity\ai;

use pocketmine\block\Block;
use pocketmine\block\Stair;
use pocketmine\block\Slab;
use pocketmine\math\Vector3;

use EntityManager\utils\MathHelper;
use EntityManager\entity\EntityPlayer;
use EntityManager\world\pathfinding\WalkNodeProcessor;

class EntityAIControlledByPlayer extends EntityAIBase{

	private $thisEntity;
	private $maxSpeed;
	private $currentSpeed = 1;
	private $speedBoosted = false;
	private $speedBoostTime = 0;
	private $maxSpeedBoostTime = 0;

	public function __construct($entitylivingIn, $maxspeed){
		$this->thisEntity = $entitylivingIn;
		$this->maxSpeed = $maxspeed;
		$this->setMutexBits(7);
	}

	public function startExecuting(){
		$this->currentSpeed = 0.0;
	}

	public function resetTask(){
		$this->speedBoosted = false;
		$this->currentSpeed = 0.0;
	}

	public function shouldExecute() : bool{
		return $this->thisEntity->isEntityAlive() && $this->thisEntity->riddenByEntity !== null && $this->thisEntity->riddenByEntity instanceof EntityPlayer && ($this->speedBoosted || $this->thisEntity->canBeSteered());
	}

	public function updateTask(){
		$entityplayer = $this->thisEntity->riddenByEntity;
		$entitycreature = $this->thisEntity;
		$f = MathHelper::wrapAngleTo180($entityplayer->rotationYaw - $entitycreature->rotationYaw) * 0.5;

		if($f > 5.0){
			$f = 5.0;
		}

		if($f < -5.0){
			$f = -5.0;
		}

		$this->thisEntity->rotationYaw = MathHelper::wrapAngleTo180($this->thisEntity->rotationYaw + $f);

		if($this->currentSpeed < $this->maxSpeed){
			$this->currentSpeed += ($this->maxSpeed - $this->currentSpeed) * 0.01;
		}

		if($this->currentSpeed > $this->maxSpeed){
			$this->currentSpeed = $this->maxSpeed;
		}

		$i = floor($this->thisEntity->posX);
		$j = floor($this->thisEntity->posY);
		$k = floor($this->thisEntity->posZ);
		$f1 = $this->currentSpeed;

		if($this->speedBoosted){
			if($this->speedBoostTime++ > $this->maxSpeedBoostTime){
				$this->speedBoosted = false;
			}

			$f1 += $f1 * 1.15 * MathHelper::sin((float)$this->speedBoostTime / (float)$this->maxSpeedBoostTime * (float)M_PI);
		}

		$f2 = 0.91;

		if($this->thisEntity->onGround){
			//$f2 = this.thisEntity.worldObj.getBlockState(new BlockPos(MathHelper.floor_float((float)i), MathHelper.floor_float((float)j) - 1, MathHelper.floor_float((float)k))).getBlock().slipperiness * 0.91F;
		}

		$f3 = 0.16277136 / ($f2 * $f2 * $f2);
		$f4 = sin($entitycreature->rotationYaw * (float)M_PI / 180.0);
		$f5 = cos($entitycreature->rotationYaw * (float)M_PI / 180.0);
		$f6 = $entitycreature->getAIMoveSpeed() * $f3;
		$f7 = max($f1, 1.0);
		$f7 = $f6 / $f7;
		$f8 = $f1 * $f7;
		$f9 = -($f8 * $f4);
		$f10 = $f8 * $f5;

		if(abs($f9) > abs($f10)){
			if($f9 < 0.0){
				$f9 -= $this->thisEntity->width / 2.0;
			}

			if($f9 > 0.0){
				$f9 += $this->thisEntity->width / 2.0;
			}

			$f10 = 0.0;
		}else{
			$f9 = 0.0;

			if($f10 < 0.0){
				$f10 -= $this->thisEntity->width / 2.0;
			}

			if($f10 > 0.0){
				$f10 += $this->thisEntity->width / 2.0;
			}
		}

		$l = floor($this->thisEntity->posX + $f9);
		$i1 = floor($this->thisEntity->posZ + $f10);
		$j1 = floor($this->thisEntity->width + 1.0);
		$k1 = floor($this->thisEntity->height + $entityplayer->height + 1.0);
		$l1 = floor($this->thisEntity->width + 1.0);

		if($i != $l || $k != $i1){
			$block = $this->thisEntity->worldObj->getBlock(new Vector3($i, $j, $k));
			$flag = !$this->isStairOrSlab($block) && ($block->getId() != Block::AIR || !$this->isStairOrSlab($this->thisEntity->worldObj->getBlock(new Vector3($i, $j - 1, $k))));

			if($flag && 0 == WalkNodeProcessor::func_176170_a($this->thisEntity->worldObj, $this->thisEntity, $l, $j, $i1, $j1, $k1, $l1, false, false, true) && 1 == WalkNodeProcessor::func_176170_a($this->thisEntity->worldObj, $this->thisEntity, $i, $j + 1, $k, $j1, $k1, $l1, false, false, true) && 1 == WalkNodeProcessor::func_176170_a($this->thisEntity->worldObj, $this->thisEntity, $l, $j + 1, $i1, $j1, $k1, $l1, false, false, true)){
				$entitycreature->getJumpHelper()->setJumping();
			}
		}

		$this->thisEntity->moveEntityWithHeading(0.0, $f1);
	}

	private function isStairOrSlab(Block $blockIn) : bool{
		return $blockIn instanceof Stair || $blockIn instanceof Slab;
	}

	public function isSpeedBoosted() : bool{
		return $this->speedBoosted;
	}

	public function boostSpeed() : void{
		$this->speedBoosted = true;
		$this->speedBoostTime = 0;
		$this->maxSpeedBoostTime = rand(0, 841) + 140;
	}

	public function isControlledByPlayer() : bool{
		return !$this->isSpeedBoosted() && $this->currentSpeed > $this->maxSpeed * 0.3;
	}
}