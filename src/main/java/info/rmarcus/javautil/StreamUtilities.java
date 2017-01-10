 

package info.rmarcus.javautil;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtilities {
	public static <T, V> Stream<Pair<T, V>> zip(Stream<T> s1, Stream<V> s2) {
		
		Iterator<T> it = s1.iterator();
		Iterator<V> iv = s2.iterator();
		
		Iterator<Pair<T, V>> pairs = new Iterator<Pair<T, V>>() {

			@Override
			public boolean hasNext() {
				return it.hasNext() && iv.hasNext();
			}

			@Override
			public Pair<T, V> next() {
				return new Pair<T, V>(it.next(), iv.next());
			}
			
		};
		
		Iterable<Pair<T, V>> itPairs = () -> pairs;
		
		return StreamSupport.stream(itPairs.spliterator(), false);
		
	}
	
	
	
	public static class Pair<T, V> {
		
		private T it;
		private V iv;
		
		public Pair(T t, V v) {
			this.it = t;
			this.iv = v;
		}
		
		
		public T getA() { return it; }
		public V getB() { return iv; }
		
		public <K> StreamUtilities.Pair<K, V> mutateA(Function<T, K> op) {
			return new Pair<K, V>(op.apply(getA()), getB());
		}
		
		public <K> StreamUtilities.Pair<T, K> mutateB(Function<V, K> op) {
			return new Pair<T, K>(getA(), op.apply(getB()));
		}
		
		
		@Override
		public String toString() {
			return "<" + getA() + ", " + getB() + ">";
		}
	}
	
}