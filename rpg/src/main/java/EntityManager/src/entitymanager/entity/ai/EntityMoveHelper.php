<?php

namespace EntityManager\entity\ai;

use pocketmine\entity\Attribute;
use pocketmine\math\Vector3;
use pocketmine\block\Block;

class EntityMoveHelper{

	protected $entity;
	protected $posX;
	protected $posY;
	protected $posZ;
	protected $speed;
	public $update = false;

	public function __construct($entitylivingIn){
		$this->entity = $entitylivingIn;
		$this->posX = $entitylivingIn->posX;
		$this->posY = $entitylivingIn->posY;
		$this->posZ = $entitylivingIn->posZ;
		$this->update = false;
	}

	public function isUpdating() : bool{
		return $this->update;
	}

	public function getSpeed() : float{
		return $this->speed;
	}

	public function setMoveTo(float $x, float $y, float $z, float $speedIn){
		$this->posX = $x;
		$this->posY = $y + 1;
		$this->posZ = $z;
		$this->speed = $speedIn;
		$this->update = true;
	}

	public function onUpdateMoveHelper(){
		$this->entity->setMoveForward(0.0);

		if($this->update){
			$this->update = false;
			$i = floor($this->entity->getEntityBoundingBox()->minY + 0.5);
			$d0 = $this->posX - $this->entity->posX;
			$d1 = $this->posZ - $this->entity->posZ;
			$d2 = $this->posY - $i;
			$d3 = $d0 * $d0 + $d2 * $d2 + $d1 * $d1;

			if($d3 >= 2.500000277905201E-7){
				$f = (atan2($d1, $d0) * 180.0 / M_PI) - 90.0;
				$this->entity->rotationYaw = $this->limitAngle($this->entity->rotationYaw, $f, 30.0);
				$this->entity->setAIMoveSpeed($this->speed * $this->entity->getAttributeMap()->getAttribute(Attribute::MOVEMENT_SPEED)->getValue() * 2);

				if ($d2 > 0.0 && $d0 * $d0 + $d1 * $d1 < 1.0){
					$this->entity->getJumpHelper()->setJumping();
				}
			}
		}
	}

	public function wrapAngleTo180(float $value) : float{
		$value = $value % 360.0;

		if ($value >= 180.0){
			$value -= 360.0;
		}

		if ($value < -180.0){
			$value += 360.0;
		}

		return $value;
	}

	protected function limitAngle(float $p_75639_1_, float $p_75639_2_, float $p_75639_3_) : float{
		$f = self::wrapAngleTo180($p_75639_2_ - $p_75639_1_);

		if ($f > $p_75639_3_){
			$f = $p_75639_3_;
		}

		if ($f < -$p_75639_3_){
			$f = -$p_75639_3_;
		}

		$f1 = $p_75639_1_ + $f;

		if ($f1 < 0.0){
			$f1 += 360.0;
		}else if ($f1 > 360.0){
			$f1 -= 360.0;
		}

		return $f1;
	}

	public function getX() : float{
		return $this->posX;
	}

	public function getY() : float{
		return $this->posY;
	}

	public function getZ() : float{
		return $this->posZ;
	}
}