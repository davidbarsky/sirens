package info.rmarcus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class NullUtils {
  public static <T> T orThrow(T t) {
    if (t == null) throw new NullPointerException();

    return t;
  }

  public static <T> T orThrow(T t, Supplier<RuntimeException> re) {
    if (t == null) {
      RuntimeException tt = re.get();
      throw tt;
    }

    return t;
  }

  public static <U, V, T> Optional<T> wrapCall(CheckedBiFunction<U, V, T> f, U arg1, V arg2) {
    try {
      T res = f.apply(arg1, arg2);
      return Optional.of(res);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static <U> Optional<Boolean> wrapCall(CheckedConsumer<U> f, U arg1) {
    try {
      f.apply(arg1);
      return Optional.of(Boolean.valueOf(true));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static <U> Optional<U> wrapCall(CheckedProducer<U> f) {
    try {
      return Optional.of(f.apply());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static <T> List<T> orEmptyList(List<T> list) {
    if (list == null) return Collections.emptyList();

    return list;
  }

  @FunctionalInterface
  public interface CheckedBiFunction<U, V, T> {
    T apply(U arg1, V arg2) throws Exception;
  }

  @FunctionalInterface
  public interface CheckedConsumer<U> {
    void apply(U arg1) throws Exception;
  }

  @FunctionalInterface
  public interface CheckedProducer<U> {
    U apply() throws Exception;
  }

  public static class Pair<T, V> {
    public T a;
    public V b;

    public Pair(T a, V b) {
      this.a = a;
      this.b = b;
    }
  }
}
