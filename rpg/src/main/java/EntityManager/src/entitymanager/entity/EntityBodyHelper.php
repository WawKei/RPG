<?php

namespace EntityManager\entity;

use pocketmine\math\AxisAlignedBB;
use pocketmine\math\Vector3;
use pocketmine\utils\Random;

use EntityManager\utils\MathHelper;

class EntityBodyHelper{

	private $theLiving;
	private $rotationTickCounter;
	private $prevRenderYawHead;

	public function __construct(EntityLivingBase $p_i1611_1_){
		$this->theLiving = $p_i1611_1_;
	}

	public function updateRenderAngles() : void{
		$d0 = $this->theLiving->posX - $this->theLiving->prevPosX;
		$d1 = $this->theLiving->posZ - $this->theLiving->prevPosZ;

		if($d0 * $d0 + $d1 * $d1 > 2.500000277905201E-7){
			$this->theLiving->renderYawOffset = $this->theLiving->rotationYaw;
			$this->theLiving->rotationYawHead = $this->computeAngleWithBound($this->theLiving->renderYawOffset, $this->theLiving->rotationYawHead, 75.0);
			$this->prevRenderYawHead = $this->theLiving->rotationYawHead;
			$this->rotationTickCounter = 0;
		}else{
			$f = 75.0;

			if(abs($this->theLiving->rotationYawHead - $this->prevRenderYawHead) > 15.0){
				$this->rotationTickCounter = 0;
				$this->prevRenderYawHead = $this->theLiving->rotationYawHead;
			}else{
				++$this->rotationTickCounter;
				$i = 10;

				if($this->rotationTickCounter > 10){
					$f = max(1.0 - (float)($this->rotationTickCounter - 10) / 10.0, 0.0) * 75.0;
				}
			}

			$this->theLiving->renderYawOffset = $this->computeAngleWithBound((float)$this->theLiving->rotationYawHead, (float)$this->theLiving->renderYawOffset, (float)$f);
		}
	}

	private function computeAngleWithBound(float $p_75665_1_, float $p_75665_2_, float $p_75665_3_) : float{
		$f = MathHelper::wrapAngleTo180($p_75665_1_ - $p_75665_2_);

		if($f < -$p_75665_3_){
			$f = -$p_75665_3_;
		}

		if($f >= $p_75665_3_){
			$f = $p_75665_3_;
		}

		return $p_75665_1_ - $f;
	}
}