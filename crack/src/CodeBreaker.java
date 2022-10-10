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
	private ThreadPoolExecutor pool;
	private final JProgressBar mainProgressBar;

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
			JButton crackButton = new JButton("Crack!");
			JButton removeTask = new JButton("Remove");
			JButton cancelTask = new JButton("Cancel");
			ProgressItem p = new ProgressItem(n, message);
			crack.add(crackButton);
			workList.add(crack);

			Runnable crackTask = () -> {
				ProgressTracker progress = new Tracker(p.getProgressBar(), mainProgressBar);
				try {
					String cleartext = Factorizer.crack(message, n, progress);
					SwingUtilities.invokeLater(() -> {
						JTextArea text = p.getTextArea();
						text.selectAll();
						text.replaceSelection(cleartext);
						p.remove(cancelTask);
						p.add(removeTask);
					});
				} catch (InterruptedException crackException) {
					return;
				}
			};

			crackButton.addActionListener((e) -> {
				workList.remove(crack);
				progressList.add(p);
				Future f = pool.submit(crackTask);
				mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);
				cancelTask.addActionListener((e3) -> {
					f.cancel(true);
					SwingUtilities.invokeLater(() -> {
						JTextArea text1 = p.getTextArea();
						text1.selectAll();
						text1.replaceSelection("canceled");
						mainProgressBar.setValue(mainProgressBar.getValue() - p.getProgressBar().getValue());
						p.getProgressBar().setValue(1000000);
						mainProgressBar.setValue(mainProgressBar.getValue() + p.getProgressBar().getValue());
						p.remove(cancelTask);
						p.add(removeTask);
					});

				});
				removeTask.addActionListener((e2) -> {
					progressList.remove(p);
					mainProgressBar.setValue(mainProgressBar.getValue() - p.getProgressBar().getValue());
					mainProgressBar.setMaximum(mainProgressBar.getMaximum() - p.getProgressBar().getMaximum());
				});
				p.add(cancelTask);
			});
		});
	}

	private static class Tracker implements ProgressTracker {
		private int totalProgress = 0;
		private JProgressBar progress;
		private JProgressBar mainProgress;

		public Tracker(JProgressBar progress, JProgressBar mainProgress) {
			this.progress = progress;
			this.progress.setMinimum(0);
			this.progress.setMaximum(1000000);
			this.mainProgress = mainProgress;
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
			totalProgress += ppmDelta; // works but not concurrently

			SwingUtilities.invokeLater(() -> mainProgress.setValue(mainProgress.getValue() + ppmDelta));

			SwingUtilities.invokeLater(() -> progress.setValue(totalProgress));
		}

	}
}
