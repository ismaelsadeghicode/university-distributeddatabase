package ws.safa.ubiversity.distributeddatabase.business.logic.datapartitioning;

import java.util.List;

public abstract class DataPartitioning<T> {
	protected List<T> data;
	protected int partitions;

	public DataPartitioning(List<T> data, int partitions) {
		this.data = data;
		this.partitions = partitions;
	}

	public abstract List<? extends List<T>> getpartitions();
}
