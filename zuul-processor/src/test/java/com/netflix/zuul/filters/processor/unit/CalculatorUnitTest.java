package com.netflix.zuul.filters.processor.unit;

import com.netflix.zuul.filters.processor.Calculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorUnitTest {

    public void testSum() {
        Calculator calculator = new Calculator();
        int result = calculator.sum(3, 4);
        assertEquals(7, result, "Sum of 3 and 4 should be 7");
    }

    @Test
    public void testSumWithNegativeNumbers() {
        Calculator calculator = new Calculator();
        int result = calculator.sum(-3, -4);
        assertEquals(-7, result, "Sum of -3 and -4 should be -7");
    }

    @Test
    public void testSumWithZero() {
        Calculator calculator = new Calculator();
        int result = calculator.sum(0, 5);
        assertEquals(5, result, "Sum of 0 and 5 should be 5");
    }
}
