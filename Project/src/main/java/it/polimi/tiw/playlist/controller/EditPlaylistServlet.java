package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.beans.Song;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.TemplateHandler;
import it.polimi.tiw.playlist.utils.EditType;
import java.util.ArrayList;

public class EditPlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public EditPlaylistServlet() {
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
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		//taking attributes from session
		String playlistName = (String)session.getAttribute("playlistName");
		EditType editType = (EditType)session.getAttribute("editType");
		session.removeAttribute("playlistName");
		session.removeAttribute("editType");
		ctx.setVariable("playlistName", playlistName);
		ctx.setVariable("editType", editType);
		String userName = (String)session.getAttribute("user");
		
		ArrayList<Song> songs = null;
		String error = null;
		
		//taking the songs that the user can add from the database
		if(editType.equals(EditType.CREATE)) {
			try {
				songs = new SongDAO(this.connection).getSongsbyUser(userName);
				if(songs == null || songs.isEmpty()) {
					error = "Upload a song before creating a playlist";
				}
				else ctx.setVariable("songs", songs);
			}
			catch(SQLException e) {
				error = "Database error: Unable to load your songs";
			}
		}
		if(editType.equals(EditType.MODIFY)) {
			try {
				songs = new SongDAO(this.connection).getSongsNotInPlaylist(playlistName, userName);
				if(songs == null || songs.isEmpty()) {
					error = "You have no more songs to add";
				}
				else ctx.setVariable("songs", songs);
			}
			catch(SQLException e) {
				error = "Database error: Unable to load your songs";
			}
		}
		if(error != null) ctx.setVariable("error", error);
		
		templateEngine.process("/WEB-INF/editPlaylist.html", ctx, response.getWriter());	
	}
	
	
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
  }
	
}
