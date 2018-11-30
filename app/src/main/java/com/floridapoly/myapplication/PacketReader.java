package com.floridapoly.myapplication;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.framer.IPv6Framer;
import io.pkts.packet.IPPacket;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

public class PacketReader {
    public static final String TAG = "PacketReader.Class";
    public List<LatLng> listOfLatLng;
    private Context mContext;

    public PacketReader(Context context){
        mContext = context;
    }

    public void readPacketFile() throws IOException {
        final Pcap pcap = Pcap.openStream(mContext.getAssets().open("general.pcap"));

        final IPv6Framer iPv6Framer = new IPv6Framer();

        pcap.loop(new PacketHandler() {
            @Override
            public boolean nextPacket(Packet packet) throws IOException {

                if (packet.hasProtocol(Protocol.TCP)) {

                    TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
                    Buffer buffer = tcpPacket.getPayload();
                    if (buffer != null) {
                        //System.out.println("TCP: " + buffer);
                        //Log.d(TAG, "TCP: " + buffer);
                        IPPacket ipPacket = tcpPacket.getParentPacket();

                        Log.d(TAG,  "\n" + "\n"
                                + "TCP Arrival: " + tcpPacket.getArrivalTime() + "\n"
                                + " --TCP Seq: " + tcpPacket.getSequenceNumber()+ "\n"
                                + " --Source Port: " + tcpPacket.getSourcePort()+ "\n"
                                + " --Source IP: " + ipPacket.getSourceIP() + "\n"
                                + " --Destination IP: " + ipPacket.getDestinationIP());

                        Log.d(TAG, "Name: "+ ipPacket.getName());




                    }
                } else if (packet.hasProtocol(Protocol.UDP)) {

                    UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
                    Buffer buffer = udpPacket.getPayload();
                    if (buffer != null) {
                        //Log.d(TAG, "UDP: " + buffer);
                    }
                }
                return true;
            }
        });

    }
}
