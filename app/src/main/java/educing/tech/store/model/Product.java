package educing.tech.store.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dell on 12-06-2015.
 */

public class Product implements Serializable
{

    public int product_id, category_id, sub_category_id, amount, weight, quantity, status;
    public double price, discount_price;
    public String product_code, product_name, description, category_name, category_thumbnail, sub_category_name, unit, product_thumbnail, product_thumbnail_path, date;


    public Product()
    {

    }


    public Product(int category_id, String category_name, String category_thumbnail)
    {
        this.category_id = category_id;
        this.category_name = category_name;
        this.category_thumbnail = category_thumbnail;
    }


    public Product(int category_id, int sub_category_id, String sub_category_name, int status)
    {
        this.category_id = category_id;
        this.sub_category_id = sub_category_id;
        this.sub_category_name = sub_category_name;
        this.status = status;
    }


    public Product(int category_id, int sub_category_id, int product_id, int quantity, String date)
    {
        this.category_id = category_id;
        this.sub_category_id = sub_category_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.date = date;
    }


    public Product(int category_id, int sub_category_id, String product_name, String product_thumbnail)
    {
        this.category_id = category_id;
        this.sub_category_id = sub_category_id;
        this.product_name = product_name;
        this.product_thumbnail = product_thumbnail;
    }


    public Product(int category_id, int sub_category_id, int product_id, String product_name, int amount, String unit, double price, String product_thumbnail)
    {

        this.category_id = category_id;
        this.sub_category_id = sub_category_id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.amount = amount;
        this.unit = unit;
        this.price = price;
        this.product_thumbnail = product_thumbnail;
    }


    public Product(String product_code, int category_id, int sub_category_id, String product_name, String description, int weight, String  unit, double price, double discount_price, String product_thumbnail, int status)
    {

        this.product_code = product_code;
        this.category_id = category_id;
        this.sub_category_id = sub_category_id;
        this.product_name = product_name;
        this.description = description;
        this.weight = weight;
        this.unit = unit;
        this.price = price;
        this.discount_price = discount_price;
        this.product_thumbnail = product_thumbnail;
        this.status = status;
    }


    public void setProductId(int product_id)
    {
        this.product_id = product_id;
    }

    public int getProductId()
    {
        return this.product_id;
    }


    public void setCategoryId(int category_id)
    {
        this.category_id = category_id;
    }

    public int getCategoryId()
    {
        return this.category_id;
    }


    public void setSubCategoryId(int sub_category_id)
    {
        this.sub_category_id = sub_category_id;
    }

    /*public int getSubCategoryId()
    {
        return this.sub_category_id;
    }*/


    public void setProductDescription(String description)
    {
        this.description = description;
    }

    /*public String getProductDescription()
    {
        return this.description;
    }*/


    public void setProductCode(String product_code)
    {
        this.product_code = product_code;
    }

    public String getProductCode()
    {
        return this.product_code;
    }


    public void setProductName(String product_name)
    {
        this.product_name = product_name;
    }

    /*public String getProductName()
    {
        return this.product_name;
    }*/


    public void setCategoryName(String category_name)
    {
        this.category_name = category_name;
    }

    public String getCategoryName()
    {
        return this.category_name;
    }


    public void setCategoryThumbnail(String category_thumbnail)
    {
        this.category_thumbnail = category_thumbnail;
    }

    /*public String getCategoryThumbnail()
    {
        return this.category_thumbnail;
    }*/


    public void setSubCategoryName(String sub_category_name)
    {
        this.sub_category_name = sub_category_name;
    }

    public String getSubCategoryName()
    {
        return this.sub_category_name;
    }


    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    /*public String getUnit()
    {
        return this.unit;
    }*/


    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getStatus()
    {
        return this.status;
    }


    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public int getWeight()
    {
        return this.weight;
    }


    public void setPrice(double price)
    {
        this.price = price;
    }

    public double getPrice()
    {
        return this.price;
    }


    public void setDiscountPrice(double discount_price)
    {
        this.discount_price = discount_price;
    }

    /*public double getDiscountPrice()
    {
        return this.discount_price;
    }*/


    public void setProductThumbnail(String product_thumbnail)
    {
        this.product_thumbnail = product_thumbnail;
    }

    /*public String getProductThumbnail()
    {
        return this.product_thumbnail;
    }*/


    public void setProductThumbnailPath(String product_thumbnail_path)
    {
        this.product_thumbnail_path = product_thumbnail_path;
    }

    public String getProductThumbnailPath()
    {
        return this.product_thumbnail_path;
    }


    public static int findIndex(List<Product> productCategoryList, int category_id)
    {

        for(int index = 0; index < productCategoryList.size(); index ++)
        {
            if(productCategoryList.get(index).getCategoryId() == category_id)
            {
                return index;
            }
        }

        return -1;
    }


    public static int findIndex(List<Product> productList, String product_code)
    {

        for(int index = 0; index < productList.size(); index ++)
        {
            if(productList.get(index).getProductCode().equals(product_code))
            {
                return index;
            }
        }

        return -1;
    }
}