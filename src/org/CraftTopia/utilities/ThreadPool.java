package org.CraftTopia.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadPool
{
	private int _maximumThreads;
	private volatile int _runningThreads;
	private List<WaitingRunnable> _waitingRunnables;

	public ThreadPool(int maximumThreads)
	{
		_maximumThreads = maximumThreads;
		_waitingRunnables = new ArrayList<WaitingRunnable>();
	}

	public void addThread(Runnable runnable, int priority)
	{
		synchronized (this)
		{
			_waitingRunnables.add(new WaitingRunnable(runnable, priority));
			Collections.sort(_waitingRunnables);
		}
		manage();
	}

	private synchronized void manage()
	{
		if (_runningThreads < _maximumThreads && !_waitingRunnables.isEmpty())
		{
			final WaitingRunnable logic = _waitingRunnables.remove(_waitingRunnables.size() - 1);
			Thread t = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					++_runningThreads;
					logic._runnable.run();
					--_runningThreads;
					manage();
				}
			});
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();
		}
	}
	
	private static class WaitingRunnable implements Comparable<WaitingRunnable>
	{
		private Runnable _runnable;
		private int _priority;
		
		public WaitingRunnable(Runnable runnable, int priority)
		{
			_runnable = runnable;
			_priority = priority;
		}
		
		@Override
		public int compareTo(WaitingRunnable o)
		{
			return o._priority - _priority;
		}
		
		
	}

	public boolean isFull()
	{
		return _maximumThreads == _runningThreads;
	}
}