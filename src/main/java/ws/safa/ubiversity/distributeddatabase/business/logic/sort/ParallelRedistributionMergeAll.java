package ws.safa.ubiversity.distributeddatabase.business.logic.sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ws.safa.ubiversity.distributeddatabase.business.logic.datapartitioning.Redistribute;
import ws.safa.ubiversity.distributeddatabase.business.logic.datapartitioning.RoundRobin;
import ws.safa.ubiversity.distributeddatabase.model.common.CPU;
import ws.safa.ubiversity.distributeddatabase.model.parallelmergall.Storage;

public class ParallelRedistributionMergeAll<T> extends ParallelSort<T> {
	private static final Logger LOG = Logger.getLogger(ParallelMergeAll.class.getName());
	private long timePhaseLocalSort;
	private long timePhaseBinaryRedistribute;

	public ParallelRedistributionMergeAll(Storage<T> storage, List<CPU<T>> cpus, Comparator<? super T> comparator) {
		super(storage, cpus, comparator);
	}

	@Override
	public List<T> doSort() throws IOException {

		try {
			List<T> result = new ArrayList<>();
			phaseRoundRobin();
			cpusHistories.put("prr", cloneCpus(cpus));

			phaseLocalSort();
			cpusHistories.put("pls", cloneCpus(cpus));

			phaseRedistribute();
			cpusHistories.put("pr", cloneCpus(cpus));

			phaseLocalSort();
			cpusHistories.put("prs", cloneCpus(cpus));

			for (CPU<T> cpu : cpus) {
				for (T t : cpu.getBuffer()) {
					result.add(t);
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void phaseRoundRobin() throws IOException {
		LOG.log(Level.INFO, "Start RoundRobin");
		timeRoundRobin = System.currentTimeMillis();
		RoundRobin<T> roundRobin = new RoundRobin<T>(storage.getBigData(), cpus.size());
		List<List<T>> result = roundRobin.getpartitions();

		int c = 0;
		for (CPU<T> cpu : cpus) {
			cpu.setBuffer(result.get(c++));
		}

		timeRoundRobin = (System.currentTimeMillis() - timeRoundRobin);
		LOG.log(Level.INFO, "End of RoundRobin");
	}

	private void phaseLocalSort() {
		LOG.log(Level.INFO, "Local Sorting Started.");
		timePhaseLocalSort = System.currentTimeMillis();
		for (CPU<T> cpu : cpus) {
			cpu.sort(comparator);
		}
		timePhaseLocalSort = (System.currentTimeMillis() - timePhaseLocalSort);
		LOG.log(Level.INFO, "Local Sorting Ended.");
	}

	private void phaseRedistribute() {
		LOG.log(Level.INFO, "Redistributing Started.");
		timePhaseBinaryRedistribute = System.currentTimeMillis();
		new Redistribute<>(cpus).doRedistribute();
		timePhaseBinaryRedistribute = (System.currentTimeMillis() - timePhaseBinaryRedistribute);
		LOG.log(Level.INFO, "Redistributing Ended.");
	}

}
