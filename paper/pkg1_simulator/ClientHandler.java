package paper.pkg1_simulator;

import java.util.ArrayList;
import paper.pkg1_simulator.regularApp.RegularClient;
import paper.pkg1_simulator.regularMiddleware.ClientSideMiddleware;
import paper.pkg1_simulator.scalableMiddleware.ScalableClientSideMiddleware;
import sacalableApp.ScalableClient;

/**
 *
 * @author amir
 * 
 * this class is responsible to manage client
 */
public class ClientHandler 
{
    private final String DEBUG_TAG="ClientHandler";
    
    private final int UNLIMITED=-1;
    
    
    public static int numberOfClients;
    
    private class ClientConfig
    {
        public int numberOfClients;//
        
        public int clientEnteranceRate;//per second
    }
    
    public static ArrayList<RegularClient> clientList;
    public static ArrayList<ScalableClient> scalableClientList;
    
    public void run()
    {
        //
        try
        {                    
            ClientConfig clientConfig=new ClientConfig();         
          
            clientConfig.numberOfClients=
                    Paper1_simulator.clientHandler_numberOfClients;
            clientConfig.clientEnteranceRate=
                    Paper1_simulator.clientHandler_clientEnteranceRate;
            //
            numberOfClients=clientConfig.numberOfClients;
            
            clientList=new ArrayList<>();
            scalableClientList=new ArrayList<>();
            
        
            int clientNum=0;

            while(Paper1_simulator.RUNNING)
            {
                if(clientConfig.numberOfClients==UNLIMITED ||
                        clientConfig.numberOfClients>clientNum)
                {
                    createClient(clientNum,String.valueOf(clientNum),setClientDomain(clientNum));
                    clientNum++;
                    Thread.sleep(1000/clientConfig.clientEnteranceRate);
                }
            }
        }
        catch(Exception e)
        {
             System.out.println("Exception: ["+DEBUG_TAG+" run():"+e.getMessage()+"]");
             Paper1_simulator.RUNNING=false;
        }
    }
   
    private String setClientDomain(int clientNumber)//distribute client in domains by round-robin
    {
        try
        {
//            System.out.println(DEBUG_TAG+" setClientDomain: clientNumber"+String.valueOf(clientNumber));
            
            if(Paper1_simulator.MIDDLEWARE_MODE==
                    Paper1_simulator.USING_REGULAR_MIDDLEWARE_MODE)
            {
                if(clientNumber % 2!=0)
                {
                    return ServerHandle.regularServer1.serverDescription_domainName;
                }
                else
                {
                    return ServerHandle.regularServer2.serverDescription_domainName;
                }
            }
            else
            {
                if(clientNumber % 2!=0)
                {
                    return ServerHandle.scalableServer1.serverDescription_domainName;
                }
                else
                {
                    return ServerHandle.scalableServer2.serverDescription_domainName;
                }
            }
            
        }
        catch(Exception e)
        {
             System.out.println("Exception: ["+DEBUG_TAG+" setClientDomain():"
                     +String.valueOf(e) +"]");
             
             Paper1_simulator.RUNNING=false;
             return null;
        }
    }
    
    private void createClient(int clientID,String clientName,String clientDomain)
    {
        try
        {   
            if(Paper1_simulator.MIDDLEWARE_MODE==
                    Paper1_simulator.USING_REGULAR_MIDDLEWARE_MODE)
            {
                RegularClient regularClient=new RegularClient();
                regularClient.clientSideMiddleware=new ClientSideMiddleware();
                regularClient.ClientDescription_domainName=clientDomain;
                regularClient.ClientDescription_clientName=clientName;
                regularClient.clientID=clientID;
                
                clientList.add(regularClient);
                
                Logger.logClientCreation(clientName, clientDomain); 
                
                start(regularClient);
            }
            else// create scalable clients
            {
//                 System.out.println(DEBUG_TAG+" create client ");
                 
                ScalableClient scalableClient=new ScalableClient();
                
                scalableClient.scalableClientSideMiddleware=
                        new ScalableClientSideMiddleware();
                scalableClient.ClientDescription_domainName=clientDomain;
                scalableClient.ClientDescription_clientName=clientName;
                scalableClient.clientID=clientID;
                
                scalableClientList.add(scalableClient);
                
                Logger.logClientCreation(clientName, clientDomain); 
                
                start(scalableClient);
            }
            
        }
        catch(Exception e)
        {
             System.out.println("Exception: ["+DEBUG_TAG+" createClient():"+e.getMessage()+"]");
             Paper1_simulator.RUNNING=false;
        }
    }
    
    
    private void start(final ScalableClient client)
    {   
        Thread processThread=new Thread()
        {
            @Override
            public void run() 
            {
                try
                {
                    client.run();
                }
                catch(Exception e)
                {
                    System.out.println(DEBUG_TAG+"receive->run->catch: "+e.getMessage());
                    Paper1_simulator.RUNNING=false;
                }
            }
        };
        
        processThread.start();
    }
    
    private void start(final RegularClient client)
    {
        Thread processThread=new Thread()
        {
            @Override
            public void run() 
            {
                try
                {
                    client.run();
                }
                catch(Exception e)
                {
                    System.out.println(DEBUG_TAG+"receive->run->catch: "+e.getMessage());
                    Paper1_simulator.RUNNING=false;
                }
            }
        };
        processThread.start();
    }    
}
