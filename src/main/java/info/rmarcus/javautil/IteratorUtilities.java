package info.rmarcus.javautil;

import java.util.Iterator;
import java.util.NoSuchElementException;

import info.rmarcus.javautil.StreamUtilities.Pair;

public class IteratorUtilities {
	public static <T> Iterator<Pair<T,T>> twoGrams(Iterator<T> it) {
		if (!it.hasNext()) {
			throw new NoSuchElementException("Must have at least one item");
		}

		return new Iterator<Pair<T, T>>() {
			private T last = it.next();

			@Override
			public boolean hasNext() {
				return it.hasNext() && last != null;
			}

			@Override
			public Pair<T, T> next() {
				if (it.hasNext()) {
					Pair<T, T> toR = new Pair<>(last, it.next());
					last = toR.getB();

					return toR;
				} else {
					return new Pair<>(last, null);
				}
			}

		};

	}
}
