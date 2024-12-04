package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = 1000;
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }
    public static SLList<Integer> calc(int n){
        SLList<Integer> ls = new SLList<>();
        while (n>0){
            ls.addLast(n);
            n--;
        }
        return ls;

    }
    public static void calcm(int n, SLList<Integer> ls){
        while (n>0){
            ls.getLast();
            n--;
        }
    }
    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        for (int i = 1000; i <= 128000; i *= 2) {
            SLList<Integer>ls=calc(i);
            Stopwatch stopwatch = new Stopwatch();
            calcm(1000,ls);
            double timeInSeconds = stopwatch.elapsedTime();
            Ns.addLast(i);
            times.addLast(timeInSeconds);
        }
        printTimingTable(Ns, times, Ns);
    }

}
