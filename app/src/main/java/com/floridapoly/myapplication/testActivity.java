package com.floridapoly.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//PCAP repository
import java.io.IOException;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

public class testActivity extends AppCompatActivity {
    public static final String TAG = "TestActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        Button readBtn = (Button) findViewById(R.id.test_btn_read);


        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    readPacketFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    ////////////////////////////////////////////////////////////////////////
    //Utility methods

    public void readPacketFile() throws IOException {
        final Pcap pcap = Pcap.openStream(getAssets().open("general.pcap"));

        pcap.loop(new PacketHandler() {
            @Override
            public boolean nextPacket(Packet packet) throws IOException {

                if (packet.hasProtocol(Protocol.TCP)) {

                    TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
                    Buffer buffer = tcpPacket.getPayload();
                    if (buffer != null) {
                        //System.out.println("TCP: " + buffer);
                        //Log.d(TAG, "TCP: " + buffer);
                        
                    }
                } else if (packet.hasProtocol(Protocol.UDP)) {

                    UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
                    Buffer buffer = udpPacket.getPayload();
                    if (buffer != null) {
                        Log.d(TAG, "UDP: " + buffer);
                    }
                }
                return true;
            }
        });

    }

}
