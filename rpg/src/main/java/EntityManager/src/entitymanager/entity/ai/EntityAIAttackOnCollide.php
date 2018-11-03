<?php

namespace EntityManager\entity\ai;

use EntityManager\entity\EntityCreature;
use pocketmine\entity\Attribute;
use pocketmine\math\Vector3;

class EntityAIAttackOnCollide extends EntityAIBase{

	public $worldObj;
	protected $attacker;
	public $attackTick;
	public $speedTowardsTarget;
	public $longMemory;
	public $entityPathEntity;
	public $classTarget;
	private $delayCounter;
	private $targetX = 0;
	private $targetY = 0;
	private $targetZ = 0;

	public function __construct(EntityCreature $creature, string $targetClass, float $speedIn, bool $useLongMemory){
		$this->classTarget = $targetClass;
		$this->attacker = $creature;
		$this->worldObj = $creature->worldObj;
		$this->speedTowardsTarget = $speedIn;
		$this->longMemory = $useLongMemory;
		$this->setMutexBits(3);
	}

	public function shouldExecute() : bool{
		$entitylivingbase = $this->attacker->getAttackTarget();

		if ($entitylivingbase == null){
			return false;
		}else if (!$entitylivingbase->isEntityAlive()){
			return false;
		}else if ($this->classTarget != null && $this->classTarget != get_class($entitylivingbase)){
			return false;
		}else{
			$this->entityPathEntity = $this->attacker->getNavigator()->getPathToEntityLiving($entitylivingbase);
			return $this->entityPathEntity != null;
		}
	}

	public function continueExecuting() : bool{
		$entitylivingbase = $this->attacker->getAttackTarget();
		return $entitylivingbase == null ? false : (!$entitylivingbase->isEntityAlive() ? false : (!$this->longMemory ? !$this->attacker->getNavigator()->noPath() : $this->attacker->isWithinHomeDistanceFromPosition(new Vector3($entitylivingbase->posX, $entitylivingbase->posY, $entitylivingbase->posZ))));
	}

	public function startExecuting(){
		$this->attacker->getNavigator()->setPath($this->entityPathEntity, $this->speedTowardsTarget);
		$this->delayCounter = 0;
	}

	public function resetTask(){
		$this->attacker->getNavigator()->clearPathEntity();
	}

	public function updateTask(){
		$entitylivingbase = $this->attacker->getAttackTarget();
		if($entitylivingbase == null){
			return;
		}
		$this->attacker->getLookHelper()->setLookPositionWithEntity($entitylivingbase, 30.0, 30.0);
		$d0 = $this->attacker->getDistanceSq($entitylivingbase->posX, $entitylivingbase->getEntityBoundingBox()->minY, $entitylivingbase->posZ);
		$d1 = $this->getReachableDistance($entitylivingbase);
		--$this->delayCounter;

		if(($this->longMemory && $this->delayCounter <= 0 && ($this->targetX == 0.0 && $this->targetY == 0.0 && $this->targetZ == 0.0 || $entitylivingbase->getDistanceSq($this->targetX, $this->targetY, $this->targetZ) >= 1.0 || (rand(0, 100) / 100) < 0.05))){
			$this->targetX = $entitylivingbase->posX;
			$this->targetY = $entitylivingbase->getEntityBoundingBox()->minY;
			$this->targetZ = $entitylivingbase->posZ;
			$this->delayCounter = 4 + rand(0, 6);

			if($d0 > 1024.0){
				$this->delayCounter += 10;
			}else if($d0 > 256.0){
				$this->delayCounter += 5;
			}

			if(!$this->attacker->getNavigator()->tryMoveToEntityLiving($entitylivingbase, $this->speedTowardsTarget)){
				$this->delayCounter += 15;
			}
		}

		$this->attackTick = max($this->attackTick - 1, 0);

		if($d0 <= $d1 && $this->attackTick <= 0){
			$this->attackTick = 20;

			$this->attacker->attackEntityAsMob($entitylivingbase);
		}
	}

	protected function getReachableDistance($attackTarget) : float{
		return ($this->attacker->width * 2.0 * $this->attacker->width * 2.0 + $attackTarget->width);
	}
}