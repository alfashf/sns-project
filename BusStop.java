import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class BusStop{

	private double lat;
	private double lon;
	private int rad;
	private String mainURL = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1";
	private String returnListURL = "&ReturnList=StopCode1,Bearing,StopPointIndicator,StopPointType,Latitude,Longitude";
	private String[] stopIndicator;
	private String[] stopCode;
	private String[] latArr;
	private String[] lonArr;

	public BusStop(double latitude, double longitude, int radius){
		lat = latitude;
		lon = longitude;
		rad = radius;
	}//constructor ends

	private String getData(){
		
		String serverOutput = null;
		
		try {
			URL apiURL = new URL(mainURL+"?Circle="+lat+","+lon+","+rad+"&stopPointState=0"+returnListURL);
			
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
	
	private void printError(String error){ //Later change to accommodate error printing in android
		
		System.out.println(error);
		
	}//printError() ends
	
	//Parse data and get bus stop parameters
	private String[] parseData(String input, String datatype){
		
		String delims1 = "[\\[\\]]+";
		String delims2 = "[,]+";
		String[] dataArray = input.split(delims1); //index zero is empty ngac0
		String[] records = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try{
			//Split every record and add to the ArrayList
			for(int i=0;i<dataArray.length;i++){
				records = dataArray[i].split(delims2);
				if((records[0].equals("0")) && (!records[1].equals("null"))) //Get only stop data & eliminating imaginary bus stops
					list.add(records);
			}//for ends
			
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: unexpected API response..");
		}//catch ends
		
		stopIndicator = new String[list.size()];
		stopCode = new String[list.size()];
		latArr = new String[list.size()];
		lonArr = new String[list.size()];
        
		try{
		//Get all appropriate records from list
			for(int i=0;i<list.size();i++){
				stopCode[i]=list.get(i)[1];
				stopIndicator[i]=list.get(i)[4];
				latArr[i]=list.get(i)[5];
				lonArr[i]=list.get(i)[6];	
			}//for ends
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: array index out of bound, unexpected API response..");
		}//catch ends
		
		if(datatype.equals("stopIndicator"))
			return stopIndicator;
		else if(datatype.equals("stopCode"))
			return stopCode;
		else if(datatype.equals("latArr"))
			return latArr;
		else
			return lonArr;
						
	}//parseData() ends
	
	
	//Get all arrival time available for certain bus and stop
	public ArrayList<String> getStopIndicatorList(){
		
		ArrayList<String> stopIndicatorList = new ArrayList<String>();
		String input = getData();
		String[] stopIndicator = parseData(input, "stopIndicator");
		
		try{
		for(int i=0;i<stopIndicator.length;i++)
			stopIndicatorList.add(stopIndicator[i].replaceAll("\"", ""));
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: array index out of bound..");
		}//catch ends
		
		return stopIndicatorList;
		
	}//getStopIndicatorList() ends
	
	public ArrayList<String> getStopCodeList(){
		
		ArrayList<String> stopCodeList = new ArrayList<String>();
		String input = getData();
		String[] stopCode = parseData(input, "stopCode");
		
		try{
		for(int i=0;i<stopCode.length;i++)
			stopCodeList.add(stopCode[i].replaceAll("\"", ""));
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: array index out of bound..");
		}//catch ends
		
		return stopCodeList;
		
	}//getStopCodeList() ends
	
	public ArrayList<String> getGeoCodeList(){ //later convert to LatLng class in android
		
		ArrayList<String> geoCodeList = new ArrayList<String>();
		String input = getData();
		String[] lat = parseData(input, "latArr");
		String[] lon = parseData(input, "lonArr");
		
		try{
			for(int i=0;i<lat.length;i++){ //!!!!Change to LatLng class in android
				geoCodeList.add(lat[i].replaceAll("\"", ""));
				geoCodeList.add(lon[i].replaceAll("\"", ""));
			}
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: array index out of bound..");
		}//catch ends
		
		return geoCodeList;
		
	}//getStopCodeList() ends
	
	public String getStopName(String stopCode){
		
		String serverOutput = null;
		
		try {
			URL apiURL = new URL(mainURL+"?StopCode1="+stopCode+"&stopPointState=0"+"&ReturnList=StopPointName");
			
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
		
		String delims1 = "[\\[\\]]+";
		String delims2 = "[,]+";
		String[] dataArray = serverOutput.split(delims1); //index zero is empty
		String[] records = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try{
			//Split every record and add to the ArrayList
			for(int i=0;i<dataArray.length;i++){
				dataArray[i].replaceAll(", ", "; ");
				records = dataArray[i].split(delims2);
				if((records[0].equals("0"))) //Get only stop data
					list.add(records);
			}//for ends
			
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: unexpected API response..");
		}//catch ends
		
		String stopName = new String();
		
		try{
		stopName=list.get(0)[1].replaceAll("\"", "");}
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: unexpected API response..");
		}
			
		return stopName;		
		
	}//getStopName ends
		
}//BusArrival ends
