package it.polimi.tiw.playlist.controller;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;

import it.polimi.tiw.playlist.dao.SongDAO;
import it.polimi.tiw.playlist.utils.ConnectionHandler;

@WebServlet("/GetImage/*")
public class GetImageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private String imgFolderPath;
	
	public GetImageServlet() {
		super();
	}
	
	public void init() throws ServletException{
		ServletContext context = getServletContext();
		imgFolderPath = context.getInitParameter("imgFolderPath");
		try {
			this.connection = ConnectionHandler.getConnection(context);
			
		} catch (UnavailableException  e) {
			
		}
	}
	

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		String userName = (String)session.getAttribute("user");
		
		//Take the pathInfo from the request
		String pathInfo = request.getPathInfo();
		
		//Check if the path info is valid
		if (pathInfo == null || pathInfo.equals("/")) return;
		
		//Take the fileName from the pathInfo without the "/" character
		String fileImage = URLDecoder.decode(pathInfo.substring(1), "UTF-8");
		
		//check if the file image belongs to the user
		try {
			if( !(new SongDAO(this.connection).imgBelongTo(fileImage, userName)) ) return;
		}catch(SQLException e) {
			return;
		}
		
		//Open the file
		String fileName = userName + "_" + fileImage;
		File file = new File(imgFolderPath, fileName);
		
		if (!file.exists() || file.isDirectory()) {
			return;
		}
		
		//Set headers for browser
		response.setHeader("Content-Type", servletContext.getMimeType(fileName));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		//inline     -> the user will watch the image immediately
		//filename   -> used to indicate a fileName if the user wants to save the file
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
		
		//Copy the file to the output stream
		Files.copy(file.toPath(), response.getOutputStream());		
	}
	
	public void destroy() {
	      ConnectionHandler.destroy(this.connection);
	}
	
}
