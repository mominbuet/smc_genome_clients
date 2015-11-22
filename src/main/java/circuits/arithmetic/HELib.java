package circuits.arithmetic;

import circuits.CircuitLib;
import static circuits.arithmetic.IntegerLib.S;
import flexsc.CompEnv;
import java.math.BigInteger;
import java.util.Arrays;
import Utilities.Paillier;
import util.Utils;

/**
 *
 * @author momin.aziz.cse @ gmail this class decrypts the biginteger( Paillier
 * crypto-system)
 */
public class HELib<T> extends CircuitLib<T> {

    static final int COUT = 1;

    public HELib(CompEnv<T> e) {
        super(e);
    }

    // full 1-bit adder
    public T[] add(T x, T y, T cin) {
        T[] res = env.newTArray(2);

        T t1 = xor(x, cin);
        T t2 = xor(y, cin);
        res[S] = xor(x, t2);
        t1 = and(t1, t2);
        res[COUT] = xor(cin, t1);

        return res;
    }

    // full n-bit adder
    public T[] addFull(T[] x, T[] y, boolean cin) {
        assert (x != null && y != null && x.length == y.length) : "add: bad inputs.";

        T[] res = env.newTArray(x.length + 1);
        T[] t = add(x[0], y[0], env.newT(cin));
        res[0] = t[S];
        for (int i = 0; i < x.length - 1; i++) {
            t = add(x[i + 1], y[i + 1], t[COUT]);
            res[i + 1] = t[S];
        }
        res[res.length - 1] = t[COUT];
        return res;
    }

    public T[] add(T[] x, T[] y, boolean cin) {
        return Arrays.copyOf(addFull(x, y, cin), x.length);
    }

    public T[] add(T[] x, T[] y) {

        return add(x, y, false);
    }

    public T[] decrypt(T[] a, T[] b) {

        return add(a,b);
    }

    public Integer outputToAlice(T[] a) {
        return Utils.toInt(env.outputToAlice(a));
    }

    public T[] inputOfAlice(BigInteger d) {
        return env.inputOfAlice(Utils.fromBigInteger(d, 512));
    }

    public T[] inputOfBob(Integer d) {
        return env.inputOfBob(Utils.fromInt(d, 32));
    }
}
