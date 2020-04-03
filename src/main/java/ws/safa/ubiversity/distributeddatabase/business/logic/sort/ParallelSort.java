package ws.safa.ubiversity.distributeddatabase.business.logic.sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ws.safa.ubiversity.distributeddatabase.model.common.CPU;
import ws.safa.ubiversity.distributeddatabase.model.parallelmergall.Storage;

public abstract class ParallelSort<T> {
	protected long timeRoundRobin;

	protected Storage<T> storage;
	protected List<CPU<T>> cpus;
	protected Map<String, List<CPU<T>>> cpusHistories;
	protected Comparator<? super T> comparator;

	public ParallelSort(Storage<T> storage, List<CPU<T>> cpus, Comparator<? super T> comparator) {
		this.storage = storage;
		this.cpus = cpus;
		this.comparator = comparator;
		cpusHistories = new HashMap<String, List<CPU<T>>>();
	}

	public long getTimeRoundRobin() {
		return timeRoundRobin;
	}

	public Map<String, List<CPU<T>>> getCpusHistories() {
		return cpusHistories;
	}

	public abstract List<T> doSort() throws IOException;

	protected List<CPU<T>> cloneCpus(List<CPU<T>> cpus) {
		List<CPU<T>> result = new ArrayList<CPU<T>>();
		for (CPU<T> cpu : cpus) {
			CPU<T> temp = new CPU<>(cpu.getCpuName(), new ArrayList<>(cpu.getBuffer()),
					new ArrayList<>(cpu.getTemporary()), cpu.getComparator(), cpu.getActivityRoundRobin(),
					cpu.getNumberOfActivities(), cpu.getActivityLocalSort(), cpu.getActivityFinalMerge());
			temp.setActivityRedistribute(cpu.getActivityRedistribute());
			result.add(temp);
		}

		return result;
	}
}
