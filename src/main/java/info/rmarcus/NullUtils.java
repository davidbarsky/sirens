package info.rmarcus;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class NullUtils {
	
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
			return new SafeOpt<T>(null);
		
		try {
			@Nullable final T tmp = opt.get();
			if (tmp != null) {
				return new SafeOpt<T>(tmp);
			}
		} catch (NoSuchElementException e) {
			
		}
		return new SafeOpt<T>(null);
		
	}
	
	public static class SafeOpt<T> {
		@Nullable private final T val;
		
		public SafeOpt(@Nullable T t) {
			this.val = t;
		}
		
		public @NonNull T get() {
			@Nullable final T v = val;
			if (v == null) {
				throw new RuntimeException("Getting a null value!");
			}
			
			return v;
		}
	}

	public static <T> List<T> orEmptyList(@Nullable List<@NonNull T> list) {
		if (list == null)
			return new ArrayList<T>();
		
		return list;
	}



}
