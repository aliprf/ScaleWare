package paper.pkg1_simulator.regularApp;

import paper.pkg1_simulator.ClientHandler;
import paper.pkg1_simulator.Logger;
import paper.pkg1_simulator.Paper1_simulator;
import paper.pkg1_simulator.ServerHandle;
import paper.pkg1_simulator.XMPPMessage.XMPPMessage;
import paper.pkg1_simulator.regularMiddleware.ClientSideMiddleware;

/**
 *
 * @author amir
 */
public class RegularClient 
{

    private final String DEBUG_TAG="RegularClient ";
    
    public ClientSideMiddleware clientSideMiddleware;
    
    public String ClientDescription_clientName;
    public String ClientDescription_domainName;
    public int clientID;
    
    private int numberOfMessagesToSend;
    private int messageSentRate;
    
    /*
     * based on the messageSentRate, 
     * sends messages to clients from other domains
     */
    int messageNumber=0;
    public void run()
    {
        try
        {
//            System.out.println(DEBUG_TAG+" run");
            numberOfMessagesToSend=Paper1_simulator.client_numberOfMessagesToSend;
            messageSentRate=Paper1_simulator.client_messageSentRate;
            
            while(Paper1_simulator.RUNNING && messageNumber<numberOfMessagesToSend)
            {
                
                send(createMsg());
                Thread.sleep(1000/messageSentRate);
                messageNumber++;
            }
            System.out.println(DEBUG_TAG+ "   FINISHED ");
            
            Paper1_simulator.remainingClient--;
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" run:"+e.getMessage()+"]");
        }
    }
    
    private XMPPMessage createMsg()
    {
        try
        {
//            System.out.println(DEBUG_TAG+" createMsg");
            
            XMPPMessage msg=new XMPPMessage();
            
            if(clientID+1==ClientHandler.numberOfClients)
            {
                if(clientID % 2!=0)
                { 
                    msg.FROM=clientID+"@"+ServerHandle.regularServer1.serverDescription_domainName;
                    msg.TO=clientID-1+"@"+ServerHandle.regularServer2.serverDescription_domainName;
                }
                else
                {
                    msg.FROM=clientID+"@"+ServerHandle.regularServer2.serverDescription_domainName;
                    msg.TO=clientID-1+"@"+ServerHandle.regularServer1.serverDescription_domainName;
                }
            }
            else
            {
                if(clientID % 2!=0)
                { 
                    msg.FROM=clientID+"@"+ServerHandle.regularServer1.serverDescription_domainName;
                    msg.TO=clientID+1+"@"+ServerHandle.regularServer2.serverDescription_domainName;
                }
                else
                {
                    msg.FROM=clientID+"@"+ServerHandle.regularServer2.serverDescription_domainName;
                    msg.TO=clientID+1+"@"+ServerHandle.regularServer1.serverDescription_domainName;
                }
            }
            
            msg.id=String.valueOf(messageNumber+"_"+msg.FROM);
            
            return  msg;
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" createMsg:"+e.getMessage()+"]");
              return null;
        }
    }
    
    public void send(XMPPMessage msg)
    {
        try
        {
//            System.out.println(DEBUG_TAG+" ======client.send.msg====== ");
//            System.out.println(DEBUG_TAG+" send.msg.FROM "+ msg.FROM);
//            System.out.println(DEBUG_TAG+" send.msg.TO "+ msg.TO);
//            System.out.println(DEBUG_TAG+" ==================== ");
  
            Logger.logSendMessageFromClient(msg.id, msg.FROM,msg.TO);
            
            this.clientSideMiddleware.sendFromClientToDomain(msg);
            
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" send:"+e.getMessage()+"]");
        }
    }
    
    public void receive(XMPPMessage msg)
    {
        try
        {
            Logger.logReceiveMessagebyClient(msg.id, msg.FROM,msg.TO);
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" receive:"+e.getMessage()+"]");
        }
    }
}
