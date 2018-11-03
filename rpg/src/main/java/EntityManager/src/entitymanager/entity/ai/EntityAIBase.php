<?php

namespace EntityManager\entity\ai;

abstract class EntityAIBase {

	private $mutexBits;

	public abstract function shouldExecute();

	public function continueExecuting() : bool{
		return $this->shouldExecute();
	}

	public function isInterruptible() : bool{
		return true;
	}

	public function startExecuting(){
	}

	public function resetTask(){
	}

	public function updateTask(){
	}

	public function setMutexBits(int $mutexBitsIn){
		$this->mutexBits = $mutexBitsIn;
	}

	public function getMutexBits() : int{
		return $this->mutexBits;
	}
}