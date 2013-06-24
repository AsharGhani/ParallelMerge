import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketThread implements Runnable
{
	String 			m_client;
	ServerSocket 	m_serverSocket;
	Socket 			m_clientSocket;	
	Thread 			m_thread;
	public boolean  m_done;
	
	public SocketThread (ServerSocket socket, String client)
	{
		m_serverSocket = socket;
		m_client = client;
		m_done = false;
		
		m_thread = new Thread(this);
		m_thread.start();
	}
	
	
	@Override
	public void run() 
	{
		BufferedReader in = null;
		try
		{
			m_clientSocket = m_serverSocket.accept();
			in = new BufferedReader(new InputStreamReader(m_clientSocket.getInputStream()));
			String inputLine;
			while((inputLine = in.readLine()) != null)
			{
				System.out.println(inputLine);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		m_done = true;
		try 
		{
			if (in != null)
				in.close();

			m_clientSocket.close();		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
