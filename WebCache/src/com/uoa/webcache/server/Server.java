import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	 public static void main(String args[]) {
	      String data = "This will be the file list";
	      try {
	         ServerSocket serverSocket = new ServerSocket(8888);
	         Socket socket = serverSocket.accept();
	         System.out.print("Server socket is listening for trafic!\n");
	         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	         System.out.print("Sending string: '" + data + "'\n");
	         out.print(data);
	         out.close();
	         socket.close();
	         serverSocket.close();
	      }
	      catch(Exception e) {
	    	  e.printStackTrace();
	         System.out.print("Whoops! It didn't work!\n");
	      }
	   }
}
