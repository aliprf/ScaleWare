/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paper.pkg1_simulator.regularApp;

import paper.pkg1_simulator.Paper1_simulator;
import paper.pkg1_simulator.XMPPMessage.XMPPMessage;
import paper.pkg1_simulator.regularMiddleware.ServerSideMiddleware;

/**
 *
 * @author amir
 */
public class RegularServer 
{
    private final String DEBUG_TAG="RegularServer ";
    
    public ServerSideMiddleware serverSideMiddleware;
    public String serverDescription_serverName;
    public String serverDescription_domainName;
    
    private int queuePopRate;
    
    public void run()
    {
        try
        {
            queuePopRate=Paper1_simulator.server_queuePopRate;
                    
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
              System.out.println("Exception: ["
                      +DEBUG_TAG+" RegularServer:"+e.getMessage()+"]");
        }
        
    }
    
    /*
     * pop a message from middleware queue
     * find the destination
     * call server-side middleware to send the meessage
     */
    private void receiveMsg()
    {
        try
        {
            
//            System.out.println(DEBUG_TAG+" ======receiveMsg.msg->====== ");
//            System.out.println(DEBUG_TAG+" ======receiveMsg.msg->size:====== "+
//                    String.valueOf(serverSideMiddleware.incommingMiddleware.size()));
            
            XMPPMessage msg=serverSideMiddleware.incommingMiddleware.remove();
            if(msg==null)
            {
//                System.out.println(DEBUG_TAG+" ======receiveMsg.msg-> null====== ");
                return;
            }
            serverSideMiddleware.incomingQueueLength--;
            
//            System.out.println(DEBUG_TAG+" ======receiveMsg.msg====== ");
//            System.out.println(DEBUG_TAG+" receiveMsg.msg.FROM "+ msg.FROM);
//            System.out.println(DEBUG_TAG+" receiveMsg.msg.TO "+ msg.TO);
//            System.out.println(DEBUG_TAG+" receive by: "+
//                    serverDescription_domainName);
//            System.out.println(DEBUG_TAG+" ==================== ");

            //already logged by serverSideMiddleware
    //        Logger.logReceiveMessagebyServer(msg.id,msg.FROM,msg.TO);

            //find destination
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
        catch(Exception e)
        {
//              System.out.println("Exception: ["
//                      +DEBUG_TAG+" receiveMsg:"+String.valueOf(e) +"]");
        }
        
    }
}
