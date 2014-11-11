import java.net.*;
import java.io.*;

class BusArrive{ //change to BusArrival

	String lineName;
	String stopCode;
	String inputLine;

	BusArrive(String busID, String stopID){ //change to BusArrival
		lineName = busID;
		stopCode = stopID;
	}//constructor ends

	void getData() throws Exception{
		URL apiURL = new URL("http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1?LineName=82&ReturnList=StopCode1,EstimatedTime,ExpireTime,Baseversion,RegistrationNumber");

		BufferedReader in = new BufferedReader(new InputStreamReader(apiURL.openStream()));

		while((inputLine = in.readLine()) != null)
		System.out.println(inputLine);

		in.close();
	}

	void parseData(){
		
	}

	void getArrivalTime(){

	}//getArrivalTime() ends

	void getLocation(){

	}//getLocation() ends
	
}//BusArrival ends

public class BusArrival{ //delete these later
	public static void main(String[] args) throws Exception{
		BusArrive test = new BusArrive("82","1234");
		test.getData();
	}
}

