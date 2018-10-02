
package sacalableApp;

import paper.pkg1_simulator.Paper1_simulator;
import paper.pkg1_simulator.XMPPMessage.XMPPMessage;
import paper.pkg1_simulator.scalableMiddleware.ScalableServerSideMiddleware;

/**
 *
 * @author amir
 */
public class ScalableServer {
    private final String DEBUG_TAG="ScalableServer ";
    
    public ScalableServerSideMiddleware serverSideMiddleware;
    public String serverDescription_serverName;
    public String serverDescription_domainName;
    
    private int queuePopRate;
    
    public void run()
    {
        try
        {
            queuePopRate=Paper1_simulator.server_queuePopRate;
            
            this.serverSideMiddleware.domainName=serverDescription_domainName;
            
            while(Paper1_simulator.RUNNING)
            {
                 Thread.sleep(1000/queuePopRate);
    
                if(!this.serverSideMiddleware.incommingMiddleware.isEmpty())
                {
                    receiveMsg();  
                }
            }
        }
        catch(Exception e)
        {
              System.out.println("Exception: ["+DEBUG_TAG
                      +" ScalableServer:"+String.valueOf(e) +"]");
        }
        
    }
    
    /*
     * pop a message from middleware queue
     * find the destination
     * call server-side middleware to send the meessage
     */
    private void receiveMsg()
    {
        XMPPMessage msg=this.serverSideMiddleware.handleMessage();
        
//        System.out.println(DEBUG_TAG+" ======receiveMsg.msg====== ");
//        System.out.println(DEBUG_TAG+" receiveMsg.msg.FROM "+ msg.FROM);
//        System.out.println(DEBUG_TAG+" receiveMsg.msg.TO "+ msg.TO);
//        System.out.println(DEBUG_TAG+" receive by: "+
//                serverDescription_domainName);
//        System.out.println(DEBUG_TAG+" ==================== ");

        //already logged by serverSideMiddleware
//        Logger.logReceiveMessagebyServer(msg.id,msg.FROM,msg.TO);
        
        //find destination
        
        serverSideMiddleware.incomingQueueLength--;
        
        if(msg.TO.contains(serverDescription_domainName))
        {
            if(msg.TO.contains(serverDescription_serverName))
            {
                //the message is delivered to server!
            }
            else
            {
                //the message should be sent to client !
                this.serverSideMiddleware.sendMessageToClient(msg);
            }
        }
        else// the message should be sent to another domain
        {
            this.serverSideMiddleware.sendMessageToServer(msg);
        }
    } 
}
