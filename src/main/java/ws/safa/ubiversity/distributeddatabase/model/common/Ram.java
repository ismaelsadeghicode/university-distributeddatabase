package ws.safa.ubiversity.distributeddatabase.model.common;

import java.util.List;

public class Ram<T> {
	private int size;
	private List<T> buffer;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<T> getBuffer() {
		return buffer;
	}

	public void setBuffer(List<T> buffer) {
		this.buffer = buffer;
	}
}
