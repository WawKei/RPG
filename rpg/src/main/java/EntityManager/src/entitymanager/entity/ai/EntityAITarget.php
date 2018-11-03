<?php

namespace EntityManager\entity\ai;

use EntityManager\entity\EntityCreature;

use pocketmine\entity\Attribute;
use pocketmine\math\Vector3;
use pocketmine\Player;
use EntityManager\entity\EntityPlayer;

abstract class EntityAITarget extends EntityAIBase{

	protected $taskOwner;
	protected $shouldCheckSight;
	private $nearbyOnly;
	private $targetSearchStatus;
	private $targetSearchDelay;
	private $targetUnseenTicks;

	public function __construct(EntityCreature $creature, bool $checkSight, bool $onlyNearby = false){
		$this->taskOwner = $creature;
		$this->shouldCheckSight = $checkSight;
		$this->nearbyOnly = $onlyNearby;
	}

	public function continueExecuting() : bool{
		$entitylivingbase = $this->taskOwner->getAttackTarget();

		if($entitylivingbase == null){
			return false;
		}else if(!$entitylivingbase->isEntityAlive()){
			return false;
		}else{
			/*$team = $this->taskOwner->getTeam();
			$team1 = $entitylivingbase->getTeam();

			if ($team != null && $team1 == $team){
				return false;
			}else{*/
				$d0 = $this->getTargetDistance();

				if($this->taskOwner->getDistanceSqToEntity($entitylivingbase) > $d0 * $d0){
					return false;
				}else{
					if ($this->shouldCheckSight){
						$this->targetUnseenTicks = 0;
					}
				}

				return true;//!($entitylivingbase instanceof EntityPlayer);
			//}
		}
	}

	protected function getTargetDistance() : float{
		return 16;//$this->taskOwner->attributeMap->getAttribute(Attribute::FOLLOW_RANGE)->getValue();
	}

	public function startExecuting(){
		$this->targetSearchStatus = 0;
		$this->targetSearchDelay = 0;
		$this->targetUnseenTicks = 0;
	}

	public function resetTask(){
		$this->taskOwner->setAttackTarget(null);
	}

	public function isSuitableTarget($a1, $a2, $a3 = null, $a4 = null) : bool{
		if($a3 == null){
			$target = $a1;
			$includeInvincibles = $a2;
			if(!$this->isSuitableTarget($this->taskOwner, $target, $includeInvincibles, $this->shouldCheckSight)){
				return false;
			}else if(!$this->taskOwner->isWithinHomeDistanceFromPosition(new Vector3($target->posX, $target->posY, $target->posZ))){
				return false;
			}else{
				if($this->nearbyOnly){
					if (--$this->targetSearchDelay <= 0){
						$this->targetSearchStatus = 0;
					}

					if ($this->targetSearchStatus == 0){
						$this->targetSearchStatus = $this->canEasilyReach($target) ? 1 : 2;
					}

					if ($this->targetSearchStatus == 2){
						return false;
					}
				}

				return true;
			}
		}else{
			$attacker = $a1;
			$target = $a2;
			$includeInvincibles = $a3;
			$checkSight = $a4;
			if($target == null){
				return false;
			}else if($target == $attacker){
				return false;
			}else if(!$target->isEntityAlive()){
				return false;
			}/*else if(!attacker->canAttackClass(get_class($target))){
				return false;
			}*/else{
				/*$team = $attacker->getTeam();
				$team1 = $target->getTeam();

				if ($team != null && $team1 == $team){
					return false;
				}else{*/
					/*if (attacker instanceof Ownable && StringUtils.isNotEmpty(((IEntityOwnable)attacker).getOwnerId())){
						if (target instanceof Ownable && ((IEntityOwnable)attacker).getOwnerId().equals(((IEntityOwnable)target).getOwnerId())){
							return false;
						}

						if (target == ((IEntityOwnable)attacker).getOwner()){
							return false;
						}
					}else */if($target instanceof Player && !$includeInvincibles){
						return false;
					}

					return true;
				//}
			}
		}
	}

	private function canEasilyReach($livingEntity) : bool{
		$this->targetSearchDelay = 10 + rand(0, 4);
		$pathentity = $this->taskOwner->getNavigator()->getPathToEntityLiving($livingEntity);

		if($pathentity == null){
			return false;
		}else{
			$pathpoint = $pathentity->getFinalPathPoint();

			if($pathpoint == null){
				return false;
			}else{
				$i = $pathpoint->xCoord - floor($livingEntity->posX);
				$j = $pathpoint->zCoord - floor($livingEntity->posZ);
				return ($i * $i + $j * $j) <= 2.25;
			}
		}
	}
}