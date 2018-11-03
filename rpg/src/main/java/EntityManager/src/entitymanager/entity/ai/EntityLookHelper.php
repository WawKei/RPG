<?php

namespace EntityManager\entity\ai;

use EntityManager\entity\EntityLiving;

class EntityLookHelper{

	private $entity;
	private $deltaLookYaw;
	private $deltaLookPitch;
	private $isLooking;
	private $posX;
	private $posY;
	private $posZ;

	public function __construct($entitylivingIn){
		$this->entity = $entitylivingIn;
	}

	public function setLookPositionWithEntity($entityIn, $deltaYaw, $deltaPitch){
		if($entityIn == null){
			$this->isLooking = false;
			return;
		}
		$this->posX = $entityIn->posX;

		if($entityIn instanceof EntityLiving){
			$this->posY = $entityIn->posY + $entityIn->getEyeHeight();
		}else{
			$this->posY = ($entityIn->getEntityBoundingBox()->minY + $entityIn->getEntityBoundingBox()->maxY) / 2.0;
		}

		$this->posZ = $entityIn->posZ;
		$this->deltaLookYaw = $deltaYaw;
		$this->deltaLookPitch = $deltaPitch;
		$this->isLooking = true;
	}

	public function setLookPosition(float $x, float $y, float $z, float $deltaYaw, float $deltaPitch){
		$this->posX = $x;
		$this->posY = $y;
		$this->posZ = $z;
		$this->deltaLookYaw = $deltaYaw;
		$this->deltaLookPitch = $deltaPitch;
		$this->isLooking = true;
	}

	public function onUpdateLook(){
		$this->entity->pitch = 0.0;

		if($this->isLooking){
			$this->isLooking = false;
			$d0 = $this->posX - $this->entity->posX;
			$d1 = $this->posY - ($this->entity->posY + $this->entity->getEyeHeight());
			$d2 = $this->posZ - $this->entity->posZ;
			$d3 = sqrt($d0 * $d0 + $d2 * $d2);
			$f = (atan2($d2, $d0) * 180.0 / M_PI) - 90.0;
			$f1 = -(atan2($d1, $d3) * 180.0 / M_PI);
			$this->entity->rotationPitch = $this->updateRotation($this->entity->rotationPitch, $f1, $this->deltaLookPitch);
			$this->entity->rotationYawHead = $this->updateRotation($this->entity->rotationYawHead, $f, $this->deltaLookYaw);
		}else{
			$this->entity->rotationYawHead = $this->updateRotation($this->entity->rotationYawHead, $this->entity->renderYawOffset, 10.0);//
		}

		$f2 = self::wrapAngleTo180($this->entity->rotationYawHead - $this->entity->renderYawOffset);

		if(!$this->entity->getNavigator()->noPath()){
			if($f2 < -75.0){
				$this->entity->rotationYawHead = $this->entity->renderYawOffset - 75.0;
			}

			if($f2 > 75.0){
				$this->entity->rotationYawHead = $this->entity->renderYawOffset + 75.0;
			}
		}
	}

	public function wrapAngleTo180(float $value) : float{
		$value = $value % 360;

		if ($value >= 180.0){
			$value -= 360;
		}

		if ($value < -180.0){
			$value += 360;
		}

		return $value;
	}

	private function updateRotation($p_75652_1_, $p_75652_2_, $p_75652_3_) : float{
		$f = self::wrapAngleTo180($p_75652_2_ - $p_75652_1_);

		if ($f > $p_75652_3_){
			$f = $p_75652_3_;
		}

		if ($f < -$p_75652_3_){
			$f = -$p_75652_3_;
		}

		return $p_75652_1_ + $f;
	}

	public function getIsLooking() : bool{
		return $this->isLooking;
	}

	public function getLookPosX() : float{
		return $this->posX;
	}

	public function getLookPosY() : float{
		return $this->posY;
	}

	public function getLookPosZ() : float{
		return $this->posZ;
	}
}