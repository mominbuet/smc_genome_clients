/*
 * Md. Momin Al Aziz(momin.aziz.cse gmail)
 * http://www.mominalaziz.com
 * 
 */
package circuits.arithmetic;

import circuits.CircuitLib;
import flexsc.CompEnv;
import java.lang.reflect.Array;
import util.Utils;
import java.lang.UnsupportedOperationException;

/**
 *
 * @author momin
 */
public class StringLib<T> extends CircuitLib<T> {

    public StringLib(CompEnv<T> e) {
        super(e);
    }

    public T Contains(T[] x, T[] y) {
        throw new UnsupportedOperationException("Getting there soon");
    }

    public T[] hammingDistance(T[] x, T[] y) {
        throw new UnsupportedOperationException("Getting there soon");
    }

    /**
     * Concatenates two strings
     *
     * @param x
     * @param y
     * @return
     */
    public T[] add(T[] x, T[] y) {
        //@SuppressWarnings("unchecked")

        T[] result = env.newTArray(x.length + y.length);
        System.arraycopy(x, 0, result, 0, x.length);
        System.arraycopy(y, 0, result, x.length, y.length);
        return result;
    }

    public String outputToAlice(T[] a) {
        return Utils.toString(env.outputToAlice(a));
    }

    public T[] inputOfAlice(String d) {
        return env.inputOfAlice(Utils.fromString(d));
    }

    public T[] inputOfBob(String d) {
        return env.inputOfBob(Utils.fromString(d));
    }
}
