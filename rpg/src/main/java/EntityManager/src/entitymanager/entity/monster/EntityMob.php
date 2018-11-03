<?php

namespace EntityManager\entity\monster;

use pocketmine\level\Level;

use EntityManager\Channel;
use EntityManager\entity\Entity;
use EntityManager\entity\EntityLivingBase;
use EntityManager\entity\EntityCreature;
use EntityManager\utils\EntityDamageSource;

abstract class EntityMob extends EntityCreature implements IMob{

	public function __construct(Level $worldIn, Channel $channel){
		parent::__construct($worldIn, $channel);
		$this->experienceValue = 5;
	}

	public function onLivingUpdate() : void{
		//$this->updateArmSwingProgress();
		//$f = $this->getBrightness(1.0);

		//if($f > 0.5){
		//	$this->entityAge += 2;
		//}

		parent::onLivingUpdate();
	}

	public function attackEntityAsMob(Entity $entityIn) : bool{
		$f = 1;//attribute attack damage
		$i = 0;

		if($entityIn instanceof EntityLivingBase){
			//$f += EnchantmentHelper::func_152377_a($this->getHeldItem(), $entityIn->getCreatureAttribute());
			//$i += EnchantmentHelper::getKnockbackModifier($this);
		}

		$flag = $entityIn->attackEntityFrom(new EntityDamageSource($this), $f);

		if($flag){
			if($i > 0){
				$entityIn->addVelocity((-sin($this->rotationYaw * M_PI / 180.0) * $i * 0.5), 0.1, (cos($this->rotationYaw * M_PI / 180.0) * $i * 0.5));
				$this->motionX *= 0.6;
				$this->motionZ *= 0.6;
			}

			//$j = EnchantmentHelper::getFireAspectModifier($this);

			//if($j > 0){
			//	$entityIn->setFire($j * 4);
			//}

			//$this->applyEnchantments($this, $entityIn);
		}

		return $flag;
	}
}