<?php

namespace EntityManager\entity\ai;

use pocketmine\level\Position;
use pocketmine\level\Level;
use pocketmine\math\Vector3;

use pocketmine\block\Transparent;
use pocketmine\block\Stair;

class EntityAIMoveTowardsTarget extends EntityAIBase {

	private $theEntity;
	private $targetEntity;
	private $movePosX;
	private $movePosY;
	private $movePosZ;
	private $speed;
	private $maxTargetDistance;

	public function __construct($creature, float $speedIn, float $targetMaxDistance){
		$this->theEntity = $creature;
		$this->speed = $speedIn;
		$this->maxTargetDistance = $targetMaxDistance;
		$this->setMutexBits(1);
	}

	public function shouldExecute() : bool{
		$this->targetEntity = $this->theEntity->getAttackTarget();

		if($this->targetEntity === null){
			return false;
		}else if($this->targetEntity->getDistanceSqToEntity($this->theEntity) > ($this->maxTargetDistance * $this->maxTargetDistance)){
			return false;
		}else{
			$vec3 = RandomPositionGenerator::findRandomTargetBlockTowards($this->theEntity, 16, 7, new Vector3($this->theEntity->posX, $this->theEntity->posY, $this->theEntity->posZ));

			if ($vec3 == null){
				return false;
			}else{
				$this->movePosX = $vec3->x;
				$this->movePosY = $vec3->y;
				$this->movePosZ = $vec3->z;
				return true;
			}
		}
	}

	public function continueExecuting() : bool{
		return !$this->theEntity->getNavigator()->noPath() && $this->targetEntity->isEntityAlive() && $this->targetEntity->getDistanceSqToEntity($this->theEntity) < ($this->maxTargetDistance * $this->maxTargetDistance);
	}

	public function startExecuting(){
		$this->theEntity->getNavigator()->tryMoveToXYZ($this->movePosX, $this->movePosY, $this->movePosZ, $this->speed);
	}
}