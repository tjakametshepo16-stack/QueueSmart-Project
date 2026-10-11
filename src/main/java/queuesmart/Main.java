package queuesmart;

public class Main {
    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }
        WebServer server = new WebServer(port);
        server.start();
        System.out.println("QueueSmart is running at http://localhost:" + port);
        System.out.println("Press Ctrl+C to stop the server.");
    }
}