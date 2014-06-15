package scheduler.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Exceptions {

    public static <A, R> Function<A, Try<R>> tryOf(ThrowableFunction<A, R> function) {
        return (A a) -> {
            try {
                return new Success<>(function.apply(a));
            } catch (Exception e) {
                return new Failure<>(e);
            }
        };
    }

    public static <A, R> Function<A, R> catchOf(ThrowableFunction<A, R> function,
                                                Consumer<Exception> exceptionHandler) {
        return (A a) -> {
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
        RuntimeException cause();

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

        @Override
        public RuntimeException cause() {
            return null;
        }
    }

    public static class Failure<R> implements Try<R> {
        private Exception e;
        public Failure(Exception e) {
            this.e = e;
        }

        @Override
        public R get() {
            throw new RuntimeException(e);
        }

        @Override
        public RuntimeException cause() {
            return new RuntimeException(e);
        }
    }
}
