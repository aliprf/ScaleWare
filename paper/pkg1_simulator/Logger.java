package paper.pkg1_simulator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author amir
 */
public class Logger 
{
    public static void printResult()
    {
        try
        {
            System.out.println("============result=================");
            System.out.println("          entity status            ");
            System.out.println(" number of servers: "+String.valueOf(serversList.size()));
            System.out.println(" number of clients: "+String.valueOf(clientList.size()));
            
            System.out.println("       sent message status         ");
            System.out.println(" by clients: "+String.valueOf(sentMsgFromClientList.size()));
            System.out.println(" by servers To Client: "+String.valueOf(sentMsgFromServerListToClient.size()));
            System.out.println(" by servers To server: "+String.valueOf(sentMsgFromServerListToServer.size()));

            System.out.println("     received message status       ");
            System.out.println(" by clients: "+String.valueOf(receiveMsgByClientList.size()));
            System.out.println(" by servers: "+String.valueOf(receiveMsgByServerList.size()));

            System.out.println("       droped message status       ");
            System.out.println(" by clients: "+String.valueOf(dropMsgByClientList.size()));
            System.out.println(" by servers: "+String.valueOf(dropMsgByServerList.size()));
            System.out.println(" by network: "+String.valueOf(dropMsgByNetworkList.size()));
            System.out.println("===================================");
   
//            if(Paper1_simulator.MIDDLEWARE_MODE==
//                    Paper1_simulator.USING_SCALABLE_MIDDLEWARE_MODE)
//            {
//                System.out.println("       ScalableclientSide middleware:       ");
//                
//                for(int i=0;i<delayMap.size();i++)
//                {
//                  //  System.out.println("client name:"+delayMap.);
//                    System.out.println("client name:");
//                }
//                
//                System.out.println("===================================");
//            }
            
            
//            System.out.println("=================clients description:==================");
//            for(int i=0;i<clientList.size();i++)
//            {
//                System.out.println("client: "+clientList.get(i).name+"@"+
//                        clientList.get(i).domain);
//            }
//            
//            System.out.println("======client's sent messages description:===============");
//            for(int i=0;i<sentMsgFromClientList.size();i++)
//            {
//                System.out.print("client from: "+
//                        sentMsgFromClientList.get(i).from.name+
//                        sentMsgFromClientList.get(i).from.domain);
//                
//                System.out.println(" ----> client to: "+
//                        sentMsgFromClientList.get(i).to.name+
//                        sentMsgFromClientList.get(i).to.domain);
//            }
//            
//            System.out.println("======client's received messages description:===============");
//            for(int i=0;i<receiveMsgByClientList.size();i++)
//            {
//                System.out.print("client from: "+
//                        receiveMsgByClientList.get(i).from.name+
//                        receiveMsgByClientList.get(i).from.domain);
//                
//                    System.out.println(" ----> client to: "+
//                        receiveMsgByClientList.get(i).to.name+
//                        receiveMsgByClientList.get(i).to.domain);
//            }
//            
//            System.out.println("======servers's sent messages description:===============");
//            for(int i=0;i<sentMsgFromServerList.size();i++)
//            {
//                System.out.print("server from: "+
//                        sentMsgFromServerList.get(i).from.name+
//                        sentMsgFromServerList.get(i).from.domain);
//                
//                System.out.println(" ----> server to: "+
//                        sentMsgFromServerList.get(i).to.name+
//                        sentMsgFromServerList.get(i).to.domain);
//            }
//            
//            System.out.println("======servers's receive messages description:===============");
//            for(int i=0;i<receiveMsgByServerList.size();i++)
//            {
//                System.out.print("server from: "+
//                        receiveMsgByServerList.get(i).from.name+
//                        receiveMsgByServerList.get(i).from.domain);
//                
//                System.out.println(" ----> server to: "+
//                        receiveMsgByServerList.get(i).to.name+
//                        receiveMsgByServerList.get(i).to.domain);
//            }
        }
        catch(Exception e)
        {
            System.out.println("printResult EXCEPTION: "+e.getMessage());
        }

    }
    ///////////////////////////
    private static ArrayList<EntityDescription> serversList=new ArrayList<>();
    private static ArrayList<EntityDescription> clientList=new ArrayList<>();
    
    private static class EntityDescription
    {
        public String name;
        public String domain;
        public Date creationDate;
    }
    
    private static class From
    {
        public String name;
        public String domain;
    }
    
    private static class TO
    {
        public String name;
        public String domain;
    }
    
    private static class MessageDescription
    {
        public String messageId;
        public From from;
        public TO to;
        public Date creationDate;
    }
    
    private static ArrayList<MessageDescription> dropMsgByClientList=new ArrayList<>();
    private static ArrayList<MessageDescription> dropMsgByServerList=new ArrayList<>();
   
    private static ArrayList<MessageDescription> dropMsgByNetworkList=new ArrayList<>();
    
    private static ArrayList<MessageDescription> sentMsgFromClientList=new ArrayList<>();
    private static ArrayList<MessageDescription> sentMsgFromServerListToServer=new ArrayList<>();
    private static ArrayList<MessageDescription> sentMsgFromServerListToClient=new ArrayList<>();
    
    private static ArrayList<MessageDescription> receiveMsgByClientList=new ArrayList<>();
    private static ArrayList<MessageDescription> receiveMsgByServerList=new ArrayList<>();
    
    
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    
    ///////////////////////clientSideMiddleware/////////////////////
  
    private static HashMap<String,ArrayList> networkDelayMap=new HashMap<>();
    private static HashMap<String,ArrayList> delayMap=new HashMap<>();
    
    public static void logDelayParameterByClientSideMiddleware(
            String clientName,long delayParam)
    {
        if(delayMap.containsKey(clientName))
        {
            ArrayList _tmp=delayMap.get(clientName);
            _tmp.add(delayParam);
            
            delayMap.put(clientName, _tmp);
        }
        else
        {
            ArrayList<Long> delayList=new ArrayList<>();
            delayList.add(delayParam);
            delayMap.put(clientName, delayList);
        }
    }
    
    public static void logNetworkLatencyDelayParameterByClientSideMiddleware(
            String clientName,long detworkDelayParam)
    {
        if(networkDelayMap.containsKey(clientName))
        {
            ArrayList _tmp=delayMap.get(clientName);
            _tmp.add(detworkDelayParam);            
            networkDelayMap.put(clientName, _tmp);
        }
        else
        {
            ArrayList<Long> delayList=new ArrayList<>();
            delayList.add(detworkDelayParam);
            networkDelayMap.put(clientName, delayList);
        }
    }
    ///////////////////////droped messages////////////////////////////////////////
    public static void logDropMessagebyClient(String msgId,String from,String to)
    {
        dropMsgByClientList.add(prepareMessage(msgId, from, to));
    }
    
    public static void logDropMessagebyServer(String msgId,String from,String to)
    {
        dropMsgByServerList.add(prepareMessage(msgId, from, to));
    }
    
    public static void logDropMessagebyNetwork(String msgId,String from,String to)
    {
        dropMsgByNetworkList.add(prepareMessage(msgId, from, to));
    }
    ///////////////////////receive messages////////////////////////////////////////
    public static void logReceiveMessagebyClient(String msgId,String from,String to)
    {
        receiveMsgByClientList.add(prepareMessage(msgId, from, to));
    }
    
    public static void logReceiveMessagebyServer(String msgId,String from,String to)
    {
        receiveMsgByServerList.add(prepareMessage(msgId, from, to));
    }
    
    ///////////////////////sent messages////////////////////////////////////////
    public static void logSendMessageFromClient(String msgId,String from,String to)
    {
        sentMsgFromClientList.add(prepareMessage(msgId, from, to));
    }
    
    public static void logSendMessageFromServerToClient(String msgId,String from,String to)
    {
        sentMsgFromServerListToClient.add(prepareMessage(msgId, from, to));
    }
    
    public static void logSendMessageFromServerToServer(String msgId,String from,String to)
    {
        sentMsgFromServerListToServer.add(prepareMessage(msgId, from, to));
    }
    
    private static MessageDescription prepareMessage(String msgId,String from, String to)
    {             
        Calendar calendar = Calendar.getInstance();
       
        MessageDescription messageDescription=new MessageDescription();
        
        messageDescription.messageId=msgId;
        
        messageDescription.from=new From();
        messageDescription.to=new TO();
        
        messageDescription.from.name=from.split("@")[0];
        messageDescription.from.domain=from.split("@")[1];
        
        messageDescription.to.name=to.split("@")[0];
        messageDescription.to.domain=to.split("@")[1];
        
        messageDescription.creationDate=calendar.getTime();   
                
        return messageDescription;
    }
    /////////////////////////entity creation////////////////////////////////////
    public static void logClientCreation(String clientName, String clientDomain)
    {
        clientList.add(prepareEntity(clientName, clientDomain));
    }
    
    public static void logServerCreation(String serverName, String serverDomain)
    {
        
        serversList.add(prepareEntity(serverName, serverDomain));
    }
    
    private static EntityDescription prepareEntity(String entityName, String entityDomain)
    {
        Calendar calendar = Calendar.getInstance();
        
        EntityDescription entityDescription=new EntityDescription();
        entityDescription.name=entityName;
        entityDescription.domain=entityDomain;
        entityDescription.creationDate=calendar.getTime();   
        return entityDescription;
    }
    ////////////////////////////////////////////////////////////////////////////
}
