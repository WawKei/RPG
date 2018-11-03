<?php

namespace EntityManager\entity\ai;

class EntityAIPanic extends EntityAIBase{

	private $theEntityCreature;
	protected $speed;
	private $randPosX;
	private $randPosY;
	private $randPosZ;

	public function __construct($creature, float $speedIn){
		$this->theEntityCreature = $creature;
		$this->speed = $speedIn;
		$this->setMutexBits(1);
	}

	public function shouldExecute() : bool{
		if ($this->theEntityCreature->getAITarget() == null && !$this->theEntityCreature->isOnFire()){
			return false;
		}else{
			$vec3 = RandomPositionGenerator::findRandomTarget($this->theEntityCreature, 5, 4);

			if ($vec3 == null){
				return false;
			}else{
				$this->randPosX = $vec3->x;
				$this->randPosY = $vec3->y;
				$this->randPosZ = $vec3->z;
				return true;
			}
		}
	}

	public function startExecuting(){
		$this->theEntityCreature->getNavigator()->tryMoveToXYZ($this->randPosX, $this->randPosY, $this->randPosZ, $this->speed);
	}

	public function continueExecuting() : bool{
		return !$this->theEntityCreature->getNavigator()->noPath();
	}
}