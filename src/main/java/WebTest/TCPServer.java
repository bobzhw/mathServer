package WebTest;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        //创建socket，绑定到65000端口
        ServerSocket serverSocket=new ServerSocket(8899);
        //循环用以监听
        while (true){//这是多线程的，通过循环创建多个socket来实现
            //监听65000端口，直到有客户端信息发过来
            Socket socket=serverSocket.accept();
            System.out.println(socket.isConnected());
            //执行相关操作
            System.out.println("新lian接");
            new getNLP(socket).start();
        }
    }
}

