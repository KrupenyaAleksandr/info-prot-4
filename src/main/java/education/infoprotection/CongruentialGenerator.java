package education.infoprotection;

import java.math.BigInteger;

public class CongruentialGenerator {

    private BigInteger MODUL = BigInteger.ONE;
    private final BigInteger MULTIPLIER = new BigInteger("1664525");
    private final BigInteger INCREMENT = new BigInteger("1013904223");
    private BigInteger currentState;

    public CongruentialGenerator(BigInteger startState) {
        MODUL = MODUL.shiftLeft(20);
        currentState = startState;
    }

    public BigInteger next() {
        currentState = currentState.multiply(MULTIPLIER).add(INCREMENT).mod(MODUL);
        return currentState;
    }

    public void setState(BigInteger state) {
        this.currentState = state;
    }
}
