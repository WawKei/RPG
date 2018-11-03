<?php

namespace EntityManager\entity\ai;

use pocketmine\level\Level;
use pocketmine\block\Block;
use pocketmine\math\Vector3;

use EntityManager\entity\EntityPet;

use EntityManager\Channel;
use EntityManager\entity\pet\EntityPlayer;
use EntityManager\entity\ai\EntityAIBase;

class EntityAIFollowOwner extends EntityAIBase{

	private $thePet;
	private $theOwner;
	private $theWorld;
	private $followSpeed;
	private $petPathfinder;
	private $field_75343_h;
	public $maxDist;
	public $minDist;
	private $field_75344_i;

	public function __construct(EntityPet $thePetIn, float $followSpeedIn, float $minDistIn, float $maxDistIn){
		$this->thePet = $thePetIn;
		$this->theWorld = $thePetIn->worldObj;
		$this->followSpeed = $followSpeedIn;
		$this->petPathfinder = $thePetIn->getNavigator();
		$this->minDist = $minDistIn;
		$this->maxDist = $maxDistIn;
		$this->setMutexBits(3);
	}

	public function shouldExecute() : bool{
		$entitylivingbase = $this->thePet->getOwner();

		if($entitylivingbase == null){
			return false;
		}else if($entitylivingbase instanceof EntityPlayer && $entitylivingbase->getPlayer()->getGamemode() == 3){
			return false;
		}else if($this->thePet->isSitting()){
			return false;
		}else if($this->thePet->getDistanceSqToEntity($entitylivingbase) < ($this->minDist * $this->minDist)){
			return false;
		}else{
			$this->theOwner = $entitylivingbase;
			return true;
		}
	}

	public function continueExecuting() : bool{
		return !$this->petPathfinder->noPath() && $this->thePet->getDistanceSqToEntity($this->theOwner) > ($this->maxDist * $this->maxDist) && !$this->thePet->isSitting();
	}

	public function startExecuting(){
		$this->field_75343_h = 0;
		$this->field_75344_i = $this->thePet->getNavigator()->getAvoidsWater();
		$this->thePet->getNavigator()->setAvoidsWater(false);
	}

	public function resetTask(){
		$this->theOwner = null;
		$this->petPathfinder->clearPathEntity();
		$this->thePet->getNavigator()->setAvoidsWater(true);
	}

	private function func_181065_a(Vector3 $p_181065_1_) : bool{
		$block = $this->theWorld->getBlock($p_181065_1_);
		return $block->getId() == Block::AIR ? true : !$block->isSolid();
	}

	public function updateTask(){
		$this->thePet->getLookHelper()->setLookPositionWithEntity($this->theOwner, 15.0, (float)$this->thePet->getVerticalFaceSpeed());

		if(!$this->thePet->isSitting()){
			if(--$this->field_75343_h <= 0){
				$this->field_75343_h = 10;
				if($this->theOwner == null) return;
				if(!$this->petPathfinder->tryMoveToEntityLiving($this->theOwner, $this->followSpeed)){
					if(!$this->thePet->getLeashed()){
						if($this->thePet->getDistanceSqToEntity($this->theOwner) >= 144.0){
							$i = floor($this->theOwner->posX) - 2;
							$j = floor($this->theOwner->posZ) - 2;
							$k = floor($this->theOwner->getEntityBoundingBox()->minY);

							for($l = 0;$l <= 4;++$l){
								for($i1 = 0;$i1 <= 4;++$i1){
									if(($l < 1 || $i1 < 1 || $l > 3 || $i1 > 3) && Channel::doesBlockHaveSolidTopSurface($this->theWorld, new Vector3($i + $l, $k - 1, $j + $i1)) && $this->func_181065_a(new Vector3($i + $l, $k, $j + $i1)) && $this->func_181065_a(new Vector3($i + $l, $k + 1, $j + $i1))){
										$this->thePet->setLocationAndAngles(((float)($i + $l) + 0.5), $k, ((float)($j + $i1) + 0.5), $this->thePet->rotationYaw, $this->thePet->rotationPitch);
										$this->petPathfinder->clearPathEntity();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}