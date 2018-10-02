/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paper.pkg1_simulator;

import paper.pkg1_simulator.XMPPMessage.XMPPMessage;
import paper.pkg1_simulator.regularApp.RegularClient;
import sacalableApp.ScalableClient;
/**
 *
 * @author amir
 */
public class NetworkHandler 
{
    private static final String DEBUG_TAG="NetworkHandler";
    
   
    private static final int UNLIMITED=0;
    
    public static class WirelessNetworkConfig{
        public static int NETWORK_CAPACITY=
                Paper1_simulator.networkHandler_NETWORK_CAPACITY;
        public static int NETWORK_LATENCY=
                Paper1_simulator.networkHandler_NETWORK_LATENCY;//mili-second
    }
    
    public static class WiredLineNetworkConfig{
        public final int NETWORK_CAPACITY=UNLIMITED;
    }
    
    //this should extended to every domain
    private static int numbersOfCurrentWirelessMessages_domain1=0;
    private static int numbersOfCurrentWirelessMessages_domain2=0;
    
    public static void sendFromClientToDomain(final XMPPMessage msg,final int mode)
    {
        try
        {
//            System.out.println(DEBUG_TAG+" sendFromClientToDomain");
                        
            Thread sendFromClientToDomainThread=new Thread()
            {
                @Override
                public void run()
                {
                    ///
                    try
                    {
                        if(mode==Paper1_simulator.USING_REGULAR_MIDDLEWARE_MODE)
                        {
                            if(WirelessNetworkConfig.NETWORK_CAPACITY!=UNLIMITED)
                            {
                                if(msg.FROM.contains(
                                        ServerHandle.regularServer1.serverDescription_domainName))
                                {

                                    if(WirelessNetworkConfig.NETWORK_CAPACITY<
                                            numbersOfCurrentWirelessMessages_domain1)
                                    {

                                        Logger.logDropMessagebyNetwork(msg.id, msg.FROM,msg.TO);
                                    }
                                    else
                                    {
//                                        System.out.println(
//                                                String.valueOf(numbersOfCurrentWirelessMessages_domain1++));
                                                
                                        numbersOfCurrentWirelessMessages_domain1++;

                                        Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);
                                        routeMessage(mode,msg);

                                        numbersOfCurrentWirelessMessages_domain1--;
                                    }   
                                }
                                else if(msg.FROM.contains(
                                        ServerHandle.regularServer2.serverDescription_domainName))
                                {

                                    if(WirelessNetworkConfig.NETWORK_CAPACITY<
                                            numbersOfCurrentWirelessMessages_domain2)
                                    {
                          
                                        Logger.logDropMessagebyNetwork(msg.id, msg.FROM,msg.TO);

                                    }
                                    else
                                    {
                                        numbersOfCurrentWirelessMessages_domain2++;

                                        Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);

                                        routeMessage(mode,msg);

                                        numbersOfCurrentWirelessMessages_domain2--;
                                    }   
                                }
                                else
                                {
                                    System.out.println("sendFromClientToDomain domain error!");
                                }

                            }
                            else
                            {
                                //just handle the message
                            }
                        }
                        else
                        {
                            if(WirelessNetworkConfig.NETWORK_CAPACITY!=UNLIMITED)
                            {
                                if(msg.FROM.contains(
                                        ServerHandle.scalableServer1.serverDescription_domainName))
                                {

                                    if(WirelessNetworkConfig.NETWORK_CAPACITY<
                                            numbersOfCurrentWirelessMessages_domain1)
                                    {
                                        createAck(msg,true);
                                        Logger.logDropMessagebyNetwork(msg.id, msg.FROM,msg.TO);
                                    }
                                    else
                                    {
                                        numbersOfCurrentWirelessMessages_domain1++;

                                        Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);

                                        routeMessage(mode,msg);

                                        numbersOfCurrentWirelessMessages_domain1--;
                                    }   
                                }
                                else if(msg.FROM.contains(
                                        ServerHandle.scalableServer2.serverDescription_domainName))
                                {

                                    if(WirelessNetworkConfig.NETWORK_CAPACITY<
                                            numbersOfCurrentWirelessMessages_domain2)
                                    {
                                        createAck(msg,true);
                                        
                                        Logger.logDropMessagebyNetwork(msg.id, msg.FROM,msg.TO);
                                    }
                                    else
                                    {
                                        numbersOfCurrentWirelessMessages_domain2++;

                                        Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);

                                        routeMessage(mode,msg);

                                        numbersOfCurrentWirelessMessages_domain2--;
                                    }   
                                }
                                else
                                {
                                    System.out.println("sendFromClientToDomain domain error!");
                                }
                            }
                            else
                            {
                                //just handle the message
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.println("Exception: ["+DEBUG_TAG+
                                " sendClientToServer()-thread:"+String.valueOf(e) +"]");
                    }
                            
                    
                    ////
                    super.run();
                }

            };
            
            sendFromClientToDomainThread.start();
         
            
            
        }
        catch(Exception e)
        {
            System.out.println("Exception: ["+DEBUG_TAG+
                    " sendClientToServer():"+String.valueOf(e) +"]");
        }
    }
    
    
    private static void createAck(XMPPMessage _msg,boolean dropping)
    {
        try
        {  
            //if msg is from our domain (sent by client, not server), sent an Ack, 
 
            XMPPMessage message=new XMPPMessage();
            message.type=XMPPMessage.ACK;
            message.isDroped=true;
            message.TO=_msg.FROM;         
            message.sentTimeStamp=_msg.sentTimeStamp;
            message.FROM="noNeedToCare@"+_msg.FROM.split("@")[1];
            
            NetworkHandler.sendServerToClient(message);
 
        }
        catch(Exception e)
        {       
              System.out.println("Exception: ["+DEBUG_TAG+" createAck():"
                      +String.valueOf(e) +"]");
              
        }
    }
    
    private static void routeMessage(int mode,XMPPMessage msg)
    {
        if(mode==Paper1_simulator.USING_REGULAR_MIDDLEWARE_MODE)
        {
            
//           System.out.println(DEBUG_TAG+" ======send.msg====== ");
//           System.out.println(DEBUG_TAG+" send.msg.FROM "+ msg.FROM);
//           System.out.println(DEBUG_TAG+" send.msg.TO "+ msg.TO);
//           System.out.println(DEBUG_TAG+" ==================== ");

            if(msg.FROM.contains(ServerHandle.regularServer1.serverDescription_domainName))
            {
//                System.out.println(DEBUG_TAG+" ======sendFromClientToDomain.msg====== ");
//                System.out.println(DEBUG_TAG+" send.msg.FROM "+ msg.FROM);
//                System.out.println(DEBUG_TAG+" send.msg.TO "+ msg.TO);
//                System.out.println(DEBUG_TAG+" receive by: "+
//                        ServerHandle.regularServer1.serverDescription_domainName);
//                System.out.println(DEBUG_TAG+" ==================== ");
                
                ServerHandle.regularServer1.serverSideMiddleware.receiveMessage(msg);
            }
            else if(msg.FROM.contains(ServerHandle.regularServer2.serverDescription_domainName))
            {
//                System.out.println(DEBUG_TAG+" ======sendFromClientToDomain.msg====== ");
//                System.out.println(DEBUG_TAG+" send.msg.FROM "+ msg.FROM);
//                System.out.println(DEBUG_TAG+" send.msg.TO "+ msg.TO);
//                System.out.println(DEBUG_TAG+" receive by: "+
//                        ServerHandle.regularServer2.serverDescription_domainName);
//                System.out.println(DEBUG_TAG+" ==================== ");
                    
                ServerHandle.regularServer2.serverSideMiddleware.receiveMessage(msg);
            }
            else
            {
                System.out.println("domain not found");
                System.out.println(DEBUG_TAG+" send.msg.FROM "+ msg.FROM);
                System.out.println(DEBUG_TAG+" send.msg.TO "+ msg.TO);
            }
        }
        else //if(mode==Paper1_simulator.USING_SCALABLE_MIDDLEWARE_MODE)
        {
            if(msg.FROM.contains(ServerHandle.scalableServer1.serverDescription_domainName))
            {
//                System.out.println(DEBUG_TAG+" ======sendFromClientToDomain.msg====== ");
//                System.out.println(DEBUG_TAG+" send.msg.FROM "+ msg.FROM);
//                System.out.println(DEBUG_TAG+" send.msg.TO "+ msg.TO);
//                System.out.println(DEBUG_TAG+" receive by: "+
//                        ServerHandle.regularServer1.serverDescription_domainName);
//                System.out.println(DEBUG_TAG+" ==================== ");
                
                ServerHandle.scalableServer1.serverSideMiddleware.receiveMessage(msg);
            }
            else if(msg.FROM.contains(ServerHandle.scalableServer2.serverDescription_domainName))
            {
//                System.out.println(DEBUG_TAG+" ======sendFromClientToDomain.msg====== ");
//                System.out.println(DEBUG_TAG+" send.msg.FROM "+ msg.FROM);
//                System.out.println(DEBUG_TAG+" send.msg.TO "+ msg.TO);
//                System.out.println(DEBUG_TAG+" receive by: "+
//                        ServerHandle.regularServer2.serverDescription_domainName);
//                System.out.println(DEBUG_TAG+" ==================== ");
                    
                ServerHandle.scalableServer2.serverSideMiddleware.receiveMessage(msg);
            }
            else
            {
                System.out.println("domain not found");
                System.out.println(DEBUG_TAG+" send.msg.FROM "+ msg.FROM);
                System.out.println(DEBUG_TAG+" send.msg.TO "+ msg.TO);
            }
        }
    }
    
    public static void sendServerToServer(XMPPMessage msg)
    {
        try
        {
            
            if(Paper1_simulator.MIDDLEWARE_MODE==
                    Paper1_simulator.USING_REGULAR_MIDDLEWARE_MODE)
            {
               
                //find destination server
                if(msg.TO.contains(ServerHandle.regularServer1.serverDescription_domainName))
                {
                    ServerHandle.regularServer1.serverSideMiddleware.receiveMessage(msg);
                }
                else if(msg.TO.contains(ServerHandle.regularServer2.serverDescription_domainName))
                {
                    ServerHandle.regularServer2.serverSideMiddleware.receiveMessage(msg);
                }
            }
            else
            {
                //find destination server
                if(msg.TO.contains(ServerHandle.scalableServer1.serverDescription_domainName))
                {
                    ServerHandle.scalableServer1.serverSideMiddleware.receiveMessage(msg);
                }
                else if(msg.TO.contains(ServerHandle.scalableServer2.serverDescription_domainName))
                {
                    ServerHandle.scalableServer2.serverSideMiddleware.receiveMessage(msg);
                }
            }
        }
        catch(Exception e) 
        {
              System.out.println("Exception: ["+DEBUG_TAG+" sendServerToServer():"+e.getMessage()+"]");
        }
    }
    
    
    public static void sendServerToClient(XMPPMessage msg)
    {
        try
        {
            if(msg.type!=XMPPMessage.ACK)
            {
                Logger.logSendMessageFromServerToClient(msg.id,msg.FROM,msg.TO);
            }
            
            
            //here we should consider network latency and network capacity
            if(Paper1_simulator.MIDDLEWARE_MODE==
                    Paper1_simulator.USING_REGULAR_MIDDLEWARE_MODE)
            {
                if(msg.TO.contains(
                        ServerHandle.regularServer1.serverDescription_domainName))
                {
                    if(WirelessNetworkConfig.NETWORK_CAPACITY<
                            numbersOfCurrentWirelessMessages_domain1)
                    {
    //                    System.out.println(DEBUG_TAG+
    //                            " sendFromClientToDomain: PACKET_DROPDED_BY_NETWORK" );

                        Logger.logDropMessagebyNetwork(msg.id, msg.FROM,msg.TO);
                    }
                    else
                    {
                        //numbersOfCurrentWirelessMessages_domain1++;

                        //Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);

                        routeMsgFromServerToClient(msg);

                        //numbersOfCurrentWirelessMessages_domain1--;
                    }   
                }
                else if(msg.TO.contains(
                        ServerHandle.regularServer2.serverDescription_domainName))
                {
                    if(WirelessNetworkConfig.NETWORK_CAPACITY<
                            numbersOfCurrentWirelessMessages_domain2)
                    {
    //                    System.out.println(DEBUG_TAG+
    //                            " sendFromClientToDomain: PACKET_DROPDED_BY_NETWORK" );

                        Logger.logDropMessagebyNetwork(msg.id, msg.FROM,msg.TO);
                    }
                    else
                    {
                       // numbersOfCurrentWirelessMessages_domain2++;

                        //Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);

                        routeMsgFromServerToClient(msg);

                        //numbersOfCurrentWirelessMessages_domain2--;
                    }   
                }
            }
            else
            {
                if(msg.type==XMPPMessage.ACK)
                 {
                     Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);
                     routeMsgFromServerToClient(msg);
                 }
                else if(msg.TO.contains(
                            ServerHandle.scalableServer1.serverDescription_domainName))
                {
                 
                    if(WirelessNetworkConfig.NETWORK_CAPACITY<
                            numbersOfCurrentWirelessMessages_domain1 &&
                            msg.type!=XMPPMessage.ACK)
                    {
    //                    System.out.println(DEBUG_TAG+
    //                            " sendFromClientToDomain: PACKET_DROPDED_BY_NETWORK" );

                        Logger.logDropMessagebyNetwork(msg.id, msg.FROM,msg.TO);
                    }
                    else
                    {
//                        if(msg.type==XMPPMessage.ACK)
//                        {
//                            Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);
//                            routeMsgFromServerToClient(msg);
//                        }
//                        else
//                        {             
                            numbersOfCurrentWirelessMessages_domain1++;
                            Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);
                            routeMsgFromServerToClient(msg);
                            numbersOfCurrentWirelessMessages_domain1--;
//                        }
                       
                    }   
                }
                else if(msg.TO.contains(
                        ServerHandle.scalableServer2.serverDescription_domainName))
                {
                    if(WirelessNetworkConfig.NETWORK_CAPACITY<
                            numbersOfCurrentWirelessMessages_domain2 &&
                            msg.type==XMPPMessage.ACK)
                    {
    //                    System.out.println(DEBUG_TAG+
    //                            " sendFromClientToDomain: PACKET_DROPDED_BY_NETWORK" );

                        Logger.logDropMessagebyNetwork(msg.id, msg.FROM,msg.TO);
                    }
                    else
                    {
//                        if(msg.type==XMPPMessage.ACK)
//                        {
//                            Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);
//                            routeMsgFromServerToClient(msg);
//                        }
//                        else
//                        {                                                     
                            numbersOfCurrentWirelessMessages_domain2++;
                            Thread.sleep(WirelessNetworkConfig.NETWORK_LATENCY);
                            routeMsgFromServerToClient(msg);
                            numbersOfCurrentWirelessMessages_domain2--;
//                        }
                        
                    }   
                }
            }
            
            ////
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["
                      +DEBUG_TAG+" sendServerToClient():"+String.valueOf(e)+"]");
        }
    }
    
    private static void routeMsgFromServerToClient(XMPPMessage msg)
    {          
        int receiverClientIndex=search(msg.TO.split("@")[0], msg.TO.split("@")[1]);
        
        if(Paper1_simulator.MIDDLEWARE_MODE==Paper1_simulator.USING_REGULAR_MIDDLEWARE_MODE)
        {
            RegularClient receiverClient=ClientHandler.clientList.get(receiverClientIndex);    
            receiverClient.clientSideMiddleware.receiveMessage(msg);
        }
        else
        {                       
            ScalableClient receiverClient=ClientHandler.
                    scalableClientList.get(receiverClientIndex);
            
            receiverClient.scalableClientSideMiddleware.receiveMessage(msg);
        }
        
    }
    
    private static int search(String clientName,String domainName)
    {
        for(int i=0;i<ClientHandler.clientList.size();i++)
        {
            RegularClient client=ClientHandler.clientList.get(i);
            if(client.ClientDescription_clientName.equals(clientName)
                    && client.ClientDescription_domainName.equals(domainName) )
            {
                return i;
            }
        }
        for(int i=0;i<ClientHandler.scalableClientList.size();i++)
        {
            ScalableClient client=ClientHandler.scalableClientList.get(i);
            
//            System.out.println("ClientDescription_clientName: "+
//                    client.ClientDescription_clientName);
//            System.out.println("ClientDescription_domainName: "+
//                    client.ClientDescription_domainName);
//            
//            System.out.println(" clientName: "+clientName);
//            System.out.println(" domainName: "+domainName);
            
            
            
            if(client.ClientDescription_clientName.equals(clientName)
                    && client.ClientDescription_domainName.equals(domainName) )
            {
                return i;
            }
        }
        return  -1;
    }
}
