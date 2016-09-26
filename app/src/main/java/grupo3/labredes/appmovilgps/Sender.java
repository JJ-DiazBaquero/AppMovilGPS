package grupo3.labredes.appmovilgps;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static android.content.ContentValues.TAG;

/**
 * Created by user on 25/09/2016.
 */
public class Sender {

    public static DatagramSocket socket = null;
    InetAddress ip;
    String message;
    int port;
    private AsyncTask<Void, Void, Void> async;

    public Sender(String StrIp, String message, int port){
        this.message = message;
        this.port = port;
        try {
            if(socket==null)
            socket = new DatagramSocket();
            ip = InetAddress.getByAddress(new byte[]{(byte)192,(byte)168,(byte)0,(byte)13});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void send() {
        try{
            async = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    int msgLength = message.length();
                    byte[] messageB = message.getBytes();
                    DatagramPacket p = new DatagramPacket(messageB, msgLength, ip, port);
                    try {
                        socket.send(p);
                        Log.d(TAG, "Enviar mensaje UDP");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            async.execute();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
