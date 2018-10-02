/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paper.pkg1_simulator.regularMiddleware;

import java.util.LinkedList;
import java.util.Queue;
import paper.pkg1_simulator.Logger;
import paper.pkg1_simulator.NetworkHandler;
import paper.pkg1_simulator.Paper1_simulator;
import paper.pkg1_simulator.XMPPMessage.XMPPMessage;

/**
 *
 * @author amir
 * no memory from yesterday
 * 
 */
public class ServerSideMiddleware 
{
    private final String DEBUG_TAG="ServerSideMiddleware";
    
    
    public int middlewareConfig_incomingQueueLength;
    
    
    public int incomingQueueLength=0;
    
    public ServerSideMiddleware ()
    {
        incommingMiddleware=new LinkedList<>();
        outGoingMiddleware=new LinkedList<>();
        
        middlewareConfig_incomingQueueLength=
                Paper1_simulator.serverSideMiddleware_incomingQueueLength;
    }
    
    public Queue<XMPPMessage>
            incommingMiddleware,
            outGoingMiddleware;
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

               Logger.logDropMessagebyServer(msg.id, msg.FROM,msg.TO);
               
               return Paper1_simulator.PACKET_DROPDED_BY_MIDDLEWARE;
           }
           else
           {
//               System.out.println(DEBUG_TAG+" ======receiveMessage.msg====== ");
//               System.out.println(DEBUG_TAG+" receiveMessage.msg.FROM "+ msg.FROM);
//               System.out.println(DEBUG_TAG+" receiveMessage.msg.TO "+ msg.TO);
//               System.out.println(DEBUG_TAG+" ==================== ");
           
               incomingQueueLength++;
               incommingMiddleware.add(msg);
               
               Logger.logReceiveMessagebyServer(msg.id, msg.FROM,msg.TO);
              
               return Paper1_simulator.PACKET_RECEIVED_BY_MIDDLEWARE;
           }
        }
        catch(Exception e)
        {       
              System.out.println("Exception: ["+DEBUG_TAG+" receiveMessage():"+String.valueOf(e) +"]");
              return Paper1_simulator.PACKET_DROPDED_BY_MIDDLEWARE;
        }
    }
    
    /*
     * get a message and give it to network
     */
    public void sendMessageToServer(XMPPMessage msg)
    {
        try
        {
//            NetworkHandler networkHandler=new NetworkHandler();
            
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
        try
        {
//            NetworkHandler networkHandler=new NetworkHandler();
            
            NetworkHandler.sendServerToClient(msg);
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG+" sendMessageToServer:"+e.getMessage()+"]");
        }
    }
    
}
