<?php

namespace EntityManager\entity;

use pocketmine\level\Level;
use pocketmine\math\AxisAlignedBB;
use pocketmine\math\Vector3;
use pocketmine\utils\Random;

use pocketmine\entity\Attribute;
use pocketmine\entity\AttributeMap;

use EntityManager\utils\MathHelper;
use EntityManager\entity\ai\EntityAITasks;
use EntityManager\entity\ai\EntityLookHelper;
use EntityManager\entity\ai\EntityMoveHelper;
use EntityManager\entity\ai\EntityJumpHelper;

use EntityManager\world\pathfinding\PathNavigate;
use EntityManager\world\pathfinding\PathNavigateGround;

use pocketmine\network\mcpe\protocol\MoveEntityPacket;

use EntityManager\Channel;

abstract class EntityLiving extends EntityLivingBase{

	private $lookHelper;
	protected $moveHelper;
	protected $jumpHelper;
	private $bodyHelper;
	protected $navigator;
	public $tasks;
	public $targetTasks;
	private $attackTarget = null;
	private $equipment = [];
	private $persistenceRequired;
	private $isLeashed = false;
	private $leashedToEntity;
	private $leashNBTTag;

	public function __construct(Level $worldIn, Channel $channel){
		parent::__construct($worldIn, $channel);
		$this->tasks = new EntityAITasks($worldIn);
		$this->targetTasks = new EntityAITasks($worldIn);
		$this->lookHelper = new EntityLookHelper($this);
		$this->moveHelper = new EntityMoveHelper($this);
		$this->jumpHelper = new EntityJumpHelper($this);
		$this->bodyHelper = new EntityBodyHelper($this);
		$this->navigator = $this->getNewNavigator($worldIn);
	}

	protected function applyEntityAttributes() : void{
		parent::applyEntityAttributes();
		$this->attributeMap->addAttribute(Attribute::getAttribute(Attribute::FOLLOW_RANGE));
		$this->attributeMap->getAttribute(Attribute::FOLLOW_RANGE)->setBaseValue(16.0);
	}

	protected function getNewNavigator(Level $worldIn) : PathNavigate{
		return new PathNavigateGround($this, $worldIn);
	}

	public function getLookHelper() : EntityLookHelper{
		return $this->lookHelper;
	}

	public function getMoveHelper() : EntityMoveHelper{
		return $this->moveHelper;
	}

	public function getJumpHelper() : EntityJumpHelper{
		return $this->jumpHelper;
	}

	public function getNavigator() : PathNavigate{
		return $this->navigator;
	}

	public function getAttackTarget() : ?EntityLivingBase{
		return $this->attackTarget;
	}

	public function setAttackTarget(?EntityLivingBase $entitylivingbaseIn) : void{
		$this->attackTarget = $entitylivingbaseIn;
	}

	//public function canAttackClass(Class <? extends EntityLivingBase > $cls) : bool{
		//return cls != EntityGhast.class;
	//	return true;
	//}


	//protected void entityInit(){
	//	super.entityInit();
	//	this.dataWatcher.addObject(15, Byte.valueOf((byte)0));
	//}

	public function getTalkInterval() : int{
		return 80;
	}

	public function onEntityUpdate() : void{
		parent::onEntityUpdate();

		$pk = new MoveEntityPacket();
		$pk->entityRuntimeId = $this->entityId;
		$pk->position = new Vector3($this->posX, $this->posY, $this->posZ);
		$pk->yaw = $this->rotationYaw;
		$pk->pitch = $this->rotationPitch;
		$pk->headYaw = $this->rotationYawHead;

		foreach($this->hasSpawned as $player){
			$player->dataPacket($pk);
		}
	}

	public function onUpdate() : void{
		parent::onUpdate();

		$this->updateLeashedState();

		$this->updateEntityActionState();
	}

	protected function func_110146_f(float $p_110146_1_, float $p_110146_2_) : float{
		$this->bodyHelper->updateRenderAngles();
		return $p_110146_2_;
	}

	public function setMoveForward(float $p_70657_1_) : void{
		$this->moveForward = $p_70657_1_;
	}

	public function setAIMoveSpeed(float $speedIn) : void{
		parent::setAIMoveSpeed($speedIn);
		$this->setMoveForward($speedIn);
	}

	public function onLivingUpdate() : void{
		parent::onLivingUpdate();
	}

	protected final function updateEntityActionState() : void{
		++$this->entityAge;
		$this->targetTasks->onUpdateTasks();
		$this->tasks->onUpdateTasks();
		$this->navigator->onUpdateNavigation();
		$this->updateAITasks();
		$this->moveHelper->onUpdateMoveHelper();
		$this->lookHelper->onUpdateLook();
		$this->jumpHelper->doJump();
	}

	protected function updateAITasks() : void{
	}

	public function getVerticalFaceSpeed() : int{
		return 40;
	}

	public function faceEntity(Entity $entityIn, float $p_70625_2_, float $p_70625_3_) : void{
		$d0 = $entityIn->posX - $this->posX;
		$d2 = $entityIn->posZ - $this->posZ;

		if($entityIn instanceof EntityLivingBase){
			$entitylivingbase = $entityIn;
			$d1 = $entitylivingbase->posY + (double)$entitylivingbase->getEyeHeight() - ($this->posY + $this->getEyeHeight());
		}else{
			$d1 = ($entityIn->getEntityBoundingBox()->minY + $entityIn->getEntityBoundingBox()->maxY) / 2.0 - ($this->posY + $this->getEyeHeight());
		}

		$d3 = sqrt($d0 * $d0 + $d2 * $d2);
		$f = (float)(atan2($d2, $d0) * 180.0 / M_PI) - 90.0;
		$f1 = (float)(-(atan2($d1, $d3) * 180.0 / M_PI));
		$this->rotationPitch = $this->updateRotation($this->rotationPitch, $f1, $p_70625_3_);
		$this->rotationYaw = $this->updateRotation($this->rotationYaw, $f, $p_70625_2_);
	}

	private function updateRotation(float $p_70663_1_, float $p_70663_2_, float $p_70663_3_) : float{
		$f = MathHelper::wrapAngleTo180($p_70663_2_ - $p_70663_1_);

		if($f > $p_70663_3_){
			$f = $p_70663_3_;
		}

		if($f < -$p_70663_3_){
			$f = -$p_70663_3_;
		}

		return $p_70663_1_ + $f;
	}

	public function getMaxFallHeight() : int{
		if($this->getAttackTarget() == null){
			return 3;
		}else{
			$i = (int)($this->getHealth() - $this->getMaxHealth() * 0.33);
			$i = $i - (3 - 0/* difficult */) * 4;

			if($i < 0){
				$i = 0;
			}

			return $i + 3;
		}
	}

	public function canBeSteered() : bool{
		return false;
	}

	public function enablePersistence() : void{
		$this->persistenceRequired = true;
	}

	public function interactFirst(EntityPlayer $playerIn) : bool{
		//if($this->getLeashed() && $this->getLeashedToEntity() == $playerIn){
		//	$this->clearLeashed(true, !$playerIn->isCreative());
		//	return true;
		//}else{
			/*ItemStack itemstack = playerIn.inventory.getCurrentItem();

			if (itemstack != null && itemstack.getItem() == Items.lead && this.allowLeashing()){
				if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed())
				{
					this.setLeashedToEntity(playerIn, true);
					--itemstack.stackSize;
					return true;
				}

				if (((EntityTameable)this).isOwner(playerIn)){
					this.setLeashedToEntity(playerIn, true);
					--itemstack.stackSize;
					return true;
				}
			}*/

			if($this->interact($playerIn)){
				return true;
			}else{
				return parent::interactFirst($playerIn);
			}
		//}
	}

	protected function interact(EntityPlayer $player) : bool{
		return false;
	}

	protected function updateLeashedState() : void{
		if ($this->leashNBTTag != null){
			$this->recreateLeash();
		}

		if($this->isLeashed){
			if(!$this->isEntityAlive()){
				$this->clearLeashed(true, true);
			}

			if($this->leashedToEntity == null || $this->leashedToEntity->isDead){
				$this->clearLeashed(true, true);
			}
		}
	}

	public function clearLeashed(bool $sendPacket, bool $dropLead) : void{
		if($this->isLeashed){
			$this->isLeashed = false;
			$this->leashedToEntity = null;

			if($dropLead){
				//$this->dropItem(Items.lead, 1);
			}
		}
	}

	public function allowLeashing() : bool{
		return !$this->getLeashed() && !($this instanceof IMob);
	}

	public function getLeashed() : bool{
		return $this->isLeashed;
	}

	public function getLeashedToEntity() : Entity{
		return $this->leashedToEntity;
	}

	public function setLeashedToEntity(Entity $entityIn, bool $sendAttachNotification) : void{
		$this->isLeashed = true;
		$this->leashedToEntity = $entityIn;
	}

	private function recreateLeash() : void{
		if($this->isLeashed && $this->leashNBTTag != null){
			/*if ($this->leashNBTTag.hasKey("UUIDMost", 4) && this.leashNBTTag.hasKey("UUIDLeast", 4)){
				UUID uuid = new UUID(this.leashNBTTag.getLong("UUIDMost"), this.leashNBTTag.getLong("UUIDLeast"));

				for (EntityLivingBase entitylivingbase : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(10.0D, 10.0D, 10.0D)))
				{
					if (entitylivingbase.getUniqueID().equals(uuid))
					{
						this.leashedToEntity = entitylivingbase;
						break;
					}
				}
			}
			else if (this.leashNBTTag.hasKey("X", 99) && this.leashNBTTag.hasKey("Y", 99) && this.leashNBTTag.hasKey("Z", 99))
			{
				BlockPos blockpos = new BlockPos(this.leashNBTTag.getInteger("X"), this.leashNBTTag.getInteger("Y"), this.leashNBTTag.getInteger("Z"));
				EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(this.worldObj, blockpos);

				if (entityleashknot == null)
				{
					entityleashknot = EntityLeashKnot.createKnot(this.worldObj, blockpos);
				}

				this.leashedToEntity = entityleashknot;
			}
			else
			{
				this.clearLeashed(false, true);
			}*/
		}

		$this->leashNBTTag = null;
	}
}