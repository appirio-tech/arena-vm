package com.topcoder.client.spectatorApp.serverSimulator;


public class ComponentServerSimulator extends Thread {
//	private static final Category cat = Category.getInstance(ComponentServerSimulator.class.getName());
//	
//	/** Port to listen on */
//	private final static int PORT = 8089;
//
//	/** Valid contestid/roundid */
//	private final int CONTESTID = 5000;
//	private final int ROUNDID = 5001;
//	private final int TOTALTIME = 120; // in seconds
//	
//	/** Component IDs valid */
//	private final ComponentData[] componentData;
//	
//	/** Component Coders */
//	private final CoderData[] coders;
//	
//	/** Reviewers */
//	private final CoderData[] reviewers;
//	
//	/** Wagers */
//	private final int[][] wagers;
//	
//	/** Scores */
//	private final int[][][] scores;
//	
//	/** Appeals */
//	private final AppealStatus[][][] appeals;
//	private final long[][][] appealsID;
//	
//	/** Lock object to access either scores or appeals */
//	private final Object lock = new Object();
//
//	/** Timer used to control events */
//	public final Timer[] timer;
//
//	/** Random seed */
//	public final static Random rand = new Random(55);
//	
//	/**
//	 * Main method used to start the simulator
//	 */
//	public static void main(String[] args) {
//		new ComponentServerSimulator();
//	}
//	
//	public ComponentServerSimulator() {
//		componentData = new ComponentData[] { new ComponentData(1, "Relation", "Java"),
//					                             new ComponentData(2, "XSD2DDL", "Java"),
//					                             new ComponentData(3, "DataView", "Java"),
//					                             new ComponentData(4, "Tie Breaker1", "Java"),
//					                             new ComponentData(5, "Tie Breaker2", "Java"),
//					                             new ComponentData(6, "Tie Breaker3", "Java"),
//		};
//		
//		coders = new CoderData[] { new CoderData(1, "Coder 1", 1234),
//			                        new CoderData(2, "Coder 2", 2391),		    
//			                        new CoderData(3, "Coder 3", 2234),		    
//			                        new CoderData(4, "Coder 4", 1929),		    
//			                        new CoderData(5, "Coder 5", 1892),		    
//			                        new CoderData(6, "Coder 6", 1999),		    
//			                        new CoderData(7, "Coder 7", 2200),		    
//			                        new CoderData(8, "Coder 8", 1000),		    
//		};
//		
//		reviewers = new CoderData[] { new CoderData(1001, "Reviewer 1", 2929),
//			                           new CoderData(1002, "Reviewer 2", 1818),
//			                           new CoderData(1003, "Reviewer 3", 2222)
//		};
//		
//		timer = new Timer[componentData.length];
//		
//		wagers = new int[componentData.length][coders.length];
//		for(int x = 0; x < coders.length; x++) {
//				int w1 = rand.nextInt(71) + 10;
//				int w2 = rand.nextInt(81-w1) + 10;
//				int w3 = 100 - w2 - w1;
//				wagers[0][x] = w1;
//				wagers[1][x] = w2;
//				wagers[2][x] = w3;
//				wagers[3][x] = w1;
//				wagers[4][x] = w2;
//				wagers[5][x] = w3;
//		}
//		
//		scores = new int[componentData.length][coders.length][reviewers.length];
//		appeals = new AppealStatus[componentData.length][coders.length][reviewers.length];
//		appealsID = new long[componentData.length][coders.length][reviewers.length];
//		for(int c = 0; c < componentData.length; c++) {
//			for(int x = 0; x < coders.length; x++) {
//				for (int y = 0; y < reviewers.length; y++) {
//					if (c < 3) {
//						scores[c][x][y] = (24+rand.nextInt(76) + 1)*100 + rand.nextInt(101);
//					} else {
//						scores[c][x][y] = x < 4 ? 5000:7500;
//					}
//					appeals[c][x][y] = AppealStatus.None;
//					appealsID[c][x][y] = -1;
//				}
//			}
//		}
//		
//		setDaemon(false);
//		start();
//	}
//	
//	private TimerTask appealUpdate(final int c, final int x, final int y, final long appealID, final AppealStatus newStatus) {
//		return appealUpdate(c, x, y, appealID, newStatus, 0);
//	}
//	private TimerTask appealUpdate(final int c, final int x, final int y, final long appealID, final AppealStatus newStatus, final int newScore) {
//		return new TimerTask() {
//			public void run() {
//				synchronized (lock) {
//					cat.info(c+":"+x+":"+y + " " + newStatus + " = " + newScore);
//					appeals[c][x][y] = newStatus;
//					appealsID[c][x][y] = appealID;
//					if (newScore != 0) {
//						scores[c][x][y] = newScore;
//					}
//				}
//			}
//		};
//	}
//
//	public void run() {
//		try {
//			cat.info("Opening port: " + PORT);
//			final ServerSocket ss = new ServerSocket(PORT);
//			
//			while(true) {
//				cat.info("Waiting for connections");
//				final Socket s = ss.accept();
//				cat.info("Connection Accepted");
//				new SocketHandler(s);
//			}
//		} catch (Exception e) {
//			cat.error("Error opening server socket for port " + PORT, e);
//		}
//	}
//	
//	private class SocketHandler extends Thread {
//		final Socket socket;
//		public SocketHandler(Socket s) { socket = s; start(); }
//		public void run() {
//			try {
//				// Get a reader/writer on the socket
//				final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//				
//				// Find the query string from the request
//				String queryString = null;
//				while(true) {
//					final String sr = br.readLine();
//					if(sr == null || sr.trim().length() == 0) break;
////					cat.info("<<< " + sr);
//					if (sr.startsWith("GET")) {
//						queryString = sr.substring(4);
//						final int pos = sr.indexOf(" ", 4);
//						if (pos >= 0) {
//							queryString = sr.substring(4, pos);
//						} else {
//							queryString = sr.substring(4);
//						}
//					}
//				}
//				
//				// Unknown request - ignore it
//				if (queryString == null) return;
//				
//				// Decode the message
//				final Message msg = MessageUtil.decodeQueryStringMessage(queryString);
//				
//				// Process the message
//				final MessagePacket mp = new MessagePacket();
//				if (msg instanceof RequestComponentRoundInfo) {
//					final RequestComponentRoundInfo request = (RequestComponentRoundInfo) msg;
//					final int c = getComponentIdx(request.getComponentID());
//					
//					if (request.getContestID() != CONTESTID) {
//						cat.warn("Invalid contest id: " + request.getContestID());
//					} else if (request.getRoundID() != ROUNDID) {
//						cat.warn("Invalid round id: " + request.getRoundID());
//					} else if (c < 0) {
//						cat.warn("Invalid component id: " + request.getComponentID());
//					} else {
//						List<ComponentCoder> componentCoders = new ArrayList<ComponentCoder>();
//						for(int x=0;x<coders.length;x++) {
//							componentCoders.add(new ComponentCoder(coders[x].getCoderID(), coders[x].getHandle(), coders[x].getRank(), wagers[c][x]));
//						}
//						
//						mp.add(new DefineComponentContest(CONTESTID, ROUNDID, componentData[c], componentCoders, Arrays.asList(reviewers)));
//						mp.addAll(getUpdate(request.getComponentID()));
//						
//						startContest(request.getComponentID());
//					}
//				} else if (msg instanceof RequestComponentUpdate) {
//					final RequestComponentUpdate request = (RequestComponentUpdate) msg;
//					final int c = getComponentIdx(request.getComponentID());
//					
//					if (request.getContestID() != CONTESTID) {
//						cat.warn("Invalid contest id: " + request.getContestID());
//					} else if (request.getRoundID() != ROUNDID) {
//						cat.warn("Invalid round id: " + request.getRoundID());
//					} else if (c < 0) {
//						cat.warn("Invalid component id: " + request.getComponentID());
//					} else {
//						mp.addAll(getUpdate(request.getComponentID()));
//					}
//				} else {
//					cat.warn("Unknown message type: " + msg);
//				}
//
//				// Write the response
//				final String response = MessageUtil.encodeXMLMessagePacket(mp);
//				bw.write(response);
//				
//				// Close everything
////				br.close();
//				bw.flush();
//				socket.close();
//			} catch (Exception e) {
//				cat.error("Error processing socket IO", e);
//			}
//		}
//		
//		public List<Message> getUpdate(long componentID)
//		{
//			final ArrayList<Message> rc = new ArrayList<Message>();
//			final int c = getComponentIdx(componentID);
//		
//			synchronized (lock) {
//				for(int x = 0; x < coders.length; x++) {
//					for(int y = 0; y < reviewers.length; y++) {
//						rc.add(new ComponentScoreUpdate(CONTESTID, ROUNDID, componentID, coders[x].getCoderID(), reviewers[y].getCoderID(), scores[c][x][y]));
//					}
//				}
//				
//				for(int x = 0; x < coders.length; x++) {
//					for(int y = 0; y < reviewers.length; y++) {
//						final String appeal;
//						if (appeals[c][x][y] == AppealStatus.None) {
//							continue;
//						} else if (appeals[c][x][y] == AppealStatus.Pending) {
//							appeal = ComponentAppeal.APPEAL_PENDING;
//						} else  if (appeals[c][x][y] == AppealStatus.Successful) {
//							appeal = ComponentAppeal.APPEAL_SUCCESSFUL;
//						} else if (appeals[c][x][y] == AppealStatus.Failed) {
//							appeal = ComponentAppeal.APPEAL_REJECTED;
//						} else {
//							cat.warn("Unknown appeal status: " + appeals[x][y]);
//							continue;
//						}
//						
//						rc.add(new ComponentAppeal(CONTESTID, ROUNDID, componentID, appealsID[c][x][y], coders[x].getCoderID(), reviewers[y].getCoderID(), appeal));
//					}
//				}
//			}
//
//			return rc;
//		}
//	}
//	
//	private int getComponentIdx(long componentID) {
//		for(int c = 0; c < componentData.length; c++) {
//			if (componentData[c].getComponentID() == componentID) return c;
//		}
//		return -1;
//	}
//	private void startContest(long componentID) {
//		final int c = getComponentIdx(componentID);
//		if (c < 0) return;
//		
//		cat.info("Starting Contest for " + componentID);
//		
//		if (timer[c] != null) timer[c].cancel();
//		timer[c] = new Timer();
//
//		if (componentID == 1) {
//			appeal(timer[c],c,0,0,10,25,AppealStatus.Failed, 0);
//			appeal(timer[c],c,1,1,35,35,AppealStatus.Failed, 0);
//			appeal(timer[c],c,2,1,10,5,AppealStatus.Successful, 25);
//			appeal(timer[c],c,2,1,12,20,AppealStatus.Failed, 0);
//			appeal(timer[c],c,2,2,10,30,AppealStatus.Successful, 25);
//			appeal(timer[c],c,3,2,60,20,AppealStatus.Failed, 0);
//			appeal(timer[c],c,4,0,78,2,AppealStatus.Failed, 0);
//			appeal(timer[c],c,5,1,42,58,AppealStatus.Failed, 0);
//			appeal(timer[c],c,6,0,45,65,AppealStatus.Successful, 893);
//			appeal(timer[c],c,6,0,46,64,AppealStatus.Successful, 1593);
//		} else if (componentID == 2) {
//			appeal(timer[c],c,0,0,10,25,AppealStatus.Failed, 0);
//			appeal(timer[c],c,0,1,33,58,AppealStatus.Failed, 0);
//			appeal(timer[c],c,0,2,35,22,AppealStatus.Failed, -793); // negative test
//			appeal(timer[c],c,1,0,35,23,AppealStatus.Failed, 0);
//			appeal(timer[c],c,1,0,55,15,AppealStatus.Failed, 0);
//			appeal(timer[c],c,1,0,75,20,AppealStatus.Successful, 3922);
//			appeal(timer[c],c,2,1,45,23,AppealStatus.Failed, 0);
//			appeal(timer[c],c,2,1,95,5,AppealStatus.Successful, 2929);
//			appeal(timer[c],c,2,1,110,3,AppealStatus.Failed, 0);
//			appeal(timer[c],c,3,2,15,15,AppealStatus.Successful, 2221);
//			appeal(timer[c],c,3,2,22,22,AppealStatus.Failed, 0);
//			appeal(timer[c],c,3,2,55,55,AppealStatus.Failed, 0);
//			appeal(timer[c],c,4,2,15,15,AppealStatus.Successful, 721);
//			appeal(timer[c],c,4,2,22,22,AppealStatus.Successful, 93);
//			appeal(timer[c],c,4,2,55,55,AppealStatus.Failed, 393);
//
////			for(int cc = 0; cc < componentData.length; cc++) {
////				int diff = scores[c][6][cc] - scores[c][5][cc];
////				if (diff > 0) {
////					appeal(timer[c],c,5,cc,5+cc,5,AppealStatus.Successful, diff);
////				} else {
////					appeal(timer[c],c,6,cc,5+cc,5,AppealStatus.Successful, -diff);
////				}
////			}
//			
//		} else if (componentID == 3) {
//			int numAppeals = rand.nextInt(coders.length * reviewers.length);
//			for(int n = 0; n < numAppeals; n++) {
//				int cdr = rand.nextInt(coders.length);
//				int rvr = rand.nextInt(reviewers.length);
//				int tm = rand.nextInt(TOTALTIME / 2);
//				int sp = rand.nextInt(TOTALTIME / 2);
//				if (rand.nextBoolean()) {
//					appeal(timer[c],c,cdr,rvr,tm,sp,AppealStatus.Successful,rand.nextInt(1000)+1);
//				} else {
//					appeal(timer[c],c,cdr,rvr,tm,sp,AppealStatus.Failed,0);
//				}
//			}
//		} else if (componentID >= 4 && componentID <= 6) {
//			// don't do anything!
//		} else {
//			cat.warn("Unknown Component ID: " + componentID);
//		}
//	}
//	
//	private long appealID = 1;
//	private void appeal(Timer timer, int c, int x, int y, int initial, int span, AppealStatus result, int newScore) {
//		long newAppealID = appealID++;
//		timer.schedule(appealUpdate(c,x,y,newAppealID,AppealStatus.Pending), initial);
//		newScore = scores[c][x][y] + newScore;
//		if (newScore > 10000) newScore = 10000;
//		
//		timer.schedule(appealUpdate(c,x,y,newAppealID,result, newScore), Math.min(initial + span, TOTALTIME-2)*1000);
//	}
}
