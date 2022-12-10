package ca.camosun.ICS226;


public class App 
{
   public static void main(String[] args) {
            if (args.length != 1) {
                System.err.println("Need <port>");
                System.exit(-99);
            }
            Server s = new Server(Integer.valueOf(args[0]));
            s.serve();
        }

}
