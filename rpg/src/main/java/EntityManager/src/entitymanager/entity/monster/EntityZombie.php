<?php

namespace EntityManager\entity\monster;

use pocketmine\level\Level;

use EntityManager\Channel;

use pocketmine\entity\Attribute;
use pocketmine\entity\AttributeMap;

use EntityManager\entity\ai\EntityAISwimming;
use EntityManager\entity\ai\EntityAIAttackOnCollide;
use EntityManager\entity\ai\EntityAIMoveTowardsRestriction;
use EntityManager\entity\ai\EntityAIMoveTowardsTarget;
use EntityManager\entity\ai\EntityAIWander;
use EntityManager\entity\ai\EntityAIWatchClosest;
use EntityManager\entity\ai\EntityAILookIdle;
use EntityManager\entity\ai\EntityAIHurtByTarget;
use EntityManager\entity\ai\EntityAINearestAttackableTarget;

class EntityZombie extends EntityMob{

	public const NETWORK_ID = 32;

	private $breakDoor;
	private $conversionTime;
	private $isBreakDoorsTaskSet = false;
	private $zombieWidth = -1.0;
	private $zombieHeight;

	public function __construct(Level $worldIn, Channel $channel){
		parent::__construct($worldIn, $channel);
		$this->getNavigator()->setBreakDoors(true);
		$this->tasks->addTask(0, new EntityAISwimming($this));
		$this->tasks->addTask(2, new EntityAIAttackOnCollide($this, "EntityManager\\entity\\EntityPlayer", 1.0, true));
		$this->tasks->addTask(5, new EntityAIMoveTowardsRestriction($this, 1.0));
		$this->tasks->addTask(7, new EntityAIWander($this, 1.0));
		$this->tasks->addTask(8, new EntityAIWatchClosest($this, "EntityManager\\entity\\EntityPlayer", 8.0));
		$this->tasks->addTask(8, new EntityAILookIdle($this));
		$this->applyEntityAI();
		$this->setSize(0.6, 1.95);
	}

	protected function applyEntityAI() : void{
		//$this->tasks->addTask(4, new EntityAIAttackOnCollide($this, "EntityManager\\entity\\passive\\EntityVillager", 1.0, true));
		//$this->tasks->addTask(4, new EntityAIAttackOnCollide($this, "EntityManager\\entity\\passive\\EntityIronGolem", 1.0, true));
		//$this->tasks->addTask(6, new EntityAIMoveThroughVillage($this, 1.0, false));
		//$this->targetTasks->addTask(1, new EntityAIHurtByTarget($this, true, ["EntityManager\\entity\\monster\\EntityPigZombie"]));
		$this->targetTasks->addTask(2, new EntityAINearestAttackableTarget($this, "EntityManager\\entity\\EntityPlayer", true));
		//$this->targetTasks->addTask(2, new EntityAINearestAttackableTarget($this, "EntityManager\\entity\\passive\\EntityVillager", false));
		//$this->targetTasks->addTask(2, new EntityAINearestAttackableTarget($this, "EntityManager\\entity\\passive\\EntityIronGolem", true));
	}

	protected function applyEntityAttributes() : void{
		parent::applyEntityAttributes();
		$this->attributeMap->addAttribute(Attribute::getAttribute(Attribute::MOVEMENT_SPEED));
		$this->attributeMap->getAttribute(Attribute::MOVEMENT_SPEED)->setValue(0.23000000417232513);
		$this->attributeMap->addAttribute(Attribute::getAttribute(Attribute::FOLLOW_RANGE));
		$this->attributeMap->getAttribute(Attribute::FOLLOW_RANGE)->setValue(35.0);
	}

	protected function entityInit() : void{
		parent::entityInit();
	}
}