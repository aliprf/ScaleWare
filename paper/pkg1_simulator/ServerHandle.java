package paper.pkg1_simulator;

import paper.pkg1_simulator.regularApp.RegularServer;
import paper.pkg1_simulator.regularMiddleware.ServerSideMiddleware;
import paper.pkg1_simulator.scalableMiddleware.ScalableServerSideMiddleware;
import sacalableApp.ScalableServer;

/**
 *
 * @author amir
 */

public class ServerHandle 
{
    private final String DEBUG_TAG="ServerHandle";
    
    public static RegularServer regularServer1,regularServer2;
    public static ScalableServer scalableServer1,scalableServer2;
    
    public void run()
    {
        try
        {
             
            if(Paper1_simulator.MIDDLEWARE_MODE==
                    Paper1_simulator.USING_REGULAR_MIDDLEWARE_MODE)
            {
                regularServer1=new RegularServer();
                regularServer1.serverSideMiddleware=new ServerSideMiddleware();
                regularServer1.serverDescription_serverName="server1";
                regularServer1.serverDescription_domainName="domain1";
                        
                regularServer2=new RegularServer();
                regularServer2.serverSideMiddleware=new ServerSideMiddleware();
                regularServer2.serverDescription_serverName="server2";
                regularServer2.serverDescription_domainName="domain2";
                
                
                Logger.logServerCreation(
                        regularServer1.serverDescription_serverName
                        , regularServer1.serverDescription_domainName);
                
                Logger.logServerCreation(
                        regularServer2.serverDescription_serverName
                        , regularServer2.serverDescription_domainName);
                
                Thread server1runThread=new Thread()
                {
                    @Override
                    public void run() 
                    {
                        regularServer1.run();
                        super.run();
                    }
                    
                };
                
                Thread server2runThread=new Thread()
                {
                    @Override
                    public void run() 
                    {
                        regularServer2.run();
                        super.run();
                    }
                    
                };
                
                server1runThread.start();
                server2runThread.start();   
            }
            else
            {
                scalableServer1=new ScalableServer();
                scalableServer1.serverSideMiddleware=
                        new ScalableServerSideMiddleware();
                scalableServer1.serverDescription_serverName="server1";
                scalableServer1.serverDescription_domainName="domain1";
                
                scalableServer1.serverSideMiddleware.domainName=
                        scalableServer1.serverDescription_domainName;        
                scalableServer1.serverSideMiddleware.serverName=
                        scalableServer1.serverDescription_serverName;
                
                
                scalableServer2=new ScalableServer();
                scalableServer2.serverSideMiddleware=
                        new ScalableServerSideMiddleware();
                scalableServer2.serverDescription_serverName="server2";
                scalableServer2.serverDescription_domainName="domain2";
                
                scalableServer2.serverSideMiddleware.domainName=
                        scalableServer1.serverDescription_domainName;        
                scalableServer2.serverSideMiddleware.serverName=
                        scalableServer1.serverDescription_serverName;
                
                
                Logger.logServerCreation(
                        scalableServer1.serverDescription_serverName
                        , scalableServer1.serverDescription_domainName);
                
                Logger.logServerCreation(
                        scalableServer2.serverDescription_serverName
                        , scalableServer2.serverDescription_domainName);
                
                Thread server1runThread=new Thread()
                {
                    @Override
                    public void run() 
                    {
                        scalableServer1.run();
                        super.run();
                    }
                    
                };
                
                Thread server2runThread=new Thread()
                {
                    @Override
                    public void run() 
                    {
                        scalableServer2.run();
                        super.run();
                    }
                    
                };
                
                server1runThread.start();
                server2runThread.start();   
                
                System.out.println("server created!");
            }
        }
        catch(Exception e)
        {
             System.out.println("Exception: ["+DEBUG_TAG+" run():"+e.getMessage()+"]");
             Paper1_simulator.RUNNING=false;
        }
    }
    
}
