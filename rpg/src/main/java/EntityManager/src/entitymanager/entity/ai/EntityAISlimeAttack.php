<?php

namespace EntityManager\entity\ai;

use pocketmine\Player;

class EntityAISlimeAttack extends EntityAIBase{

	private $slime;
	private $field_179465_b;

	public function __construct($slime){
		$this->slime = $slime;
		$this->setMutexBits(2);
	}

	public function shouldExecute() : bool{
		$entitylivingbase = $this->slime->getAttackTarget();
		return $entitylivingbase == null ? false : (!$entitylivingbase->isAlive() ? false : !($entitylivingbase instanceof Player) || !$entitylivingbase->isCreative());
	}

	public function startExecuting(){
		$this->field_179465_b = 300;
		parent::startExecuting();
	}

	public function continueExecuting() : bool{
		$entitylivingbase = $this->slime->getAttackTarget();
		return $entitylivingbase == null ? false : (!$entitylivingbase->isAlive() ? false : ($entitylivingbase instanceof Player && $entitylivingbase->isCreative() ? false : --$this->field_179465_b > 0));
	}

	public function updateTask(){
		$this->slime->faceEntity($this->slime->getAttackTarget(), 10.0, 10.0);
		$this->slime.getMoveHelper()->func_179920_a($this->slime->yaw, $this->slime->canDamagePlayer());
	}
}