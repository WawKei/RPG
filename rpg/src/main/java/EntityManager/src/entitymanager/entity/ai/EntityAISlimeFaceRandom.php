<?php

namespace EntityManager\entity\ai;

class EntityAISlimeFaceRandom extends EntityAIBase{

	private $slime;
	private $randomYaw;
	private $field_179460_c;

	public function __construct($slime){
		$this->slime = $slime;
		$this->setMutexBits(2);
	}

	public function shouldExecute() : bool{
		return $this->slime->getAttackTarget() == null && ($this->slime->onGround || $this->slime->isInWater() || $this->slime->isInOfLava());
	}

	public function updateTask(){
		if(--$this->field_179460_c <= 0){
			$this->field_179460_c = 40 + rand(0, 59);
			$this->randomYaw = rand(0, 359);
                }

		$this->slime->getMoveHelper()->func_179920_a($this->randomYaw, false);
            }
}