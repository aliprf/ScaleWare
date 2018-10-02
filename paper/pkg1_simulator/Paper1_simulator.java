package paper.pkg1_simulator;

/**
 *
 * @author amir
 */
public class Paper1_simulator 
{ 
    
    public static int clientHandler_numberOfClients=5;
    public static int clientHandler_clientEnteranceRate=10;
    
    public static int networkHandler_NETWORK_CAPACITY=5;
    public static int networkHandler_NETWORK_LATENCY=500;
    
    public static int client_messageSentRate=2;
    public static int client_numberOfMessagesToSend=100;
    
    public static int server_queuePopRate=5;
    
    public static int serverSideMiddleware_incomingQueueLength=5;
    
    
    public static int remainingClient;
    //////////////////////////13383 
    
    static Thread serverThread;
    static Thread clientThread;
            
    public static boolean RUNNING=true;
    public static int 
            MIDDLEWARE_MODE=0,
            USING_SCALABLE_MIDDLEWARE_MODE=0,
            USING_REGULAR_MIDDLEWARE_MODE=1;
    
    public static final int 
            PACKET_SENT_BY_NETWORK=1,
            PACKET_DROPDED_BY_NETWORK=0,
            PACKET_DROPDED_BY_MIDDLEWARE=0,
            PACKET_RECEIVED_BY_MIDDLEWARE=0;
     
    public static void main(String[] args) 
    {
        remainingClient=clientHandler_numberOfClients;
                
        final ServerHandle serverHandle=new ServerHandle();
                
        serverThread=new Thread()
        {
            @Override
            public void run() 
            {
                serverHandle.run();
                super.run();
            }
            
        };
        
        serverThread.start();
        
        
        try
        {
            Thread.sleep(100);
        }
        catch(Exception e)
        {
            System.out.println("exception in: runTimerThread");
        }


        
        
        final ClientHandler clientHandler=new ClientHandler();
        
        clientThread=new Thread()
        {
            @Override
            public void run() 
            {
                clientHandler.run();
                super.run();
            }
        };

        clientThread.start();
        
        
        Thread runTimerThread=new Thread()
        {
            @Override
            public void run() 
            {
                try
                {
                    while(remainingClient>0)
                    {
                        Thread.sleep(1000);
//                        System.out.println(String.valueOf(remainingClient));
                    }
                    
                    Thread.sleep(10000);//to be sure that all message will route
                    
                    RUNNING=false;
                    clientThread.stop();
                    serverThread.stop();
                    
                    clientThread=null;
                    serverThread=null;
                    
                    Logger.printResult();
                }
                catch(Exception e)
                {
                    System.out.println("exception in: runTimerThread");
                }
                
                super.run();
            }
        };
        
        runTimerThread.start();
        
    }
}
