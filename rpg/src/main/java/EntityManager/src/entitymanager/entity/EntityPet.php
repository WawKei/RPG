<?php

namespace EntityManager\entity;

use pocketmine\Player;
use pocketmine\level\Level;

use pocketmine\math\Vector3;

use EntityManager\Channel;
use EntityManager\EntityManagerMain;
use EntityManager\utils\DamageSource;

abstract class EntityPet extends EntityLiving{

	public $owner = null;
	public $sitting = false;

	public function __construct(Level $worldIn, Channel $channel, Player $player){
		parent::__construct($worldIn, $channel);
		$this->propertyManager->setString(self::DATA_NAMETAG, "§a".$player->getName()."'s PET");
		$this->setOwner(EntityManagerMain::getInstance()->getJoinedChannel($player)->getEntityPlayer($player));
	}

	public function isSitting() : bool{
		return $this->sitting;
	}

	public function setSitting(bool $sit) {
		$this->sitting = $sit;
		$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SITTING, $sit);
		$this->sendDataAll();
	}

	public function attackEntityFrom(DamageSource $source, float $amount) : bool{
		return true;
	}

	public function getOwner() : ?EntityPlayer{
		return $this->owner;
	}

	public function setOwner(?EntityPlayer $player) : void{
		$this->owner = $player;
	}
}