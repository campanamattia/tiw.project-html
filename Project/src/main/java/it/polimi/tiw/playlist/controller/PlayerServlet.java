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

public class PlayerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private String audioFolderPath;
	private String imgFolderPath;
	
	public PlayerServlet() {
		super();
	}
	
	public void init() throws ServletException{
		ServletContext context = getServletContext();
		imgFolderPath = context.getInitParameter("imgFolderPath");
		audioFolderPath = context.getInitParameter("audioFolderPath");
		try {
			this.connection = ConnectionHandler.getConnection(context);
			this.templateEngine = TemplateHandler.getTemplateEngine(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	
	//this page must be accessible only by selecting a song in the playlist page,
	//Doing so, the parameters in the session have been already checked
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		SongDAO songDAO = new SongDAO(this.connection);
		String songError = null;
		
		//taking attributes from session
		String userName = (String)session.getAttribute("user");
		Integer songId = (Integer)session.getAttribute("songId");
		
		//removing the attribute from the session because it identifies that the page has been loaded in the intended way 
		session.removeAttribute("songId");
		
		//taking the song details
		Song song = null;
		try {
			song = songDAO.playSong(songId);
		}
		catch(SQLException e) {
			songError = "Database error, try again";
		}
		
		if(songError != null) {
			ctx.setVariable("songError", songError);
			templateEngine.process("/WEB-INF/player.html", ctx, response.getWriter());
			return;
		}
		
		//setting the correct paths for the image and for the audio
		song.getAlbum().setFileImage(this.imgFolderPath + userName + "_" + song.getAlbum().getFileImage());
		song.setFileAudio(this.audioFolderPath + userName + "_" + song.getFileAudio());

		//starting to prepare the presentation of the page
		ctx.setVariable("song", song);
		
		templateEngine.process("/WEB-INF/player.html", ctx, response.getWriter());	
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
