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
	
	public ArrayList<Playlist> allPlaylists(String userName) throws SQLException {
		ArrayList<Playlist> result = new ArrayList<Playlist>();
		String query = "SELECT * FROM PLAYLIST WHERE User = ? ORDER BY CreationDate DESC";
		ResultSet queryRes = null;
		PreparedStatement pStatement = null;
		
		try {
			pStatement = con.prepareStatement(query);
			pStatement.setString(1 , userName);
			
			queryRes = pStatement.executeQuery();
			
			while(queryRes.next()) {
				result.add( new Playlist(queryRes.getInt("Id") , queryRes.getString("Name") , queryRes.getDate("CreationDate")) );
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
	
}