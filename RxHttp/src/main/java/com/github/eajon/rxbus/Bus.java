package com.github.eajon.rxbus;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;

/**
 * Interface of Bus
 */
public interface Bus {

    /**
     * Fire a event
     *
     * @param event event will fire.
     */
    void post(@NonNull Object event);

    /**
     * Get the specific type event observable.
     * <p> You should manage life of observable,especially when {@code subscriber} is going to dispose!
     *
     * @param eventType the event type that you want listen
     * @param <T>       event type
     * @return Observable of {@code T}
     */
    <T> Flowable<T> ofType(@NonNull Class<T> eventType);

    /**
     * Returns true if the subject has any Observers.
     * <p>The method is thread-safe.
     *
     * @return false for no observers.
     */
    boolean hasObservers();
}
