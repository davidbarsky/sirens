package edu.brandeis.dag.models;

public class StartEndTime {
    private final int start;
	private final int end;

    public StartEndTime(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
}
