package com.tranek.chivalryserverbrowser;

public class ServerFilters {
	
	public String name;
	public String type;
	public boolean hidePassword;
	public int minRank;
	public int maxRank;
	public int maxPing;
	public boolean hideEmpty;
	public boolean hideFull;
	public boolean officialservers;
	public int perspective;
	public int numThreads;
	
	public ServerFilters() {
		this.name = "";
		this.type = "ALL";
		this.hidePassword = false;
		this.minRank = -1;
		this.maxPing = -1;
		this.maxRank = -1;
		this.hideEmpty = false;
		this.hideFull = false;
		this.officialservers = false;
		this.perspective = 0;
		this.numThreads = 8;
	}
	
	public ServerFilters(String name, String type, boolean hidepassword,
			int minrank, int maxrank, int maxping, boolean hideempty,
			boolean hidefull, boolean officialservers, int perspective,
			int numthreads) {
		this.name = name;
		this.type = type;
		this.hidePassword = hidepassword;
		this.minRank = minrank;
		this.maxRank = maxrank;
		this.maxPing = maxping;
		this.hideEmpty = hideempty;
		this.hideFull = hidefull;
		this.officialservers = officialservers;
		this.perspective = perspective;
		this.numThreads = numthreads;
	}

}
