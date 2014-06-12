package scheduler.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Exceptions {

    public static <A, R> Function<A, Try<R>> tryOf(ThrowableFunction<A, R> function) {
        return a -> {
            try {
                return new Success<>(function.apply(a));
            } catch (Exception e) {
                return new Failure<>(e);
            }
        };
    }

    public static <A, R> Function<A, R> catchOf(ThrowableFunction<A, R> function,
                                                Consumer<Exception> exceptionHandler) {
        return a -> {
            try {
                return function.apply(a);
            } catch (Exception e) {
                exceptionHandler.accept(e);
                return null;
            }
        };
    }


    @FunctionalInterface
    public static interface ThrowableFunction<A, R> {
        R apply(A a) throws Exception;
    }

    public static interface Try<R> {
        R get();

        default boolean isSuccess() {
            return this instanceof Success;
        }

        default boolean isFailed() {
            return !isSuccess();
        }
    }

    public static class Success<R> implements Try<R> {
        private R r;
        public Success(R r) {
            this.r = r;
        }

        @Override
        public R get() {
            return r;
        }
    }

    public static class Failure<R> implements Try<R> {
        private Exception e;
        public Failure(Exception e) {
            this.e = e;
        }

        @Override
        public R get() {
            return (R) e;
        }
    }
}
