package com.minecraftbonanaza.animalweightsscaling;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeightScalesTest {
    @Test
    void tableMatchesSpecifiedCurve() {
        assertEquals(new WeightScales.ScaleFactors(0.78f, 0.94f), WeightScales.forWeight(0));
        assertEquals(new WeightScales.ScaleFactors(0.86f, 0.97f), WeightScales.forWeight(1));
        assertEquals(new WeightScales.ScaleFactors(0.91f, 0.98f), WeightScales.forWeight(2));
        assertEquals(new WeightScales.ScaleFactors(0.96f, 0.99f), WeightScales.forWeight(3));
        assertEquals(new WeightScales.ScaleFactors(1.00f, 1.00f), WeightScales.forWeight(4));
        assertEquals(new WeightScales.ScaleFactors(1.05f, 1.01f), WeightScales.forWeight(5));
        assertEquals(new WeightScales.ScaleFactors(1.10f, 1.02f), WeightScales.forWeight(6));
        assertEquals(new WeightScales.ScaleFactors(1.15f, 1.03f), WeightScales.forWeight(7));
        assertEquals(new WeightScales.ScaleFactors(1.20f, 1.04f), WeightScales.forWeight(8));
    }

    @Test
    void outOfRangeWeightsClamp() {
        assertEquals(WeightScales.forWeight(0), WeightScales.forWeight(-3));
        assertEquals(WeightScales.forWeight(8), WeightScales.forWeight(99));
    }

    @Test
    void averageWeightIsIdentity() {
        assertTrue(WeightScales.forWeight(4).isIdentity());
        assertFalse(WeightScales.forWeight(0).isIdentity());
        assertFalse(WeightScales.forWeight(8).isIdentity());
    }
}
