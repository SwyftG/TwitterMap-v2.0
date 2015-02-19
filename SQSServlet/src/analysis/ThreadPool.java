package analysis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	ExecutorService pool = null; // fix number
	private static final ThreadPool threadPool = new ThreadPool();

	private ThreadPool() {

	}

	public static ThreadPool getInstance() {
		return threadPool;
	}

	public ExecutorService startPool() {
		
		pool = Executors.newFixedThreadPool(5);
		System.out.println("after start pool isShutdown:"+pool.isShutdown());
		System.out.println("start pool id:"+pool);
		return pool;
	}

	public void shutPool() {
		System.out.println("shut pool id:"+pool);
		pool.shutdown();
//		try {
//			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("After shut pool isShutdown:"+pool.isShutdown());
	}

	public static void main(String args[]) throws InterruptedException {
		ThreadPool threadPool = ThreadPool.getInstance();
		ExecutorService service = threadPool.startPool();
		for (int i = 0; i < 10; i++) {
			Runnable run = new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 3; i++) {
						System.out.println(i);
					}
				}
			};
			Thread.sleep(1000);
			service.execute(run);
		}
		Thread.sleep(3000);
		threadPool.shutPool();
	}
}
