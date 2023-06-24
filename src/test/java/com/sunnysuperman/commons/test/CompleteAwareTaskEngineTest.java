package com.sunnysuperman.commons.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.commons.task.CompleteAwareTaskEngine;

class CompleteAwareTaskEngineTest {

	private class MyTask implements Runnable {
		int index;
		Map<Integer, Boolean> completedMap;
		boolean waitForAWhile = true;

		public MyTask(int index, Map<Integer, Boolean> completedMap) {
			super();
			this.index = index;
			this.completedMap = completedMap;
		}

		public MyTask(int index, Map<Integer, Boolean> completedMap, boolean waitForAWhile) {
			super();
			this.index = index;
			this.completedMap = completedMap;
			this.waitForAWhile = waitForAWhile;
		}

		@Override
		public void run() {
			log("task start: " + index);
			if (waitForAWhile) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			completedMap.put(index, Boolean.TRUE);
			log("task end: " + index);
		}

		private void log(String msg) {
			System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
		}

	}

	@Test
	void done() {
		Map<Integer, Boolean> completedMap = new ConcurrentHashMap<Integer, Boolean>();
		CompleteAwareTaskEngine engine = new CompleteAwareTaskEngine(3);
		int tasksNum = 10;
		for (int i = 1; i <= tasksNum; i++) {
			engine.addTask(new MyTask(i, completedMap));
		}
		engine.waitUntilDone();
		assertEquals(tasksNum, completedMap.size());
	}

	@Test
	void doneAndDoMore() {
		Map<Integer, Boolean> completedMap = new ConcurrentHashMap<Integer, Boolean>();
		CompleteAwareTaskEngine engine = new CompleteAwareTaskEngine(8);
		int tasksNum = 10;

		for (int i = 1; i <= tasksNum; i++) {
			engine.addTask(new MyTask(i, completedMap));
		}
		assertTrue(engine.getIncompleteTasksNum() > 0);
		engine.waitUntilDone();
		assertEquals(0, engine.getIncompleteTasksNum());

		for (int i = 1; i <= tasksNum - 1; i++) {
			engine.addTask(new MyTask(tasksNum + i, completedMap));
		}
		engine.waitUntilDone();

		assertEquals(2 * tasksNum - 1, completedMap.size());
	}

	@Test
	void useMoreThreads() {
		Map<Integer, Boolean> completedMap = new ConcurrentHashMap<Integer, Boolean>();
		CompleteAwareTaskEngine engine = new CompleteAwareTaskEngine(30);
		int tasksNum = 10;
		for (int i = 1; i <= tasksNum; i++) {
			engine.addTask(new MyTask(i, completedMap));
		}
		engine.waitUntilDone();
		assertEquals(tasksNum, completedMap.size());
	}

	@Test
	void doNothing() {
		Map<Integer, Boolean> completedMap = new ConcurrentHashMap<Integer, Boolean>();
		CompleteAwareTaskEngine engine = new CompleteAwareTaskEngine(3);
		engine.waitUntilDone();
		assertEquals(0, completedMap.size());
	}

	@Test
	void doQuickly() throws Exception {
		for (int k = 0; k < 100; k++) {
			Map<Integer, Boolean> completedMap = new ConcurrentHashMap<Integer, Boolean>();
			CompleteAwareTaskEngine engine = new CompleteAwareTaskEngine(30);
			int tasksNum = 1;
			for (int i = 1; i <= tasksNum; i++) {
				engine.addTask(new MyTask(i, completedMap, false));
			}
			engine.waitUntilDone();
			assertEquals(tasksNum, completedMap.size());
		}
	}

	@Test
	void waitUntilDoneAndShutdown() throws Exception {
		Map<Integer, Boolean> completedMap = new ConcurrentHashMap<Integer, Boolean>();
		CompleteAwareTaskEngine engine = new CompleteAwareTaskEngine(3);
		int tasksNum = 10;
		for (int i = 1; i <= tasksNum; i++) {
			engine.addTask(new MyTask(i, completedMap));
		}
		engine.waitUntilDoneAndShutdown();
		assertTrue(engine.isShutdown());
		assertEquals(tasksNum, completedMap.size());
	}

}
