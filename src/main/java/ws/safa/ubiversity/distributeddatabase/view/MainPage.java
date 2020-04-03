package ws.safa.ubiversity.distributeddatabase.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import ws.safa.ubiversity.distributeddatabase.business.logic.sort.ParallelBinaryMerge;
import ws.safa.ubiversity.distributeddatabase.business.logic.sort.ParallelMergeAll;
import ws.safa.ubiversity.distributeddatabase.business.logic.sort.ParallelPartitionedSort;
import ws.safa.ubiversity.distributeddatabase.business.logic.sort.ParallelRedistributionBinaryMerge;
import ws.safa.ubiversity.distributeddatabase.business.logic.sort.ParallelRedistributionMergeAll;
import ws.safa.ubiversity.distributeddatabase.model.common.CPU;
import ws.safa.ubiversity.distributeddatabase.model.parallelmergall.Storage;

@Named
@ViewScoped

public class MainPage implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean hasError = false;
	private String errorMessage = "";

	private List<CPU<Integer>> cpus;
	private Storage<Integer> storage;

	private int cpuCount;
	private int noRandomGeneration;
	private boolean saveStep;
	private String selectedAlgorithm = "";
	private int progress;

	private boolean showChart = false;

	private ParallelMergeAll<Integer> parallelMergeAll;
	private ParallelBinaryMerge<Integer> parallelBinaryMerge;
	private ParallelRedistributionBinaryMerge<Integer> parallelRedistributionBinaryMerge;
	private ParallelRedistributionMergeAll<Integer> parallelRedistributionMergeAll;
	private ParallelPartitionedSort<Integer> parallelPartitionedSort;

	public boolean hasError() {
		return hasError;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public List<CPU<Integer>> getCpus() {
		if (cpus == null) {
			cpus = new ArrayList<CPU<Integer>>();
			for (int i = 1; i <= cpuCount; i++) {
				CPU<Integer> cpu = new CPU<Integer>();
				cpu.setCpuName("cpu_" + i);
				cpus.add(cpu);
			}
		}
		return cpus;
	}

	public Storage<Integer> getStorage() {
		if (storage == null) {
			storage = new Storage<Integer>();
		}
		if (storage.getBigData() == null) {
			storage.setBigData(new ArrayList<Integer>());
		}
		return storage;
	}

	public int getCpuCount() {
		if (cpuCount == 0) {
			cpuCount = Runtime.getRuntime().availableProcessors();
		}
		return cpuCount;
	}

	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}

	public int getNoRandomGeneration() {
		if (noRandomGeneration == 0) {
			noRandomGeneration = 1000000;
		}
		return noRandomGeneration;
	}

	public void setNoRandomGeneration(int noRandomGeneration) {
		this.noRandomGeneration = noRandomGeneration;
	}

	public boolean isSaveStep() {
		return saveStep;
	}

	public void setSaveStep(boolean saveStep) {
		this.saveStep = saveStep;
	}

	public String getFullnameSelectedAlgorithm() {
		switch (selectedAlgorithm) {
		case "merge_all":
			return "Parallel Merge-All";
		case "binary_merge":
			return "Parallel Binary-Merge";
		case "redistribution_binary_merge":
			return "Parallel Redistribution Binary-Merge";
		case "redistribution_merge_all":
			return "Parallel Redistribution Merge-All";
		case "partitioned":
			return "Parallel Partitioned";
		default:
			return "";
		}
	}

	public String getSelectedAlgorithm() {
		return selectedAlgorithm;
	}

	public void setSelectedAlgorithm(String selectedAlgorithm) {
		this.selectedAlgorithm = selectedAlgorithm;
	}

	public boolean isShowChart() {
		return showChart;
	}

	public void setShowChart(boolean showChart) {
		this.showChart = showChart;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public ParallelMergeAll<Integer> getParallelMergeAll() {
		if (parallelMergeAll == null) {
			parallelMergeAll = new ParallelMergeAll<Integer>(getStorage(), getCpus(), (x, y) -> x.compareTo(y));
		}
		return parallelMergeAll;
	}

	public ParallelBinaryMerge<Integer> getParallelBinaryMerge() {
		if (parallelBinaryMerge == null) {
			parallelBinaryMerge = new ParallelBinaryMerge<Integer>(getStorage(), getCpus(), (x, y) -> x.compareTo(y));
		}
		return parallelBinaryMerge;
	}

	public ParallelRedistributionBinaryMerge<Integer> getParallelRedistributionBinaryMerge() {
		if (parallelRedistributionBinaryMerge == null) {
			parallelRedistributionBinaryMerge = new ParallelRedistributionBinaryMerge<Integer>(getStorage(), getCpus(),
					(x, y) -> x.compareTo(y));
		}
		return parallelRedistributionBinaryMerge;
	}

	public ParallelRedistributionMergeAll<Integer> getParallelRedistributionMergeAll() {
		if (parallelRedistributionMergeAll == null) {
			parallelRedistributionMergeAll = new ParallelRedistributionMergeAll<Integer>(getStorage(), getCpus(),
					(x, y) -> x.compareTo(y));
		}
		return parallelRedistributionMergeAll;
	}

	public ParallelPartitionedSort<Integer> getParallelPartitionedSort() {
		if (parallelPartitionedSort == null) {
			parallelPartitionedSort = new ParallelPartitionedSort<Integer>(getStorage(), getCpus(),
					(x, y) -> x.compareTo(y));
		}
		return parallelPartitionedSort;
	}

	public void btnStartClick() throws IOException {
		if (!validation()) {
			return;
		}
		reset();
		fillStorage();
		switch (selectedAlgorithm) {
		case "merge_all":
			parallelMergeAll = new ParallelMergeAll<Integer>(getStorage(), getCpus(), (x, y) -> x.compareTo(y));
			getStorage().setSortedData(parallelMergeAll.doSort());
			break;
		case "binary_merge":
			parallelBinaryMerge = new ParallelBinaryMerge<Integer>(getStorage(), getCpus(), (x, y) -> x.compareTo(y));
			getStorage().setSortedData(parallelBinaryMerge.doSort());
			break;
		case "redistribution_binary_merge":
			parallelRedistributionBinaryMerge = new ParallelRedistributionBinaryMerge<Integer>(getStorage(), getCpus(),
					(x, y) -> x.compareTo(y));
			getStorage().setSortedData(parallelRedistributionBinaryMerge.doSort());
			break;
		case "redistribution_merge_all":
			parallelRedistributionMergeAll = new ParallelRedistributionMergeAll<Integer>(getStorage(), getCpus(),
					(x, y) -> x.compareTo(y));
			getStorage().setSortedData(parallelRedistributionMergeAll.doSort());
			break;
		case "partitioned":
			parallelPartitionedSort = new ParallelPartitionedSort<Integer>(getStorage(), getCpus(),
					(x, y) -> x.compareTo(y));
			getStorage().setSortedData(parallelPartitionedSort.doSort());
			break;

		default:
			break;
		}
		showChart = true;
	}

	public void onKeyuplistener() {
		cpus = null;
	}

	public void addProgressBar() {
		progress += 1;
	}

	private void fillStorage() {
		for (int i = 0; i < noRandomGeneration; i++) {
			getStorage().getBigData().add(Math.abs(new Random().nextInt()));
		}
	}

	private void reset() {
		cpus = null;
		storage = null;
		errorMessage = "";
		hasError = false;
	}

	private boolean validation() {
		if (cpuCount % 2 != 0) {
			hasError = true;
			selectedAlgorithm = "";
			errorMessage = "تعداد سی پی یو ها در یک سیستم همیشه زوج هستند :)";
			return false;
		}
		if (cpuCount > noRandomGeneration) {
			hasError = true;
			selectedAlgorithm = "";
			errorMessage = "تعداد اعداد تصادفی کمتر از سی پی یو هاست :)";
			return false;
		}

		if (selectedAlgorithm.contentEquals("binary_merge")) {
			if (!Integer.toBinaryString(cpuCount - 1).replace("1", "").isEmpty()) {
				hasError = true;
				selectedAlgorithm = "";
				errorMessage = "تعداد سی پی یو ها برای الگوریتم انتخابی شما مناسب نیستند :)";
				return false;
			}
		}
		return true;
	}

	public String createChartlabels() {
		StringBuilder result = new StringBuilder();
		switch (selectedAlgorithm) {
		case "merge_all":
			result.append("[ 'Round Robin', 'Local Sort', 'Final Merge', 'Total Activity']");
			break;
		case "binary_merge":
			int sm = parallelBinaryMerge.getAllStepsCpus().size() - 1;
			result.append("[ 'Round Robin',");
			for (int i = 1; i <= sm; i++) {
				result.append("'Sort Step ");
				result.append(i);
				result.append("', 'Merge Step ");
				result.append(i);
				result.append("',");
			}
			result.append("'Total Activity']");
			break;
		case "redistribution_binary_merge":
			result.append(
					"[ 'Round Robin', 'Local Sort',  'Bin Redistribute', 'Local Sort',  'Final Redistribute', 'Local Sort', 'Total Activity']");
			break;
		case "redistribution_merge_all":
			result.append("[ 'Round Robin', 'Local Sort',  'Final Redistribute', 'Local Sort', 'Total Activity']");
			break;
		case "partitioned":
			result.append("[ 'Round Robin', 'Final Redistribute', 'Local Sort','Total Activity']");
			break;

		default:
			break;
		}
		return result.toString();
	}

	public String createChartDatasets() {
		String[] colors = new String[] { "red", "orange", "yellow", "green", "blue", "purple", "grey", "silver", "gray",
				"maroon", "olive", "teal", "fuchsia", "khaki", "springgreen", "skyblue", "burlywood" };

		StringBuilder result = new StringBuilder();
		result.append("[");

		// switch (selectedAlgorithm) {
		// case "merge_all": {
		int c = 1;
		for (CPU<Integer> cpu : cpus) {
			result.append("{");
			result.append("label: '");
			result.append(cpu.getCpuName());
			result.append("',");

			result.append("backgroundColor: color(window.chartColors.");
			result.append(colors[colors.length % c]);
			result.append(").alpha(0.5).rgbString(),");

			result.append("borderColor : window.chartColors.");
			result.append(colors[colors.length % c]);
			result.append(",");

			result.append("borderWidth : 1,");

			result.append("data : [ ");
			switch (selectedAlgorithm) {
			case "merge_all":
				result.append(getDataChartMergeAll(cpu));
				break;
			case "binary_merge":
				result.append(getDataChartBinaryMerge(cpu));
				break;
			case "redistribution_binary_merge":
				result.append(getDataRedistributionBinaryMerge(cpu));
				break;
			case "redistribution_merge_all":
				result.append(getDataRedistributionMergeAll(cpu));
				break;
			case "partitioned":
				result.append(getDataPartitionedSort(cpu));
				break;
			}
			result.append("] ");

			result.append("},");
			c++;
		}
		// break;
		// }
		// case "":

		// }

		result.append("]");
		return result.toString();
	}

	private String getDataChartMergeAll(CPU<Integer> cpu) {
		StringBuilder result = new StringBuilder();
		// [ 'Round Robin', 'Local Sort', 'Final Merge', 'Total Activity']
		result.append(cpu.getActivityRoundRobin());
		result.append(",");
		result.append(cpu.getActivityLocalSort());
		result.append(",");
		result.append(cpu.getActivityFinalMerge());
		result.append(",");
		result.append(cpu.getNumberOfActivities());
		return result.toString();
	}

	private String getDataChartBinaryMerge(CPU<Integer> cpu) {
		StringBuilder result = new StringBuilder();
		// [ 'Round Robin', 'Local Sort', 'Final Merge', ... , 'Total Activity']

		result.append(cpu.getActivityRoundRobin());
		result.append(",");
		Map<String, List<CPU<Integer>>> allStep = parallelBinaryMerge.getCpusHistories();
		int sm = parallelBinaryMerge.getAllStepsCpus().size() - 1;
		for (int i = 0; i < sm; i++) {
			String ps = "pls" + i;
			String pm = "pm" + i;
			CPU<Integer> cpups = allStep.get(ps).parallelStream().filter(c -> c.getCpuName().equals(cpu.getCpuName()))
					.findAny().orElse(new CPU<>());
			CPU<Integer> cpupm = allStep.get(pm).parallelStream().filter(c -> c.getCpuName().equals(cpu.getCpuName()))
					.findAny().orElse(new CPU<>());
			result.append(cpups.getActivityLocalSort());
			result.append(",");
			result.append(cpupm.getActivityFinalMerge());
			result.append(",");
		}
		result.append(cpu.getNumberOfActivities());

		return result.toString();
	}

	private String getDataRedistributionBinaryMerge(CPU<Integer> cpu) {
		StringBuilder result = new StringBuilder();
		// [ 'Round Robin', 'Local Sort', 'Bin Redistribute', 'Local Sort', 'Final
		// Redistribute', 'Local Sort', 'Total Activity']

		CPU<Integer> temp = parallelRedistributionBinaryMerge.getCpusHistories().get("prr").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(temp.getActivityRoundRobin());
		result.append(",");

		temp = parallelRedistributionBinaryMerge.getCpusHistories().get("pls").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(temp.getActivityLocalSort());
		result.append(",");

		temp = parallelRedistributionBinaryMerge.getCpusHistories().get("pbr").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(temp.getActivityRedistribute());
		result.append(",");

		temp = parallelRedistributionBinaryMerge.getCpusHistories().get("pbrs").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(temp.getActivityLocalSort());
		result.append(",");

		temp = parallelRedistributionBinaryMerge.getCpusHistories().get("pr").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(cpu.getActivityRedistribute());
		result.append(",");

		temp = parallelRedistributionBinaryMerge.getCpusHistories().get("prs").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(cpu.getActivityRedistribute());
		result.append(",");

		result.append(cpu.getNumberOfActivities());
		return result.toString();
	}

	private String getDataRedistributionMergeAll(CPU<Integer> cpu) {
		StringBuilder result = new StringBuilder();
		// [ 'Round Robin', 'Local Sort', 'Bin Redistribute', 'Local Sort', 'Final
		// Redistribute', 'Local Sort', 'Total Activity']

		CPU<Integer> temp = parallelRedistributionMergeAll.getCpusHistories().get("prr").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(temp.getActivityRoundRobin());
		result.append(",");

		temp = parallelRedistributionMergeAll.getCpusHistories().get("pls").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(temp.getActivityLocalSort());
		result.append(",");

		temp = parallelRedistributionMergeAll.getCpusHistories().get("pr").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(cpu.getActivityRedistribute());
		result.append(",");

		temp = parallelRedistributionMergeAll.getCpusHistories().get("prs").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(cpu.getActivityRedistribute());
		result.append(",");

		result.append(cpu.getNumberOfActivities());
		return result.toString();
	}

	private String getDataPartitionedSort(CPU<Integer> cpu) {
		StringBuilder result = new StringBuilder();
		// [ 'Round Robin', 'Local Sort', 'Bin Redistribute', 'Local Sort', 'Final
		// Redistribute', 'Local Sort', 'Total Activity']

		CPU<Integer> temp = parallelPartitionedSort.getCpusHistories().get("prr").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(temp.getActivityRoundRobin());
		result.append(",");

		temp = parallelPartitionedSort.getCpusHistories().get("pr").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(cpu.getActivityRedistribute());
		result.append(",");

		temp = parallelPartitionedSort.getCpusHistories().get("prs").parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		result.append(cpu.getActivityRedistribute());
		result.append(",");

		result.append(cpu.getNumberOfActivities());
		return result.toString();
	}

	public void btnDownloadDataClick() {
		try {
			download(getStorage().getBigData(), "bigdata.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadSortDataClick() {
		try {
			download(getStorage().getSortedData(), "sortedBigdata.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// MergeAll
	public void btnDownloadParallelMergeAllRR(int cpuindex) {
		try {
			download(parallelMergeAll.getCpusHistories().get("prr").get(cpuindex).getBuffer(),
					"ParallelMergeAll_CPU_NO_" + (cpuindex + 1) + "_RoundRobin.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadParallelMergeAllLS(int cpuindex) {
		try {
			download(parallelMergeAll.getCpusHistories().get("pls").get(cpuindex).getBuffer(),
					"ParallelMergeAll_CPU_NO_" + (cpuindex + 1) + "_LocalSort.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadParallelMergeAllFM(int cpuindex) {
		try {
			download(parallelMergeAll.getCpusHistories().get("pfm").get(cpuindex).getBuffer(),
					"ParallelMergeAll_CPU_NO_" + (cpuindex + 1) + "_FinalMerge.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStylebtnDownloadParallelMergeAllFM(int cpuindex) {
		return parallelMergeAll.getCpusHistories().get("pfm").get(cpuindex).getBuffer().isEmpty()
				? "btn btn-outline-warning"
				: "btn btn-info";
	}

	// BinaryMerge
	public void btnDownloadParallelBinaryMergeRR(int cpuindex) {
		try {
			download(parallelBinaryMerge.getCpusHistories().get("prr").get(cpuindex).getBuffer(),
					"ParallelBinaryMerge_CPU_NO_" + (cpuindex + 1) + "_RoundRobin.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Integer> getParallelBinarySteps() {
		List<Integer> result = new ArrayList<Integer>();
		int sm = parallelBinaryMerge.getAllStepsCpus().size() - 1;
		for (int i = 0; i < sm; i++) {
			result.add(i);
		}
		return result;
	}

	public void btnDownloadParallelBinaryMergePS(CPU<Integer> cpu, int step) {
		try {
			String pls = "pls" + step;
			CPU<Integer> cpupls = parallelBinaryMerge.getCpusHistories().get(pls).parallelStream()
					.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
			download(cpupls.getBuffer(),
					"ParallelBinaryMerge_CPU_NO_" + cpu.getCpuName() + "_SortStep_" + step + ".csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadParallelBinaryMergePM(CPU<Integer> cpu, int step) {
		try {
			String pm = "pm" + step;
			CPU<Integer> cpupm = parallelBinaryMerge.getCpusHistories().get(pm).parallelStream()
					.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
			download(cpupm.getBuffer(),
					"ParallelBinaryMerge_CPU_NO_" + cpu.getCpuName() + "_MergeStep_" + step + ".csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStyleBtnDownloadParallelBinaryMergePS(CPU<Integer> cpu, int step) {
		String pls = "pls" + step;
		CPU<Integer> cpupls = parallelBinaryMerge.getCpusHistories().get(pls).parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		return cpupls.getBuffer().isEmpty() ? "btn btn-outline-warning" : "btn btn-info";
	}

	public String getStyleBtnDownloadParallelBinaryMergePM(CPU<Integer> cpu, int step) {
		String pm = "pm" + step;
		CPU<Integer> cpupls = parallelBinaryMerge.getCpusHistories().get(pm).parallelStream()
				.filter(c -> c.getCpuName().equals(cpu.getCpuName())).findAny().orElse(new CPU<>());
		return cpupls.getBuffer().isEmpty() ? "btn btn-outline-warning" : "btn btn-info";
	}

	// RedistributionBinaryMerge
	public void btnDownloadRedistributionBinaryMergeRR(int cpuindex) {
		try {
			download(parallelRedistributionBinaryMerge.getCpusHistories().get("prr").get(cpuindex).getBuffer(),
					"ParallelRedistributionBinaryMerge_CPU_NO_" + (cpuindex + 1) + "_RoundRobin.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadRedistributionBinaryMergeLS(int cpuindex) {
		try {
			download(parallelRedistributionBinaryMerge.getCpusHistories().get("pls").get(cpuindex).getBuffer(),
					"ParallelRedistributionBinaryMerge_CPU_NO_" + (cpuindex + 1) + "_LocalSort_1.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadRedistributionBinaryMergeBR(int cpuindex) {
		try {
			download(parallelRedistributionBinaryMerge.getCpusHistories().get("pbr").get(cpuindex).getBuffer(),
					"ParallelRedistributionBinaryMerge_CPU_NO_" + (cpuindex + 1) + "_BinaryRedistribute.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadRedistributionBinaryMergeBRS(int cpuindex) {
		try {
			download(parallelRedistributionBinaryMerge.getCpusHistories().get("pbrs").get(cpuindex).getBuffer(),
					"ParallelRedistributionBinaryMerge_CPU_NO_" + (cpuindex + 1) + "_LocalSort_2.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadRedistributionBinaryMergePR(int cpuindex) {
		try {
			download(parallelRedistributionBinaryMerge.getCpusHistories().get("pr").get(cpuindex).getBuffer(),
					"ParallelRedistributionBinaryMerge_CPU_NO_" + (cpuindex + 1) + "_Redistribute.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadRedistributionBinaryMergeRS(int cpuindex) {
		try {
			download(parallelRedistributionBinaryMerge.getCpusHistories().get("prs").get(cpuindex).getBuffer(),
					"ParallelRedistributionBinaryMerge_CPU_NO_" + (cpuindex + 1) + "_LocalSort_3.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// PartitionedSort
	public void btnDownloadPartitionedSortRR(int cpuindex) {
		try {
			download(parallelPartitionedSort.getCpusHistories().get("prr").get(cpuindex).getBuffer(),
					"ParallelPartitionedSort_CPU_NO_" + (cpuindex + 1) + "_RoundRobin.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadPartitionedSortPR(int cpuindex) {
		try {
			download(parallelPartitionedSort.getCpusHistories().get("pr").get(cpuindex).getBuffer(),
					"ParallelPartitionedSort_CPU_NO_" + (cpuindex + 1) + "_Redistribute.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadPartitionedSortRS(int cpuindex) {
		try {
			download(parallelPartitionedSort.getCpusHistories().get("prs").get(cpuindex).getBuffer(),
					"ParallelPartitionedSort_CPU_NO_" + (cpuindex + 1) + "_LocalSort.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Redistribution Merge-All
	public void btnDownloadRedistributionMergeAllRR(int cpuindex) {
		try {
			download(parallelRedistributionMergeAll.getCpusHistories().get("prr").get(cpuindex).getBuffer(),
					"ParallelRedistributionBinaryMerge_CPU_NO_" + (cpuindex + 1) + "_RoundRobin.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadRedistributionMergeAllLS(int cpuindex) {
		try {
			download(parallelRedistributionBinaryMerge.getCpusHistories().get("pls").get(cpuindex).getBuffer(),
					"ParallelRedistribution_Merge_All_CPU_NO_" + (cpuindex + 1) + "_LocalSort_1.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadRedistributionMergeAllPR(int cpuindex) {
		try {
			download(parallelRedistributionBinaryMerge.getCpusHistories().get("pr").get(cpuindex).getBuffer(),
					"ParallelRedistribution_Merge_All_CPU_NO_" + (cpuindex + 1) + "_Redistribute.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void btnDownloadRedistributionMergeAllRS(int cpuindex) {
		try {
			download(parallelRedistributionBinaryMerge.getCpusHistories().get("prs").get(cpuindex).getBuffer(),
					"ParallelRedistribution_Merge_All_CPU_NO_" + (cpuindex + 1) + "_LocalSort_Final.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void download(List<Integer> data, String filename) throws IOException {
		String collect = data.stream().map(Object::toString).collect(Collectors.joining(","));
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		response.setContentType("text/csv");
		response.setContentLength(collect.getBytes().length);
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		response.getOutputStream().write(collect.getBytes());
		response.getOutputStream().flush();
		response.getOutputStream().close();
		FacesContext.getCurrentInstance().responseComplete();
	}

}
