package com.longmao.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @Description in order to process decimal, we use a fraction to represent a number, as for int type, denominator=1
 * @Author zimu young
 * Date 2021/6/21 22:12
 * Version 1.0
 **/
@Data
@EqualsAndHashCode
public class Fraction implements Serializable {
    private BigInteger numerator=BigInteger.ZERO;

    private BigInteger denominator = BigInteger.ONE;

    private boolean infinity = false;

    private BigInteger infinityNumerator = BigInteger.ZERO;

    private BigInteger infinityDenominator = BigInteger.ONE;

    // 约化分数
    public Fraction fractionReduction(){
        BigInteger gcd = this.numerator.gcd(this.denominator);
        if (gcd.compareTo(BigInteger.ONE) == 1){
            this.numerator = this.numerator.divide(gcd);
            this.denominator = this.denominator.divide(gcd);
        }
        if (this.infinityNumerator.equals(BigInteger.ZERO)){
            this.infinity = false;
        }
        if (this.infinity){
            gcd = this.infinityNumerator.gcd(this.infinityDenominator);
            if (gcd.compareTo(BigInteger.ONE) == 1){
                this.infinityNumerator = this.infinityNumerator.divide(gcd);
                this.infinityDenominator = this.infinityDenominator.divide(gcd);
            }
        }
        return this;
    }

    public Fraction fractionAddition(Fraction adder){
        this.numerator = this.numerator.multiply(adder.denominator).add(this.denominator.multiply(adder.numerator));
        this.denominator = this.denominator.multiply(adder.denominator);
        if (this.infinity || adder.infinity){
            this.infinityNumerator = this.infinityNumerator.multiply(adder.infinityDenominator).add(this.infinityDenominator.multiply(adder.infinityDenominator));
            this.infinityDenominator = this.infinityDenominator.multiply(adder.infinityDenominator);
            this.infinity = true;
        }
        this.fractionReduction();
        return this;
    }

    public Fraction fractionSubtraction(Fraction subtrahend){
        this.numerator = this.numerator.multiply(subtrahend.denominator).subtract(this.denominator.multiply(subtrahend.numerator));
        this.denominator = this.denominator.multiply(subtrahend.denominator);
        if (this.infinity || subtrahend.infinity){
            this.infinityNumerator = this.infinityNumerator.multiply(subtrahend.infinityDenominator).subtract(this.infinityDenominator.multiply(subtrahend.infinityNumerator));
            this.infinityDenominator = this.infinityDenominator.multiply(subtrahend.infinityDenominator);
            this.infinity = true;
        }
        this.fractionReduction();
        return this;
    }

    /*
     * 仅实现n1/d1 * (n2/d2+in2*inf/id2), 不实现(n1/d1+in1*inf/id1) * (n2/d2+in2*inf/id2)
     * 未实现的计算出现当且仅当c为inf时，两个c相乘，计算中不会出现此情况
     */
    public Fraction fractionMultiplication(Fraction multiplicand){
        this.numerator = this.numerator.multiply(multiplicand.numerator);
        this.denominator = this.denominator.multiply(multiplicand.denominator);
        if (this.infinity){
            this.infinityNumerator = this.infinityNumerator.multiply(multiplicand.numerator);
            this.infinityDenominator = this.infinityDenominator.multiply(multiplicand.denominator);
        }
        else if (multiplicand.infinity){
            this.infinity = true;
            this.infinityNumerator = this.numerator.multiply(multiplicand.infinityNumerator);
            this.infinityDenominator = this.denominator.multiply(multiplicand.infinityDenominator);
        }
        this.fractionReduction();
        return this;
    }

    /*
     * 仅实现(n1/d1)/(n2/d2)，除法不会出现inf
     */
    public Fraction fractionDivision(Fraction divisor){
        // exchange the negative sign of the numerator to denominator
        if (divisor.numerator.compareTo(BigInteger.ZERO) == -1){
            this.numerator = this.numerator.multiply(divisor.denominator.multiply(new BigInteger("-1")));
            this.denominator = this.denominator.multiply(divisor.numerator.multiply(new BigInteger("-1")));
        }
        else {
            this.numerator = this.numerator.multiply(divisor.denominator);
            this.denominator = this.denominator.multiply(divisor.numerator);
        }
        this.fractionReduction();
        return this;
    }

    public Fraction oppositeFraction(){
        Fraction negativeOne = new Fraction();
        negativeOne.setNumerator(new BigInteger("-1"));
        this.fractionMultiplication(negativeOne);
        return this;
    }

    public boolean judgeInteger(){
        boolean integer = false;
        this.fractionReduction();
        if (this.denominator.compareTo(BigInteger.ONE) == 0){
            integer = true;
        }

        return integer;
    }

    /**
     * @title roundDown
     * @description 返回向下取整的结果
     * @author longmao
     * @updateTime 2021/8/4 11:17
     * @return: com.longmao.dto.Fraction
     * @throws
     */
    public Fraction roundDown(){
        Fraction roundDownInteger = new Fraction();
        BigInteger remainder = this.numerator.mod(this.denominator);
        roundDownInteger.setNumerator(this.numerator.subtract(remainder).divide(this.denominator));

        return roundDownInteger;
    }
}
