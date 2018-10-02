/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paper.pkg1_simulator.XMPPMessage;

import java.util.ArrayList;

/**
 *
 * @author amir
 */
public class XMPPMessage 
{
    public String id;//msgNumber_clientId@domainName :: msgNumber_from
    public String FROM;
    public String TO;
    public int type;
    public boolean isDroped=false;
    public long sentTimeStamp;
    
    public static int ACK=0,MESSAGE=1;
    
    public ArrayList<ServerStatus> serverStatusList;
    
}

