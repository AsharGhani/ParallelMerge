import gitHelper.utils.RunIt;
import gitHelper.utils.Constants.Relationship;
import gitHelper.utils.RunIt.Output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class MergeController 
{
	private static String s_usage = "MergeController fileNameForClientIPAddresses User1/Repository1[,User2/Repository2,User3/Repository3...]" ;
	private static List<String> s_clientNames = new ArrayList<String>();
	private static List<String> s_userRepositories = new ArrayList<String>();
	private static Hashtable<String,List<String>> s_mapClientsToRepositories = new Hashtable<String,List<String>>();
	
	private static int s_socketPort = 8765;
	private static ServerSocket s_serverSocket;
	
	private static List<SocketThread> s_socketThreads = new ArrayList<SocketThread>();
	
	static void printParsingError (String invalidArgument)
	{
		System.out.println ("Error! Invalid Argument: " + (null != invalidArgument?invalidArgument:"") + "\n\nUsage:\n\n" + s_usage);
	}
	
	
	static void Initialize()
	{
		s_clientNames.clear();
		s_userRepositories.clear();
		s_mapClientsToRepositories.clear();
	}
	
	
	static boolean parseInputArgs (String args[])
	{
		Initialize();
		
		if (args.length != 2)
		{
			printParsingError(null);
			return false;
		}
		
		if (!populateClientsList(args[0]))
			return false;
		
		
		// parse list of UserName/Repository combinations
		String[] splitsArray = args[1].split(",");
		for (String split : splitsArray)
		{
			// confirm that each split is in the form of "userName/repository"
			String[] userAndRepo = split.split("/");
			if (userAndRepo.length != 2)
				printParsingError(args[1]);
			
			split = split.trim();
			s_userRepositories.add(split);
		}
		
		return true;
	}
	
	
	static boolean populateClientsList (String inputFileName)
	{
		File clientFile = new File (inputFileName);
		if (!clientFile.exists())
			return false;
		
		BufferedReader br;
		try 
		{
			br = new BufferedReader (new FileReader (clientFile));
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		String line;
		try {
			while((line = br.readLine()) != null)
			{
				line = line.trim();
				if (line.isEmpty())
					continue;
				
				s_clientNames.add (line);
				s_mapClientsToRepositories.put(line, new ArrayList<String>());
			}
		}
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
		try 
		{
			br.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		if (s_clientNames.size() > 0)
			return true;
		
		return false;
	}
	
	
	static String assignRepositoryToClient (String userRepo)
	{
		String clientWithMinimumRepos = null;
		int minimumReposNumber = Integer.MAX_VALUE;
		
		for (String currentClient : s_mapClientsToRepositories.keySet())
		{
			List<String> reposList = s_mapClientsToRepositories.get (currentClient);
			if (reposList.size() < minimumReposNumber)
			{
				clientWithMinimumRepos = currentClient;
				minimumReposNumber = reposList.size();
			}
			
			for (String currentUserRepo : reposList)
			{
				if (userRepo == currentUserRepo)
					return currentClient;
			}
		}
		
		if (null == clientWithMinimumRepos)
			return null;
		
		List<String> reposList = s_mapClientsToRepositories.get (clientWithMinimumRepos);
		
		reposList.add(userRepo);
		
		return clientWithMinimumRepos;
	}
	
	
	static boolean startServerSocket()
	{
		try 
		{
			s_serverSocket = new ServerSocket(s_socketPort);
		} 
		catch (IOException e) 
		{
			System.out.println("Could not listen on port: " + s_socketPort);
			e.printStackTrace();
			return false;
		}
		
		//Create separate threads for listening to inputs from each client 
		for (String client : s_clientNames)
		{
			s_socketThreads.add (new SocketThread(s_serverSocket, client));
		}
		
		return true;
	}
	
	
	static boolean stopServerSocket() throws InterruptedException
	{
		// TODO: wait for all threads to end
		while(!s_socketThreads.get(1).m_done)
		{
			System.out.println("Waiting for thread to finish execution");
			Thread.sleep(5000);
		}
		
		try
		{
			s_serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	static boolean startClientMerge(String client, String repo)
	{
		// Parse the repo to get the userName and repository combination
		String[] splits = repo.split("/");
		if (splits.length != 2)
			return false;
		
		String executablePath = RunIt.getExecutable("ssh");
		
		String userName = splits[0];
		String repositoryName = splits[1];
		String controllerAddress = null;
		try 
		{
			controllerAddress = InetAddress.getLocalHost().toString();
		} 
		catch (UnknownHostException e1) 
		{
			e1.printStackTrace();
			return false;
		}
		
		Output output;
		try {
			String[] myArgs = { client,
								"\'bash -s\'",
								"<",
								"scripts/RunMergeClient.sh", 
								userName, 
								repositoryName,   
								controllerAddress, 
								Integer.toString(s_socketPort)
								};
			
			output = RunIt.execute(executablePath, myArgs, System.getProperty("user.dir"), false);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	public static void main (String[] args) throws InterruptedException
	{				
		if (!parseInputArgs(args))
			return;

		// start serverSocket
		if (!startServerSocket())
			return;
		
		for (String currentUserRepo : s_userRepositories)
		{
			String currentClient = assignRepositoryToClient(currentUserRepo);
			if (null == currentClient)
				continue;
			
			if (!startClientMerge(currentClient, currentUserRepo))
				continue;
		}
		
		// start serverSocket
		stopServerSocket();
	}
}
