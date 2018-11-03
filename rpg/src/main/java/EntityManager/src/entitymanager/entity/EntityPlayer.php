<?php

namespace EntityManager\entity;

use pocketmine\Player;

use pocketmine\level\Level;
use pocketmine\math\AxisAlignedBB;
use pocketmine\math\Vector3;
use pocketmine\utils\Random;

use EntityManager\utils\MathHelper;
use EntityManager\entity\ai\EntityAITasks;
use EntityManager\entity\ai\EntityLookHelper;
use EntityManager\entity\ai\EntityMoveHelper;
use EntityManager\entity\ai\EntityJumpHelper;
use EntityManager\entity\ai\EntityBodyHelper;

use EntityManager\world\pathfinding\PathNavigate;
use EntityManager\world\pathfinding\PathNavigateGround;

use EntityManager\utils\DamageSource;

use EntityManager\Channel;

class EntityPlayer extends EntityLivingBase{

	private $player;

	public function __construct(Level $worldIn, Channel $channel, Player $player){
		parent::__construct($worldIn, $channel);
		$this->player = $player;
		$this->entityId = $player->getId();
	}

	public function getPlayer() : Player{
		return $this->player;
	}

	public function isCreative() : bool{
		return $this->player->isCreative();
	}

	protected function applyEntityAttributes() : void{
	}

	public function getTalkInterval() : int{
		return 80;
	}

	public function onEntityUpdate() : void{
	}

	public function onUpdate() : void{
		$this->posX = $this->player->x;
		$this->posY = $this->player->y;
		$this->posZ = $this->player->z;
		$this->setEntityBoundingBox($this->player->getBoundingBox());
		
	}

	public function setMoveForward(float $p_70657_1_) : void{
		$this->moveForward = $p_70657_1_;
	}

	public function setAIMoveSpeed(float $speedIn) : void{
		$this->setMoveForward($speedIn);
	}

	public function onLivingUpdate() : void{
	}

	protected final function updateEntityActionState() : void{
	}

	protected function updateAITasks() : void{
	}

	public function getVerticalFaceSpeed() : int{
		return 40;
	}

	public function faceEntity(Entity $entityIn, float $p_70625_2_, float $p_70625_3_) : void{
	}

	public function canBeSteered() : bool{
		return false;
	}

	public function enablePersistence() : void{
		$this->persistenceRequired = true;
	}

	public function interactFirst(EntityPlayer $playerIn) : bool{
		return false;
	}

	protected function interact(EntityPlayer $player) : bool{
		return false;
	}

	public function attackEntityFrom(DamageSource $source, float $amount) : bool{
		//$this->player->sendMessage($amount."damage");
		return true;
	}

	public function spawnTo(Player $player){
	}

	public function despawnFrom(Player $player, bool $send = true){
	}

	public function despawnFromAll(){
	}
}