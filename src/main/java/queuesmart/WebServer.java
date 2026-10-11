package queuesmart;

import com.sun.net.httpserver.*;
import queuesmart.Database.*;
import queuesmart.Logic.*;
import queuesmart.Models.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public class WebServer {
    private final int port;
    private HttpServer server;
    private final Map<String,User> sessions = new ConcurrentHashMap<>();
    private final AuthLogic auth = new AuthLogic();
    private final StudentLogic students = new StudentLogic();
    private final StaffDAO staffDAO = new StaffDAO();
    private final QueueLogic queue = new QueueLogic();
    private final ServiceLogic services = new ServiceLogic();
    private final AppointmentLogic appointments = new AppointmentLogic();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final FeedbackLogic feedback = new FeedbackLogic();
    private final LaptopLogic laptops = new LaptopLogic();
    private final AuditLogDAO audit = new AuditLogDAO();

    public WebServer(int port){this.port=port;}

    public void start(){
        try{
            server=HttpServer.create(new InetSocketAddress("localhost",port),0);
            server.createContext("/",this::handle);
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            if(DatabaseConnection.testConnection()) System.out.println("MySQL connection: OK");
            else System.out.println("MySQL connection: FAILED - run sql/schema.sql and check db.properties.");
        }catch(IOException e){throw new RuntimeException("Could not start QueueSmart on port "+port,e);}
    }

    private void handle(HttpExchange ex){
        try{
            addCors(ex);
            String path=ex.getRequestURI().getPath();
            if("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())){send(ex,204,"");return;}
            if(path.startsWith("/api/")){api(ex,path);return;}
            staticFile(ex,path);
        }catch(Exception e){
            e.printStackTrace();
            try { sendJson(ex,500,"{\"message\":\"Server error: "+json(e.getMessage())+"\"}"); } catch(IOException ignored) {}
        }
    }

    private void api(HttpExchange ex,String path)throws Exception{
        String method=ex.getRequestMethod();
        if(path.equals("/api/me")&&method.equals("GET")){User u=current(ex);if(u==null)sendJson(ex,401,"{\"message\":\"Not signed in\"}");else sendJson(ex,200,"{\"user\":"+userJson(u)+"}");return;}
        if(path.equals("/api/login")&&method.equals("POST")){Map<String,String> p=params(ex);User u=auth.login(p.get("username"),p.get("password"));if(u==null){sendJson(ex,401,"{\"message\":\"Invalid username or password.\"}");return;}String token=UUID.randomUUID().toString();sessions.put(token,u);ex.getResponseHeaders().add("Set-Cookie","QS_SESSION="+token+"; Path=/; HttpOnly; SameSite=Lax");sendJson(ex,200,"{\"user\":"+userJson(u)+"}");return;}
        if(path.equals("/api/logout")&&method.equals("POST")){String token=cookie(ex,"QS_SESSION");if(token!=null)sessions.remove(token);ex.getResponseHeaders().add("Set-Cookie","QS_SESSION=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");sendJson(ex,200,"{\"ok\":true}");return;}
        if(path.equals("/api/register")&&method.equals("POST")){Map<String,String> p=params(ex);String err=auth.registerStudent(p.get("username"),p.get("password"),p.get("fullName"),p.get("email"),p.get("studentNumber"));if(err!=null){sendJson(ex,400,"{\"message\":\""+json(err)+"\"}");return;}sendJson(ex,200,"{\"ok\":true}");return;}

        User u=current(ex);
        if(u==null){sendJson(ex,401,"{\"message\":\"Please sign in first.\"}");return;}

        if(path.equals("/api/services")&&method.equals("GET")){sendJson(ex,200,servicesJson());return;}
        if(path.equals("/api/student/status")&&method.equals("GET")&&"STUDENT".equals(u.getRole())){int sid=students.getStudentIdByUserId(u.getUserId());QueueEntry q=queue.getActiveEntryForStudent(sid);int pos=queue.getPositionInLine(q);sendJson(ex,200,"{\"active\":"+(q==null?"null":queueJson(q))+",\"position\":"+pos+"}");return;}
        if(path.equals("/api/queue/join")&&method.equals("POST")&&"STUDENT".equals(u.getRole())){Map<String,String> p=params(ex);int sid=students.getStudentIdByUserId(u.getUserId());int n=queue.joinQueue(sid,intVal(p.get("serviceId")));if(n<=0){sendJson(ex,400,"{\"message\":\"You may already be in an active queue, or the queue could not be created.\"}");return;}sendJson(ex,200,"{\"queueNumber\":"+n+"}");return;}
        if(path.equals("/api/student/appointments")&&method.equals("GET")&&"STUDENT".equals(u.getRole())){int sid=students.getStudentIdByUserId(u.getUserId());StringBuilder a=new StringBuilder("[");boolean first=true;for(Appointment x:appointments.getStudentAppointments(sid)){if(!first)a.append(',');first=false;a.append(appointmentJson(x));}a.append(']');sendJson(ex,200,"{\"appointments\":"+a+"}");return;}
        if(path.equals("/api/appointment/book")&&method.equals("POST")&&"STUDENT".equals(u.getRole())){Map<String,String> p=params(ex);int sid=students.getStudentIdByUserId(u.getUserId());try{String err=appointments.bookAppointment(sid,intVal(p.get("serviceId")),LocalDate.parse(p.get("date")),LocalTime.parse(p.get("time")));if(err!=null){sendJson(ex,400,"{\"message\":\""+json(err)+"\"}");return;}sendJson(ex,200,"{\"ok\":true}");}catch(Exception e){sendJson(ex,400,"{\"message\":\"Please provide a valid appointment date and time.\"}");}return;}
        if(path.equals("/api/appointment/cancel")&&method.equals("POST")&&"STUDENT".equals(u.getRole())){int sid=students.getStudentIdByUserId(u.getUserId());if(!appointments.cancelAppointment(intVal(params(ex).get("appointmentId")),sid)){sendJson(ex,400,"{\"message\":\"Appointment could not be cancelled.\"}");return;}sendJson(ex,200,"{\"ok\":true}");return;}
        if(path.equals("/api/feedback")&&method.equals("POST")&&"STUDENT".equals(u.getRole())){Map<String,String> p=params(ex);Feedback f=new Feedback();f.setStudentId(students.getStudentIdByUserId(u.getUserId()));f.setRating(intVal(p.get("rating")));f.setQueueId(intVal(p.get("queueId")));f.setComment(p.get("comment"));String err=feedback.submitFeedback(f.getStudentId(),f.getRating(),f.getComment());if(err!=null){sendJson(ex,400,"{\"message\":\""+json(err)+"\"}");return;}sendJson(ex,200,"{\"ok\":true}");return;}

        if(path.equals("/api/staff/queue")&&method.equals("GET")&&"STAFF".equals(u.getRole())){sendJson(ex,200,"{\"queue\":"+queueListJson(queue.getTodaysActiveQueue())+"}");return;}
        if(path.equals("/api/staff/call")&&method.equals("POST")&&"STAFF".equals(u.getRole())){QueueEntry e=findQueue(intVal(params(ex).get("queueId")));if(e==null||!queue.callNext(e,u.getUserId())){sendJson(ex,400,"{\"message\":\"The queue entry could not be called.\"}");return;}sendJson(ex,200,"{\"ok\":true}");return;}
        if(path.equals("/api/staff/complete")&&method.equals("POST")&&"STAFF".equals(u.getRole())){QueueEntry e=findQueue(intVal(params(ex).get("queueId")));if(e==null||!queue.completeService(e,u.getUserId())){sendJson(ex,400,"{\"message\":\"The queue entry could not be completed.\"}");return;}sendJson(ex,200,"{\"ok\":true}");return;}
        if(path.equals("/api/laptops")&&method.equals("GET")&&("STAFF".equals(u.getRole())||"ADMIN".equals(u.getRole()))){StringBuilder a=new StringBuilder("[");boolean f=true;for(Laptop l:laptops.getAvailableLaptops()){if(!f)a.append(',');f=false;a.append(laptopJson(l));}a.append(']');sendJson(ex,200,"{\"laptops\":"+a+"}");return;}
        if(path.equals("/api/laptop/allocate")&&method.equals("POST")&&"STAFF".equals(u.getRole())){Map<String,String> p=params(ex);if(!laptops.allocateLaptop(intVal(p.get("laptopId")),intVal(p.get("studentId")),u.getUserId())){sendJson(ex,400,"{\"message\":\"Laptop allocation failed. Check the laptop and student ID.\"}");return;}sendJson(ex,200,"{\"ok\":true}");return;}

        if(path.equals("/api/admin/summary")&&method.equals("GET")&&"ADMIN".equals(u.getRole())){sendJson(ex,200,"{\"average\":"+feedback.getAverageRating()+",\"logs\":"+logsJson()+"}");return;}
        if(path.equals("/api/admin/staff")&&method.equals("POST")&&"ADMIN".equals(u.getRole())){Map<String,String> p=params(ex);String err=auth.registerStaff(u.getUserId(),p.get("username"),p.get("password"),p.get("fullName"),p.get("email"),p.get("staffNumber"));if(err!=null){sendJson(ex,400,"{\"message\":\""+json(err)+"\"}");return;}sendJson(ex,200,"{\"ok\":true}");return;}

        sendJson(ex,404,"{\"message\":\"Endpoint not found.\"}");
    }

    private QueueEntry findQueue(int id){for(QueueEntry q:queue.getTodaysActiveQueue())if(q.getQueueId()==id)return q;return null;}

    private void staticFile(HttpExchange ex,String path)throws IOException{
        if(path.equals("/"))path="/index.html";
        if(path.contains("..")){send(ex,403,"Forbidden");return;}
        String resource="web"+path;
        InputStream in=WebServer.class.getClassLoader().getResourceAsStream(resource);
        if(in==null){send(ex,404,"Not found");return;}
        byte[] data=in.readAllBytes();in.close();
        String type=path.endsWith(".css")?"text/css; charset=UTF-8":path.endsWith(".js")?"application/javascript; charset=UTF-8":"text/html; charset=UTF-8";
        ex.getResponseHeaders().set("Content-Type",type);ex.sendResponseHeaders(200,data.length);try(OutputStream o=ex.getResponseBody()){o.write(data);}
    }

    private User current(HttpExchange ex){String t=cookie(ex,"QS_SESSION");return t==null?null:sessions.get(t);}
    private String cookie(HttpExchange ex,String name){String h=ex.getRequestHeaders().getFirst("Cookie");if(h==null)return null;for(String c:h.split(";")){String[] p=c.trim().split("=",2);if(p.length==2&&p[0].equals(name))return p[1];}return null;}
    private Map<String,String> params(HttpExchange ex)throws IOException{String body=new String(ex.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);Map<String,String> m=new HashMap<>();for(String pair:body.split("&")){if(pair.isBlank())continue;String[] p=pair.split("=",2);String k=URLDecoder.decode(p[0],StandardCharsets.UTF_8);String v=p.length>1?URLDecoder.decode(p[1],StandardCharsets.UTF_8):"";m.put(k,v);}return m;}
    private int intVal(String s){try{return Integer.parseInt(s==null?"0":s);}catch(Exception e){return 0;}}
    private void addCors(HttpExchange ex){ex.getResponseHeaders().set("Cache-Control","no-store");}
    private void sendJson(HttpExchange ex,int code,String body)throws IOException{ex.getResponseHeaders().set("Content-Type","application/json; charset=UTF-8");send(ex,code,body);}
    private void send(HttpExchange ex,int code,String body)throws IOException{byte[] b=body.getBytes(StandardCharsets.UTF_8);ex.sendResponseHeaders(code,b.length);try(OutputStream o=ex.getResponseBody()){o.write(b);}}
    private String json(String s){if(s==null)return "";return s.replace("\\","\\\\").replace("\"","\\\"").replace("\r","\\r").replace("\n","\\n");}
    private String userJson(User u){return "{\"userId\":"+u.getUserId()+",\"username\":\""+json(u.getUsername())+"\",\"fullName\":\""+json(u.getFullName())+"\",\"email\":\""+json(u.getEmail())+"\",\"role\":\""+json(u.getRole())+"\"}";}
    private String servicesJson(){StringBuilder b=new StringBuilder("{\"services\":[");boolean f=true;for(Service s:services.getActiveServices()){if(!f)b.append(',');f=false;b.append("{\"serviceId\":"+s.getServiceId()+",\"serviceName\":\""+json(s.getServiceName())+"\",\"description\":\""+json(s.getDescription())+"\"}");}return b.append("]}").toString();}
    private String queueJson(QueueEntry q){return "{\"queueId\":"+q.getQueueId()+",\"queueNumber\":"+q.getQueueNumber()+",\"serviceName\":\""+json(q.getServiceName())+"\",\"status\":\""+json(q.getStatus())+"\",\"joinedAt\":\""+(q.getJoinedAt()==null?"":q.getJoinedAt())+"\"}";}
    private String queueListJson(List<QueueEntry> list){StringBuilder b=new StringBuilder("[");boolean f=true;for(QueueEntry q:list){if(!f)b.append(',');f=false;b.append("{\"queueId\":"+q.getQueueId()+",\"studentId\":"+q.getStudentId()+",\"queueNumber\":"+q.getQueueNumber()+",\"studentName\":\""+json(q.getStudentName())+"\",\"serviceName\":\""+json(q.getServiceName())+"\",\"status\":\""+json(q.getStatus())+"\",\"joinedAt\":\""+(q.getJoinedAt()==null?"":q.getJoinedAt())+"\"}");}return b.append(']').toString();}
    private String appointmentJson(Appointment a){return "{\"appointmentId\":"+a.getAppointmentId()+",\"serviceName\":\""+json(appointmentDAO.getServiceName(a.getAppointmentId()))+"\",\"date\":\""+a.getAppointmentDate()+"\",\"time\":\""+a.getAppointmentTime()+"\",\"status\":\""+a.getStatus()+"\"}";}
    private String laptopJson(Laptop l){return "{\"laptopId\":"+l.getLaptopId()+",\"assetNumber\":\""+json(l.getAssetTag())+"\",\"status\":\""+json(l.getStatus())+"\"}";}
    private String logsJson(){StringBuilder b=new StringBuilder("[");boolean f=true;for(AuditLog a:audit.getRecentLogs(50)){if(!f)b.append(',');f=false;b.append("{\"action\":\""+json(a.getAction())+"\",\"details\":\""+json(a.getDetails())+"\",\"createdAt\":\""+(a.getCreatedAt()==null?"":a.getCreatedAt())+"\"}");}return b.append(']').toString();}
}
