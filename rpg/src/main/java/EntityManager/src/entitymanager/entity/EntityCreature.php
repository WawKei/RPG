<?php

namespace EntityManager\entity;

use pocketmine\level\Level;

use pocketmine\math\Vector3;

use EntityManager\Channel;

abstract class EntityCreature extends EntityLiving{

	private $homePosition;
	private $maximumHomeDistance = -1.0;
	private $aiBase;
	private $isMovementAITaskSet;

	public function __construct(Level $worldIn, Channel $channel){
		parent::__construct($worldIn, $channel);
		$this->homePosition = new Vector3(0, 0, 0);
	}

	public function getBlockPathWeight(Vector3 $pos) : float{
		return 0.0;
	}

	public function hasPath() : bool{
		return !$this->navigator->noPath();
	}

	public function isWithinHomeDistanceCurrentPosition() : bool{
		return $this->isWithinHomeDistanceFromPosition(new Vector3($this->posX, $this->posY, $this->posZ));
	}

	public function isWithinHomeDistanceFromPosition(Vector3 $pos) : bool{
		return $this->maximumHomeDistance === -1.0 ? true : $this->homePosition->distanceSquared($pos) < ($this->maximumHomeDistance * $this->maximumHomeDistance);
	}

	public function setHomePosAndDistance(Vector3 $pos, int $distance) : void{
		$this->homePosition = $pos;
		$this->maximumHomeDistance = $distance;
	}

	public function getHomePosition() : Vector3{
		return $this->homePosition;
	}

	public function getMaximumHomeDistance() : float{
		return $this->maximumHomeDistance;
	}

	public function detachHome() : void{
		$this->maximumHomeDistance = -1.0;
	}

	public function hasHome() : bool{
		return $this->maximumHomeDistance != -1.0;
	}

	//TODO leash

	protected function func_142017_o(float $p_142017_1_) : void{
	}
}