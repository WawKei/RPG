<?php

namespace EntityManager\entity\ai;

class EntityAIPigZombieTargetAggressor extends EntityAINearestAttackableTarget{

	public function __construct($pigzombie){
		parent::__construct($pigzombie, "pocketmine\Player", true);
	}


	public function shouldExecute() : bool{
		return $this->taskOwner->isAngry() && parent::shouldExecute();
	}
}