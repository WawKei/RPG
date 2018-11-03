<?php
namespace EntityManager;

use pocketmine\Player;
use pocketmine\Server;
use pocketmine\block\Slab;
use pocketmine\block\Stair;
use pocketmine\block\Hopper;
use pocketmine\block\SnowLayer;

use pocketmine\math\AxisAlignedBB;
use pocketmine\math\Vector3;
use pocketmine\level\Level;

use EntityManager\entity\Entity;
use EntityManager\entity\EntityLiving;
use EntityManager\entity\EntityPlayer;

use EntityManager\utils\MathHelper;
use EntityManager\utils\EntityDamageSource;

class Channel{

	private $name;

	private $players = [];

	private $entityPlayers = [];

	private $entities = [];

	public function __construct(string $name){
		$this->name = $name;
		Server::getInstance()->getScheduler()->scheduleRepeatingTask(new CallbackTask([$this,"update"],[]), 1);
	}

	public function joinChannel(Player $player) : bool{
		if(!$this->spawnEntities($player)) return false;
		$this->players[$player->getName()] = $player;

		EntityManagerMain::getInstance()->joined[$player->getName()] = $this;

		return true;
	}

	public function quitChannel(Player $player) : bool{
		if(!$this->despawnEntities($player)) return false;
		unset($this->players[$player->getName()]);

		unset(EntityManagerMain::getInstance()->joined[$player->getName()]);

		return true;
	}

	public function getPlayers() : array{
		return $this->players;
	}

	public function getEntityPlayers() : array{
		return $this->entityPlayers;
	}

	public function addEntity(Entity $entity) : bool{
		$this->entities[$entity->getEntityId()] = $entity;

		foreach($this->players as $player){
			$entity->spawnTo($player);
		}

		return true;
	}

	public function removeEntity(Entity $entity) : bool{
		unset($this->entities[$entity->getEntityId()]);

		foreach($this->players as $player){
			$entity->despawnFrom($player);
		}

		return true;
	}

	public function getEntities() : array{
		return $this->entities;
	}

	public function addEntityPlayer(EntityPlayer $ep){
		$this->entityPlayers[$ep->getPlayer()->getName()] = $ep;
		//$this->entities[$ep->getPlayer()->getId()] = $ep;
	}

	public function getEntityPlayer(Player $player){
		return $this->entityPlayers[$player->getName()];
	}

	public function removeEntityPlayer(Player $player){
		unset($this->entityPlayers[$player->getName()]);
		//unset($this->entities[$player->getId()]);
	}

	private function spawnEntities(Player $player) : bool{
		foreach($this->entities as $entity){
			$entity->spawnTo($player);
		}
		$ep = new EntityPlayer($player->getLevel(), $this, $player);
		$this->addEntity($ep);
		$this->addEntityPlayer($ep);

		return true;
	}

	private function despawnEntities(Player $player) : bool{
		foreach($this->entities as $entity){
			$entity->despawnFrom($player);
		}
		$this->removeEntityPlayer($player);

		return true;
	}

	public function getCollidingBoundingBoxes(Entity $entity, AxisAlignedBB $bb) : array{
		$list = [];
		$i = MathHelper::floor_float($bb->minX);
		$j = MathHelper::floor_float($bb->maxX + 1.0);
		$k = MathHelper::floor_float($bb->minY);
		$l = MathHelper::floor_float($bb->maxY + 2.0);
		$i1 = MathHelper::floor_float($bb->minZ);
		$j1 = MathHelper::floor_float($bb->maxZ + 1.0);
		$vec3 = new Vector3(0.0, 0.0, 0.0);

		for($k1 = $i;$k1 < $j;++$k1){
			for($l1 = $k;$l1 < $l;++$l1){
				for($i2 = $i1;$i2 < $j1;++$i2){
					$vec3->setComponents($k1, $l1, $i2);
					$block = $entity->worldObj->getBlock($vec3);
					$bbb = $block->getBoundingBox();

					if($bbb !== null && $bb->intersectsWith($bbb)){
						$list[] = $bbb;
					}
				}
			}
		}

		foreach($this->getEntities() as $ent){
			if($ent !== $entity and $ent->getEntityBoundingBox()->intersectsWith($bb)){
				$list[] = $ent->getEntityBoundingBox();
			}
		}

		return $list;
	}

	public function getEntitiesWithinAABB(Entity $entity, AxisAlignedBB $bb) : array{
		$nearby = [];

		//if($entity->canBeCollidedWith){
			foreach($this->getEntities() as $ent){
				if($ent !== $entity and $ent->getEntityBoundingBox()->intersectsWith($bb)){
					$nearby[] = $ent;
				}
			}
		//}

		return $nearby;
	}

	public function handleMaterialAcceleration(AxisAlignedBB $bb, array $ids, Entity $entityIn, Level $level) : bool{
		$i = MathHelper::floor_float($bb->minX);
		$j = MathHelper::floor_float($bb->maxX + 1.0);
		$k = MathHelper::floor_float($bb->minY);
		$l = MathHelper::floor_float($bb->maxY + 1.0);
		$i1 = MathHelper::floor_float($bb->minZ);
		$j1 = MathHelper::floor_float($bb->maxZ + 1.0);

		$flag = false;
		$vec3 = new Vector3(0.0, 0.0, 0.0);
		$vec32 = new Vector3(0.0, 0.0, 0.0);

		for($k1 = $i;$k1 < $j;++$k1){
			for($l1 = $k;$l1 < $l;++$l1){
				for($i2 = $i1;$i2 < $j1;++$i2){
					$vec32->setComponents($k1, $l1, $i2);
					$block = $level->getBlock($vec32);

					if(in_array($block->getId(), $ids)){
						$d0 = ($l1 + 1) - 1;//liqidPercent

						if($l >= $d0){
							$flag = true;
							//$vec3 = $block->modifyAcceleration($this, blockpos$mutableblockpos, $entityIn, $vec3);
						}
					}
				}
			}
		}

		if($vec3->length() > 0.0 && $entityIn->isPushedByWater()){
			$vec3 = $vec3->normalize();
			$d1 = 0.014;
			$entityIn->motionX += $vec3->x * $d1;
			$entityIn->motionY += $vec3->y * $d1;
			$entityIn->motionZ += $vec3->z * $d1;
		}

		return $flag;
	}

	public function onInteract(Player $player, $eid){
		if(empty($this->entities[$eid])) return;
		$this->entities[$eid]->interactFirst($this->getEntityPlayer($player));
	}

	public function onAttack(Player $player, $eid){
		if(empty($this->entities[$eid])) return;
		$damage = 1;
		if($player->getGamemode() === 1) $damage = 999;
		$this->entities[$eid]->attackEntityFrom(new EntityDamageSource($this->getEntityPlayer($player)), $damage);
	}

	public function update(){
		foreach($this->getEntities() as $ent){
			if(!$ent->isEntityAlive()){
				$this->removeEntity($ent);
				continue;
			}
			$ent->onUpdate();
		}
	}

	public static function doesBlockHaveSolidTopSurface($blockAccess, Vector3 $pos){
		$block = $blockAccess->getBlock($pos);
		return !$block->isTransparent() && $block->isSolid() ? true : ($block instanceof Stair ? ($block->getDamage() & 0x04) === 0 : ($block instanceof Slab ? ($block->getDamage() & 0x08) > 0 : ($block instanceof Hopper ? true : ($block instanceof SnowLayer ? $block->getDamage() == 7 : false))));
	}
}