package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.EditType;


public class CreatePlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public CreatePlaylistServlet() {
		super();
	}
	
	public void init() throws ServletException{
		try {
			ServletContext context = getServletContext();
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		
		String playlistName = request.getParameter("playlistName");
		String userName = (String)session.getAttribute("user");
		String playlistError = null;
		
		//checking whether the given playlist name is valid or not
		if(playlistName == null || playlistName.isEmpty()) {
			playlistError = "Missing parameters";
		}
		else if(playlistName.length() > 50) {
			playlistError = "Playlist name is too long";
		}
		if(playlistError == null){
			try {
				if(new PlaylistDAO(this.connection).taken(playlistName, userName)) {
					playlistError = playlistName + " playlist already exist";
				}
			} catch (SQLException e) {
				playlistError = "Database error, try again";
			}
		}
		
		if(playlistError != null) {
			session.setAttribute("playlistError", playlistError);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//forward to the Playlist page
		session.setAttribute("playlistName", playlistName);
		session.setAttribute("editType", EditType.CREATE);
		
		String path = servletContext.getContextPath() + "/Playlist";
		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(path);
		dispatcher.forward(request,response);
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
