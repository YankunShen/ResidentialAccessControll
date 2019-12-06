import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import roles.*;

import javax.json.*;
import javax.jws.WebMethod;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Path("admin")
public class ControlAPI {
    @GET
    @Produces("text/html")
    // This method will return a simple message of type HTML
    public String displayMessage(){
        return "Access Control app is running well";
    }

    //get all user
    @GET
    @Path("/user")
    @Produces("text/json")
    public JsonObject getAllusers(){
        List<User> users = new ArrayList<>();
        users = Connect.getAllUsers();
        ObjectMapper mapper = new ObjectMapper();
        List<String> list = new ArrayList<>();
        for(User user : users){
            try{
                list.add(mapper.writeValueAsString(user));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        JsonArrayBuilder builder = Json.createArrayBuilder();

        for(String s : list){
            JsonReader reader = Json.createReader(new StringReader(s));
            JsonObject userObject = reader.readObject();
            builder.add(userObject);
        }
        JsonObjectBuilder obuilder = Json.createObjectBuilder();
        obuilder.add("data", builder);
        JsonObject array = obuilder.build();
        return array;
    }

    //get user info by id
    @GET
    @Path("/user/{id}")
    @Produces("text/json")
    public JsonObject getUserById(@PathParam("id")int id){
        User user = new User();
        user = Connect.getUserbyId(id);
        ObjectMapper mapper = new ObjectMapper();
        String s = "";
        try{
            s = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        JsonReader reader = Json.createReader(new StringReader(s));
        JsonObject userObject = reader.readObject();
        return userObject;
    }

    //add user
    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/json")
    public Response addUser( User user){
        Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
        int res = Connect.addUser(user);
        if(res != -1){
            builder.status(Response.Status.OK);
        }
        return builder.build();

    }

    //delete user by id
    @DELETE
    @Path("/user/{id}")
    public Response deleteUser(@PathParam("id")int id){
        Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
        int res = Connect.deleteUser(id);
        if(res != -1){
            builder.status(Response.Status.OK);
        }
        return builder.build();
    }

    //get area
    @GET
    @Path("/area")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getArea(){
        List<Area> areas = new ArrayList<>();
        areas = Connect.getALlarea();
        ObjectMapper mapper = new ObjectMapper();
        List<String> list = new ArrayList<>();
        for(Area area : areas){
            try{
                list.add(mapper.writeValueAsString(area));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for(String s : list){
            JsonReader reader = Json.createReader(new StringReader(s));
            JsonObject userObject = reader.readObject();
            builder.add(userObject);
        }
        JsonObjectBuilder obuilder = Json.createObjectBuilder();
        obuilder.add("data", builder);
        JsonObject array = obuilder.build();
        return array;
    }

    @POST
    @Path("/area")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addArea(Area area){
        Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
        int res = Connect.addArea(area);
        if(res != -1){
            builder.status(Response.Status.OK);
        }
        return builder.build();
    }

    @POST
    @Path("/authority")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAuthority(Authority authority){

        Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
        int res = Connect.addAuthority(authority);
        if(res != -1){
            builder.status(Response.Status.OK);
        }
        return builder.build();
    }

    @POST
    @Path("/card")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCard(Card card){
        Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
        int res = Connect.addCard(card);
        if(res != -1){
            builder.status(Response.Status.OK);
        }
        return builder.build();
    }

    @GET
    @Path("/log")
    @Produces("text/json")
    public JsonObject getLogs(){
        List<Log> logs = new ArrayList<>();
        logs = Connect.getLog();
        ObjectMapper mapper = new ObjectMapper();
        List<String> list = new ArrayList<>();
        for(Log log : logs){
            try{
                list.add(mapper.writeValueAsString(log));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for(String s : list){
            JsonReader reader = Json.createReader(new StringReader(s));
            JsonObject userObject = reader.readObject();
            builder.add(userObject);
        }
        JsonObjectBuilder obuilder = Json.createObjectBuilder();
        obuilder.add("data", builder);
        JsonObject array = obuilder.build();
        return array;
    }

    @GET
    @Path("/card/{cardkey}/{areaid}")
    public boolean isAuthorized(@PathParam("areaid")int areaid, @PathParam("cardkey")String cardkey){
        boolean res = Connect.isAuthorized(areaid, cardkey);
        return res;
    }

    @GET
    @Path("/weather/{city}")
    @Produces("text/xml")
    public Document getWeatherApi(@PathParam("city")String city) throws IOException {
        Document document = getWeather(city);
        return document;
    }

    @WebMethod
    public Document getWeather(String city) throws IOException {
        String wsurl = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=68e0ff6090075986039364ab84df828b&mode=xml";
        URL url = new URL(wsurl);
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection)urlConnection;
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        httpConn.setRequestMethod("GET");
        httpConn.setDoOutput(true);
        OutputStream out = httpConn.getOutputStream();
        InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);
        String outputString = "";
        String responseString = "";
        while ((responseString = in.readLine()) != null) {
            outputString = outputString + responseString;
        }
        Document document = parseXmlFile(outputString);
        return document;
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
