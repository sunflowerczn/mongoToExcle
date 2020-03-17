import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: mongoToExcle
 * @description: 聊天室服务端
 * @author: chenzhennan
 * @create: 2020-03-06 13:56
 **/
public class TCPServer {
    private static ExecutorService pool = Executors.newFixedThreadPool(10);
    public static void main(String[] args) {

        try {
            final ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("服务器启动成功...");
            while(true){
                final Socket socket = serverSocket.accept();
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        String ip = socket.getInetAddress().getHostAddress();
                        try {
                            OutputStream outputStream = socket.getOutputStream();
                            String welcome = "欢迎:" + ip + "来到聊天室";
                            outputStream.write(welcome.getBytes());
                            System.out.println(ip + "访问了我们的系统...");
                            InputStream inputStream = socket.getInputStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            String line = "";
                            while ((line = bufferedReader.readLine()) != null){
                                System.out.println(ip + "说==>" + line);
                            }
                            bufferedReader.close();
                            inputStream.close();
                            outputStream.close();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            try {
                                serverSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
