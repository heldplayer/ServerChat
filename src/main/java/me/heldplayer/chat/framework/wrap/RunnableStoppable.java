
package me.heldplayer.chat.framework.wrap;

public abstract class RunnableStoppable implements Runnable {

    private boolean running = true;;

    @Override
    public final void run() {
        while (this.shouldRun()) {
            this.doRun();

            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                this.stop();
            }
        }
    }

    public abstract void doRun();

    public void stop() {
        this.running = false;
    }

    public boolean shouldRun() {
        return this.running;
    }

}
