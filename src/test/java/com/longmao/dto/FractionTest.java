package com.longmao.dto;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigInteger;

/**
 * @Description fraction addition, subtraction, multiplication, division test
 * @Author zimu young
 * Date 2021/6/22 14:50
 * Version 1.0
 **/
public class FractionTest {
    @Test
    public void testFractionReduction(){
        Fraction fraction = new Fraction();
        fraction.setNumerator(new BigInteger("-374"));
        fraction.setDenominator(new BigInteger("66"));
        fraction.fractionReduction();
        Assert.assertEquals(fraction.getNumerator(), new BigInteger("-17"));
        Assert.assertEquals(fraction.getDenominator(), new BigInteger("3"));
    }

    @Test
    public void testFractionAddition(){
        Fraction adder1 = new Fraction();
        adder1.setNumerator(new BigInteger("-1"));
        adder1.setDenominator(new BigInteger("3"));
        Fraction adder2 = new Fraction();
        adder2.setNumerator(new BigInteger("1"));
        adder2.setDenominator(new BigInteger("12"));
        Fraction sum = adder1.fractionAddition(adder2);
        Assert.assertEquals(sum.getNumerator(), new BigInteger("-1"));
        Assert.assertEquals(sum.getDenominator(), new BigInteger("4"));
    }

    @Test
    public void testFractionSubtraction(){
        Fraction minuend = new Fraction();
        minuend.setNumerator(new BigInteger("1"));
        minuend.setDenominator(new BigInteger("12"));
        Fraction subtrahend = new Fraction();
        subtrahend.setNumerator(new BigInteger("-3"));
        subtrahend.setDenominator(new BigInteger("4"));
        Fraction sum = minuend.fractionSubtraction(subtrahend);
        Assert.assertEquals(sum.getNumerator(), new BigInteger("5"));
        Assert.assertEquals(sum.getDenominator(), new BigInteger("6"));
    }

    @Test
    public void testFractionMultiplication(){
        Fraction multiplicand = new Fraction();
        multiplicand.setNumerator(new BigInteger("3"));
        multiplicand.setDenominator(new BigInteger("2"));
        Fraction factor2 = new Fraction();
        factor2.setNumerator(new BigInteger("-8"));
        factor2.setDenominator(new BigInteger("9"));
        Fraction sum = multiplicand.fractionMultiplication(factor2);
        Assert.assertEquals(sum.getNumerator(), new BigInteger("-4"));
        Assert.assertEquals(sum.getDenominator(), new BigInteger("3"));
    }

    @Test
    public void testFractionDivision(){
        Fraction dividend = new Fraction();
        dividend.setNumerator(new BigInteger("9"));
        dividend.setDenominator(new BigInteger("2"));
        Fraction divisor = new Fraction();
        divisor.setNumerator(new BigInteger("-3"));
        divisor.setDenominator(new BigInteger("4"));
        Fraction sum = dividend.fractionDivision(divisor);
        Assert.assertEquals(sum.getNumerator(), new BigInteger("-6"));
        Assert.assertEquals(sum.getDenominator(), new BigInteger("1"));
    }
    
    @Test
    public void testRoundDown(){
        Fraction fraction = new Fraction();
        fraction.setNumerator(new BigInteger("-17"));
        fraction.setDenominator(new BigInteger("3"));
        Fraction roundDownInteger = fraction.roundDown();
        Assert.assertEquals(roundDownInteger.getNumerator(), new BigInteger("-6"));
    }
}
