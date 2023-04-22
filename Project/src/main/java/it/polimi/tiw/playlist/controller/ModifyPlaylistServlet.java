package it.polimi.tiw.playlist.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import it.polimi.tiw.playlist.utils.EditType;


public class ModifyPlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public ModifyPlaylistServlet() {
		super();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		HttpSession session = request.getSession(true);
		ServletContext servletContext = getServletContext();
		
		String playlistName = request.getParameter("playlistName");		
		
		session.setAttribute("playlistName", playlistName);
		session.setAttribute("editType", EditType.MODIFY);
		
		String path = servletContext.getContextPath() + "/EditPlaylist";
		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(path);
		dispatcher.forward(request,response);
	}
	
}
