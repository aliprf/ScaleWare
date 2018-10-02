/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paper.pkg1_simulator.queue;

import java.util.Queue;
import paper.pkg1_simulator.XMPPMessage.XMPPMessage;

/**
 *
 * @author amir
 */
public class Queues 
{
    private final String DEBUG_TAG="Queues";
    
    public Queue<XMPPMessage>
            serverSideIncommingMiddleware,
            serverSideOutGoingMiddleware,
            
            clientSideOutgoingMiddleware;
//    
//    public Queues()
//    {
//        serverSideIncommingMiddleware=new LinkedList<>();
//        serverSideOutGoingMiddleware=new LinkedList<>();
//        clientSideOutgoingMiddleware=new LinkedList<>();
//    }
            
            
}
