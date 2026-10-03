import java.util.Random;

/**
 * Variant 2 - Scaling Limits in Practice
 *
 * Compares the practical scaling limits of:
 *   O(n)       - Linear workload
 *   O(n log n) - Merge Sort
 *   O(n^2)     - Bubble Sort
 *
 * A tested input is considered feasible when its execution
 * time does not exceed the 5-second budget.
 */
public class Variant2Benchmark {

    private static final double BUDGET_SECONDS = 5.0;

    private static final long BUDGET_NS =
            (long) (BUDGET_SECONDS * 1_000_000_000L);

    /*
     * Used to make the result of the computation observable
     * and reduce the possibility of unused work being removed.
     */
    private static volatile long blackhole;

    public static void main(String[] args) {

        System.out.println("Variant 2 - Scaling Limits in Practice");
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println(
                "OS: " + System.getProperty("os.name")
                + " " + System.getProperty("os.arch")
        );
        System.out.println(
                "Budget: " + BUDGET_SECONDS + " s per tested input"
        );
        System.out.println();

        /*
         * An optional command-line argument can be used to run
         * only one experiment:
         *
         * java Variant2Benchmark linear
         * java Variant2Benchmark merge
         * java Variant2Benchmark bubble
         *
         * With no argument, all experiments are executed.
         */
        if (args.length == 0
                || args[0].equalsIgnoreCase("linear")) {
            runLinear();
        }

        if (args.length == 0
                || args[0].equalsIgnoreCase("merge")) {
            runMergeSort();
        }

        if (args.length == 0
                || args[0].equalsIgnoreCase("bubble")) {
            runBubbleSort();
        }
    }

    // =========================================================
    // O(n) - LINEAR WORKLOAD
    // =========================================================

    private static void runLinear() {

        System.out.println("LINEAR WORKLOAD - O(n)");

        /*
         * Warm-up execution.
         * This allows the JVM to compile frequently executed code
         * before the actual measurements begin.
         */
        linearWork(20_000_000L);

        long[] sizes = {
                500_000_000L,
                1_000_000_000L,
                1_500_000_000L,
                2_000_000_000L,
                2_250_000_000L,
                2_500_000_000L
        };

        runLongSizes(sizes, Variant2Benchmark::linearWork);

        System.out.println();
    }

    /*
     * Synthetic O(n) workload.
     *
     * A dependent arithmetic operation is performed once for
     * every iteration. No array proportional to n is required,
     * so the experiment uses O(1) additional memory.
     */
    private static void linearWork(long n) {

        for (long i = 0; i < n; i++) {
            blackhole += (i & 7L);
        }
    }

    // =========================================================
    // O(n log n) - MERGE SORT
    // =========================================================

    private static void runMergeSort() {

        System.out.println("MERGE SORT - O(n log n)");

        int[] sizes = {
                8_000_000,
                16_000_000,
                24_000_000,
                28_000_000,
                32_000_000,
                36_000_000,
                40_000_000
        };

        /*
         * JVM warm-up.
         *
         * Larger warm-up inputs are used so that the relevant
         * sorting methods are executed enough times before
         * collecting the measurements.
         */
        for (int w = 0; w < 3; w++) {

            int[] warm =
                    randomArray(1_000_000, 42L + w);

            mergeSort(warm);
        }

        int lastFeasible = -1;
        double lastTime = Double.NaN;

        for (int n : sizes) {

            /*
             * Input generation takes place before timing.
             */
            int[] data =
                    randomArray(n, 1000L + n);

            long start = System.nanoTime();

            mergeSort(data);

            long elapsed =
                    System.nanoTime() - start;

            /*
             * Consume one value from the result.
             */
            blackhole ^= data[n / 2];

            double seconds =
                    elapsed / 1_000_000_000.0;

            System.out.printf(
                    "n=%,d time=%.3f s %s%n",
                    n,
                    seconds,
                    elapsed <= BUDGET_NS
                            ? "FEASIBLE"
                            : "OVER BUDGET"
            );

            if (elapsed <= BUDGET_NS) {

                lastFeasible = n;
                lastTime = seconds;

            } else {

                /*
                 * Stop after the first tested input
                 * that exceeds the budget.
                 */
                break;
            }
        }

        System.out.printf(
                "Largest tested feasible n: %,d (%.3f s)%n%n",
                lastFeasible,
                lastTime
        );
    }

    private static void mergeSort(int[] a) {

        int[] aux = new int[a.length];

        mergeSort(
                a,
                aux,
                0,
                a.length
        );
    }

    private static void mergeSort(
            int[] a,
            int[] aux,
            int lo,
            int hi) {

        if (hi - lo <= 1) {
            return;
        }

        int mid = (lo + hi) >>> 1;

        mergeSort(a, aux, lo, mid);
        mergeSort(a, aux, mid, hi);

        int i = lo;
        int j = mid;
        int k = lo;

        while (i < mid && j < hi) {

            aux[k++] =
                    a[i] <= a[j]
                            ? a[i++]
                            : a[j++];
        }

        while (i < mid) {
            aux[k++] = a[i++];
        }

        while (j < hi) {
            aux[k++] = a[j++];
        }

        System.arraycopy(
                aux,
                lo,
                a,
                lo,
                hi - lo
        );
    }

    // =========================================================
    // O(n^2) - BUBBLE SORT
    // =========================================================

    private static void runBubbleSort() {

        System.out.println("BUBBLE SORT - O(n^2)");

        int[] sizes = {
                50_000,
                60_000,
                70_000,
                80_000,
                90_000,
                100_000,
                105_000,
                110_000
        };

        /*
         * JVM warm-up.
         */
        for (int w = 0; w < 3; w++) {

            int[] warm =
                    randomArray(20_000, 84L + w);

            bubbleSort(warm);
        }

        int lastFeasible = -1;
        double lastTime = Double.NaN;

        for (int n : sizes) {

            /*
             * Generate the input before starting the timer.
             */
            int[] data =
                    randomArray(n, 2000L + n);

            long start = System.nanoTime();

            bubbleSort(data);

            long elapsed =
                    System.nanoTime() - start;

            blackhole ^= data[n / 2];

            double seconds =
                    elapsed / 1_000_000_000.0;

            System.out.printf(
                    "n=%,d time=%.3f s %s%n",
                    n,
                    seconds,
                    elapsed <= BUDGET_NS
                            ? "FEASIBLE"
                            : "OVER BUDGET"
            );

            if (elapsed <= BUDGET_NS) {

                lastFeasible = n;
                lastTime = seconds;

            } else {

                break;
            }
        }

        System.out.printf(
                "Largest tested feasible n: %,d (%.3f s)%n%n",
                lastFeasible,
                lastTime
        );
    }

    private static void bubbleSort(int[] a) {

        for (int end = a.length - 1;
             end > 0;
             end--) {

            boolean swapped = false;

            for (int i = 0; i < end; i++) {

                if (a[i] > a[i + 1]) {

                    int temp = a[i];

                    a[i] = a[i + 1];
                    a[i + 1] = temp;

                    swapped = true;
                }
            }

            /*
             * If no swap was required, the array is
             * already sorted and execution can stop.
             */
            if (!swapped) {
                break;
            }
        }
    }

    // =========================================================
    // DATA GENERATION
    // =========================================================

    private static int[] randomArray(
            int n,
            long seed) {

        Random random =
                new Random(seed);

        int[] data =
                new int[n];

        for (int i = 0; i < n; i++) {
            data[i] = random.nextInt();
        }

        return data;
    }

    // =========================================================
    // LINEAR SCALING UTILITY
    // =========================================================

    @FunctionalInterface
    interface LongWork {
        void run(long n);
    }

    private static void runLongSizes(
            long[] sizes,
            LongWork work) {

        long lastFeasible = -1;
        double lastTime = Double.NaN;

        for (long n : sizes) {

            long start =
                    System.nanoTime();

            work.run(n);

            long elapsed =
                    System.nanoTime() - start;

            double seconds =
                    elapsed / 1_000_000_000.0;

            System.out.printf(
                    "n=%,d time=%.3f s %s%n",
                    n,
                    seconds,
                    elapsed <= BUDGET_NS
                            ? "FEASIBLE"
                            : "OVER BUDGET"
            );

            if (elapsed <= BUDGET_NS) {

                lastFeasible = n;
                lastTime = seconds;

            } else {

                break;
            }
        }

        System.out.printf(
                "Largest tested feasible n: %,d (%.3f s)%n",
                lastFeasible,
                lastTime
        );
    }
}
