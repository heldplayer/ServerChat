
package me.heldplayer.chat.framework.wrap;

public class ThreadWrapper {

    private RunnableStoppable runnable;
    private Thread thread;

    public void attemptStop() {
        runnable.stop();
    }

    @SuppressWarnings("deprecation")
    public void forceStop() {
        thread.stop();
    }

    @SuppressWarnings("deprecation")
    public void forceStop(Throwable e) {
        thread.stop(e);
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
        }
    }

    public void start(RunnableStoppable runnable, String name) {
        if (this.thread == null || !this.thread.isAlive()) {
            this.runnable = runnable;
            this.thread = new Thread(runnable, name);
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }

    public void startDaemon(RunnableStoppable runnable) {
        if (this.thread == null || !this.thread.isAlive()) {
            this.runnable = runnable;
            this.thread = new Thread(runnable);
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }

    public void startDaemon(RunnableStoppable runnable, String name) {
        if (this.thread == null || !this.thread.isAlive()) {
            this.runnable = runnable;
            this.thread = new Thread(runnable, name);
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }

}
