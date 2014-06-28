
package me.heldplayer.chat.framework.wrap;

import me.heldplayer.chat.framework.ConnectionsList;
import me.heldplayer.chat.framework.logging.Log;

public class ThreadWrapper {
    
    public static Log log = ConnectionsList.log.getSubLog("Threads");

    private RunnableStoppable runnable;
    private Thread thread;

    public void attemptStop() {
        this.runnable.stop();
    }

    @SuppressWarnings("deprecation")
    public void forceStop() {
        this.thread.stop();
    }

    @SuppressWarnings("deprecation")
    public void forceStop(Throwable e) {
        this.thread.stop(e);
    }

    public boolean isAlive() {
        return this.thread != null ? this.thread.isAlive() : false;
    }

    public void start(RunnableStoppable runnable) {
        if (this.thread == null || !this.thread.isAlive()) {
            this.runnable = runnable;
            this.thread = new Thread(runnable);
            this.thread.setDaemon(true);
            this.thread.start();
            log.debug("Starting thread %s (%s)", "No name", runnable);
        }
    }

    public void start(RunnableStoppable runnable, String name) {
        if (this.thread == null || !this.thread.isAlive()) {
            this.runnable = runnable;
            this.thread = new Thread(runnable, name);
            this.thread.setDaemon(true);
            this.thread.start();
            log.debug("Starting thread %s (%s)", name, runnable);
        }
    }

    public void startDaemon(RunnableStoppable runnable) {
        if (this.thread == null || !this.thread.isAlive()) {
            this.runnable = runnable;
            this.thread = new Thread(runnable);
            this.thread.setDaemon(true);
            this.thread.start();
            log.debug("Starting daemon thread %s (%s)", "No name", runnable);
        }
    }

    public void startDaemon(RunnableStoppable runnable, String name) {
        if (this.thread == null || !this.thread.isAlive()) {
            this.runnable = runnable;
            this.thread = new Thread(runnable, name);
            this.thread.setDaemon(true);
            this.thread.start();
            log.debug("Starting daemon thread %s (%s)", name, runnable);
        }
    }

}
