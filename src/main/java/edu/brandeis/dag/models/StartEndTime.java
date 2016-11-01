package edu.brandeis.dag.models;

public class StartEndTime {
    private final int start;
	private final int end;
	private final int duration;

    public StartEndTime(int start, int end) {
        this.start = start;
        this.end = end;
        this.duration = end - start;
    }
    
    public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

    public int getDuration() {
        return duration;
    }
}
