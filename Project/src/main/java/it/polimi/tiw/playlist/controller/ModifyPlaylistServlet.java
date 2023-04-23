package it.polimi.tiw.playlist.controller;

import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.EditType;


public class ModifyPlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public ModifyPlaylistServlet() {
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
		String error = null;
		
		//checking whther the playlistName is valid or not
		try {
			if(playlistName == null || playlistName.isEmpty() || !(new PlaylistDAO(this.connection).belongTo(playlistName, userName))) {
				error = "You can not modify the selected playlist";
			}	
		} catch (SQLException e) {
			error = "Database error, try again";
		}
		if(error != null) {
			session.setAttribute("generalError", error);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//forward to the EditPlaylist page
		session.setAttribute("playlistName", playlistName);
		session.setAttribute("editType", EditType.MODIFY);
		
		String path = servletContext.getContextPath() + "/EditPlaylist";
		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(path);
		dispatcher.forward(request,response);
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
