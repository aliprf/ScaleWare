/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paper.pkg1_simulator.regularMiddleware;

import paper.pkg1_simulator.Logger;
import paper.pkg1_simulator.NetworkHandler;
import paper.pkg1_simulator.Paper1_simulator;
import paper.pkg1_simulator.XMPPMessage.XMPPMessage;

/**
 *
 * @author amir
 */
public class ClientSideMiddleware 
{    
    private final String DEBUG_TAG="ClientSideMiddleware";

    /*
     * receive a message and add it to 
     * incomming Middleware Queue
     */
    public int receiveMessage(XMPPMessage msg)
    {
        try
        {
//         System.out.println(DEBUG_TAG+" client mid receiveMessage");
            
            Logger.logReceiveMessagebyClient(msg.id, msg.FROM,msg.TO);
            
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
            NetworkHandler.sendFromClientToDomain(msg,Paper1_simulator.USING_REGULAR_MIDDLEWARE_MODE);
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
