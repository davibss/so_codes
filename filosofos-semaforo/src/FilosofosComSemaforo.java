import java.util.concurrent.Semaphore;

public class FilosofosComSemaforo {

    private static final int sleepTime = 1000;

    private static final int N = 5;
    private static int left(int i) { return (i+N-1)%N; }
    private static int right(int i) { return (i+1)%N; }
    private static final int THINKING = 0;
    private static final int HUNGRY = 1;
    private static final int EATING = 2;

    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore[] s = new Semaphore[N];
    private static final int[] state = new int[N];

    public static class Philosopher extends Thread{

        private final int i;

        Philosopher(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            while(true){
                think();
                take_forks(i);
                eat();
                put_forks(i);
            }
        }
    }

    private static void put_forks(int i) {
        mutex.release();
        state[i] = THINKING;
        System.out.println("filósofo: "+i+" pensando");
        test(left(i));
        test(right(i));
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void take_forks(int i) {
        mutex.release();
        state[i] = HUNGRY;
        System.out.println("filósofo: "+i+" com fome");
        test(i);
        try {
            mutex.acquire();
        } catch (InterruptedException e) { }
        s[i].release();
    }

    private static void eat() {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void think() {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void test(int i) {
        if (state[i] == HUNGRY && state[left(i)] != EATING && state[right(i)] != EATING) {
            state[i] = EATING;
            System.out.println("filósofo: "+i+" comendo");
            try {
                s[i].acquire();
            } catch (InterruptedException e) { }
        }
    }

    public static void main(String[] args) {
        // Iniciando semáforos para cada filósofo
        for (int j=0;j<s.length;j++){
            s[j] = new Semaphore(1);
        }

        // Iniciando vetor de filósofos
        Philosopher[] philosophers = new Philosopher[N];
        for (int j=0;j<N;j++) {
            // atribuindo um número para cada filósofo
            philosophers[j] = new Philosopher(j);
        }
        // Iniciando "vida" dos filósofos
        for (int j=0;j<N;j++) {
            philosophers[j].start();
        }
    }
}
