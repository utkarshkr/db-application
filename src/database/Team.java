package database;

import java.sql.*;
import java.util.*;

public class Team {
	public static ResultSet getTeams(int id){
		Connection connection=null;
		ResultSet rs = null;
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement("select * from teams natural join teamAssign where user_id=?");
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			return rs;
		} catch(SQLException sqle){
			System.out.println("SQL exception when getting teams of the user");
		} finally{
			closeConnection(connection);
		}
		return rs;
	}
	
	public static ResultSet getLeaderTeams(int id){
		Connection connection=null;
		ResultSet rs = null;
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement("select * from teams where leader_id=?");
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			return rs;
		} catch(SQLException sqle){
			System.out.println("SQL exception when getting teams of which the user is a leader");
		} finally{
			closeConnection(connection);
		}
		return rs;
	}
	
	public static void makeTeam(String name, int leader_id){
		Connection connection=null;
		
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement("insert into teams (team_name, leader_id) values (?, ?)");
			pstmt.setString(1, name);
			pstmt.setInt(2, leader_id);
			pstmt.executeUpdate();
			PreparedStatement pstmt1= connection.prepareStatement("select max(team_id) from teams");
			ResultSet rs1 = pstmt1.executeQuery();
			rs1.next();
			int task_id = rs1.getInt(1);
			PreparedStatement pstmt2= connection.prepareStatement("insert into teamAssign (team_id, user_id) values (?, ?)");
			pstmt2.setInt(1, task_id);
			pstmt2.setInt(2, leader_id);
			pstmt2.executeUpdate();
		} catch(SQLException sqle){
			System.out.println("SQL exception creating team");
		} finally{
			closeConnection(connection);
		}
	}

	public static ResultSet getallTeams(){
		Connection connection=null;
		
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement("select team_id, team_name from teams");
			return  pstmt.executeQuery();
		} catch(SQLException sqle){
			System.out.println("SQL exception getting all teams");
		} finally{
			closeConnection(connection);
		}
		return null;
	}

	public static void changeLeader(int teamID, int leader){
		Connection connection=null;
		
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement("update teams set leader_id=? where team_id=?");
			pstmt.setInt(1, leader);
			pstmt.setInt(2, teamID);
			pstmt.executeUpdate();
		} catch(SQLException sqle){
			System.out.println("SQL exception changing team leader");
		} finally{
			closeConnection(connection);
		}
	}

	public static void deleteTeam(int teamID){
		Connection connection=null;
		
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement("delete from teams where team_id=?");
			pstmt.setInt(1, teamID);
			pstmt.executeUpdate();
		} catch(SQLException sqle){
			System.out.println("SQL exception deleting team");
		} finally{
			closeConnection(connection);
		}
	}

	public static ResultSet getMembers(int teamID){
		Connection connection=null;
		ResultSet rs = null;
		try{
			connection=getConnection();
			if(teamID == -1)
			{
				rs = User.getallUsers();
				return rs;
			}
			PreparedStatement pstmt= connection.prepareStatement("select * from users natural join teamAssign where team_id=?");
			pstmt.setInt(1, teamID);
			rs= pstmt.executeQuery();
			return rs;
		} catch(SQLException sqle){
			System.out.println("SQL exception when getting team members");
		} finally{
			closeConnection(connection);
		}
		return rs;
	}
	
	public static int getLeader(int teamID){
		Connection connection=null;
		
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement("select leader_id from teams where team_id=?");
			pstmt.setInt(1, teamID);
			ResultSet rs= pstmt.executeQuery();
			if(!rs.next()) return -1;
			return rs.getInt(1);
		} catch(SQLException sqle){
			System.out.println("SQL exception when getting team members");
		} finally{
			closeConnection(connection);
		}
		return -1;
	}
	
	public static ResultSet getMyMembers(int userID){
		Connection connection=null;
		ResultSet rs = null;
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement(" select distinct user_id,username,email from users natural join teamAssign natural join teams where leader_id = ?");
			pstmt.setInt(1, userID);
			rs= pstmt.executeQuery();
			return rs;
		} catch(SQLException sqle){
			System.out.println("SQL exception when getting team members from all teams of the user");
		} finally{
			closeConnection(connection);
		}
		return rs;
	}
	
	public static void addMember(int id, int teamID){
		Connection connection=null;
		
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement("insert into teamAssign (team_id, user_id) values (?,?)");
			pstmt.setInt(1, teamID);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		} catch(SQLException sqle){
			System.out.println("SQL exception when adding user to the team");
		} finally{
			closeConnection(connection);
		}
	}
	
	public static void deleteMember(int teamID, int userID){
		Connection connection=null;
		
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement("delete from teamAssign where (team_id, user_id) = (?,?)");
			pstmt.setInt(1, teamID);
			pstmt.setInt(2, userID);
			pstmt.executeUpdate();
		} catch(SQLException sqle){
			System.out.println("SQL exception when removing user from the team");
		} finally{
			closeConnection(connection);
		}
	}
	
	public static boolean checkMembers(ArrayList<Integer> user_id, int teamID){
		Connection connection=null;
		
		try{
			connection=getConnection();
			for(int i=0;i<user_id.size();++i)
			{
				PreparedStatement pstmt= connection.prepareStatement("select * from teamAssign where (team_id, user_id) = (?,?)");
				pstmt.setInt(1, teamID);
				pstmt.setInt(2, user_id.get(i));
				ResultSet rs = pstmt.executeQuery();
				if(!rs.next()) return false;
			}
			return true;
		} catch(SQLException sqle){
			System.out.println("SQL exception when checking users belong to the team");
		} finally{
			closeConnection(connection);
		}
		return false;
	}

	static Connection getConnection() {
		String dbURL = "jdbc:postgresql://10.105.1.12/cs387";
        String dbUser = "db130050022";
        String dbPass = "root";
        Connection connection=null;
        try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(dbURL, dbUser, dbPass);
        } catch(ClassNotFoundException cnfe){
        	System.out.println("JDBC Driver not found");
        } catch(SQLException sqle){
        	System.out.println("Error in getting connetcion from the database");
        }
        
        return connection;
	}
	
	static void closeConnection(Connection connection) {
		try{
			connection.close();
		} catch(SQLException sqle) {
			System.out.println("Error in close database connetcion");
		}
	}
}
