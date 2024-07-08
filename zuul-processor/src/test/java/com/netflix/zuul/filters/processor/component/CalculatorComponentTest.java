package com.netflix.zuul.filters.processor.component;

import com.netflix.zuul.filters.processor.Calculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CalculatorComponentTest {

    @Test
    public void testComponentSum() {
        Calculator calculator = new Calculator();
        int result = calculator.sum(10, 22);
        assertEquals(32, result, "Sum of 10 and 20 should be 30");
    }
}
