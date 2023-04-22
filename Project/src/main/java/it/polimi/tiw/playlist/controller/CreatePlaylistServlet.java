package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlist.dao.PlaylistDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.TemplateHandler;
import it.polimi.tiw.playlist.utils.EditType;


public class CreatePlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public CreatePlaylistServlet() {
		super();
	}
	
	public void init() throws ServletException{
		try {
			ServletContext context = getServletContext();
			this.connection = ConnectionHandler.getConnection(context);
			this.templateEngine = TemplateHandler.getTemplateEngine(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		
		String playlistName = request.getParameter("playlistName");
		String userName = (String)session.getAttribute("user");
		String playlistError = null;
		
		if(playlistName == null || playlistName.isEmpty()) {
			playlistError = "Missing parameters";
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
		
		session.setAttribute("playlistName", playlistName);
		session.setAttribute("editType", EditType.CREATE);
		String path = getServletContext().getContextPath() + "/EditPlaylist";

		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(path);
		dispatcher.forward(request,response);
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
  }
	
}
