package ws.safa.ubiversity.distributeddatabase.model.common;

import java.util.List;

public class BusStorageRam<T> {
	private int pageSize;
	private List<T> buffer;
	private Storage<T> storage;
	private Ram<T> ram;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<T> getBuffer() {
		return buffer;
	}

	public void setBuffer(List<T> buffer) {
		this.buffer = buffer;
	}

	public Storage<T> getStorage() {
		return storage;
	}

	public void setStorage(Storage<T> storage) {
		this.storage = storage;
	}

	public Ram<T> getRam() {
		return ram;
	}

	public void setRam(Ram<T> ram) {
		this.ram = ram;
	}

}
