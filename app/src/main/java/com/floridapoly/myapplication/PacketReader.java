package com.floridapoly.myapplication;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public int packetsRead = 0;
    public List<LatLng> listOfLatLng = new ArrayList<>();
    public List<MarkerOptions> markerList = new ArrayList<MarkerOptions>();
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
                    packetsRead +=1;

                    TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
                    Buffer buffer = tcpPacket.getPayload();
                    if (buffer != null) {

                        IPPacket ipPacket = tcpPacket.getParentPacket();

                        Log.d(TAG,  "\n" + "\n"
                                + "TCP Arrival: " + tcpPacket.getArrivalTime() + "\n"
                                + " --TCP Seq: " + tcpPacket.getSequenceNumber()+ "\n"
                                + " --Source Port: " + tcpPacket.getSourcePort()+ "\n"
                                + " --Source IP: " + ipPacket.getSourceIP() + "\n"
                                + " --Destination IP: " + ipPacket.getDestinationIP());

                        Log.d(TAG, "Name: "+ ipPacket.getName());

                        //DORIANS FUNCTION
                        getGEOLocation(ipPacket.getSourceIP());



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


    private void getGEOLocation(String sourceIP){

        if(sourceIP.equalsIgnoreCase("2603:9000:ad08:f800:586a:cb5:f62e:31c6")){
            return;
        }
        //DORIAN PLACE YOUR CODE HERE FOR EXTRACTING THE GEO LOCATION OF AN IP ADDRESS


        //RANDOM FOR NOW
        Random r = new Random();
        double lat = -80 + (80 - (-80)) * r.nextDouble();
        Log.d("GEO", "Lat: " + lat);
        double lng = -170+ (170 - (-170)) * r.nextDouble();
        Log.d("GEO", "Lng: " + lng);
        Log.d("GEO", "Count: " + packetsRead);
/////////////////////////

        ///////////////////////////////////////////
        //This is for Heatmap
        LatLng geoTag = new LatLng(lat,lng);
        listOfLatLng.add(geoTag);

        //this is for marker map
        MarkerOptions tag = new MarkerOptions().position(geoTag);
        markerList.add(tag);


    }
}
