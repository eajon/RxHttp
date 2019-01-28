package com.github.eajon.rxbus;

import com.github.eajon.annotation.RxSubscribe;
import com.github.eajon.enums.EventThread;
import com.github.eajon.model.RxEvent;
import com.github.eajon.util.LoggerUtils;
import com.jakewharton.rxrelay2.PublishRelay;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.schedulers.Schedulers;

/**
 * once an {@link Observer} has subscribed, emits all subsequently observed items to the
 * subscriber.<br>
 * See also {@link PublishRelay}
 */
@SuppressWarnings("WeakerAccess")
public class RxBus extends BaseBus {

    private static volatile RxBus defaultBus;


    private Map<Object, CompositeDisposable> subscriptions = new HashMap<>();
    private final Map<Integer, List<Object>> stickyEventMap;

//    /*
//     * Use {@link #getDefault()} instead.
//     */
//    @Deprecated
//    public static RxBus getInstance() {
//        return getDefault();
//    }

    /**
     * Get the default instance of RxBus.
     *
     * @return {@link com.github.eajon.rxbus.RxBus}
     */
    public static RxBus getDefault() {
        if (defaultBus == null) {
            synchronized (RxBus.class) {
                if (defaultBus == null) {
                    defaultBus = new RxBus();
                }
            }
        }
        return defaultBus;
    }

    public RxBus(PublishRelay<Object> publishRelay) {
        super(publishRelay);
        stickyEventMap = new ConcurrentHashMap<>();
    }

    /**
     * Default constructor,use {@link PublishRelay} for internal bus.
     */
    public RxBus() {
        this(PublishRelay.create());
    }

    @Override
    public void post(Object event) {
        super.post(new RxEvent(event));
    }

    public void post(String eventId, @NonNull Object event) {
        super.post(new RxEvent(eventId, event));
    }

    /**
     * Fire a sticky event.
     *
     * @param event sticky event.
     */
    public void post(@NonNull Object event, boolean isStick) {
        ObjectHelper.requireNonNull(event, "event == null");
        if (isStick) {
            synchronized (stickyEventMap) {
                List<Object> stickyEvents = stickyEventMap.get(event.getClass().hashCode());
                boolean isStickEventListInMap = true;
                if (stickyEvents == null) {
                    stickyEvents = new LinkedList<>();
                    isStickEventListInMap = false;
                }
                stickyEvents.add(new RxEvent(event, true));
                if (!isStickEventListInMap) {
                    stickyEventMap.put(event.getClass().hashCode(), stickyEvents);
                }
            }
        }
        super.post(new RxEvent(event, isStick));
    }

    public void post(String tag, @NonNull Object event, boolean isStick) {
        ObjectHelper.requireNonNull(event, "event == null");
        if (isStick) {
            synchronized (stickyEventMap) {
                List<Object> stickyEvents = stickyEventMap.get(event.getClass().hashCode());
                boolean isStickEventListInMap = true;
                if (stickyEvents == null) {
                    stickyEvents = new LinkedList<>();
                    isStickEventListInMap = false;
                }
                stickyEvents.add(new RxEvent(tag, event, true));
                if (!isStickEventListInMap) {
                    stickyEventMap.put(event.getClass().hashCode(), stickyEvents);
                }
            }
        }
        super.post(new RxEvent(tag, event, isStick));
    }


    /**
     * Get unmodifiable list of specific type sticky event.
     * <p>
     * DO NOT ALTER (ADD REMOVE) THIS STICKY EVENT LIST!
     * </p>
     * If you want to delete some of this list,please use {@link #removeStickyEventType(Class)}  or {@link #removeStickyEvent(Object)} or {@link #clearAllSticky()} <br>
     * If you want to add some sticky event,please use {@link #post(Object)}
     *
     * @param eventType type of T
     * @param <T>       the sticky event type that you want
     * @return list of specific stick event
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> List<T> getSticky(@NonNull Class<T> eventType) {
        ObjectHelper.requireNonNull(eventType, "eventType == null");
        synchronized (stickyEventMap) {
            List<T> list = ( List<T> ) stickyEventMap.get(eventType.hashCode());
            return list == null ? null : Collections.unmodifiableList(list);
        }
    }

    /**
     * Remove specific sticky event
     *
     * @param event the sticky event that you want to remove
     */
    public void removeStickyEvent(@NonNull Object event) {
        ObjectHelper.requireNonNull(event, "event == null");
        synchronized (stickyEventMap) {
            List<Object> stickyEvents = stickyEventMap.get(event.getClass().hashCode());
            if (stickyEvents != null) {
                stickyEvents.remove(event);
            }
        }
    }

    /**
     * Remove specific sticky event
     *
     * @param eventType the EventType
     * @param position  the location of EventType
     */
    public void removeStickyEventAt(@NonNull Class<?> eventType, int position) {
        ObjectHelper.requireNonNull(eventType, "eventType == null");
        synchronized (stickyEventMap) {
            List<Object> stickyEvents = stickyEventMap.get(eventType.hashCode());
            if (stickyEvents != null) {
                stickyEvents.remove(position);
            }
        }
    }

    /**
     * Remove specific type sticky event
     *
     * @param eventType the sticky event type that you want remove
     */
    public void removeStickyEventType(@NonNull Class<?> eventType) {
        ObjectHelper.requireNonNull(eventType, "eventType == null");
        synchronized (stickyEventMap) {
            stickyEventMap.remove(eventType.hashCode());
        }
    }

    /**
     * Remove all sticky event.
     */
    public void clearAllSticky() {
        synchronized (stickyEventMap) {
            stickyEventMap.clear();
        }
    }

    /**
     * Get the specific type sticky event observable
     *
     * @param <T>       event type
     * @param eventType the sticky event type that you want listen
     * @return Observable of {@code T}
     */
    public <T> Flowable<T> ofStickyType(@NonNull Class<T> eventType) {
        synchronized (stickyEventMap) {
            @SuppressWarnings("unchecked")
            List<T> stickyEvents = ( List<T> ) stickyEventMap.get(eventType.hashCode());
            if (stickyEvents != null && stickyEvents.size() > 0) {
                return Flowable.fromIterable(stickyEvents).mergeWith(ofType(( Class<T> ) RxEvent.class));
            }
        }
        return ofType(( Class<T> ) RxEvent.class);
    }

    /**
     * unSubscribe all registered event and clear all sticky event.
     */
    public void reset() {
        Observable.fromIterable(subscriptions.values())
                .filter(new Predicate<CompositeDisposable>() {
                    @Override
                    public boolean test(CompositeDisposable compositeDisposable) throws Exception {
                        return compositeDisposable != null && !compositeDisposable.isDisposed();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<CompositeDisposable>() {
                    @Override
                    public void accept(CompositeDisposable compositeDisposable) throws Exception {
                        compositeDisposable.clear();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LoggerUtils.error(throwable, "Dispose subscription");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        stickyEventMap.clear();
                        subscriptions.clear();
                    }
                });
    }

    /**
     * Indicate {@code subscriber} is registered.
     *
     * @param subscriber subscriber to subscribe event
     * @return true for registered
     */
    public synchronized boolean isRegistered(@NonNull Object subscriber) {
        ObjectHelper.requireNonNull(subscriber, "subscriber == null");
        return subscriptions.containsKey(subscriber.hashCode());
    }

    /**
     * register with {@link RxSubscribe} annotation method
     *
     * @param subscriber the instance of class that you want to find {@link RxSubscribe} annotation method
     */
    public void register(@NonNull final Object subscriber) {
        ObjectHelper.requireNonNull(subscriber, "subscriber == null");
        Observable.just(subscriber)
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object obj) throws Exception {
                        boolean registered = isRegistered(obj);
                        if (registered) {
                            LoggerUtils.warning("%s has already registered", obj);
                        }
                        return !registered;
                    }
                })
                .flatMap(new Function<Object, ObservableSource<Method>>() {
                    @Override
                    public ObservableSource<Method> apply(Object obj) throws Exception {
                        LoggerUtils.debug("start to analyze subscriber: %s", obj);
                        return Observable.fromArray(obj.getClass().getDeclaredMethods());
                    }
                })
                .map(new Function<Method, Method>() {
                    @Override
                    public Method apply(Method method) throws Exception {
                        LoggerUtils.debug("set method can accessible: %s ", method);
                        method.setAccessible(true);
                        return method;
                    }
                })
                .filter(new Predicate<Method>() {
                    @Override
                    public boolean test(Method method) throws Exception {
                        boolean hasRxSubscribeAnnotation = method.isAnnotationPresent(RxSubscribe.class);
                        if (hasRxSubscribeAnnotation) {
                            LoggerUtils.debug("%s present @RxSubscribe annotation", method.getName());
                            boolean isOnlyHaveOneParam = method.getParameterTypes() != null && method.getParameterTypes().length == 1;
                            if (!isOnlyHaveOneParam) {
                                throw new RuntimeException("Although [" + method + "] present @RxSubscribe annotation. But we expect ONLY ONE param in method.");
                            }
                            return true;
                        }
                        return false;
//                        boolean hasRxSubscribeAnnotationAndOnlyOneParam = method.isAnnotationPresent(RxSubscribe.class) && method.getParameterTypes() != null && method.getParameterTypes().length == 1;
//                        LoggerUtil.debug("%s has @RxSubscribe annotation and only one param ? %s", method, hasRxSubscribeAnnotationAndOnlyOneParam);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Method>() {
                    @Override
                    public void accept(Method method) throws Exception {
                        LoggerUtils.debug("now start to add subscription method: %s", method);
                        addSubscriptionMethod(subscriber, method);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LoggerUtils.error(throwable, "%s failed on register method", subscriber);
                        throw new RuntimeException(subscriber + " failed on register method", throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        LoggerUtils.debug("%s registered complete", subscriber);
                    }
                });
    }

    private void addSubscriptionMethod(final Object subscriber, final Method method) {
        Disposable subscribe = Flowable.just(method.getParameterTypes()[0])
                .doOnNext(new Consumer<Class<?>>() {
                    @Override
                    public void accept(Class<?> type) throws Exception {
                        LoggerUtils.debug("Origin: [method: %s ] , param[0] type: %s", method, type);
                    }
                })
                .map(new Function<Class<?>, Class<?>>() {
                    @Override
                    public Class<?> apply(Class<?> type) throws Exception {
                        Class<?> eventType = getEventType(type);
                        LoggerUtils.debug("Listen event type: %s", eventType);
                        return eventType;
                    }
                })
                .flatMap(new Function<Class<?>, Flowable<?>>() {
                    @Override
                    public Flowable<?> apply(Class<?> type) throws Exception {
                        RxSubscribe rxAnnotation = method.getAnnotation(RxSubscribe.class);
                        LoggerUtils.debug("%s @RxSubscribe Annotation: %s", method, rxAnnotation.observeOnThread());
                        Flowable<?> flowable = rxAnnotation.isSticky() ? ofStickyType(type) : ofType(RxEvent.class);
                        return flowable.observeOn(EventThread.getScheduler(rxAnnotation.observeOnThread()));
                    }
                })
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object obj) {
                        RxEvent event = ( RxEvent ) obj;
                        RxSubscribe rxAnnotation = method.getAnnotation(RxSubscribe.class);
//                            if (rxAnnotation.tag().equals(event.getTag())) {
//                                LoggerUtils.debug("eventID same"+rxAnnotation.tag()+"/"+event.getTag());
//                            } else {
//                                LoggerUtils.debug("eventID diff"+rxAnnotation.tag()+"/"+event.getTag());
//                            }
//                            if (method.getParameterTypes()[0].equals(event.getSource().getClass())) {
//                                LoggerUtils.debug("class same" + event.getSource().getClass());
//                            } else {
//                                LoggerUtils.debug("class diff" + event.getSource().getClass() + method.getParameterTypes()[0]);
//                            }
                        if (rxAnnotation.tag().equals(event.getTag()) && rxAnnotation.isSticky() == event.isStick() && method.getParameterTypes()[0].equals(event.getSource().getClass())) {
                            return true;
                        } else {
                            return false;
                        }


                    }
                })
                .subscribe(
                        new Consumer<Object>() {
                            @Override
                            @SuppressWarnings("all")
                            public void accept(Object obj) throws Exception {
                                LoggerUtils.debug("Subscriber:%s invoke Method:%s", subscriber, method);
                                method.setAccessible(true);

                                RxEvent event = ( RxEvent ) obj;
                                method.invoke(subscriber, event.getSource());

                                //now RxBus2 do not handle exception for method. you should do it by yourself.
//                                try {
//                                    method.invoke(subscriber, obj);
//                                } catch (IllegalAccessException e) {
//                                    LoggerUtil.error(e, "%s invoke error", method);
//                                } catch (InvocationTargetException e) {
//                                    LoggerUtil.error(e, "%s invoke error", method);
//                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LoggerUtils.error(throwable, "[%s] invoke method:[%s] failed", subscriber, method);
                                throw new RuntimeException(throwable);//throw exception for whom subscribe this.
                            }
                        });
        CompositeDisposable compositeDisposable = subscriptions.get(subscriber.hashCode());
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(subscribe);
        subscriptions.put(subscriber.hashCode(), compositeDisposable);
        LoggerUtils.debug("Registered method %s complete", method);
    }

    /**
     * unregister {@link RxSubscribe} annotation method
     *
     * @param subscriber the instance with {@link RxSubscribe} annotation method.
     */
    public void unregister(@NonNull final Object subscriber) {
        ObjectHelper.requireNonNull(subscriber, "subscriber == null");
        Flowable.just(subscriber)
                .map(new Function<Object, CompositeDisposable>() {
                    @Override
                    public CompositeDisposable apply(Object subscriber) throws Exception {
                        return subscriptions.get(subscriber.hashCode());
                    }
                })
                .filter(new Predicate<CompositeDisposable>() {
                    @Override
                    public boolean test(CompositeDisposable compositeDisposable) throws Exception {
                        return compositeDisposable != null && !compositeDisposable.isDisposed();
                    }
                })
                .subscribe(new Subscriber<CompositeDisposable>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(CompositeDisposable compositeDisposable) {
                        compositeDisposable.dispose();
                        subscriptions.remove(subscriber.hashCode());
                        LoggerUtils.debug("remove subscription of %s", subscriber);
                    }

                    @Override
                    public void onError(Throwable t) {
                        LoggerUtils.error(t, "%s unregister RxBus", subscriber);
                    }

                    @Override
                    public void onComplete() {
                        LoggerUtils.debug("%s unregister RxBus completed!", subscriber);
                    }
                });
    }

    private Class<?> getEventType(Class<?> cls) {
        String clsName = cls.getName();
        if (clsName.equals(int.class.getName())) {
            cls = Integer.class;
        } else if (clsName.equals(double.class.getName())) {
            cls = Double.class;
        } else if (clsName.equals(float.class.getName())) {
            cls = Float.class;
        } else if (clsName.equals(long.class.getName())) {
            cls = Long.class;
        } else if (clsName.equals(byte.class.getName())) {
            cls = Byte.class;
        } else if (clsName.equals(short.class.getName())) {
            cls = Short.class;
        } else if (clsName.equals(boolean.class.getName())) {
            cls = Boolean.class;
        } else if (clsName.equals(char.class.getName())) {
            cls = Character.class;
        }
        return cls;
    }


}
