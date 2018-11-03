<?php
namespace EntityManager;

use pocketmine\event\Listener;
use pocketmine\plugin\PluginBase;
use pocketmine\Player;
use pocketmine\Server;
use pocketmine\event\player\PlayerJoinEvent;
use pocketmine\event\player\PlayerInteractEvent;
use pocketmine\event\server\DataPacketReceiveEvent;
use pocketmine\network\mcpe\protocol\InventoryTransactionPacket;

use EntityManager\entity\EntityPlayer;
use EntityManager\entity\monster\EntityZombie;

class EntityManagerMain extends PluginBase implements Listener{

	public $channels = [];

	public $joined = [];

	public static $instance;

	public function onEnable(){
		self::$instance = $this;
		$this->getServer()->getPluginManager()->registerEvents($this, $this);
		$this->createChannel("default");
	}

	public function onPlayerJoin(PlayerJoinEvent $event){
		$player = $event->getPlayer();
		$this->joined[$player->getName()] = null;
		$this->getChannel("default")->joinChannel($player);
	}

	public function createChannel(string $name) : Channel{
		$channel = new Channel($name);
		$this->channels[$name] = $channel;

		return $channel;
	}

	public function getChannel(string $name) : ?Channel{
		if(empty($this->channels[$name])) return null;
		return $this->channels[$name];
	}

	public function setChannel(string $name, Channel $channel){
		$this->channels[$name] = $channel;
	}

	public function getJoinedChannel(Player $player) : ?Channel{
		return $this->joined[$player->getName()];
	}

	public function onRecievePacket(DataPacketReceiveEvent $event){
		$player = $event->getPlayer();
		$packet = $event->getPacket();
		if($packet instanceof InventoryTransactionPacket){
			switch($packet->transactionType){
				case InventoryTransactionPacket::TYPE_USE_ITEM_ON_ENTITY:
					$eid = $packet->trData->entityRuntimeId;
					$type = $packet->trData->actionType;
					$channel = $this->getJoinedChannel($player);

					switch($type){
						case InventoryTransactionPacket::USE_ITEM_ON_ENTITY_ACTION_INTERACT:
							if($channel !== null){
								$channel->onInteract($player, $eid);
							}
							break;
						case InventoryTransactionPacket::USE_ITEM_ON_ENTITY_ACTION_ATTACK:
							if($channel !== null){
								$channel->onAttack($player, $eid);
							}
							break;
					}
					break;
			}
		}
	}

	public static function getInstance(){
		return self::$instance;
	}
}