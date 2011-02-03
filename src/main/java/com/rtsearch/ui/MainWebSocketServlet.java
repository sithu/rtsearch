package com.rtsearch.ui;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

/**
 * Servlet implementation class MainWebSocketServlet
 */
@WebServlet(description = "It hanldes search query from UI and streams the result in realtime.", urlPatterns = { "/" })
public class MainWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
    
	private static final com.rtsearch.QueryProcessor queryProcessor = new com.rtsearch.QueryProcessor();
	
    /**
     * @see WebSocketServlet#WebSocketServlet()
     */
    public MainWebSocketServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getNamedDispatcher("default").forward(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected WebSocket doWebSocketConnect(HttpServletRequest arg0, String arg1) {
		System.out.println("Attempting WebSocket connection...");
		return new SearchWebSocket(this.queryProcessor);
	}

}
