package com.github.eajon.enums;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Thread for listen event.
 */

public enum EventThread {
    /**
     * Main Thread(UI Thread)
     */
    MAIN,
    /**
     * New Thread
     */
    NEW,
    /**
     * Read/Write Thread
     */
    IO,
    /**
     * Computation thread. No io block.
     */
    COMPUTATION,
    /**
     * Running at current thread by sequence
     */
    TRAMPOLINE,
    /**
     * {@link Schedulers#SINGLE}
     */
    SINGLE;


    /**
     * This factory method produce {@link Scheduler} for use.
     * <p>
     * Please be careful if you use  {@link EventThread#MAIN} .
     * You should provide your runtime environment main thread(UI thread) before use it.
     * </p>
     *
     * @param threadMode {@link EventThread} type
     * @return {@link Scheduler}
     */
    public static Scheduler getScheduler(EventThread threadMode) {
        Scheduler scheduler;
        switch (threadMode) {
            default:
            case MAIN:
                scheduler = AndroidSchedulers.mainThread();
                break;
            case NEW:
                scheduler = Schedulers.newThread();
                break;
            case IO:
                scheduler = Schedulers.io();
                break;
            case COMPUTATION:
                scheduler = Schedulers.computation();
                break;
            case TRAMPOLINE:
                scheduler = Schedulers.trampoline();
                break;
            case SINGLE:
                scheduler = Schedulers.single();
        }
        return scheduler;
    }
}
