<?php

namespace EntityManager\entity\ai;

use EntityManager\entity\Entity;
use EntityManager\entity\EntityCreature;

use EntityManager\entity\EntityPlayer;

class EntityAINearestAttackableTarget extends EntityAITarget{

	protected $targetClass;
	private $targetChance;
	protected $targetEntity;

	public function __construct(EntityCreature $creature, string $classTarget, bool $checkSight, int $chance = 10, bool $onlyNearby = false, $targetSelector = null){
		parent::__construct($creature, $checkSight, $onlyNearby);
		$this->targetClass = $classTarget;
		$this->targetChance = $chance;
		$this->setMutexBits(1);
	}

	public function shouldExecute() : bool{
		if ($this->targetChance > 0 && rand(0, $this->targetChance - 1) != 0){
			return false;
		}else{
			$d0 = $this->getTargetDistance();
			$bb = clone $this->taskOwner->getEntityBoundingBox();
			$list = $this->taskOwner->channel->getEntitiesWithinAABB($this->taskOwner, $bb->expand($d0, 4.0, $d0));
			foreach($list as $index => $entity){
				if(get_class($entity) != $this->targetClass){
					unset($list[$index]);
				}
				//if(!$this->isSuitableTarget($entity, false)){
				//	unset($list[$index]);
				//}
			}
			if(count($list) == 0){
				return false;
			}else{
				$target = $this->getNearestAttackableTarget($list);
				if($target instanceof Entity){
					$this->targetEntity = $target;
					return true;
				}
				return false;
			}
		}
	}

	public function startExecuting(){
		$this->taskOwner->setAttackTarget($this->targetEntity);
		parent::startExecuting();
	}

	public function getNearestAttackableTarget($list){
		$result = null;
		$distance = null;
		foreach ($list as $entity){
			$d = $entity->getDistanceSqToEntity($this->taskOwner);
			if($distance == null || $distance > $d){
				$distance = $d;
				$result = $entity;
			}
		}
		return $result;
	}
}