package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;

public class ForwardPlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public ForwardPlaylistServlet() {
		super();
	}
	
	public void init() throws ServletException{
		try {
			ServletContext context = getServletContext();
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	//method called by EditPlaylistServlet doPost method through redirect, in order to not send again the form when the user will refresh the playlist page
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		String error = null;
		
		String playlistName = request.getParameter("playlistName");
		String userName = (String)session.getAttribute("user");

		
		//if someone tries to load the playlist page without a valid playlistName, he will be redirected to the home
		try {
			if(playlistName == null  || playlistName.isEmpty() || !(new PlaylistDAO(this.connection).belongTo(playlistName, userName))) {
				error = "Playlist not found";
			}
		}
		catch(SQLException e) {
			error = "Database error, try again";
		}
		
		if(error != null) {
			session.setAttribute("generalError", error);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//forward to the playlist page
		session.setAttribute("playlistName", playlistName);
		String path = servletContext.getContextPath() + "/Playlist";
		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(path);
		dispatcher.forward(request,response);	
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
