import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class StatusChecker {
		
	private static final String TARGET_URL = "https://egov.uscis.gov/casestatus/mycasestatus.do?";
	
	private static int startingNumber = 0;
	
	private static final int CHECK_NUMBER = 100;
	
	private static Case[] cases;
	
	private static final String CASE_NUMBER = "YSC1690041894";
	
	private static int optCount = 0;
	
    public static void main(String[] args) {
    	
    	cases = new Case[CHECK_NUMBER];
    	
    	//environment setup
    	System.setProperty("https.protocols", "TLSv1");
    	
//    	System.out.println("Please enter a starting receipt number:");
//    	Scanner in = new Scanner(System.in);
    	startingNumber = 1690040894;//in.nextInt();
//    	in.close();
    	List<String> parameters = generateParameters(startingNumber, CHECK_NUMBER);
       	process(parameters);
       	
       	count();

       	System.out.println(optCount);
    }
    
    private static void count() {
    	Pattern pattern = Pattern.compile("I-765");
    	Matcher matcher;
    	for (Case c: cases) {
       		matcher = pattern.matcher(c.content);
    		if(matcher.find()) {
    			optCount++;
    		}
    		if (!c.status.equals("Case Was Received")) {
    			System.out.println(c.toString());
    		}
    	}
    }

    private static List<String> generateParameters(int startingNumber, int checkNumber) {
		List<String> parameters = new ArrayList<String>();
    	
		for (int i = 0; i < checkNumber; i++) {
    		String parameter = "appReceiptNum=YSC" + Integer.toString(startingNumber + i);
    		parameters.add(parameter);
    	}
    	
		return parameters;
	}

	private static void process(List<String> parameters) {
		for (int i = 0; i < parameters.size(); i++) {
			String response = readResponse(doHttpPost(TARGET_URL, parameters.get(i)));
			parseContent(response, i, parameters.get(i));
		}
		
	}

	private static void parseContent(String response, int currentIndex, String reciptNumber) {
		
		Document doc = Jsoup.parse(response);
		Element div = doc.select("div.rows.text-center").first();
		String updateDate = getUpdateDate(div.child(1).text());
		cases[currentIndex] = new Case();
		cases[currentIndex].reciptNumber = reciptNumber.substring(14);
		cases[currentIndex].status = div.child(0).text();
		cases[currentIndex].content = div.child(1).text();
		cases[currentIndex].updateDate = updateDate;
	}

	private static String getUpdateDate(String text) {
		if (text == null || text.length() == 0)
			return "";
		Pattern pattern = Pattern.compile("On (.*?\\d{4}),");
		Matcher matcher = pattern.matcher(text);
		if(matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}

	public static HttpURLConnection doHttpPost(String targetUrl, String urlEncodedContent)
    {
        try
        {
        	URL url = new URL(TARGET_URL);
            HttpURLConnection urlConnection = (HttpURLConnection)(url.openConnection());
            
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

        
            HttpURLConnection.setFollowRedirects(true);


            DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());

            // throws IOException
            dataOutputStream.writeBytes(urlEncodedContent);
            dataOutputStream.flush();
            dataOutputStream.close();

            return urlConnection;           

        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null; 
    }

    private static String readResponse(HttpURLConnection urlConnection)
    {
        BufferedReader bufferedReader = null;
        try
        {

            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String responeLine;

            StringBuilder response = new StringBuilder();

            while ((responeLine = bufferedReader.readLine()) != null)
            {
                response.append(responeLine);
            }

            return response.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally  // closing stream
        {
            if (bufferedReader != null)
            {   try
                {                   
                    bufferedReader.close();                   
                }
                catch (IOException e)   
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
