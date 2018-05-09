package geller.omry.anyvisiontask.Model;

import android.content.Context;
import android.util.Xml;
import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by omry on 5/8/2018.
 */

public class FeedManager {

    private static FeedManager singletonFeedManager;
    private Context context;

    public FeedManager(Context context) {
        this.context=context;
    }

    /**
     * This method is responsible for creating a single instance of FeedManager.
     * It is synchronized to prevent the creation of multiple instances in case it is approached by more then one thread.
     * @param context - mandatory variable for the FeedManger (ApplicationContext is the best case).
     * @return the return value is the single instance created.
     */
    public static synchronized FeedManager getInstance(Context context){
        if(singletonFeedManager == null)
            return new FeedManager(context);
        else
            return singletonFeedManager;
    }

    /**
     * This method is parsing the data provided by the InputStream parameter and creates a List of feed Items according to the data retrieved.
     * @param inputStream - the stream that holds the data that was extarcted from the web feed.
     * @return return A list of feed items the same as the web feed.
     * @throws XmlPullParserException - possible exception if somthing went wrong with the parsing operation.
     * @throws IOException possible exception if problem occured with closing the InputStream.
     */
    public List<FeedItem> parseFeedContent(InputStream inputStream) throws XmlPullParserException, IOException {

        if(inputStream == null)
            return null;

        Map<String,FeedItem> feedMap=new HashMap<>();

        XmlPullParser xmlPullParser = Xml.newPullParser();

        xmlPullParser.setInput(inputStream, null);

        try {
            String title="";

            String description="";

            String link="";

            String date="";

            String enclosure="";

            boolean isItem = false;

            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {

                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();

                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                String result = "";

                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                }
                else if (name.equalsIgnoreCase("link")) {
                    link = result;
                }
                else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }
                else if (name.equalsIgnoreCase("pubDate")) {
                    date = result;
                }
                else if (name.equalsIgnoreCase("enclosure") || name.equalsIgnoreCase("img")) {
                    enclosure = xmlPullParser.getAttributeValue(0);;
                }

                if(createFeedItem(title,link,description,date,enclosure,isItem,feedMap)) {
                    title = "";

                    description = "";

                    link = "";

                    date = "";

                    enclosure = "";
                }
            }
        }
        finally {
            inputStream.close();
        }
        return new ArrayList<>(feedMap.values());
    }

    /**
     * This method checks if the extracted feedItem properties(title,link,date,etc..)  is not empty,
     * if not empty it creates a new FeedItem and puts it in the feedMap paramater.
     * @param title - feed item title should not be empty inorder for the method to return true.
     * @param link - feed item link should not be empty inorder for the method to return true.
     * @param description - feed item description should not be empty inorder for the method to return true.
     * @param date - feed item date should not be empty inorder for the method to return true.
     * @param enclosure - feed item enclosure should not be empty inorder for the method to return true.
     * @param isItem
     * @param feedMap
     * @return - the return value will be true if the feedItem properties are not empty.
     * #Notice# that it doesn't mmean the creation of the feed item was succesfull because the if the image of the item was not extracted succesfull
     * the method will return false.
     */
    private boolean createFeedItem(String title,
                                   String link,
                                   String description,
                                   String date,
                                   String enclosure,
                                   boolean isItem,
                                   Map<String,FeedItem> feedMap){

        if ((title != null && !title.isEmpty()) && (link != null && !link.isEmpty()) && (description != null && !description.isEmpty())
                && (date != null && !date.isEmpty()) && (enclosure != null && !enclosure.isEmpty())) {

            if(isItem && !feedMap.containsKey(title)) {

                byte[] imageBytes=getFeedItemImage(enclosure);

                if(imageBytes != null) {
                    description="Description-\n"+description.split("</p>")[0].split("<p>")[1];

                    FeedItem feedItem = new FeedItem(title, description, link, imageBytes, date);

                    feedMap.put(title, feedItem);

                }
            }
            return true;
        }
        return false;
    }

    /**
     * This method extracts byte array representing the feedItem image from the http response.
     * @param url - the url address that the byte array is extracted from.
     * @return - this method returns a byte array representing a buffer for the FeedItem image.
     */
    private byte[] getFeedItemImage(String url){
        byte[] imageBytes=null;
        InputStream in = null;
        try{
            HttpURLConnection connection=(HttpURLConnection)new URL(url).openConnection();
            connection.setUseCaches(false);
            in=connection.getInputStream();
            imageBytes= IOUtils.toByteArray(in);
            connection.disconnect();
        }catch (IOException ex){
            if(in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return imageBytes;
    }

}
