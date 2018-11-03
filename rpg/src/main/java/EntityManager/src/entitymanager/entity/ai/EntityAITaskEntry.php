<?php

namespace EntityManager\entity\ai;

class EntityAITaskEntry{

	public $action;
	public $priority;

	public function __construct($priorityIn, $task){
		$this->priority = $priorityIn;
		$this->action = $task;
	}
}