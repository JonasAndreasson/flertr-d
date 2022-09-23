import java.awt.Container;
import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.*;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;
    private BlockingQueue<Runnable> queue;
    private Executor pool;
    private final JProgressBar mainProgressBar;

    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();

        workList        = w.getWorkList();
        progressList    = w.getProgressList();
        mainProgressBar = w.getProgressBar();
        queue = new LinkedBlockingQueue<>();        	
        pool = new ThreadPoolExecutor(2,2,0,TimeUnit.SECONDS,queue);
    }
    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) {

        /*
         * Most Swing operations (such as creating view elements) must be performed in
         * the Swing EDT (Event Dispatch Thread).
         * 
         * That's what SwingUtilities.invokeLater is for.
         */

        SwingUtilities.invokeLater(() -> {
            CodeBreaker codeBreaker = new CodeBreaker();
            new Sniffer(codeBreaker).start();
        });
    }

    // -----------------------------------------------------------------------

    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n) {
        System.out.println("message intercepted (N=" + n + ")...");
        SwingUtilities.invokeLater(() -> {
        	WorklistItem crack = new WorklistItem(n, message);
        	JButton b = new JButton("Crack!");
        	crack.add(b);
			workList.add(crack);
        	b.addActionListener((e)->{
        		Container p = b.getParent();
        		workList.remove(p);
        		progressList.add(p);
        		pool.execute(() -> {
					try {
						ProgressTracker progress = new Tracker();
						Factorizer.crack(message, n, progress);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
        });
    });
    }
    
    private static class Tracker implements ProgressTracker {
        private int totalProgress = 0;

        /**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add upp to 1000000 (one million).
         * 
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
            totalProgress += ppmDelta;
            System.out.println("progress = " + totalProgress + "/1000000");
        }
    }
}
