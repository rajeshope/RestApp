package restconnect;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.HttpClient;
	
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;
import org.json.JSONException;
	 
	public class RestMain {
	 
	    static final String USERNAME     = "abcxyz@contract.com";
	    static final String PASSWORD     = "abc@123$";
	    static final String LOGINURL     = "https://test.salesforce.com";
	    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
	    static final String CLIENTID     = "3MVG954MqIw6FnnMSAAbJVQWsQBoPd1Guta624aUCPWn5JPv8R1fDDnL9nIyAQVVFQ4HMyJEdrUNEEi.eYlzi";
	    static final String CLIENTSECRET = "2225421647438035856";
	    static final String redirectURI = "https://10.95.9.27:8443/RestTest/oauth/_callback";
	    private static String baseUri;
	    private static Header oauthHeader;
	    private static String REST_ENDPOINT = "/services" ;
	    private static String API_VERSION = "/v39.0";
	    private static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
	    private static String accountId ;
	    private static String accountNumber;
	    private static String accountName;
	    private static String accountOwner;
	    private static String phone; //Name, AccountNumber, Owner, Phone
	 
	    public static void main(String[] args) {
	 
	        HttpClient httpclient = HttpClientBuilder.create().build();
	 
	        // Assemble the login request URL
	        String loginURL = LOGINURL +
	                          GRANTSERVICE +
	                          "&client_id=" + CLIENTID +
	                          "&client_secret=" + CLIENTSECRET +
	                          "&username=" + USERNAME +
	                          "&password=" + PASSWORD+
	        				  "&redirect_URI=" + redirectURI;
	         
	        // Login requests must be POSTs
	        HttpPost httpPost = new HttpPost(loginURL);
	        HttpResponse response = null;
	 
	        try {
	            // Execute the login POST request
	            response = httpclient.execute(httpPost);
	            
	            System.out.println("response  === 448 "+response);
	        } catch (ClientProtocolException cpException) {
	            cpException.printStackTrace();
	        } catch (IOException ioException) {
	            ioException.printStackTrace();
	        }
	 
	        // verify response is HTTP OK
	        final int statusCode = response.getStatusLine().getStatusCode();
	        if (statusCode != HttpStatus.SC_OK) {
	            System.out.println("Error authenticating to Force.com: "+statusCode);
	            // Error is in EntityUtils.toString(response.getEntity())
	            return;
	        }
	 
	        String getResult = null;
	        try {
	            getResult = EntityUtils.toString(response.getEntity());
	            System.out.println("getResult *********"+getResult);
	        } catch (IOException ioException) {
	            ioException.printStackTrace();
	        }
	        JSONObject jsonObject = null;
	        String loginAccessToken = null;
	        String loginInstanceUrl = null;
	        try {
	            jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
	            loginAccessToken = jsonObject.getString("access_token");
	            loginInstanceUrl = jsonObject.getString("instance_url");
	        } catch (JSONException jsonException) {
	            jsonException.printStackTrace();
	        }
	        System.out.println(response.getStatusLine());
	        baseUri = loginInstanceUrl + REST_ENDPOINT;// + API_VERSION ;
	        oauthHeader = new BasicHeader("Authorization", "OAuth " + loginAccessToken) ;
	        System.out.println("oauthHeader1: " + oauthHeader);
	        System.out.println("\n" + response.getStatusLine());
	        System.out.println("Successful login");
	        System.out.println("instance URL: "+loginInstanceUrl);
	        System.out.println("access token/session ID: "+loginAccessToken);
	        System.out.println("baseUri: "+ baseUri);        
	 
	      accountQuery();
	       // createaccounts();
	        //updateaccounts();
	       // deleteAccount();
	     
	        // release connection
	        httpPost.releaseConnection();
	    }
	     
	    public static void accountQuery(){
	        System.out.println("\n_______________ account QUERY _______________");
	        try {
	 
	            //Set up the HTTP objects needed to make the request. 
	            HttpClient httpClient = HttpClientBuilder.create().build();
	            
	            String uri = baseUri + "/data/v39.0/query/?q=Select+Id,+Name,+AccountNumber,+OwnerID,+Phone+From+Account+Limit+5";
	            ///services/data/v39.0/query/?q=Select+Id,+Name+From+Account+Limit+5
	            
	            System.out.println("Query URL: " + uri);
	            HttpGet httpGet = new HttpGet(uri);
	            System.out.println("oauthHeader2: " + oauthHeader);
	            httpGet.addHeader(oauthHeader);
	          //  httpGet.addHeader(prettyPrintHeader);
	            // Make the request.
	            HttpResponse response = httpClient.execute(httpGet);
	          //  System.out.println("Response----"+response);  getString("AccountNumber");
	            // Process the result
	            int statusCode = response.getStatusLine().getStatusCode();
	            System.out.println("statusCode +++++++++"+statusCode);
	            if (statusCode == 200) {
	                String response_string = EntityUtils.toString(response.getEntity());
	                System.out.println("response string----------"+response_string);
	                try {
	                    JSONObject json = new JSONObject(response_string);
	                    System.out.println("JSON result of Query:\n" + json.toString(1));
	                    JSONArray j = json.getJSONArray("records");
	                    for (int i = 0; i < j.length(); i++){
	                    	accountId = json.getJSONArray("records").getJSONObject(i).getString("Id");
	                        accountName = json.getJSONArray("records").getJSONObject(i).getString("Name");
	                        
	                        
	                        if(!json.getJSONArray("records").getJSONObject(i).isNull("AccountNumber"))
	                           		accountNumber = json.getJSONArray("records").getJSONObject(i).getString(accountNumber);
	                        if(!json.getJSONArray("records").getJSONObject(i).isNull("Ownerid"))
	                        		accountOwner = json.getJSONArray("records").getJSONObject(i).getString("Ownerid"); 
	                        if(!json.getJSONArray("records").getJSONObject(i).isNull("Phone"))
	                        		phone = json.getJSONArray("records").getJSONObject(i).getString("Phone");
	                      
	                        System.out.println("Account record is: " + i + ". " + accountId + " " + accountName + " " + accountNumber + " " + accountOwner + " "+phone);
	                    }
	                } catch (JSONException je) {
	                    je.printStackTrace();
	                }
	            } else {
	                System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
	                System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
	                System.out.println(getBody(response.getEntity().getContent()));
	                System.exit(-1);
	            }
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        } catch (NullPointerException npe) {
	            npe.printStackTrace();
	        }
	      }
	  	      
	       
	    public static void createaccounts() {
	           System.out.println("\n_______________apexrest/AccountInfo Account INSERT _______________");
	    
	           String uri = baseUri + "/apexrest/AccountInfo";
	           System.out.println("uri while inserting record --------"+uri);
	           try {
	        	   
	               //create the JSON object containing the new account details.
	        	  
	               JSONObject account = new JSONObject();
	               account.put("name", "Account from WB2");
	               account.put("billingcity","WBCity2");
	               account.put("billingcountry", "Austria");
	               account.put("billingstreet", "WBstreet2");
	             //  account.put("phone", "0000555666");
	    
	               System.out.println("JSON for account record to be inserted:\n" + account.toString(1));
	    
	               //Construct the objects needed for the request
	               HttpClient httpClient = HttpClientBuilder.create().build();
	               HttpPost httpPost = new HttpPost(uri);
	               httpPost.addHeader(oauthHeader);
	               httpPost.addHeader(prettyPrintHeader);
	               // The message we are going to post
	               StringEntity body = new StringEntity(account.toString(1));
	               body.setContentType("application/json");
	               httpPost.setEntity(body);
	    
	               //Make the request
	               HttpResponse response = httpClient.execute(httpPost);
	    
	               //Process the results
	               int statusCode = response.getStatusLine().getStatusCode();
	             //  System.out.println(EntityUtils.toString(response.getEntity()));
	               if (statusCode == 200) {
	                   String response_string = EntityUtils.toString(response.getEntity());
	                   System.out.println("response_string************   "+ response_string);
	                   JSONObject json = new JSONObject(response_string);
	                   // Store the retrieved account id to use when we update the account.
	                   accountId = json.getString("id");
	                   System.out.println("New account id from response: " + accountId);
	               } else {
	                   System.out.println("Insertion unsuccessful. Status code returned is " + statusCode);
	               }
	           } catch (JSONException e) {
	               System.out.println("Issue creating JSON or processing results");
	               e.printStackTrace();
	           } catch (IOException ioe) {
	               ioe.printStackTrace();
	           } catch (NullPointerException npe) {
	               npe.printStackTrace();
	           }
	       }
	       
	       public static void updateaccounts() {
	           System.out.println("\n_______________ account UPDATE _______________");
	           String accountId = "0018E00000X5PwP";
	           //Notice, the id for the record to update is part of the URI, not part of the JSON
	           String uri = baseUri + "/apexrest/AccountInfo/"+accountId;
	           try {
	               //Create the JSON object containing the updated account last name
	               //and the id of the account we are updating.
	               JSONObject account = new JSONObject();
	               account.put("Name","Rexis123");
	               account.put("Billingstreet","Berlin");
	               System.out.println("JSON for update of account record:\n" + account.toString(1));
	    
	               //Set up the objects necessary to make the request.
	               //DefaultHttpClient httpClient = new DefaultHttpClient();
	               HttpClient httpClient = HttpClientBuilder.create().build();
	               System.out.println("uri  "+uri);
	               HttpPatch httpPatch = new HttpPatch(uri);
	               httpPatch.addHeader(oauthHeader);
	               httpPatch.addHeader(prettyPrintHeader);
	               StringEntity body = new StringEntity(account.toString(1));
	               body.setContentType("application/json");
	               httpPatch.setEntity(body);
	    
	               //Make the request
	               HttpResponse response = httpClient.execute(httpPatch);
	               System.out.println("response**********    "+response);
	    
	               //Process the response
	               int statusCode = response.getStatusLine().getStatusCode();
	               if (statusCode == 200) {
	                   System.out.println("Updated the account successfully.");
	               } else {
	                   System.out.println("account update NOT successfully. Status code is " + statusCode);
	               }
	           } catch (JSONException e) {
	               System.out.println("Issue creating JSON or processing results");
	               e.printStackTrace();
	           } catch (IOException ioe) {
	               ioe.printStackTrace();
	           } catch (NullPointerException npe) {
	               npe.printStackTrace();
	           }
	       }
	       
	     public static void deleteAccount() {
	       System.out.println("\n_______________ account DELETE _______________");
	       
	       	String accountId = "0018E00000X5Q0dQAF";
	        //Notice, the id for the record to update is part of the URI, not part of the JSON
	        String uri = baseUri + "/apexrest/AccountInfo/" + accountId;
	        try {
	            //Set up the objects necessary to make the request.
	            HttpClient httpClient = HttpClientBuilder.create().build();
	 
	            HttpDelete httpDelete = new HttpDelete(uri);
	            httpDelete.addHeader(oauthHeader);
	            httpDelete.addHeader(prettyPrintHeader);
	 
	            //Make the request
	            HttpResponse response = httpClient.execute(httpDelete);
	 
	            //Process the response
	            int statusCode = response.getStatusLine().getStatusCode();
	            if (statusCode == 204) {
	                System.out.println("Deleted the account successfully.");
	            } else {
	                System.out.println("account delete NOT successful. Status code is " + statusCode);
	            }
	        } catch (JSONException e) {
	            System.out.println("Issue creating JSON or processing results");
	            e.printStackTrace();
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        } catch (NullPointerException npe) {
	            npe.printStackTrace();
	        }
	       
	     }   
	      
	     private static String getBody(InputStream inputStream) {
	         String result = "";
	         try {
	             BufferedReader in = new BufferedReader(
	                     new InputStreamReader(inputStream)
	             );
	             String inputLine;
	             while ( (inputLine = in.readLine() ) != null ) {
	                 result += inputLine;
	                 result += "\n";
	             }
	             in.close();
	         } catch (IOException ioe) {
	             ioe.printStackTrace();
	         }
	         return result;
	     }
	     
   }
	
	
	

