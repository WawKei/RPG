<?php

namespace EntityManager\entity\ai;

use pocketmine\block\Block;
use pocketmine\math\Vector3;

class EntityAIEatGrass extends EntityAIBase{

	private $grassEaterEntity;
	private $entityWorld;
	public $eatingGrassTimer;

	public function __construct($grassEaterEntityIn){
		$this->grassEaterEntity = $grassEaterEntityIn;
		$this->entityWorld = $grassEaterEntityIn->worldObj;
		$this->setMutexBits(7);
	}

	public function shouldExecute() : bool{
		if (rand(0, $this->grassEaterEntity->isBaby() ? 50 : 1000) != 0){
			return false;
		}else{
			$blockpos = new Vector3($this->grassEaterEntity->x, $this->grassEaterEntity->y, $this->grassEaterEntity->z);
			return $this->entityWorld->getBlock($blockpos->getSide(Vector3::SIDE_DOWN))->getId() == Block::GRASS;
		}
	}

	public function startExecuting(){
		$this->eatingGrassTimer = 40;
		$this->grassEaterEntity->doEatGrass();
		$this->grassEaterEntity->getNavigator()->clearPathEntity();
	}

	public function resetTask(){
		$this->eatingGrassTimer = 0;
	}

	public function continueExecuting() : bool{
		return $this->eatingGrassTimer > 0;
	}

	public function getEatingGrassTimer() : int{
		return $this->eatingGrassTimer;
	}

	public function updateTask(){
		$this->eatingGrassTimer = max(0, $this->eatingGrassTimer - 1);

		if ($this->eatingGrassTimer == 4){
			$blockpos = new Vector3($this->grassEaterEntity->x, $this->grassEaterEntity->y, $this->grassEaterEntity->z);
			$blockpos1 = $blockpos->getSide(Vector3::SIDE_DOWN);

			if ($this->entityWorld->getBlock($blockpos1)->getId() == Block::GRASS){
				$this->entityWorld->setBlock($blockpos1, Block::get(Block::DIRT));
			}

			$this->grassEaterEntity->eatGrassBonus();
                }
	}
}