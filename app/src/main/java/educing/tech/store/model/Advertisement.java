package educing.tech.store.model;

/**
 * Created by CHIRANJIT on 4/26/2016.
 */
public class Advertisement
{

    public int advertisement_id;
    public String message, timestamp, file_name;


    public Advertisement()
    {

    }


    public Advertisement(String message, String file_name, String timestamp)
    {

        this.file_name = file_name;
        this.message = message;
        this.timestamp = timestamp;
    }


    public void setAdvertisementId(int advertisement_id)
    {
        this.advertisement_id = advertisement_id;
    }

    public int getAdvertisementId()
    {
        return this.advertisement_id;
    }


    public void setFileName(String file_name)
    {
        this.file_name = file_name;
    }

    public String getFileName()
    {
        return this.file_name;
    }


    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }


    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getTimestamp()
    {
        return this.timestamp;
    }
}
