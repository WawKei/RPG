<?php

namespace EntityManager\entity\ai;

use pocketmine\math\Vector3;

use EntityManager\entity\Entity;

class EntityAIWatchClosest extends EntityAIBase{

	protected $theWatcher;
	protected $closestEntity;
	protected $maxDistanceForPlayer;
	private $lookTime;
	private $chance;
	protected $watchedClass;

	public function __construct($entitylivingIn, string $watchTargetClass, float $maxDistance, float $chanceIn = 0.02){
		$this->theWatcher = $entitylivingIn;
		$this->watchedClass = $watchTargetClass;
		$this->maxDistanceForPlayer = $maxDistance;
		$this->chance = $chanceIn;
		$this->setMutexBits(2);
	}

	public function shouldExecute() : bool{
		if(rand(0, 100) / 100 >= $this->chance){
			return false;
		}else{
			if($this->theWatcher->getAttackTarget() != null){
				$this->closestEntity = $this->theWatcher->getAttackTarget();
			}

			if($this->watchedClass == "EntityManager\entity\EntityPlayer"){
				$distance = $this->maxDistanceForPlayer;
				$target = null;
				foreach($this->theWatcher->channel->getEntityPlayers() as $player){
					$p2e_distance = $player->getDistanceSqToEntity($this->theWatcher);
					if($distance > $p2e_distance and !$player->isCreative()){
						$target = $player;
						$distance = $p2e_distance;
					}
				}
				$this->closestEntity = $target;
			}else{
				$bb = clone $this->theWatcher->getEntityBoundingBox();
				$list = $this->theWatcher->channel->getEntitiesWithinAABB($this->theWatcher, $bb->expand($this->maxDistanceForPlayer, 3.0, $this->maxDistanceForPlayer));
				$distance = $this->maxDistanceForPlayer;
				$target = null;
				foreach($list as $entity){
					if(get_class($entity) != $this->watchedClass) continue;
					$p2e_distance = $entity->getDistanceSqToEntity($this->theWatcher);
					if($distance > $p2e_distance){
						$target = $entity;
						$distance = $p2e_distance;
					}
				}
				$this->closestEntity = $target;
			}

			return $this->closestEntity != null;
		}
	}

	public function continueExecuting() : bool{
		if(!($this->closestEntity instanceof Entity)) return false;
		return !$this->closestEntity->isEntityAlive() ? false : ($this->theWatcher->getDistanceSqToEntity($this->closestEntity) > ($this->maxDistanceForPlayer * $this->maxDistanceForPlayer) ? false : $this->lookTime > 0);
	}

	public function startExecuting(){
		$this->lookTime = 40 + rand(0, 4);
	}

	public function resetTask(){
		$this->closestEntity = null;
	}

	public function updateTask(){
		if($this->closestEntity instanceof Entity) 
		$this->theWatcher->getLookHelper()->setLookPosition($this->closestEntity->posX, $this->closestEntity->posY + $this->closestEntity->getEyeHeight(), $this->closestEntity->posZ, 10.0, 40);
		--$this->lookTime;
	}
}