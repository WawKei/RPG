<?php

namespace EntityManager\entity\ai;

class EntityAISlimeFloat extends EntityAIBase{

	private $slime;

	public function __construct($slime){
		$this->slime = $slime;
		$this->setMutexBits(5);
		$slime->getNavigator()->setCanSwim(true);
	}

	public function shouldExecute() : bool{
		return $this->slime->isInsideOfWater() || $this->slime->isInsideOfLava();
	}

	public function updateTask(){
		if ((rand(0, 10) / 10) < 0.8){
			$this->slime->getJumpHelper()->setJumping();
		}

		$this->slime->getMoveHelper()->setSpeed(1.2);
	}
}