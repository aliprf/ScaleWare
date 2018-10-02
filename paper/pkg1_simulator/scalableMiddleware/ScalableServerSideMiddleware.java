package paper.pkg1_simulator.scalableMiddleware;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import paper.pkg1_simulator.Logger;
import paper.pkg1_simulator.NetworkHandler;
import paper.pkg1_simulator.Paper1_simulator;
import paper.pkg1_simulator.XMPPMessage.ServerStatus;
import paper.pkg1_simulator.XMPPMessage.XMPPMessage;


/**
 *
 * @author amir
 */
public class ScalableServerSideMiddleware 
{
    private final String DEBUG_TAG="ScalableServerSideMiddleware";
    
    public int middlewareConfig_incomingQueueLength;
    
    ////////////
    private long startTimeStamp;
    private Calendar calendar;
    ////////////////
    public String domainName="",serverName="";
    ////
    HashMap<String,Long> serverStatusMap;
    HashMap<String,Long> preServerStatusMap;
    
    private long averageSentRate=0;
    private int sentNumber=0;
    
    
    public ScalableServerSideMiddleware ()
    {
        calendar = Calendar.getInstance();
        startTimeStamp=calendar.getTimeInMillis();
        
        incommingMiddleware=new LinkedList<>();
        outGoingMiddleware=new LinkedList<>();
        
        serverStatusMap=new HashMap<>();
        preServerStatusMap=new HashMap<>();
        
        middlewareConfig_incomingQueueLength=
                Paper1_simulator.serverSideMiddleware_incomingQueueLength;        
    }
    

    public int incomingQueueLength=0;
    
    public Queue<XMPPMessage>
            incommingMiddleware,
            outGoingMiddleware;
 
    ///////// we just care about incoming queue
    int pushMessagesNumber=0,//incomming messages which are pushed to incomming queue by middleware
        popMessagesNumber=0;// incomming messages which are poped from incomming queue by server application
    long pushQueueRate=0,popQueueRate=0;
    /////////////////////////////////////////
    
    /*
    * receive a message and add it to 
    * incomming Middleware Queue
    */ 
    public int receiveMessage(XMPPMessage msg)
    {
        try
        {
            
            if(incomingQueueLength>middlewareConfig_incomingQueueLength)
            {
                //               System.out.println(DEBUG_TAG+" receiveMessage: PACKET_DROPDED_BY_MIDDLEWARE");
                //               System.out.println(DEBUG_TAG+
                //                       " incomingQueueLength: "+String.valueOf(incomingQueueLength));
                //               System.out.println(DEBUG_TAG+
                //                       " MiddlewareConfig.incomingQueueLength: "+String.valueOf(middlewareConfig_incomingQueueLength));
                //               System.out.println(DEBUG_TAG+" receiveMessage: =============================");
                ////               
                pushMessagesNumber++;
                
                
                createAck(msg,true);
                
                Logger.logDropMessagebyServer(msg.id, msg.FROM,msg.TO);

                return Paper1_simulator.PACKET_DROPDED_BY_MIDDLEWARE;
            }
            else
            {
                //               System.out.println(DEBUG_TAG+" ======receiveMessage.msg====== ");
                //               System.out.println(DEBUG_TAG+" receiveMessage.msg.FROM "+ msg.FROM);
                //               System.out.println(DEBUG_TAG+" receiveMessage.msg.TO "+ msg.TO);
                //               System.out.println(DEBUG_TAG+" ==================== ");
                
                processMsgHeader(msg);
                
                /////////////////////
                
                pushMessagesNumber++;
                calculatePushQueueRate();
                //////////////
                
                incomingQueueLength++;
                incommingMiddleware.add(msg);

                Logger.logReceiveMessagebyServer(msg.id, msg.FROM,msg.TO);
                
                createAck(msg,false);

                return Paper1_simulator.PACKET_RECEIVED_BY_MIDDLEWARE;
            }
        }
        catch(Exception e)
        {       
              System.out.println("Exception: ["+DEBUG_TAG+" receiveMessage():"+String.valueOf(e) +"]");
              return Paper1_simulator.PACKET_DROPDED_BY_MIDDLEWARE;
        }
    }
    
    private void createAck(XMPPMessage _msg,boolean dropping)
    {
        try
        {  
            //if msg is from our domain (sent by client, not server), sent an Ack, 
            if(_msg.FROM.split("@")[1].equals(domainName))
            {
                XMPPMessage message=new XMPPMessage();
                message.type=XMPPMessage.ACK;
                message.isDroped=true;
                message.TO=_msg.FROM;         
                message.sentTimeStamp=_msg.sentTimeStamp;           
                message.FROM=serverName+"@"+domainName;
                message=prepareMessage(message,dropping);

                NetworkHandler.sendServerToClient(message);
            }
        }
        catch(Exception e)
        {       
              System.out.println("Exception: ["+DEBUG_TAG+" createAck():"
                      +String.valueOf(e) +"]");
              
        }
    }
    
    
    /*
     * get a message and give it to network
     */
    private XMPPMessage prepareMessage(XMPPMessage msg,boolean dropping)
    {
        if(msg.serverStatusList==null)
        {
            msg.serverStatusList=new ArrayList<>();
        }
            
        ServerStatus serverStatus=new ServerStatus();
        serverStatus.serverDomain=domainName;
        if(dropping)
        {
            if(popQueueRate!=0)
            {
                serverStatus.queueInFillingRate=pushQueueRate*incomingQueueLength 
                        /popQueueRate;
                msg.serverStatusList.add(serverStatus);
            }
            else
            {
                serverStatus.queueInFillingRate=pushQueueRate*incomingQueueLength 
                        /Paper1_simulator.server_queuePopRate;
                msg.serverStatusList.add(serverStatus);
            }
        }
        else
        {
            if(popQueueRate!=0)
            {
                serverStatus.queueInFillingRate=pushQueueRate/popQueueRate;
                msg.serverStatusList.add(serverStatus);
            }
            else
            {
                serverStatus.queueInFillingRate=pushQueueRate/
                        Paper1_simulator.server_queuePopRate;
                msg.serverStatusList.add(serverStatus);
            }
        }
        
//        System.out.println(msg.serverStatusList.get(0).queueInFillingRate);    
        return msg;
    }
    public void sendMessageToServer(XMPPMessage msg)
    {
        try
        {           
            sentNumber++;
            
            //prepare message
            msg=prepareMessage(msg,false);
            /////////////////////50,470 143,581=194051
            /////////////////////121,565 78,320=199885 
            ///check if we have any status about the destination server.
            /////if the destination need slow down,
            ///////calculate the delay;
            //////if out going queue wasn't full: the the message to queue
            //////else: remove the message with nearest time to send, and put the current message.
            
            //BUT NOW, as we have only one destination,
            //if the outgoing queue was full, just pop the queue and push the current message
    
            calculateSentRate();
            long serverDelayParameter=calulateServerDelay(msg.TO);//
            if(serverDelayParameter==0)
            {
                serverDelayParameter=2000;                
            }
            else
            {
                serverDelayParameter*=2000;                
            }
            
            //Thread.sleep(serverDelayParameter);
            
           // System.out.println("delayParameter== "+String.valueOf(serverDelayParameter));
            
            ///
            
            Logger.logSendMessageFromServerToServer(msg.id, msg.FROM,msg.TO);
            
            NetworkHandler.sendServerToServer(msg);
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
        try//no delay is needed here
        {
            NetworkHandler.sendServerToClient(msg);
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" sendMessageToServer:"+e.getMessage()+"]");
        }
    }
    
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    
    
    
    
    private void calculatePushQueueRate()
    {
        try
        {
            calendar = Calendar.getInstance();
            long currentTimeStamp=calendar.getTimeInMillis();
            pushQueueRate=1000*pushMessagesNumber/(currentTimeStamp-startTimeStamp);
        }
        catch(Exception e)
        {       
              System.out.println("Exception: ["+DEBUG_TAG
                      +" calculatePushQueueRate():"+String.valueOf(e) +"]");
        }
    }
    
    private void calculatePopQueueRate()
    {
        try
        {
            calendar = Calendar.getInstance();
            long currentTimeStamp=calendar.getTimeInMillis();
            popQueueRate=1000*popMessagesNumber/(currentTimeStamp-startTimeStamp);
        }
        catch(Exception e)
        {       
              System.out.println("Exception: ["+DEBUG_TAG
                      +" calculatePopQueueRate():"+String.valueOf(e) +"]");
        }
    }

    
    
    public XMPPMessage handleMessage()
    {
        popMessagesNumber++;
        calculatePopQueueRate();
        
        XMPPMessage msg=incommingMiddleware.remove();
        incomingQueueLength--;
        return msg;
    }
    
    private void calculateSentRate()
    {
        try
        {
            calendar = Calendar.getInstance();
            long currentTimeStamp=calendar.getTimeInMillis();
            long dif=currentTimeStamp-startTimeStamp;
            averageSentRate=1000*sentNumber/(dif);
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" calculateSentRate:"+e.getMessage()+"]");
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
                    //we should care about all domains
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
            else
            {
                System.out.println("processMsgHeader: serverStatusList is null");
            }
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" calculateSentRate:"+e.getMessage()+"]");
        }
    }
    
    private long calulateServerDelay(String to)
    {
        try
        {
            String domainName=to.split("@")[1];
            if(serverStatusMap.containsKey(domainName) 
                    && preServerStatusMap.containsKey(domainName))
                //both should be present to calculate the delay
            {
                if(preServerStatusMap.get(domainName)==0)
                {
                    return 1;
                }
                
                
                long serverQueueStatus=serverStatusMap.get(domainName)
                        /preServerStatusMap.get(domainName);
                //the more the 'serverQueueStatus', the more should be the delay:
                
                if(serverQueueStatus==0)
                {
                    return averageSentRate*serverQueueStatus;
                }
                return averageSentRate;
            }
            
            return 1;
        }
        catch(Exception e)
        {
              //System.out.println("Exception: ["+DEBUG_TAG+" calulateServerDelay:"+String.valueOf(e) +"]");
              return 0;
        }
    }
   
}
