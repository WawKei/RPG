<?php

namespace EntityManager\entity\ai;

use EntityManager\entity\EntityLiving;

class EntityAISwimming extends EntityAIBase{

	private $theEntity;

	public function __construct(EntityLiving $entitylivingIn){
		$this->theEntity = $entitylivingIn;
		$this->setMutexBits(4);
		$entitylivingIn->getNavigator()->setCanSwim(true);
	}

	public function shouldExecute() : bool{
		return $this->theEntity->isInWater() || $this->theEntity->isInLava();
	}

	public function updateTask(){
		if(rand(0, 10) / 10 < 0.8){
			$this->theEntity->getJumpHelper()->setJumping();
		}
	}
}