package paper.pkg1_simulator.scalableMiddleware;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import paper.pkg1_simulator.Logger;
import paper.pkg1_simulator.NetworkHandler;
import paper.pkg1_simulator.Paper1_simulator;
import paper.pkg1_simulator.XMPPMessage.XMPPMessage;

/**
 *
 * @author amir
 */
public class ScalableClientSideMiddleware 
{    
    private final String DEBUG_TAG="ScalableClientSideMiddleware";

    private long delayParameter=0;
    private long networkDelayParameter=0;
    
    private long averageSentRate=0;
    private int sentNumber=0;
    
    private long startTimeStamp;
    private Calendar calendar;
    
    HashMap<String,Long> serverStatusMap;
    HashMap<String,Long> preServerStatusMap;
        
    public ScalableClientSideMiddleware()
    {
        calendar = Calendar.getInstance();
        startTimeStamp=calendar.getTimeInMillis();
        
        serverStatusMap=new HashMap<>();
        preServerStatusMap=new HashMap<>();
    }
   
    
    /*
     * receive a message and add it to 
     * incomming Middleware Queue
     */
    
    public int receiveMessage(XMPPMessage msg)
    {
        try
        {   
            if(msg.type==XMPPMessage.ACK)
            {        
                if(msg.isDroped)
                {
                    calculateNetworkLatency(msg.sentTimeStamp,true);
                }              
                else
                {
                    calculateNetworkLatency(msg.sentTimeStamp,false);
                }
            }
            else
            {             
                Logger.logReceiveMessagebyClient(msg.id, msg.FROM,msg.TO);
            }
            
            processMsgHeader(msg);
            
           return Paper1_simulator.PACKET_RECEIVED_BY_MIDDLEWARE;
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" receiveMessage():"+e.getMessage()+"]");
              return Paper1_simulator.PACKET_DROPDED_BY_MIDDLEWARE;
        }
    }
    
    /*
     * get a message and give it to its domain
     */  
    public void sendFromClientToDomain(XMPPMessage msg)
    {
        try
        {  
            sentNumber++;
            
            calculateSentRate();
            
            calculateDelayParameter();
            
            long serverDelayParameter=1000*calulateServerDelay(msg.FROM);
            
//            if(serverDelayParameter!=0)
//            {
//                System.out.println("serverDelayParameter!=0"+String.valueOf(serverDelayParameter));
//            }
//            if(delayParameter!=0)
//            {
//                System.out.println("delayParameter!=0 "+String.valueOf(delayParameter));
//            }
            
            Thread.sleep(serverDelayParameter+delayParameter);
                    //ThreadLocalRandom.current().
//                    nextInt(0, (int)(serverDelayParameter+delayParameter )+ 1));
//            
            //add sent timestamp to msg
            calendar = Calendar.getInstance();
            msg.sentTimeStamp=calendar.getTimeInMillis();
            
            /////init serverStausList
            msg.serverStatusList=new ArrayList<>();
            msg.type=XMPPMessage.MESSAGE;
            ////////////////////
            
            NetworkHandler.sendFromClientToDomain(msg,Paper1_simulator.USING_SCALABLE_MIDDLEWARE_MODE);
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" sendMessageToServer:"+e.getMessage()+"]");
        }
    }
     
    /*
     * get a message and give it to network
     */
    public void sendMessageToClient(XMPPMessage msg)
    {
        try
        {            
            NetworkHandler.sendServerToClient(msg);
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" sendMessageToServer:"+e.getMessage()+"]");
        }
    }
    ////////////////////////////
    private void calculateDelayParameter()
    {
        try
        {    
            if(priNetworkLatency==0)
            {
//                delayParameter=0; 
                delayParameter = 10*averageSentRate;
                if(delayParameter<=100)
                {
                    delayParameter*=700;
                }
            }
            else
            {
                delayParameter = 1000*averageSentRate *(currentNetworkLatency /priNetworkLatency);
                
            }
            
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" calculateDelayParameter:"+e.getMessage()+"]");
        }
    }
    
    private void calculateSentRate()
    {
        try
        {
            calendar = Calendar.getInstance();
            long currentTimeStamp=calendar.getTimeInMillis();
            
//            System.out.println("startTimeStamp: "+String.valueOf(startTimeStamp));
//            System.out.println("currentTimeStamp: "+String.valueOf(currentTimeStamp));
            
            long dif=currentTimeStamp-startTimeStamp;
            if(dif==0)
            {
                averageSentRate=0;
            }
            else
            {
                averageSentRate=1000*sentNumber/dif;
            }
            
            
            
        }
        catch(Exception e)
        {
            System.out.println("Exception: ["+DEBUG_TAG+
                    " calculateSentRate:"+String.valueOf(e)+"]");
        }
    }
    
    long currentNetworkLatency=0;
    long priNetworkLatency=0;
    
    private void calculateNetworkLatency(long sentTimeStamp,boolean isDropped)
    {
        try
        {            
            priNetworkLatency=currentNetworkLatency;
            
            calendar = Calendar.getInstance();
            long currentTimeStamp=calendar.getTimeInMillis();
            
            if(isDropped)
            {
                currentNetworkLatency=NetworkHandler.WirelessNetworkConfig.NETWORK_LATENCY*
                    currentTimeStamp-sentTimeStamp;
            }
            else
            {
                currentNetworkLatency=currentTimeStamp-sentTimeStamp;
            }
            
            
//            System.out.println("calculateNetworkLatency:"
//                    +String.valueOf(currentNetworkLatency));
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" calculateNetworkLatency:"+e.getMessage()+"]");
        }
    }
    
    private long calulateServerDelay(String from)
    {
        try
        {
            String domainName=from.split("@")[1];
              
            if(serverStatusMap.containsKey(domainName) 
                    && preServerStatusMap.containsKey(domainName))
                //both should be present to calculate the delay
            {
                long serverQueueStatus=serverStatusMap.get(domainName)/preServerStatusMap.get(domainName);
                //the more the 'serverQueueStatus', the more should be the delay:
                
//                 System.out.println("calulateServerDelay:"
//                    +String.valueOf(averageSentRate*serverQueueStatus));
                
                return averageSentRate*serverQueueStatus;
            }
            
            return 0;
        }
        catch(Exception e)
        {
            
            //System.out.println("Exception: ["+DEBUG_TAG+" calulateServerDelay:"+String.valueOf(e) +"]");
            return 0;
        }
    }
    
    private void processMsgHeader(XMPPMessage msg)
    {
        try
        {
            //each entity care about its next server
            
            if(msg.serverStatusList!=null)
            {
                for(int i=0;i<msg.serverStatusList.size();i++)
                {
                    //finding servers in common domain
                    if(msg.serverStatusList.get(i).
                            serverDomain.equals(msg.FROM.split("@")[1]))
                    {
                        if(serverStatusMap.containsKey(msg.serverStatusList.get(i).serverDomain))
                        {
                            //change current with pre
                            preServerStatusMap.remove(msg.serverStatusList.get(i).serverDomain);
                            
                            preServerStatusMap.put(msg.serverStatusList.get(i).serverDomain
                                    , serverStatusMap.get(msg.serverStatusList.get(i).serverDomain));
                            
                            //remove current
                            serverStatusMap.remove(msg.serverStatusList.get(i).serverDomain);
    
                            //add current
                            serverStatusMap.put(
                                msg.serverStatusList.get(i).serverDomain,
                                msg.serverStatusList.get(i).queueInFillingRate);
                        }
                        else if(preServerStatusMap.containsKey(
                                msg.serverStatusList.get(i).serverDomain))
                            //second time: preMap hash object but, current is free
                        {
                            //add current
                            serverStatusMap.put(
                                msg.serverStatusList.get(i).serverDomain,
                                msg.serverStatusList.get(i).queueInFillingRate);
                        }
                        else// first time: both of them are free;
                        {
                            preServerStatusMap.put(msg.serverStatusList.get(i).serverDomain
                                    , serverStatusMap.get(msg.serverStatusList.get(i).serverDomain));
                        }
                    }
                }
            }
            else
            {
               // System.out.println("processMsgHeader: serverStatusList is null");
            }
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" processMsgHeader:"+String.valueOf(e) +"]");
        }
    }
}
