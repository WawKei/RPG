<?php

namespace EntityManager\entity\ai;

class EntityJumpHelper{

	private $entity;
	protected $isJumping = false;

	public function __construct($entityIn){
		$this->entity = $entityIn;
	}

	public function setJumping(){
		$this->isJumping = true;
	}

	public function doJump(){
		$this->entity->setJumping($this->isJumping);
		$this->isJumping = false;
	}
}