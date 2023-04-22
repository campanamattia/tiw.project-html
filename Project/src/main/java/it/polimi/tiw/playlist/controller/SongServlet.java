package it.polimi.tiw.playlist.controller;

import java.io.*;
import java.nio.file.Path;

import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import java.util.Calendar;

import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;
import it.polimi.tiw.playlist.utils.TemplateHandler;



public class SongServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private String imgFolderPath;
	private String audioFolderPath;
	
	public SongServlet() {
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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		String userName = (String)session.getAttribute("user");
		
		String songTitle = (String)request.getAttribute("songTitle");
		String genre = (String)request.getAttribute("genre");
		String singer = (String)request.getAttribute("singer");
		String albumTitle = (String)request.getAttribute("albumTitle");
		String year = (String)request.getAttribute("year");
		int publicationYear = -1;
		
		Part fileImage = (Part)request.getAttribute("fileImage");
		Part fileAudio = (Part)request.getAttribute("fileAudio");
		
		String songError = null;
		
		//Checking the String parameters
		if(songTitle == null || songTitle.isEmpty() || genre == null || genre.isEmpty() || singer == null || singer.isEmpty()
				|| albumTitle == null || albumTitle.isEmpty() || year == null || year.isEmpty() 
				|| fileImage == null || fileImage.getSize() <= 0 || fileAudio == null ||  fileAudio.getSize() <= 0) {
			songError = "Missing parameters";
		}
		if(songError == null && (songTitle.length() > 50)) songError = "Song title is too long";
		if(songError == null && !( genre.equals("Others") || genre.equals("Rap") || genre.equals("Rock") || genre.equals("Jazz") || genre.equals("Pop") )) songError = "Genre not valid";
		if(songError == null && (singer.length() > 50)) songError = "Singer name is too long";
		if(songError == null && (singer.length() > 50)) songError = "Album title is too long";
		if(songError == null) {
			try {
				publicationYear = Integer.parseInt(year);
				int currentYear = Calendar.getInstance().get(Calendar.YEAR);
				if(publicationYear > currentYear)
					songError = "The release year of the album is bigger than the current year";
			}catch(NumberFormatException e) {
				songError = "The release year of the album is not valid";
			}
		}
		
		//Checking the two Files
		if(songError == null) {
			if(!fileImage.getContentType().startsWith("image"))
				songError = "The image file is not valid;";
			else {
				if(fileImage.getSize() > 1000000) { //1 000 000 bytes = 1MB
					songError = "Image size is too big;";
				}	
			}
		}
		if(songError == null) {
			if(!fileAudio.getContentType().startsWith("audio"))
				songError = "The audio file is not valid;";
			else {
				if(fileAudio.getSize() > 1000000) { //1 000 000 bytes = 1MB
					songError = "Audio file size is too big;";
				}	
			}
		}
		
		//if an error occurred the home page would be reloaded
		if(songError != null) {
			session.setAttribute("songError", songError);
			String path = servletContext.getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		
		//storing the two files
		String fileImageName = Path.of(fileImage.getSubmittedFileName()).getFileName().toString();
		if(fileImageName.length() > 50) songError = "The image file name is too long";
		fileImageName = userName + "_" + fileImageName;
		String fileImagePath = this.imgFolderPath + fileImageName;
		
		String fileAudioName = Path.of(fileAudio.getSubmittedFileName()).getFileName().toString();
		if(fileAudioName.length() > 50) songError = "The audio file name is too long";
		fileAudioName = userName + "_" + fileAudioName;
		String fileAudioPath = this.audioFolderPath + fileAudioName;
		
		
		
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
