package educing.tech.store.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 10-09-2015.
 */
public class Order extends Address implements Serializable
{

    public String order_no, category_name, category_image, product_name, product_image, store_name, order_date, unit, timestamp, order_status;
    public int user_id, store_id, product_id, weight, quantity, read_status;
    public double price, delivery_charge, discount_price, rating;


    public static List<Order> orderList = new ArrayList<>();


    public Order()
    {

    }


    public Order(int user_id, String order_no, String timestamp, double delivery_charge, String order_status)
    {

        this.user_id = user_id;
        this.order_no = order_no;
        this.timestamp = timestamp;
        this.delivery_charge = delivery_charge;
        this.order_status = order_status;
    }


    public Order(String order_no, int user_id)
    {
        this.order_no = order_no;
        this.user_id = user_id;
    }


    public Order(String order_no, int product_id, String product_name, String product_image, int weight, String unit, double price, double discount_price, int quantity)
    {

        this.order_no = order_no;
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_image = product_image;
        this.weight = weight;
        this.unit = unit;
        this.price = price;
        this.discount_price = discount_price;
        this.quantity = quantity;
    }


    public Order(int user_id, String order_no, String category_name, String category_image, String store_name, String order_date, String order_status, double rating, double delivery_charge)
    {

        this.user_id = user_id;
        this.order_no = order_no;
        this.category_name = category_name;
        this.category_image = category_image;
        this.store_name = store_name;
        this.order_date = order_date;
        this.order_status = order_status;
        this.rating = rating;
        this.delivery_charge = delivery_charge;
    }


    public void setOrderNo(String order_no)
    {
        this.order_no = order_no;
    }

    public String getOrderNo()
    {
        return this.order_no;
    }


    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getTimestamp()
    {
        return this.timestamp;
    }


    public void setUserID(int user_id)
    {
        this.user_id = user_id;
    }

    public int getUserID()
    {
        return this.user_id;
    }


    public void setProductID(int product_id)
    {
        this.product_id = product_id;
    }

    /*public int getProductID()
    {
        return this.product_id;
    }*/


    public void setProductName(String product_name)
    {
        this.product_name = product_name;
    }

    /*public String getProductName()
    {
        return this.product_name;
    }*/


    public void setProductImage(String product_image)
    {
        this.product_image = product_image;
    }

    /*public String getProductImage()
    {
        return this.product_image;
    }*/


    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    /*public String getUnit()
    {
        return this.unit;
    }*/


    public void setReadStatus(int read_status)
    {
        this.read_status = read_status;
    }

    /*public int getReadStatus()
    {
        return this.read_status;
    }*/


    public void setOrderStatus(String order_status)
    {
        this.order_status = order_status;
    }

    /*public String getOrdertatus()
    {
        return this.order_status;
    }*/


    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public int getWeight()
    {
        return this.weight;
    }


    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    /*public int getQuantity()
    {
        return this.quantity;
    }*/


    public void setDiscountPrice(double discount_price)
    {
        this.discount_price = discount_price;
    }

    /*public double getDiscountPrice()
    {
        return this.discount_price;
    }*/


    public void setDeliveryCharge(double delivery_charge)
    {
        this.delivery_charge = delivery_charge;
    }

    public double getDeliveryCharge()
    {
        return this.delivery_charge;
    }


    public void setPrice(double price)
    {
        this.price = price;
    }

    public double getPrice()
    {
        return this.price;
    }


    public static float calculateSubTotal(List<Order> list)
    {

        float total = 0;

        for (int i = 0; i< list.size(); i++)
        {
            total += list.get(i).price * list.get(i).quantity;
        }

        return total;
    }


    public static float calculateTotalDiscount(List<Order> list)
    {

        float discount_total = 0;

        for (int i = 0; i< list.size(); i++)
        {
            discount_total += (list.get(i).price - list.get(i).discount_price) * list.get(i).quantity;
        }

        return discount_total;
    }


    public static double calculateGrandTotal(double sub_total, double discount_total, double delivery_charge)
    {
        return (sub_total - discount_total + delivery_charge);
    }


    public static int findIndex(List<Order> orderList, String order_number)
    {

        for(int index=0; index< orderList.size(); index++)
        {

            if(orderList.get(index).getOrderNo().equals(order_number))
            {
                return index;
            }
        }

        return -1;
    }
}