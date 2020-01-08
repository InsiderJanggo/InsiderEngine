package org.CraftTopia.utilities;

public class MultiTimer
{
	private int _timerCount;
	private long[] _startTimes;
	private long[] _stopTimes;
	private long[] _times;
	
	public MultiTimer(int timerCount)
	{
		_timerCount = timerCount;
		_startTimes = new long[timerCount];
		_stopTimes = new long[timerCount];
		_times = new long[timerCount];
	}
	
	public void start(int i)
	{
		_startTimes[i] = System.nanoTime();
	}
	
	public void stop(int i)
	{
		_stopTimes[i] = System.nanoTime();
		_times[i] = _stopTimes[i] - _startTimes[i];
	}
	
	/**
	 * Returns the time in nanoseconds
	 * @param i the index of the timer
	 * @return
	 */
	public long get(int i)
	{
		return _times[i];
	}
	
	public int getTimerCount()
	{
		return _timerCount;
	}
	
}
