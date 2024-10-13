package education.infoprotection;

import java.math.BigInteger;

public class BlumBlumShubMachine {
    private BigInteger p;
    private BigInteger q;
    private BigInteger m;
    private BigInteger currentState;

    public BlumBlumShubMachine(BigInteger p, BigInteger q) {
        this.p = p;
        this.q = q;
        this.m = p.multiply(q);
    }

    public BigInteger next() {
        currentState = currentState.modPow(BigInteger.TWO, m);
        return currentState;
    }

    public void setState(BigInteger state) {
        this.currentState = state;
    }
}