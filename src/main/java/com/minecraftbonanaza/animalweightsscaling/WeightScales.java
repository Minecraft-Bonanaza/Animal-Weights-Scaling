package com.minecraftbonanaza.animalweightsscaling;

/**
 * Discrete width/height multipliers for Animal Weights values 0 through 8.
 *
 * Width (X/Z) changes more than height so underfed animals read as emaciated
 * and well-fed animals read as plump rather than uniformly larger.
 */
public final class WeightScales {
    public static final int MIN_WEIGHT = 0;
    public static final int MAX_WEIGHT = 8;

    private static final ScaleFactors[] BY_WEIGHT = {
            new ScaleFactors(0.78f, 0.94f), // 0 visibly emaciated
            new ScaleFactors(0.86f, 0.97f), // 1 lean
            new ScaleFactors(0.91f, 0.98f), // 2
            new ScaleFactors(0.96f, 0.99f), // 3
            new ScaleFactors(1.00f, 1.00f), // 4 average
            new ScaleFactors(1.05f, 1.01f), // 5
            new ScaleFactors(1.10f, 1.02f), // 6
            new ScaleFactors(1.15f, 1.03f), // 7
            new ScaleFactors(1.20f, 1.04f)  // 8 well-fed / plump
    };

    private WeightScales() {
    }

    public static ScaleFactors forWeight(int weight) {
        int index = Math.max(MIN_WEIGHT, Math.min(MAX_WEIGHT, weight));
        return BY_WEIGHT[index];
    }

    public record ScaleFactors(float width, float height) {
        public static final ScaleFactors IDENTITY = new ScaleFactors(1.0f, 1.0f);

        public boolean isIdentity() {
            return width == 1.0f && height == 1.0f;
        }
    }
}
