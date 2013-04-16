package com.tranek.chivalryserverbrowser;

/**
 * 
 * A data structure class that holds all of the server filters for a {@link ServerListInterface}.
 *
 */
public class ServerFilters {
	/** Server name filter. */
	protected String name;
	/** Game mode filter. */
	protected String type;
	/** Hide passworded servers filter. */
	protected boolean hidePassword;
	/** Minimum rank filter. */
	protected int minRank;
	/** Maximum rank filter. */
	protected int maxRank;
	/** Maximum ping filter. */
	protected int maxPing;
	/** Hide empty servers filter. */
	protected boolean hideEmpty;
	/** Hide full servers filter. */
	protected boolean hideFull;
	/** Show only official servers filter. */
	protected boolean officialservers;
	/** Server's allowed player perspectives filter. */
	protected int perspective;
	/** Number of threads to query the servers with. */
	protected int numThreads;
	
	/**
	 * Creates a new ServerFilters with default values.
	 */
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
	
	/**
	 * Creates a new ServerFilters.
	 * 
	 * @param name the server name filter
	 * @param type the game mode filter
	 * @param hidepassword hide passworded servers filter
	 * @param minrank the minimum rank filter
	 * @param maxrank the maximum rank filter
	 * @param maxping the maximum ping filter
	 * @param hideempty hide empty servers filter
	 * @param hidefull hide full servers filter
	 * @param officialservers show only official servers filter
	 * @param perspective the server's allowed player perspectives
	 * @param numthreads the number of threads to query the servers with
	 */
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
