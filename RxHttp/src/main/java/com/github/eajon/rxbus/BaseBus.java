package com.github.eajon.rxbus;

import com.github.eajon.enums.EventThread;
import com.github.eajon.util.LoggerUtils;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * Base bus
 * Created by threshold on 2017/1/18.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class BaseBus implements Bus {


    /**
     * Set {@link EventThread#MAIN} {@link Scheduler} in your current environment.<br>
     * For example in Android,you probably set @{code AndroidSchedulers.mainThread()}.
     * <p>
     * Handy method for {@link EventThread#setMainThreadScheduler(Scheduler)}
     * </p>
     *
     * @param mainScheduler mainScheduler for {@link EventThread#MAIN}
     */
    public static void setMainScheduler(@NonNull Scheduler mainScheduler) {
        EventThread.setMainThreadScheduler(mainScheduler);
    }

    private Relay<Object> relay;

    public BaseBus(Relay<Object> relay) {
        this.relay = relay.toSerialized();
    }

    @Override
    public void post(@NonNull Object event) {
        ObjectHelper.requireNonNull(event, "event == null");
        if (hasObservers()) {
            LoggerUtils.debug("post event: %s", event);
            relay.accept(event);
        } else {
            LoggerUtils.warning("no observers,event will be discard:%s", event);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> Flowable<T> ofType(@NonNull Class<T> eventType) {
        if (eventType.equals(Object.class)) {
            return ( Flowable<T> ) relay.toFlowable(BackpressureStrategy.LATEST);
        }
        return relay.toFlowable(BackpressureStrategy.LATEST).ofType(eventType);
    }


    @Override
    public boolean hasObservers() {
        return relay.hasObservers();
    }

}
