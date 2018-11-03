<?php

namespace EntityManager\entity\ai;

use pocketmine\math\AxisAlignedBB;

use EntityManager\entity\EntityCreature;
use EntityManager\entity\EntityLivingBase;

class EntityAIHurtByTarget extends EntityAITarget{

	private $entityCallsForHelp;
	private $revengeTimerOld;
	private $targetClasses;

	public function __construct(EntityCreature $creatureIn, bool $entityCallsForHelpIn, array $targetClassesIn){
		parent::__construct($creatureIn, false);
		$this->entityCallsForHelp = $entityCallsForHelpIn;
		$this->targetClasses = $targetClassesIn;
		$this->setMutexBits(1);
	}

	public function shouldExecute() : bool{
		$i = $this->taskOwner->getRevengeTimer();
		return $i != $this->revengeTimerOld && $this->isSuitableTarget($this->taskOwner->getAITarget(), false);
	}

	public function startExecuting(){
		$this->taskOwner->setAttackTarget($this->taskOwner->getAITarget());
		$this->revengeTimerOld = $this->taskOwner->getRevengeTimer();

		if($this->entityCallsForHelp){
			$d0 = $this->getTargetDistance();

			$bb = new AxisAlignedBB($this->taskOwner->posX, $this->taskOwner->posY, $this->taskOwner->posZ, $this->taskOwner->posX + 1.0, $this->taskOwner->posY + 1.0, $this->taskOwner->posZ + 1.0);
			$l = $this->taskOwner->channel->getEntitiesWithinAABB($this->taskOwner, $bb->expand($d0, 10.0, $d0));
			foreach($l as $entitycreture){
				if($this->taskOwner != $entitycreature && $entitycreature->getAttackTarget() == null/* && !$entitycreature->isOnSameTeam($this->taskOwner->getAITarget())*/){
					$flag = false;

					foreach($this->targetClasses as $targetClass){
						if ($targetClass == get_class($entitycreature)){
							$flag = true;
							break;
						}
					}

					if(!$flag){
						$this->setEntityAttackTarget($entitycreature, $this->taskOwner->getAITarget());
					}
				}
			}
		}

		parent::startExecuting();
	}

	protected function setEntityAttackTarget(EntityCreature $creatureIn, EntityLivingBase $entityLivingBaseIn){
		$creatureIn->setAttackTarget($entityLivingBaseIn);
	}
}