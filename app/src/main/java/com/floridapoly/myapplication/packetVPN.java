package com.floridapoly.myapplication;

import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;
import java.util.Enumeration;

public class packetVPN extends VpnService implements Handler.Callback, Runnable{
    private static final String TAG = "ToyVpnService";
    public static final String ACTION_CONNECT = "com.floridapoly.myapplication.START";
    public static final String ACTION_DISCONNECT = "com.floridapoly.myapplication.STOP";



    private Handler mHandler;
    private Thread mThread;

    private ParcelFileDescriptor mInterface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = new Handler(this);
        }

        // Stop the previous session by interrupting the thread.
        if (mThread != null) {
            mThread.interrupt();
        }

        // Start a new session by creating a new thread.
        mThread = new Thread(this, "ToyVpnThread");
        mThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message != null) {
            Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public synchronized void run() {
        Log.i(TAG,"running vpnService");
        try {
            runVpnConnection();
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, "Got " + e.toString());
        } finally {
            try {
                mInterface.close();
            } catch (Exception e) {
                // ignore
            }
            mInterface = null;

            Log.i(TAG, "Exiting");
        }
    }

    private void runVpnConnection() throws Exception {

        configure();

        FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());
        FileOutputStream out = new FileOutputStream((mInterface.getFileDescriptor()));
        ByteBuffer packet = ByteBuffer.allocate(32767);



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Allocate the buffer for a single packet.

        while (true) {
            int length = in.read(packet.array()); //Commented out for the out stream version below

            if (length > 100) {
                Log.v(TAG, "Nick D: " + length);


                //byte buffer2 = packet.get();
                int buffer = packet.get();
                int ipVersion = buffer >> 4;
                int headerLength = buffer & 0x0F; //number of 32 bit words in header
                headerLength *= 4;
                packet.get();
                int totalLength = packet.getChar(); //Total Length
                packet.getChar();                   //Identification
                packet.getChar();                   //Flags + Fragment Offset
                packet.get();                       //Time to Live
                int protocol = packet.get();        //Protocol
                packet.getChar();                   //Header checksum

                String sourceIP = "";
                sourceIP += packet.get() & 0xFF; //Source IP 1st Octet
                sourceIP += ".";
                sourceIP += packet.get() & 0xFF; //Source IP 2nd Octet
                sourceIP += ".";
                sourceIP += packet.get() & 0xFF; //Source IP 3rd Octet
                sourceIP += ".";
                sourceIP += packet.get() & 0xFF; //Source IP 4th Octet

                String destIP = "";
                destIP += packet.get() & 0xFF; //Destination IP 1st Octet
                destIP += ".";
                destIP += packet.get() & 0xFF; //Destination IP 2nd Octet
                destIP += ".";
                destIP += packet.get() & 0xFF; //Destination IP 3rd Octet
                destIP += ".";
                destIP += packet.get() & 0xFF; //Destination IP 4th Octet
                //Log.v(TAG, "Source: " + sourceIP);
                //Log.v(TAG, "Destination: " + destIP);
                int sourcePortUdp = packet.getChar();
                int destPortUdp = packet.getChar();
                packet.getChar(); //UDP Data Length
                packet.getChar(); //UDP Checksum
//NOTE: DNS HEADERS INSIDE UDP DATA - https://routley.io/tech/2017/12/28/hand-writing-dns-messages.html
                packet.getChar(); //DNS ID
                packet.get(); //OPTIONS: QR + OPCODE + AA + TC + RD
                packet.get(); //OPTIONS: Z + RCODE
                packet.getChar(); //DNS QDCOUNT //number of entities/questions
                packet.getChar(); //DNS ANCOUNT //num answers
                packet.getChar(); //DNS NSCOUNT //num auth records
                packet.getChar(); //DNS ARCOUNT //num additional records
//NOTE: QNAME is url encoded, in several separated sections, each preceded by an int saying the number of bytes
//NOTE: The QNAME section is terminated with a zero byte (00).
                int qnameSectionByteCount = packet.get();
                byte[] qnameBytes = new byte[0];
                byte[] qnameSectionBytes;
                int oldLength;
                while (qnameSectionByteCount > 0 && qnameSectionByteCount <= packet.remaining()) {
                    qnameSectionBytes = new byte[qnameSectionByteCount];
                    packet.get(qnameSectionBytes);
                    //insert the bytes from the new section
                    oldLength = qnameBytes.length;
                    qnameBytes = Arrays.copyOf(qnameBytes, oldLength + qnameSectionBytes.length);
                    System.arraycopy(qnameSectionBytes, 0, qnameBytes, oldLength, qnameSectionBytes.length);
                    //get the byte that determines if there is another loop iteration
                    qnameSectionByteCount = packet.get();
                    //add a connecting dot if there will be another loop iteration
                    if (qnameSectionByteCount > 0) {
                        //add a dot
                        byte[] dot = ".".getBytes();
                        oldLength = qnameBytes.length;
                        qnameBytes = Arrays.copyOf(qnameBytes, oldLength + dot.length);
                        System.arraycopy(dot, 0, qnameBytes, oldLength, dot.length);
                    }
                }
                packet.getChar(); //QCLASS
                packet.getChar(); //QCLASS

                String destHostName;
                try {
                    InetAddress addr = InetAddress.getByName(destIP);
                    destHostName = addr.getHostName();
                } catch (UnknownHostException e) {
                    destHostName = "Unresolved";
                }

                int orphanDataLength = packet.remaining();
                String dataStr = null;
                if (orphanDataLength > 0) {
                    byte[] data = new byte[orphanDataLength];
                    packet.get(data, packet.arrayOffset(), orphanDataLength);
                    dataStr = new String(data, Charset.forName("UTF-8"));
                }

                Log.v(TAG, "---\nHeaders:\nIP Version=" + ipVersion + "\nHeader-Length=" + headerLength
                        + "\nTotal-Length=" + totalLength + "\nDestination=" + destIP + " / "
                        + destHostName + "\nSource-IP=" + sourceIP + "\nProtocol=" + protocol
                        + "\nSource-Port=" + sourcePortUdp + "\nDestPortUdp=" + destPortUdp + "\nQname="
                        + new String(qnameBytes, Charset.forName("UTF-8")) + "\nRemaining-Data: " + dataStr);

            }
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    /*
        // We keep forwarding packets till something goes wrong.
        while (true) {
            // Assume that we did not make any progress in this iteration.
            boolean idle = true;

            // Read the outgoing packet from the input stream.
            int length = in.read(packet.array());
            if (length > 0) {
                //int buffer = packet.get();


                Charset charset = Charset.forName("ISO-8859-1");
                CharsetDecoder decoder = charset.newDecoder();

                //String decoded = new String(packet.array());
                String decoded = decoder.decode(packet).toString();


                Log.i(TAG,"decoded: "+ decoded);
                System.out.println("extra decoded:" + decoded);
                Log.i(TAG,"Length: "+ length);

                //System.exit(-1);

                Log.i(TAG,"************new packet");

                while (packet.hasRemaining()) {
                    byte notCoded = packet.get();

                    Log.i(TAG,"Not Cooded: "+ notCoded);



                }

                packet.limit(length);
                //  tunnel.write(packet);
                packet.clear();

                // There might be more outgoing packets.
                idle = false;
            }
            Thread.sleep(50);
        }
        */

    }
    public String getLocalIpAddress()
    {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    Log.i(TAG,"****** INET ADDRESS ******");
                    Log.i(TAG,"address: "+inetAddress.getHostAddress());
                    Log.i(TAG,"hostname: "+inetAddress.getHostName());
                    Log.i(TAG,"address.toString(): "+inetAddress.getHostAddress().toString());
                    if (!inetAddress.isLoopbackAddress()) {
                        //IPAddresses.setText(inetAddress.getHostAddress().toString());
                        Log.i(TAG,"IS NOT LOOPBACK ADDRESS: "+inetAddress.getHostAddress().toString());
                        return inetAddress.getHostAddress().toString();
                    } else{
                        Log.i(TAG,"It is a loopback address");
                    }
                }
            }
        } catch (SocketException ex) {
            String LOG_TAG = null;
            Log.e(LOG_TAG, ex.toString());
        }

        return null;
    }

    private void configure() throws Exception {
        // If the old interface has exactly the same parameters, use it!
        if (mInterface != null) {
            Log.i(TAG, "Using the previous interface");
            return;
        }

        // Configure a builder while parsing the parameters.
        Builder builder = new Builder();
        builder.setMtu(1500);
        //builder.addAddress("192.168.0.7", 24); //We commented this out to try the new getlocalIP address method
        builder.addAddress(getLocalIpAddress(), 24);
        builder.addRoute("0.0.0.0", 0);
        try {
            mInterface.close();
        } catch (Exception e) {
            // ignore
        }

        mInterface = builder.establish();
        Log.i(TAG, "Interface: " + mInterface);

    }
}