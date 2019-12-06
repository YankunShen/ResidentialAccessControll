import roles.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connect {
    protected static String dbClassName = "com.mysql.jdbc.Driver";     //Database Connection Class
    protected static String dbUrl = "jdbc:mysql://45.32.225.3:3306/access_control?autoReconnect=true&useSSL=false";  //Database Connection URL
    protected static String dbUser = "root";		    //username
    protected static String dbPwd = "UWTacoma123!";			//password
    private static Connection conn = null;

    private Connect() {										//Constructor
        try {
            if (conn == null) {							//if empty
                Class.forName(dbClassName).newInstance();				//Loading the driver
                conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);//Get the connection
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private static ResultSet executeQuery(String sql) {	 //select
        try {
            if(conn==null)  new Connect();  //recall construct
            return conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE).executeQuery(sql);//execute query
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
        }
    }

    private static int executeUpdate(String sql) {		//update
        try {
            if(conn==null)  new Connect();
            return conn.createStatement().executeUpdate(sql);//execute update
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
        }
    }

    //Close connection
    public static void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            conn = null;
        }
    }

    //get all users
    public static List<User> getAllUsers(){
        ResultSet rs = Connect.executeQuery("SELECT * FROM user left join card c on user.id = c.userid");
        List<User> users = new ArrayList<>();
        Map<Integer, List<Integer>> m = new HashMap<>();
        try{
            while(rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setGender(rs.getString("gender"));
                user.setCardkey(rs.getString("cardkey"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        rs = Connect.executeQuery("SELECT userid, areaid FROM user left join authority a on user.id = a.userid");
        try{
            while(rs.next()){
                int userid = rs.getInt("userid");
                int areaid = rs.getInt("areaid");
                if(!m.containsKey(userid)){
                    m.put(userid, new ArrayList<>());
                }
                m.get(userid).add(areaid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(User u : users){
            u.setArea(m.get(u.getId()));
        }
        return users;
    }

    //get user by id
    public static User getUserbyId(int id){
        ResultSet rs = Connect.executeQuery("SELECT * FROM user left join card c on user.id = c.userid where id =" + Integer.toString(id));
        User user = new User();
        try{
            while(rs.next()){
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setGender(rs.getString("gender"));
                user.setCardkey(rs.getString("cardkey"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Integer> list = new ArrayList<>();
        rs = Connect.executeQuery("SELECT userid, areaid FROM authority where userid=" + Integer.toString(id));
        try{
            while(rs.next()){
                list.add(rs.getInt("areaid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        user.setArea(list);
        return user;
    }
    //add user
    public static int addUser(User user) {
        int res = 0;
        try {
            String sql = "insert into user(name, gender, phone, email) value(" +
                    "'" + user.getName() + "'," +
                    "'" + user.getGender() + "'," +
                    "'" + user.getPhone() + "'," +
                    "'" + user.getEmail() + "')";
            res = Connect.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //update user
    public static int updateUser(User user) {
        int res = 0;
        try {
            String sql = "update user set " +
                    "name='" + user.getName() + "'," +
                    "gender='" + user.getGender() + "'," +
                    "phone='" + user.getPhone() + "'," +
                    "email='" + user.getEmail() + "' " +
                    "where id=" + user.getId();
            System.out.println(sql);
            res = Connect.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //delete user
    public static int deleteUser(int id){
        int i = 0;
        try{
            String sql = "delete from user where id ="+ id;
            i = Connect.executeUpdate(sql);
        }catch(Exception e){
            e.printStackTrace();
        }
        Connect.close();
        return i;
    }

    //get area
    public static List<Area> getALlarea(){
        ResultSet rs = Connect.executeQuery("select * from area");
        List<Area> areas = new ArrayList<>();
        try{
            while(rs.next()){
                Area area = new Area();
                area.setAreaid(rs.getInt("id"));
                area.setName(rs.getString("name"));
                areas.add(area);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return areas;
    }

    //Add area
    public static int addArea(Area area){
        int res = 0;
        try{
            String sql = "insert into area(id, name) value(" + area.getAreaid() + ", '" + area.getName() + "')";
            res = Connect.executeUpdate(sql);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //Add authority
    public static int addAuthority(Authority authority){
        ResultSet rs = Connect.executeQuery("select max(number) num from authority");
        int number = 0;
        try{
            rs.next();
            number = rs.getInt("num");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        number++;
        int res = 0;
        try{
            String sql = "insert into authority (number, userid, areaid) value ("+
                    number + "," + authority.getUserid() + ", " + authority.getAreaid() + ")";
            res = Connect.executeUpdate(sql);
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    //Add card
    public static int addCard(Card card){
        int res = 0;
        try{
            String sql = "insert into card(userid, cardkey) value (" + card.getUserid() + ",'" + card.getCardkey() + "')";
            res = Connect.executeUpdate(sql);
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    //delete card
    public static int deleteCard(String cardkey, int userid){
        int res = 0;
        try{
            String sql = "delete from card where userid=" + userid + " and cardkey='" + cardkey + "'";
            res = Connect.executeUpdate(sql);
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }


    //get log
    public static List<Log> getLog(){
        List<Log> logs = new ArrayList<>();
        ResultSet rs = Connect.executeQuery("select * from log");
        try{
            while(rs.next()){
                Log log = new Log();
                log.setNumber(rs.getInt("number"));
                log.setUserid(rs.getInt("userid"));
                log.setAreaid(rs.getInt("areaid"));
                log.setCardkey(rs.getString("cardkey"));
                log.setTime(rs.getTimestamp("time"));
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    //Authorized
    public static boolean isAuthorized(int areaid, String cardkey){
        ResultSet rs = Connect.executeQuery("select userid from card where cardkey = " + "'" + cardkey + "'");
        int userid = 0;
        try{
            if(rs == null){
                System.out.println("Incorrect input");
                return false;
            }
            rs.next();
            userid = rs.getInt("userid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Integer> list = new ArrayList<>();
        rs = Connect.executeQuery("select areaid from authority where userid =" + userid);
        try{
            while(rs.next()){
                list.add(rs.getInt("areaid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.contains(areaid) ? true : false;
    }
}
