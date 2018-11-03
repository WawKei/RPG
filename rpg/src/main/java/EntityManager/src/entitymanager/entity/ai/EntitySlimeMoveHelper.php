<?php

namespace EntityManager\entity\ai;

use pocketmine\entity\Attribute;

class EntitySlimeMoveHelper extends EntityMoveHelper{

	private $field_179922_g = 0;
	private $field_179924_h = 0;
	private $slime;
	private $field_179923_j = false;
	protected $posX;
	protected $posY;
	protected $posZ;

	public function __construct($slime){
		parent::__construct($slime);
		$this->slime = $slime;
		$this->posX = $slime->posX;
		$this->posY = $slime->posY;
		$this->posZ = $slime->posZ;
	}

	public function func_179920_a(float $p_179920_1_, bool $p_179920_2_){
		$this->field_179922_g = $p_179920_1_;
		$this->field_179923_j = $p_179920_2_;
	}

	public function setSpeed(float $speedIn){
		$this->speed = $speedIn;
		$this->update = true;
	}

	public function onUpdateMoveHelper() {
		$d0 = $this->posX - $this->entity->posX;
		$d1 = $this->posZ - $this->entity->posZ;
		$f = (atan2($d1, $d0) * 180.0 / M_PI) - 90.0;
		$this->entity->rotationYaw = $this->limitAngle($this->entity->rotationYaw, $f/*$this->field_179922_g*/, 30.0);
		$this->entity->rotationHeadYaw = $this->entity->rotationYaw;
		$this->entity->renderYawOffset = $this->entity->rotationYaw;

		if(!$this->update){
			$this->entity->setMoveForward(0.0);
		}else{
			$this->update = false;

			if($this->entity->onGround){
				$this->entity->setAIMoveSpeed($this->speed * $this->entity->getAttributeMap()->getAttribute(Attribute::MOVEMENT_SPEED)->getValue() * 2);

				if($this->field_179924_h-- <= 0){
					$this->field_179924_h = $this->slime->getJumpDelay();

					if($this->field_179923_j){
						$this->field_179924_h /= 3;
					}

					$this->slime->getJumpHelper()->setJumping();
				}else{
					$this->slime->moveStrafing = $this->slime->moveForward = 0.0;
					$this->entity->setAIMoveSpeed(0.0);
				}
			}else{
				$this->entity->setAIMoveSpeed($this->speed * $this->entity->getAttributeMap()->getAttribute(Attribute::MOVEMENT_SPEED)->getValue() * 2);
			}
		}
	}
}