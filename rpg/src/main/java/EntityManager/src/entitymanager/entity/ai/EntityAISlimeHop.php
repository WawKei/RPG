<?php

namespace EntityManager\entity\ai;

class EntityAISlimeHop extends EntityAIBase{

	private $slime;

	public function __construct($slime){
		$this->slime = $slime;
		$this->setMutexBits(5);
	}

	public function shouldExecute() : bool{
		return true;
	}

	public function updateTask(){
		$this->slime->getMoveHelper()->setSpeed(1.0);
	}
}