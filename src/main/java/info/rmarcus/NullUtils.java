package info.rmarcus;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class NullUtils {
	public static <T> T orThrow(@Nullable T t) {
		if (t == null) 
			throw new NullPointerException();
		
		return t;
	}
	
	public static <T> T orThrow(@Nullable T t, Supplier<@NonNull RuntimeException> re) {
		if (t == null) {
			@Nullable RuntimeException tt = re.get();
			throw tt;
		}
		
		return t;
	}
	
	public static <T> T[] orThrow(T @Nullable [] t, Supplier<@NonNull RuntimeException> re) {
		if (t == null) {
			@Nullable RuntimeException tt = re.get();
			throw tt;
		}
		
		return t;
	}

	
	public static <T> @Nullable T deoptional(@Nullable Optional<@NonNull T> min) {
		if (min != null && min.isPresent())
			return min.get();
		
		return null;
	}
	
	public static <T> SafeOpt<T> wrap(@Nullable Optional<T> opt) {
		if (opt == null)
			return new SafeOpt<T>((@Nullable T) null);
		
		try {
			@Nullable final T tmp = opt.get();
			if (tmp != null) {
				return new SafeOpt<T>(tmp);
			}
		} catch (NoSuchElementException e) {
			
		}
		return new SafeOpt<T>((@Nullable T)null);
		
	}
	
	@FunctionalInterface
	public interface CheckedBiFunction<U, V, T> {
	   T apply(U arg1, V arg2) throws Exception;
	}
	
	@FunctionalInterface
	public interface CheckedConsumer<U> {
	   void apply(U arg1) throws Exception;
	}
	
	public static <U, V, T> SafeOpt<T> wrapCall(CheckedBiFunction<U, V, T> f, U arg1, V arg2) {
		try {
			T res = f.apply(arg1, arg2);
			return new SafeOpt<T>(res);
		} catch (Exception e) {
			return new SafeOpt<T>(e);
		}
	}
	
	public static <U> SafeOpt<Boolean> wrapCall(CheckedConsumer<U> f, U arg1) {
		try {
			f.apply(arg1);
			return new SafeOpt<Boolean>(Boolean.valueOf(true));
		} catch (Exception e) {
			return new SafeOpt<Boolean>(e);
		}
	}
	
	@SuppressWarnings("null")
	public static <T> List<T> orEmptyList(@Nullable List<T> list) {
		if (list == null)
			return Collections.emptyList();
		
		return list;
	}
	
	
	public static class SafeOpt<T> {
		@Nullable private final T val;
		@Nullable private final Exception e;
		
		public SafeOpt(@Nullable T t) {
			this.val = t;
			e = null;
		}
		
		public SafeOpt(@Nullable Exception e) {
			val = null;
			this.e = e;
		}
		
		public @NonNull T get() {
			@Nullable final T v = val;
			if (v == null) {
				throw new RuntimeException("Getting a null value!");
			}
			
			return v;
		}
				
		public Exception getException() {
			@Nullable final Exception v = e;
			if (v == null) {
				throw new RuntimeException("SafeOpt did not have an exception!");
			}
			
			return v;
		}
		
		public boolean hasValue() {
			return val != null;
		}
		
		public <K> SafeOpt<K> map(@Nullable Function<T, @Nullable K> f) {
			@Nullable final T v = val;
			if (v == null || f == null)
				return new SafeOpt<K>(e);
			
			return new SafeOpt<K>(f.apply(v));
		}
		
		public SafeOpt<Double> apply(@Nullable ToDoubleFunction<T> f) {
			@Nullable final T v = val;
			if (v == null || f == null)
				return new SafeOpt<Double>(e);
			
			return new SafeOpt<Double>(Double.valueOf(f.applyAsDouble(v)));
		}
		
		public void throwIfExceptionPresent() {
			final Exception exp = e;
			if (exp != null) {
				throw new RuntimeException(exp.getMessage());
			}
		}
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
