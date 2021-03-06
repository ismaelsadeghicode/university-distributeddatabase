package ws.safa.ubiversity.distributeddatabase.model.parallelmergall;

import java.util.List;

public class BusRamCpu<T> {
	private int recordSize;
	private List<Object> buffer;
	private Ram<T> ram;
	private CPU<T> cpu;

	public int getRecordSize() {
		return recordSize;
	}

	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}

	public List<Object> getBuffer() {
		return buffer;
	}

	public void setBuffer(List<Object> buffer) {
		this.buffer = buffer;
	}

	public Ram<T> getRam() {
		return ram;
	}

	public void setRam(Ram<T> ram) {
		this.ram = ram;
	}

	public CPU<T> getCpu() {
		return cpu;
	}

	public void setCpu(CPU<T> cpu) {
		this.cpu = cpu;
	}

}
