package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ButtonsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public ButtonsServlet() {
		super();
	}
	
	//method called when the user use previous or next buttons in the playlist page
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		String error = null;
		
		//taking parameters from request
		String playlistName = request.getParameter("playlistName");
		Integer lowerBound = -1;
		try{
			lowerBound = request.getParameter("lowerBound") != null ? Integer.parseInt(request.getParameter("lowerBound")) : 0;
		}
		catch(NumberFormatException e) {
			error = "Unable to load your playlist page";
		}
		
		//checking the lowerBound parameter
		if(error == null && lowerBound%5 != 0) error = "Unable to load your playlist page";
		
		//if an error occurred, the user will be redirected to the home page
		if(error != null) {
			session.setAttribute("generalError", error);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//forward to the playlist page
		session.setAttribute("playlistName", playlistName);
		session.setAttribute("lowerBound", lowerBound);
		String path = servletContext.getContextPath() + "/Playlist";
		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(path);
		dispatcher.forward(request,response);	
	}
	
}