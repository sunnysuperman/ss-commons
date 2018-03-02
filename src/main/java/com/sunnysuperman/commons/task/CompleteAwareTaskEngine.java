package com.sunnysuperman.commons.task;

/**
 * 任务引擎类<br>
 * 
 * <p>
 * 通常是这么用的：<br>
 * 
 * <pre>
 * CompleteAwareTaskEngine engine = new CompleteAwareTaskEngine(4);// 创建包含4个工作线程的任务引擎，
 * for (int i = 0; i &lt; 1000; i++) {
 *     engine.add(new MyTask());// 往任务引擎中添加任务，即往taskList中添加任务，然后由4个工作线程随机取出任务执行
 * }
 * engine.waitUntilDoneAndExit();// 若任务列表为空，则表示所有任务都被取出执行，则可结束引擎
 * </pre>
 * 
 * 
 *
 */
public class CompleteAwareTaskEngine extends TaskEngine {
    /**
     * 未完成的任务数
     */
    protected int incompleteTaskCount = 0;
    /**
     * 任务全部完成的同步锁
     */
    protected final Object completeLock = new byte[0];

    /**
     * 打印日志，log4j的info级别
     * 
     * @param s
     *            待打印的字符串
     */
    protected void log(String s) {
        LOGGER.info(s);
    }

    /**
     * 
     * @param workerNum
     */
    public CompleteAwareTaskEngine(String groupName, int workerNum) {
        super(groupName, workerNum);
    }

    /**
     * 工作线程
     * 
     * 
     *
     */
    protected class CompleteAwareTaskEngineWorker extends Thread {
        public void run() {
            while (!stopped) {
                Runnable task = nextTask();
                if (task == null) {
                    break;
                }
                try {
                    task.run();
                } catch (Throwable t) {
                    LOGGER.error(null, t);
                }
                synchronized (addLock) {
                    incompleteTaskCount--;
                    if (logEnabled) {
                        log("incompleteTaskCount: " + incompleteTaskCount);
                    }
                }
                synchronized (completeLock) {
                    completeLock.notifyAll();
                }
            }
        }
    }

    /**
     * 
     */
    public void addTask(Runnable task) {
        synchronized (addLock) {
            taskList.addFirst(task);
            incompleteTaskCount++;
            addLock.notifyAll();
        }
    }

    /**
     * 
     */
    @Override
    protected Thread newTaskEngineWorker() {
        return new CompleteAwareTaskEngineWorker();
    }

    /**
     * 线程等待<br>
     * 若未完成的任务数大于num，则主线程继续等待；否则，主线程结束<br>
     * <strong>此方法并不影响工作线程从taskList中取任务执行</strong>
     * 
     * @param num
     *            任务数
     */
    public void waitUntilTaskNumLessThan(int num) {
        boolean done = false;
        synchronized (addLock) {
            done = (incompleteTaskCount < num);
        }
        if (done) {
            return;
        }
        if (logEnabled) {
            log("waiting until task num less than " + num);
        }
        synchronized (completeLock) {
            try {
                completeLock.wait();
            } catch (InterruptedException e) {
                LOGGER.error(null, e);
            }
        }
        waitUntilTaskNumLessThan(num);
    }

    /**
     * 获取未完成的任务数
     * 
     * @return
     */
    public int getInCompleteTaskNum() {
        return incompleteTaskCount;
    }

    /**
     * 线程等待，调用{@linkplain #waitUntilTaskNumLessThan(int)
     * waitUntilTaskNumLessThan}{@code (1)}
     */
    public void waitUntilTasksDone() {
        waitUntilTaskNumLessThan(1);
    }

    /**
     * 线程等待并结束引擎
     */
    public void waitUntilDoneAndExit() {
        waitUntilTasksDone();
        exit();
    }
}
