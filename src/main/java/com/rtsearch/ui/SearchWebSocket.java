package com.rtsearch.ui;

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket;

import com.rtsearch.QueryProcessor;

/**
 * @author saung
 *
 */
public class SearchWebSocket implements WebSocket {
	private final QueryProcessor queryProcessor;
	private Outbound outbound;
	
	/**
	 * 
	 * @param queryProcessor
	 */
	public SearchWebSocket(QueryProcessor queryProcessor) {
		this.queryProcessor = queryProcessor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jetty.websocket.WebSocket#onConnect(org.eclipse.jetty.websocket.WebSocket.Outbound)
	 */
	@Override
	public void onConnect(Outbound outbound) {
		System.out.println("got connected!");
		this.outbound = outbound;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.websocket.WebSocket#onDisconnect()
	 */
	@Override
	public void onDisconnect() {
		System.out.println("got disconnected!");

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.websocket.WebSocket#onFragment(boolean, byte, byte[], int, int)
	 */
	@Override
	public void onFragment(boolean arg0, byte arg1, byte[] arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.websocket.WebSocket#onMessage(byte, java.lang.String)
	 */
	@Override
	public void onMessage(byte frame, String data) {
		try {
			System.out.println("::onMessage::keyword=" + data);
			this.outbound.sendMessage(frame, this.queryProcessor.searchTweet(data).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.websocket.WebSocket#onMessage(byte, byte[], int, int)
	 */
	@Override
	public void onMessage(byte arg0, byte[] arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}
	
	
}
