import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;

public class BusArrival{

	String lineName;
	String stopCode;
	String mainURL = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1";
	String returnListURL = "&ReturnList=StopCode1,EstimatedTime,ExpireTime,RegistrationNumber";
	String serverOutput = new String();
	String[] arrivalTime;

	public BusArrival(String busID, String stopID){
		lineName = busID;
		stopCode = stopID;
	}//constructor ends

	String getData(){
		
		try {
			URL apiURL = new URL(mainURL+"?LineName="+lineName+"&stopCode1="+stopCode+returnListURL);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(apiURL.openStream()));
			
			String inputLine;
			while((inputLine = in.readLine()) != null)
				serverOutput=serverOutput+inputLine;
			in.close();
		}//try ends
		
		catch (MalformedURLException e) {
			e.printStackTrace();
			printError("Could not access TFL API.. Try again later..");
		}//catch URLException ends 
		catch (IOException e) {
			e.printStackTrace();
			printError("Could not read data from TFL API..");
		}//catch IOException ends
		
		return serverOutput;
		
	}//getData() ends
	
	void printError(String error){ //Later change to accommodate error printing in android
		
		System.out.println(error);
		
	}//printError() ends
	
	//Parse data and get epoch arrival timing parameter
	String[] parseData(String input){
		
		String delims1 = "[\\[ \\]]+";
		String delims2 = "[, \"]+";
		String[] dataArray = input.split(delims1); //index zero is empty
		String[] records = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		//Split every record and add to the ArrayList
		for(int i=0;i<dataArray.length;i++){
			records = dataArray[i].split(delims2);
			if(records.length>3)
				list.add(records);
		}//for ends
		
		arrivalTime = new String[list.size()];
        
		//Get arrival epoch time (array column number 3) for each record
		for(int i=0;i<list.size();i++)
			arrivalTime[i]=list.get(i)[3];
					
		return arrivalTime;
		
	}//parseData() ends
	
	//Calculate epoch arrival timing to date format, to display remaining time later compare to android sntp client
	Date epochToDate(String time){
		
		Date date = new Date(Long.parseLong(time));
		return date;
		
	}//epochToRemaining(String time) ends
	
	//Get all arrival time available for certain bus and stop
	ArrayList<Date> getArrival(){
		
		ArrayList<Date> dateList = new ArrayList<Date>();
		String input = getData();
		String[] times = parseData(input);
		
		for(int i=0;i<times.length;i++)
			dateList.add(epochToDate(times[i]));
		
		return dateList;
		
	}//getArrival() ends
		
}//BusArrival ends
