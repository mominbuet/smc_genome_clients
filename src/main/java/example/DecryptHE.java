package example;

import circuits.arithmetic.HELib;
import flexsc.CompEnv;
import gc.BadLabelException;
import java.math.BigInteger;
import java.util.Arrays;
import util.EvaRunnable;
import util.GenRunnable;
import Utilities.Paillier;
import util.Utils;

/**
 *
 * @author momin.aziz.cse@gmail.com
 */
public class DecryptHE {

    static public <T> T[] compute(CompEnv<T> gen, T[] inputA, T[] inputB) {

        return new HELib<T>(gen).decrypt(inputA, inputB);
    }

    public static class Generator<T> extends GenRunnable<T> {

        T[] inputA;
        T[] inputB;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {
            System.out.println("Input Decrypted " + new Paillier(true).Decryption(new BigInteger(args[0])));
            System.out.println("INput " + new BigInteger(args[0]));
            inputA = gen.inputOfAlice(Utils.fromBigInteger(new BigInteger(args[0]), 1024));
            inputB = gen.inputOfBob(new boolean[1024]);
        }

        @Override
        public void secureCompute(CompEnv<T> gen) {
            scResult = compute(gen, inputA, inputB);
        }

        @Override
        public void prepareOutput(CompEnv<T> gen) throws BadLabelException {
//            System.out.println("GEN1 " + Utils.toBigInteger(gen.outputToAlice(scResult)));
            System.out.println("Output Gen " + new Paillier(true).Decryption(Utils.toBigInteger(gen.outputToAlice(scResult))));
        }

    }

    public static class Evaluator<T> extends EvaRunnable<T> {

        T[] inputA;
        T[] inputB;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {

            inputA = gen.inputOfAlice(new boolean[1024]);
            gen.flush();
            boolean[] in = Utils.fromBigInteger(new BigInteger(args[0]), 1024);
            inputB = gen.inputOfBob(in);
            System.out.println("Input from Evaluator:" + Arrays.toString(in));
        }

        @Override
        public void secureCompute(CompEnv<T> gen) {
            scResult = compute(gen, inputA, inputB);
        }

        @Override
        public void prepareOutput(CompEnv<T> gen) throws BadLabelException {
            gen.outputToAlice(scResult);
        }
    }

}
