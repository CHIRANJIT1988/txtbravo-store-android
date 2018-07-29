package educing.tech.store.model;

import java.io.Serializable;

/**
 * Created by CHIRANJIT on 10/8/2015.
 */
public class Address implements Serializable
{

    public int store_id;
    public double latitude, longitude;
    public String customer_name, pincode, phone_no, landmark, address, city, state, country;

    public Address()
    {

    }


    public void setStoreId(int store_id)
    {
        this.store_id = store_id;
    }

    public int getStoreId()
    {
        return this.store_id;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return this.address;
    }


    public void setCustomerName(String customer_name)
    {
        this.customer_name = customer_name;
    }

    public String getCustomerName()
    {
        return this.customer_name;
    }


    public void setPhoneNo(String phone_no)
    {
        this.phone_no = phone_no;
    }

    public String getPhoneNo()
    {
        return this.phone_no;
    }


    public void setLandmark(String landmark)
    {
        this.landmark = landmark;
    }

    public String getLandmark()
    {
        return this.landmark;
    }


    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCity()
    {
        return this.city;
    }


    public void setState(String state)
    {
        this.state = state;
    }

    public String getState()
    {
        return this.state;
    }


    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getCountry()
    {
        return this.country;
    }


    public void setPincode(String pincode)
    {
        this.pincode = pincode;
    }

    public String getPincode()
    {
        return this.pincode;
    }



    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLatitude()
    {
        return this.latitude;
    }


    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLongitude()
    {
        return this.longitude;
    }
}