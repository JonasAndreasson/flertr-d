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
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
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
	private int itemCounter;

	// -----------------------------------------------------------------------

	private CodeBreaker() {
		StatusWindow w = new StatusWindow();
		w.enableErrorChecks();
		
		workList = w.getWorkList();
		progressList = w.getProgressList();
		mainProgressBar = w.getProgressBar();
		queue = new LinkedBlockingQueue<>();
		pool = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS, queue);
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
			JButton removeTask = new JButton("Remove");
			JButton cancelTask = new JButton("Cancel");
			crack.add(b);
			workList.add(crack);
			b.addActionListener((e) -> {
				workList.remove(crack);
				ProgressItem p = new ProgressItem(n, message);
				progressList.add(p);
				itemCounter++;
				int tempMax = mainProgressBar.getMaximum() + 1000000;
				mainProgressBar.setMaximum(tempMax);
				p.add(cancelTask);
				cancelTask.addActionListener((e2)->{
					JTextArea text1 = p.getTextArea(); //cancel task button
					text1.selectAll();
					text1.replaceSelection("canceled");
				});
				pool.execute(() -> {
					try {
						ProgressTracker progress = new Tracker(p.getProgressBar(), mainProgressBar, itemCounter);
						String cleartext = Factorizer.crack(message, n, progress);
						SwingUtilities.invokeLater(() -> {
							JTextArea text = p.getTextArea();
							text.selectAll();
							text.replaceSelection(cleartext);
							cancelTask.addActionListener((e3) -> {
								 progressList.remove(p);
								 itemCounter--;
								 int tempMax2 = mainProgressBar.getMaximum() - 1000000;
							     
							     mainProgressBar.setMaximum(tempMax2);
							 });
							p.add(removeTask);
							removeTask.addActionListener((e3) -> {
								 progressList.remove(p);
								 itemCounter--;
								 int tempMax2 = mainProgressBar.getMaximum() - 1000000;
							     
							     mainProgressBar.setMaximum(tempMax2);
							 });
						});

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
		private int allProgress;
		private int itemCounter = 0;
		private JProgressBar progress;
		private JProgressBar mainProgress;
		private JPanel progressList;
		

		public Tracker(JProgressBar progress, JProgressBar mainProgress, int itemCounter) {
			this.progress = progress;
			this.progress.setMinimum(0);
			this.progress.setMaximum(1000000);
			this.mainProgress = mainProgress;
			this.itemCounter = itemCounter;
			this.allProgress = mainProgress.getValue();
			
			
		}

		/**
		 * Called by Factorizer to indicate progress. The total sum of ppmDelta from all
		 * calls will add upp to 1000000 (one million).
		 * 
		 * @param ppmDelta portion of work done since last call, measured in ppm (parts
		 *                 per million)
		 */
		@Override
		public void onProgress(int ppmDelta) {
			totalProgress += ppmDelta; //works but not concurrently
			if(itemCounter==1) {
				allProgress = totalProgress;
			}else {
				allProgress += ppmDelta;
			}
			
			SwingUtilities.invokeLater(()-> mainProgress.setValue(allProgress));
			
			SwingUtilities.invokeLater(() -> progress.setValue(totalProgress));
		}
		
	}
}
