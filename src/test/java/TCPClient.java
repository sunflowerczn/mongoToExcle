import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * @program: mongoToExcle
 * @description: 聊天室客户端
 * @author: chenzhennan
 * @create: 2020-03-06 13:54
 **/
public class TCPClient {
    public static void main(String[] args) {

        try {
            //连接服务器
            Socket socket = new Socket("localhost",8080);
            //读取服务端发过来的数据
            InputStream inputStream = socket.getInputStream();
            byte[] b = new byte[2048];
            inputStream.read(b);
            System.out.println(new String(b));
            //向服务器发送数据
            System.out.println("请输入聊天内容:");
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream),true);
            Scanner sc = new Scanner(System.in);
            while(sc.hasNextLine()){
                printWriter.println(sc.nextLine());
            }
            sc.close();
            printWriter.close();
            outputStream.close();
            inputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
