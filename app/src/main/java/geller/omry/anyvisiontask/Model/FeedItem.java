package geller.omry.anyvisiontask.Model;

import java.net.URL;
import java.util.Date;

/**
 * Created by omry on 5/8/2018.
 */

public class FeedItem {
    private String title;
    private String description;
    private String url;
    private String date;
    private byte[] image;

    public FeedItem(String title, String description, String url, byte[] image,String date) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.image = image;
        this.date=date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
