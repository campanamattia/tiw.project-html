package it.polimi.tiw.playlist.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.playlist.beans.Playlist;

public class PlaylistDAO {
	
	private Connection con;
	
	public PlaylistDAO(Connection c) {
		this.con = c;
	}
	
	// method that returns all playlists related to a User
	public ArrayList<Playlist> allPlaylists(String userName) throws SQLException {
		ArrayList<Playlist> result = new ArrayList<Playlist>();
		String query = "SELECT * FROM PLAYLIST WHERE UserName = ? ORDER BY CreationDate DESC";
		ResultSet queryRes = null;
		PreparedStatement pStatement = null;
		
		try {
			pStatement = con.prepareStatement(query);
			pStatement.setString(1 , userName);
			
			queryRes = pStatement.executeQuery();
			
			while(queryRes.next()) {
				result.add( new Playlist(queryRes.getString("Name") , queryRes.getDate("CreationDate")) );
			}
		}catch(SQLException e) {
			throw new SQLException();
		}finally {
			try {
				if(queryRes != null) {
					queryRes.close();
				}
			}catch(Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if(pStatement != null) {
					pStatement.close();
				}
			}catch(Exception e2) {
				throw new SQLException(e2);
			}
		}
		
		return result;
	}
	
	//method that states whether a playlist name is already taken or not
	public boolean taken(String playlistName, String userName) throws SQLException {
		boolean result = false;
		String query = "SELECT * FROM PLAYLIST WHERE Name = ? AND UserName = ?";
		ResultSet queryRes = null;
		PreparedStatement pStatement = null;
		
		try {
			pStatement = con.prepareStatement(query);
			pStatement.setString(1, playlistName);
			pStatement.setString(2, userName);
			queryRes = pStatement.executeQuery();
			
			if(queryRes.next()) result = true;
			
		}catch(SQLException e) {
			throw new SQLException();
		}finally {
			try {
				if(queryRes != null) {
					queryRes.close();
				}
			}catch(Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if(pStatement != null) {
					pStatement.close();
				}
			}catch(Exception e2) {
				throw new SQLException(e2);
		    }
		}
		return result;
	}
	
	//method that creates a new playlist
	public boolean create(String playlistName, String userName, Date creationDate) throws SQLException {
		boolean result = false;
		String query = "INSERT into PLAYLIST (Name , UserName, CreationDate) VALUES (? , ? , ?)";
		PreparedStatement pStatement = null;
		
		try {
			pStatement = con.prepareStatement(query);
			pStatement.setString(1 , playlistName);
			pStatement.setString(2, userName);
			pStatement.setDate(3 , creationDate);
			if(!this.taken(playlistName, userName)) {
				pStatement.executeUpdate();
				result = true;
			}
		}catch(SQLException e) {
			throw new SQLException();
		}finally {
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
		}
		return result;
	}
	
	//method that states whether a song is in a playlist or not
	public boolean songAlreadyIn(String playlistName, String userName, int songId) throws SQLException {
		boolean result = false;
		String query = "SELECT * FROM CONTAINS WHERE PlaylistName = ? AND PlaylistUser = ? AND Song = ?";
		
		PreparedStatement pStatement = null;
		ResultSet queryRes = null;
		
		try {
			pStatement = con.prepareStatement(query);
			pStatement.setString(1, playlistName);
			pStatement.setString(2, userName);
			pStatement.setInt(3, songId);
			
			queryRes = pStatement.executeQuery();
			
			if(queryRes.next())
				result = true;
		}catch(SQLException e) {
			throw new SQLException();
		}finally{
			try {
				if(queryRes != null) {
					queryRes.close();
				}
			}catch(Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if(pStatement != null) {
					pStatement.close();
				}
			}catch(Exception e2) {
				throw new SQLException(e2);
		    }
		}
		return result;
	}
	
	public boolean addSong(String playlistName, String userName, int songId) throws SQLException {
		boolean result = false;
		String query = "INSERT into CONTAINS (PlaylistName, PlaylistUser, Song) VALUES (? , ? , ?)";
		PreparedStatement pStatement = null;
		
		try {
			pStatement = con.prepareStatement(query);
			pStatement.setString(1, playlistName);
			pStatement.setString(2, userName);
			pStatement.setInt(3, songId);
			if(!this.songAlreadyIn(playlistName, userName, songId)) {
				pStatement.executeUpdate();
				result = true;
			}
		}catch(SQLException e) {
			throw new SQLException();
		}finally {
			try {
				if(pStatement != null) {
					pStatement.close();
				}
			}catch(Exception e2) {
				throw new SQLException(e2);
		    }
		}
		return result;
	}
	
}