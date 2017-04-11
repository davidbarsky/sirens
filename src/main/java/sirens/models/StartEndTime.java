package sirens.models;

public class StartEndTime {
    private final int start;
    private final int networkingStart;
	private final int end;
	private final int duration;

    public StartEndTime(int start, int end, int networkStart) {
        this.start = start;
        this.end = end;
        this.duration = end - start ;
        this.networkingStart = networkStart;
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
    
    public int getNetworkingStart() {
    	return networkingStart;
    }
}
