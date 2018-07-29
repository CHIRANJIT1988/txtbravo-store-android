package educing.tech.store.model;

/**
 * Created by CHIRANJIT on 3/13/2016.
 */
public class DeliveryDetails
{

    public int store_id, amount, delivery_charge, delivery_status;


    public DeliveryDetails()
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


    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    /*public int getAmount()
    {
        return this.amount;
    }*/


    public void setDeliveryCharge(int delivery_charge)
    {
        this.delivery_charge = delivery_charge;
    }

    /*public int getDeliveryCharge()
    {
        return this.delivery_charge;
    }*/


    public void setDeliveryStatus(int delivery_status)
    {
        this.delivery_status = delivery_status;
    }

    /*public int getDeliveryStatus()
    {
        return this.delivery_status;
    }*/
}
