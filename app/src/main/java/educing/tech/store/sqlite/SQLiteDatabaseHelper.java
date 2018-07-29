package educing.tech.store.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import educing.tech.store.model.Address;
import educing.tech.store.model.Advertisement;
import educing.tech.store.model.ChatMessage;
import educing.tech.store.model.DeliveryDetails;
import educing.tech.store.model.Order;
import educing.tech.store.model.Product;
import educing.tech.store.model.Store;
import educing.tech.store.session.SessionManager;


public class SQLiteDatabaseHelper extends SQLiteOpenHelper
{

    private SessionManager session;

    Context context;

    // Database version
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "EducingTechDB";


    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_ORDER_DETAILS = "order_details";
    public static final String TABLE_CHAT_USERS = "chat_users";
    public static final String TABLE_CHAT_MESSAGES = "chat_messages";
    public static final String TABLE_CHAT_IMAGES = "chat_images";
    public static final String TABLE_PRODUCT_CATEGORY = "product_category";
    public static final String TABLE_PRODUCT_SUB_CATEGORY = "product_sub_category";
    public static final String TABLE_PRODUCT = "product";
    public static final String TABLE_PRODUCT_IMAGE = "product_image";
    public static final String TABLE_STORE_PROFILE = "store_profile";
    public static final String TABLE_STORE_ADDRESS = "store_address";
    public static final String TABLE_DELIVERY_DETAILS = "delivery_details";
    public static final String TABLE_ADVERTISEMENT = "advertisement";

    // Complain table column names
    private static final String KEY_CUSTOMER_NAME = "customer_name";
    private static final String KEY_PHONE_NO = "phone_no";
    private static final String KEY_LANDMARK = "landmark";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_PINCODE = "pincode";
    public static final String KEY_ORDER_NO = "order_no";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_DELIVERY_CHARGE = "delivery_charge";
    private static final String KEY_DELIVERY_STATUS = "delivery_status";
    private static final String KEY_ORDER_STATUS = "order_status";
    public static final String KEY_STATUS = "status";

    public static final String KEY_ID = "id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_STORE_ID = "store_id";
    private static final String KEY_SENDER_ID = "sender_id";
    private static final String KEY_SENDER_NAME = "sender_name";
    private static final String KEY_RECIPIENT_ID = "recipient_id";
    public static final String KEY_MESSAGE_ID = "message_id";
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_READ_STATUS = "read_status";
    private static final String KEY_MESSAGE_TYPE = "message_type";
    private static final String KEY_SYNC_STATUS = "sync_status";


    public static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_SUB_CATEGORY_ID = "sub_category_id";
    private static final String KEY_CATEGORY_NAME = "category_name";
    private static final String KEY_CATEGORY_IMAGE = "category_image";
    private static final String KEY_SUB_CATEGORY_NAME = "sub_category_name";
    private static final String KEY_PRODUCT_CODE = "product_code";
    private static final String KEY_PRODUCT_NAME = "product_name";
    private static final String KEY_PRODUCT_IMAGE = "product_image";
    private static final String KEY_PRICE = "price";
    private static final String KEY_DISCOUNT_PRICE = "discount_price";
    private static final String KEY_PRODUCT_WEIGHT = "product_weight";
    private static final String KEY_UNIT = "unit";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_PRODUCT_DESCRIPTION = "product_description";
    private static final String KEY_PATH = "path";

    private static final String KEY_STORE_NAME = "store_name";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ALTERNATE_PHONE_NO = "alternate_phone_number";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_IMAGE = "image";


    private static final String CREATE_TABLE_ORDERS = "CREATE TABLE "
            + TABLE_ORDERS + "(" + KEY_ORDER_NO + " TEXT PRIMARY KEY," + KEY_USER_ID + " INTEGER," + KEY_TIMESTAMP + " TEXT,"
            + KEY_DELIVERY_CHARGE + " REAL DEFAULT 0," + KEY_CUSTOMER_NAME + " TEXT," + KEY_PHONE_NO + " TEXT," + KEY_LANDMARK + " TEXT,"
            + KEY_ADDRESS + " TEXT," + KEY_CITY + " TEXT," + KEY_STATE + " TEXT," + KEY_COUNTRY + " TEXT," + KEY_PINCODE + " TEXT,"
            + KEY_READ_STATUS + " INTEGER DEFAULT 0," + KEY_ORDER_STATUS + " TEXT DEFAULT 'RECEIVED'," + KEY_SYNC_STATUS + " INTEGER INTEGER DEFAULT 1)";


    private static final String CREATE_TABLE_ORDER_DETAILS = "CREATE TABLE "
            + TABLE_ORDER_DETAILS + "(" + KEY_ORDER_NO + " TEXT," + KEY_PRODUCT_ID + " INTEGER," + KEY_PRODUCT_NAME + " TEXT," + KEY_PRODUCT_IMAGE
            + " TEXT," + KEY_PRICE + " REAL," + KEY_DISCOUNT_PRICE + " REAL," + KEY_PRODUCT_WEIGHT + " INTEGER," + KEY_UNIT + " TEXT,"
            + KEY_QUANTITY + " INTEGER," + KEY_STATUS + " INTEGER DEFAULT 0," + KEY_SYNC_STATUS + " INTEGER DEFAULT 0," + " FOREIGN KEY ("
            + KEY_ORDER_NO + ") REFERENCES " + TABLE_ORDERS + "(" + KEY_ORDER_NO + ") ON DELETE CASCADE, UNIQUE " + "(" + KEY_ORDER_NO + ", "
            + KEY_PRODUCT_ID + "))";


    private static final String CREATE_TABLE_CHAT_USERS = "CREATE TABLE "
            + TABLE_CHAT_USERS + "(" + KEY_USER_ID + " INTEGER PRIMARY KEY," + KEY_USER_NAME + " TEXT," + KEY_TIMESTAMP + " TEXT)";


    private static final String CREATE_TABLE_CHAT_MESSAGES = "CREATE TABLE "
            + TABLE_CHAT_MESSAGES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_MESSAGE_ID + " TEXT," + KEY_USER_ID + " INTEGER,"
            + KEY_MESSAGE + " TEXT, " + KEY_IMAGE + " TEXT, " + KEY_TIMESTAMP + " TEXT," + KEY_READ_STATUS + " INTEGER DEFAULT 0," + KEY_SYNC_STATUS + " INTEGER DEFAULT 0,"
            + KEY_MESSAGE_TYPE + " INTEGER," + " FOREIGN KEY (" + KEY_USER_ID + ") REFERENCES " + TABLE_CHAT_USERS + "(" + KEY_USER_ID + ") ON DELETE CASCADE, UNIQUE "
            + "(" + KEY_MESSAGE_ID + "))";

    private static final String CREATE_TABLE_CHAT_IMAGES = "CREATE TABLE "
            + TABLE_CHAT_IMAGES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_MESSAGE_ID + " TEXT," + KEY_PATH + " TEXT,"
            + KEY_SYNC_STATUS + " INTEGER DEFAULT 0, FOREIGN KEY (" + KEY_MESSAGE_ID + ") REFERENCES " + TABLE_CHAT_MESSAGES + "(" + KEY_MESSAGE_ID + ") ON DELETE CASCADE)";


    private static final String CREATE_TABLE_PRODUCT_CATEGORY = "CREATE TABLE "
            + TABLE_PRODUCT_CATEGORY + "(" + KEY_CATEGORY_ID + " INTEGER PRIMARY KEY," + KEY_CATEGORY_NAME + " TEXT,"
            + KEY_CATEGORY_IMAGE + " TEXT," + KEY_STATUS + " INTEGER DEFAULT 0, " + KEY_SYNC_STATUS + " INTEGER DEFAULT 1)";


    private static final String CREATE_TABLE_PRODUCT_SUB_CATEGORY = "CREATE TABLE "
            + TABLE_PRODUCT_SUB_CATEGORY + "(" + KEY_SUB_CATEGORY_ID + " INTEGER PRIMARY KEY," + KEY_CATEGORY_ID + " INTEGER,"
            + KEY_SUB_CATEGORY_NAME + " TEXT," + KEY_STATUS + " INTEGER DEFAULT 0)";


    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE "
            + TABLE_PRODUCT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CATEGORY_ID + " INTEGER," + KEY_SUB_CATEGORY_ID + " INTEGER,"
            + KEY_PRODUCT_CODE + " TEXT," + KEY_PRODUCT_NAME + " TEXT," + KEY_PRODUCT_DESCRIPTION + " TEXT," + KEY_PRODUCT_IMAGE + " TEXT," + KEY_PRICE + " REAL,"
            + KEY_DISCOUNT_PRICE + " REAL," + KEY_PRODUCT_WEIGHT + " INTEGER," + KEY_UNIT + " TEXT," + KEY_STATUS + " INTEGER DEFAULT 1," + KEY_SYNC_STATUS
            + " INTEGER DEFAULT 0, UNIQUE(" + KEY_PRODUCT_CODE + "))";


    private static final String CREATE_TABLE_PRODUCT_IMAGE = "CREATE TABLE "
            + TABLE_PRODUCT_IMAGE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_PATH + " TEXT," + KEY_SYNC_STATUS + " INTEGER DEFAULT 0)";


    private static final String CREATE_TABLE_STORE_PROFILE = "CREATE TABLE "
            + TABLE_STORE_PROFILE + "(" + KEY_STORE_ID + " INTEGER PRIMARY KEY," + KEY_STORE_NAME + " TEXT," + KEY_OWNER + " TEXT,"
            + KEY_PHONE_NO + " TEXT," + KEY_ALTERNATE_PHONE_NO + " TEXT," + KEY_EMAIL + " TEXT," + KEY_SYNC_STATUS + " INTEGER DEFAULT 0)";


    private static final String CREATE_TABLE_STORE_ADDRESS = "CREATE TABLE "
            + TABLE_STORE_ADDRESS + "(" + KEY_STORE_ID + " INTEGER," + KEY_ADDRESS + " TEXT," + KEY_CITY + " TEXT,"
            + KEY_STATE + " TEXT," + KEY_PINCODE + " TEXT," + KEY_LATITUDE + " REAL DEFAULT 0," + KEY_LONGITUDE + " REAL DEFAULT 0,"
            + KEY_SYNC_STATUS + " INTEGER INTEGER DEFAULT 0," + " FOREIGN KEY (" + KEY_STORE_ID + ") REFERENCES " + TABLE_STORE_PROFILE
            + "(" + KEY_STORE_ID + ") ON DELETE CASCADE, UNIQUE(" + KEY_STORE_ID + "))";


    private static final String CREATE_TABLE_DELIVERY_DETAILS = "CREATE TABLE "
            + TABLE_DELIVERY_DETAILS + "(" + KEY_STORE_ID + " INTEGER," + KEY_AMOUNT + " INTEGER DEFAULT 0,"
            + KEY_DELIVERY_CHARGE + " INTEGER DEFAULT 0," + KEY_DELIVERY_STATUS + " INTEGER DEFAULT 0," + KEY_SYNC_STATUS
            + " INTEGER INTEGER DEFAULT 0," + " FOREIGN KEY (" + KEY_STORE_ID + ") REFERENCES " + TABLE_STORE_PROFILE
            + "(" + KEY_STORE_ID + ") ON DELETE CASCADE, UNIQUE(" + KEY_STORE_ID + "))";


    private static final String CREATE_TABLE_ADVERTISEMENT = "CREATE TABLE "
            + TABLE_ADVERTISEMENT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_MESSAGE + " TEXT," + KEY_IMAGE + " TEXT," + KEY_TIMESTAMP + " TEXT," + KEY_READ_STATUS + " INTEGER DEFAULT 0)";


    public SQLiteDatabaseHelper(Context context)
    {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        session = new SessionManager(context);
    }


    @Override
    public void onCreate(SQLiteDatabase database)
    {

        database.execSQL(CREATE_TABLE_ORDERS);
        database.execSQL(CREATE_TABLE_ORDER_DETAILS);
        database.execSQL(CREATE_TABLE_CHAT_USERS);
        database.execSQL(CREATE_TABLE_CHAT_MESSAGES);
        database.execSQL(CREATE_TABLE_CHAT_IMAGES);

        database.execSQL(CREATE_TABLE_PRODUCT_CATEGORY);
        database.execSQL(CREATE_TABLE_PRODUCT_SUB_CATEGORY);
        database.execSQL(CREATE_TABLE_PRODUCT);
        database.execSQL(CREATE_TABLE_PRODUCT_IMAGE);
        database.execSQL(CREATE_TABLE_STORE_PROFILE);
        database.execSQL(CREATE_TABLE_STORE_ADDRESS);
        database.execSQL(CREATE_TABLE_DELIVERY_DETAILS);
        database.execSQL(CREATE_TABLE_ADVERTISEMENT);

        Log.v("CREATE TABLE: ", "Inside onCreate()");
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DETAILS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_USERS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_IMAGES);

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_CATEGORY);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_SUB_CATEGORY);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_IMAGE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE_PROFILE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE_ADDRESS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_DELIVERY_DETAILS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ADVERTISEMENT);

        onCreate(database);

        Log.v("UPGRADE TABLE: ", "Inside onUpgrade()");
    }


    public boolean insertOrder(Order order)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_USER_ID, order.getUserID());
        values.put(KEY_ORDER_NO, order.getOrderNo());
        values.put(KEY_TIMESTAMP, order.getTimestamp());
        values.put(KEY_DELIVERY_CHARGE, order.getDeliveryCharge());

        values.put(KEY_CUSTOMER_NAME, order.getCustomerName());
        values.put(KEY_PHONE_NO, order.getPhoneNo());
        values.put(KEY_LANDMARK, order.getLandmark());
        values.put(KEY_ADDRESS, order.getAddress());
        values.put(KEY_CITY, order.getCity());
        values.put(KEY_STATE, order.getState());
        values.put(KEY_COUNTRY, order.getCountry());
        values.put(KEY_PINCODE, order.getPincode());

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_ORDERS, null, values) > 0;

        Log.v("createSuccessful", "" + createSuccessful);

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertOrderDetails(Order order)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ORDER_NO, order.order_no);
        values.put(KEY_PRODUCT_ID, order.product_id);
        values.put(KEY_PRODUCT_NAME, order.product_name);
        values.put(KEY_PRODUCT_IMAGE, order.product_image);
        values.put(KEY_PRICE, order.price);
        values.put(KEY_DISCOUNT_PRICE, order.discount_price);
        values.put(KEY_PRODUCT_WEIGHT, order.weight);
        values.put(KEY_UNIT, order.unit);
        values.put(KEY_QUANTITY, order.quantity);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_ORDER_DETAILS, null, values) > 0;

        Log.v("createSuccessful", "" + createSuccessful);

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertChatUser(ChatMessage message)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_USER_ID, message.getUserId());
        values.put(KEY_USER_NAME, message.getUserName());
        values.put(KEY_TIMESTAMP, message.getTimestamp());

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_CHAT_USERS, null, values) > 0;

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertChatMessage(ChatMessage message, int sync_status)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_MESSAGE_ID, message.getMessageId());
        values.put(KEY_USER_ID, message.getUserId());
        values.put(KEY_MESSAGE, message.getMessage());
        values.put(KEY_IMAGE, message.getImage());
        values.put(KEY_TIMESTAMP, message.getTimestamp());
        values.put(KEY_READ_STATUS, message.getReadStatus());
        values.put(KEY_MESSAGE_TYPE, message.getMessageType());
        values.put(KEY_SYNC_STATUS, sync_status);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_CHAT_MESSAGES, null, values) > 0;

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertChatImages(String message_id, String path)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_MESSAGE_ID, message_id);
        values.put(KEY_PATH, path);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_CHAT_IMAGES, null, values) > 0;

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertProductCategory(Product product)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_CATEGORY_ID, product.category_id);
        values.put(KEY_CATEGORY_NAME, product.category_name);
        values.put(KEY_CATEGORY_IMAGE, product.category_thumbnail);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_PRODUCT_CATEGORY, null, values) > 0;

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertProductSubCategory(Product product)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_SUB_CATEGORY_ID, product.sub_category_id);
        values.put(KEY_CATEGORY_ID, product.category_id);
        values.put(KEY_SUB_CATEGORY_NAME, product.sub_category_name);
        values.put(KEY_STATUS, product.status);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_PRODUCT_SUB_CATEGORY, null, values) > 0;

        Log.v("createSuccessful ", "" + createSuccessful);

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertProduct(Product product)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_CATEGORY_ID, product.category_id);
        values.put(KEY_SUB_CATEGORY_ID, product.sub_category_id);
        values.put(KEY_PRODUCT_CODE, product.product_code);
        values.put(KEY_PRODUCT_NAME, product.product_name);
        values.put(KEY_PRODUCT_DESCRIPTION, product.description);
        values.put(KEY_PRODUCT_IMAGE, product.product_thumbnail);
        values.put(KEY_PRICE, product.price);
        values.put(KEY_DISCOUNT_PRICE, product.discount_price);
        values.put(KEY_PRODUCT_WEIGHT, product.weight);
        values.put(KEY_UNIT, product.unit);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_PRODUCT, null, values) > 0;

        Log.v("createSuccessful ", "" + createSuccessful);

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertProductImage(Product product)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_PATH, product.product_thumbnail_path);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_PRODUCT_IMAGE, null, values) > 0;

        // Closing database connection
        database.close();

        return createSuccessful;
    }

    public boolean insertStoreProfile(Store store)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_STORE_ID, store.store_id);
        values.put(KEY_STORE_NAME, store.name);
        values.put(KEY_OWNER, store.owner_name);
        values.put(KEY_PHONE_NO, store.mobileNo);
        values.put(KEY_ALTERNATE_PHONE_NO, store.alternate_mobile_no);
        values.put(KEY_EMAIL, store.email);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_STORE_PROFILE, null, values) > 0;

        Log.v("createSuccessful ", "" + createSuccessful);

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertStoreAddress(Address address)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_STORE_ID, address.store_id);
        values.put(KEY_ADDRESS, address.address);
        values.put(KEY_CITY, address.city);
        values.put(KEY_STATE, address.state);
        values.put(KEY_PINCODE, address.pincode);
        values.put(KEY_LATITUDE, address.latitude);
        values.put(KEY_LONGITUDE, address.longitude);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_STORE_ADDRESS, null, values) > 0;

        Log.v("createSuccessful ", "" + createSuccessful);

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertDeliveryDetails(DeliveryDetails details)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_STORE_ID, details.store_id);
        values.put(KEY_AMOUNT, details.amount);
        values.put(KEY_DELIVERY_CHARGE, details.delivery_charge);
        values.put(KEY_DELIVERY_STATUS, details.delivery_status);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_DELIVERY_DETAILS, null, values) > 0;

        Log.v("createSuccessful ", "" + createSuccessful);

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertAdvertisement(Advertisement advertisement)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_MESSAGE, advertisement.getMessage());
        values.put(KEY_IMAGE, advertisement.getFileName());
        values.put(KEY_TIMESTAMP, advertisement.getTimestamp());

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_ADVERTISEMENT, null, values) > 0;

        Log.v("createSuccessful ", "" + createSuccessful);

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public ArrayList<ChatMessage> getAllChatMessage(String user_id, int x, int y)
    {

        ArrayList<ChatMessage> messagesList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_USER_ID + "='" + user_id
                + "' ORDER BY " + KEY_ID + " DESC LIMIT " + x + "," + y;

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                ChatMessage message = new ChatMessage();

                message.setMessageId(cursor.getString(1));
                message.setUserId(cursor.getString(2));
                message.setMessage(cursor.getString(3));
                message.setImage(cursor.getString(4));
                message.setTimestamp(cursor.getString(5));
                message.setReadStatus(cursor.getInt(6));
                message.setSyncStatus(cursor.getInt(7));
                message.setMessageType(cursor.getInt(8));

                messagesList.add(message);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return messagesList;
    }


    public ArrayList<ChatMessage> getAllChatImages()
    {

        ArrayList<ChatMessage> numberList = new ArrayList<>();

        String selectQuery = "SELECT " + TABLE_CHAT_IMAGES + "." + KEY_MESSAGE_ID + ", " + KEY_PATH + "," + KEY_IMAGE
                + " FROM " + TABLE_CHAT_MESSAGES + " LEFT JOIN " + TABLE_CHAT_IMAGES + " ON " + TABLE_CHAT_MESSAGES + "." + KEY_MESSAGE_ID
                + "=" + TABLE_CHAT_IMAGES + "." + KEY_MESSAGE_ID + " WHERE " + TABLE_CHAT_IMAGES + "." + KEY_SYNC_STATUS + "='0'";


        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                ChatMessage numberObj = new ChatMessage();

                numberObj.setMessageId(cursor.getString(0));
                numberObj.setFilePath(cursor.getString(1));
                numberObj.setImage(cursor.getString(2));

                numberList.add(numberObj);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return numberList;
    }


    public ChatMessage getLastMessage(String user_id)
    {

        ChatMessage message = new ChatMessage();

        String query = "SELECT " + KEY_MESSAGE + "," + KEY_TIMESTAMP + ", "
                + "(SELECT COUNT("+ KEY_READ_STATUS + ") FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_USER_ID + "='" + user_id + "' AND " + KEY_READ_STATUS + "='0')"
                + " FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_ID + "=(" + "SELECT MAX(" + KEY_ID + ") FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_USER_ID + "='" + user_id + "')";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            message.setMessage(cursor.getString(0));
            message.setTimestamp(cursor.getString(1));
            message.setUnreadMessageCount(cursor.getInt(2));
        }

        database.close();
        cursor.close();

        return message;
    }


    public ArrayList<ChatMessage> getAllChatUsers()
    {

        ArrayList<ChatMessage> messagesList = new ArrayList<>();

        /*String selectQuery = "SELECT " + TABLE_CHAT_USERS + "." + KEY_USER_ID + ", " + KEY_USER_NAME + ", " + KEY_MESSAGE + ", "
                + KEY_TIMESTAMP + ", (SELECT COUNT("+ KEY_READ_STATUS + ") FROM " + TABLE_CHAT_MESSAGES + " AS unread_message WHERE "
                + KEY_READ_STATUS + "='0')" + " FROM " + TABLE_CHAT_MESSAGES + " JOIN " + TABLE_CHAT_USERS + " ON " + TABLE_CHAT_USERS + "."
                + KEY_USER_ID + "=" + TABLE_CHAT_MESSAGES + "." + KEY_USER_ID + " WHERE "
                + KEY_ID + "=(" + "SELECT MAX(" + KEY_ID + ") FROM " + TABLE_CHAT_MESSAGES + ")";*/

        String selectQuery = "SELECT DISTINCT " + KEY_USER_ID + ", " + KEY_USER_NAME + ", " + KEY_TIMESTAMP
                + " FROM " + TABLE_CHAT_USERS + " WHERE " + KEY_USER_ID + " != 0 ORDER BY " + KEY_TIMESTAMP + " DESC";


        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                ChatMessage message = new ChatMessage();

                message.setUserId(cursor.getString(0));
                message.setUserName(cursor.getString(1));
                message.setTimestamp(cursor.getString(2));

                ChatMessage temp_msg = getLastMessage(message.user_id);

                message.setMessage(temp_msg.message);
                message.setUnreadMessageCount(temp_msg.unread_message);

                messagesList.add(message);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return messagesList;
    }


    public ArrayList<Product> getAllProductCategory()
    {

        ArrayList<Product> categoryList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_CATEGORY + " ORDER BY " + KEY_CATEGORY_ID + " ASC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Product product = new Product();

                product.setCategoryId(cursor.getInt(0));
                product.setCategoryName(cursor.getString(1));
                product.setCategoryThumbnail(cursor.getString(2));
                product.setStatus(cursor.getInt(3));

                categoryList.add(product);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return categoryList;
    }


    public Store getStoreProfile()
    {

        Store store = new Store();

        String selectQuery = "SELECT * FROM " + TABLE_STORE_PROFILE;

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                store.setStoreId(cursor.getInt(0));
                store.setStoreName(cursor.getString(1));
                store.setOwnerName(cursor.getString(2));
                store.setMobileNo(cursor.getString(3));
                store.setAlternateMobileNo(cursor.getString(4));
                store.setEmail(cursor.getString(5));
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return store;
    }


    public Address getStoreAddress()
    {

        Address address = new Address();

        String selectQuery = "SELECT * FROM " + TABLE_STORE_ADDRESS;

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                address.setStoreId(cursor.getInt(0));
                address.setAddress(cursor.getString(1));
                address.setCity(cursor.getString(2));
                address.setState(cursor.getString(3));
                address.setPincode(cursor.getString(4));
                address.setLatitude(cursor.getDouble(5));
                address.setLongitude(cursor.getDouble(6));
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return address;
    }


    public DeliveryDetails getDeliveryDetails()
    {

        DeliveryDetails details = new DeliveryDetails();

        String selectQuery = "SELECT * FROM " + TABLE_DELIVERY_DETAILS;

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                details.setStoreId(cursor.getInt(0));
                details.setAmount(cursor.getInt(1));
                details.setDeliveryCharge(cursor.getInt(2));
                details.setDeliveryStatus(cursor.getInt(3));
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return details;
    }


    public ArrayList<Product> getAllProduct()
    {

        ArrayList<Product> categoryList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT + " ORDER BY " + KEY_PRODUCT_NAME + " ASC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Product product = new Product();

                product.setProductId(cursor.getInt(0));
                product.setCategoryId(cursor.getInt(1));
                product.setSubCategoryId(cursor.getInt(2));
                product.setProductCode(cursor.getString(3));
                product.setProductName(cursor.getString(4));
                product.setProductDescription(cursor.getString(5));
                product.setProductThumbnail(cursor.getString(6));
                product.setPrice(cursor.getDouble(7));
                product.setDiscountPrice(cursor.getDouble(8));
                product.setWeight(cursor.getInt(9));
                product.setUnit(cursor.getString(10));
                product.setStatus(cursor.getInt(11));

                categoryList.add(product);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return categoryList;
    }


    public ArrayList<Product> getAllProductImage()
    {

        ArrayList<Product> productList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_IMAGE + " WHERE " + KEY_SYNC_STATUS + "='0'";


        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Product numberObj = new Product();

                numberObj.setProductId(cursor.getInt(0));
                numberObj.setProductThumbnailPath(cursor.getString(1));

                productList.add(numberObj);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return productList;
    }


    public ArrayList<Product> getActiveProductCategory()
    {

        ArrayList<Product> categoryList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_CATEGORY + " WHERE " + KEY_STATUS + " = '1'" + " ORDER BY " + KEY_CATEGORY_NAME + " ASC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Product product = new Product();

                product.setCategoryId(cursor.getInt(0));
                product.setCategoryName(cursor.getString(1));
                product.setCategoryThumbnail(cursor.getString(2));
                product.setStatus(cursor.getInt(3));

                categoryList.add(product);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return categoryList;
    }


    public ArrayList<Product> getProductSubCategory(int category_id)
    {

        ArrayList<Product> subCategoryList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_SUB_CATEGORY + " WHERE " + KEY_CATEGORY_ID + "='" + category_id + "' ORDER BY " + KEY_SUB_CATEGORY_NAME + " ASC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Product product = new Product();

                product.setSubCategoryId(cursor.getInt(0));
                product.setCategoryId(cursor.getInt(1));
                product.setSubCategoryName(cursor.getString(2));
                product.setStatus(cursor.getInt(3));

                subCategoryList.add(product);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return subCategoryList;
    }


    public String chatMessageJSONData()
    {

        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_SYNC_STATUS + " = '0'";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                HashMap<String, String> map = new HashMap<>();

                map.put(KEY_ID, cursor.getString(0));
                map.put(KEY_MESSAGE_ID, cursor.getString(1));
                map.put(KEY_RECIPIENT_ID, cursor.getString(2));
                map.put(KEY_MESSAGE, cursor.getString(3));
                map.put(KEY_IMAGE, cursor.getString(4));
                map.put(KEY_TIMESTAMP, cursor.getString(5));
                map.put(KEY_SENDER_NAME, getStoreDetails().getStoreName());
                map.put(KEY_SENDER_ID, String.valueOf(getStoreDetails().getStoreId()));

                wordList.add(map);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        Gson gson = new GsonBuilder().create();

        // Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }


    public String storeProfileJSONData()
    {

        HashMap<String, String> map = new HashMap<>();

        String selectQuery = "SELECT * FROM " + TABLE_STORE_PROFILE + " WHERE " + KEY_SYNC_STATUS + " = '0'";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                map.put(KEY_STORE_ID, cursor.getString(0));
                map.put(KEY_STORE_NAME, cursor.getString(1));
                map.put(KEY_OWNER, cursor.getString(2));
                map.put(KEY_ALTERNATE_PHONE_NO, cursor.getString(4));
                map.put(KEY_EMAIL, cursor.getString(5));

            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        Gson gson = new GsonBuilder().create();

        // Use GSON to serialize Array List to JSON
        return gson.toJson(map);
    }


    public String deliveryDetailsJSONData()
    {

        HashMap<String, String> map = new HashMap<>();

        String selectQuery = "SELECT * FROM " + TABLE_DELIVERY_DETAILS + " WHERE " + KEY_SYNC_STATUS + " = '0'";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                map.put(KEY_STORE_ID, cursor.getString(0));
                map.put(KEY_AMOUNT, cursor.getString(1));
                map.put(KEY_DELIVERY_CHARGE, cursor.getString(2));
                map.put(KEY_DELIVERY_STATUS, cursor.getString(3));

            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        Gson gson = new GsonBuilder().create();

        // Use GSON to serialize Array List to JSON
        return gson.toJson(map);
    }


    public String orderJSONData()
    {

        HashMap<String, String> map = new HashMap<>();

        String selectQuery = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + KEY_SYNC_STATUS + " = '0'";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                map.put(KEY_ORDER_NO, cursor.getString(0));
                map.put(KEY_ORDER_STATUS, cursor.getString(13));

            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        Gson gson = new GsonBuilder().create();

        // Use GSON to serialize Array List to JSON
        return gson.toJson(map);
    }


    public String storeAddressJSONData()
    {

        HashMap<String, String> map = new HashMap<>();

        String selectQuery = "SELECT * FROM " + TABLE_STORE_ADDRESS + " WHERE " + KEY_SYNC_STATUS + " = '0'";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                map.put(KEY_STORE_ID, cursor.getString(0));
                map.put(KEY_ADDRESS, cursor.getString(1));
                map.put(KEY_CITY, cursor.getString(2));
                map.put(KEY_STATE, cursor.getString(3));
                map.put(KEY_PINCODE, cursor.getString(4));
                map.put(KEY_LATITUDE, cursor.getString(5));
                map.put(KEY_LONGITUDE, cursor.getString(6));
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        Gson gson = new GsonBuilder().create();

        // Use GSON to serialize Array List to JSON
        return gson.toJson(map);
    }


    public String storeCategoryJSONData()
    {

        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_CATEGORY  + " WHERE " + KEY_SYNC_STATUS + " = '0'";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                HashMap<String, String> map = new HashMap<>();

                map.put(KEY_STORE_ID, String.valueOf(getStoreDetails().getStoreId()));
                map.put(KEY_CATEGORY_ID , cursor.getString(0));
                map.put(KEY_STATUS , cursor.getString(3));

                wordList.add(map);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        Gson gson = new GsonBuilder().create();

        // Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }


    public String productJSONData()
    {

        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + KEY_SYNC_STATUS + " = '0'";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                HashMap<String, String> map = new HashMap<>();

                map.put(KEY_ID, cursor.getString(0));
                map.put(KEY_CATEGORY_ID, cursor.getString(1));
                map.put(KEY_SUB_CATEGORY_ID, cursor.getString(2));
                map.put(KEY_PRODUCT_CODE, cursor.getString(3));
                map.put(KEY_PRODUCT_NAME, cursor.getString(4));
                map.put(KEY_PRODUCT_DESCRIPTION, cursor.getString(5));
                map.put(KEY_PRODUCT_IMAGE, cursor.getString(6));
                map.put(KEY_PRICE, cursor.getString(7));
                map.put(KEY_DISCOUNT_PRICE, cursor.getString(8));
                map.put(KEY_PRODUCT_WEIGHT, cursor.getString(9));
                map.put(KEY_UNIT, cursor.getString(10));
                map.put(KEY_STATUS, cursor.getString(11));
                map.put(KEY_STORE_ID, String.valueOf(getStoreDetails().getStoreId()));

                wordList.add(map);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        Gson gson = new GsonBuilder().create();

        // Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }


    public ArrayList<Order> getAllOrders(int x, int y)
    {

        ArrayList<Order> orderList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ORDERS + " ORDER BY " + KEY_TIMESTAMP + " DESC LIMIT "  + x + "," + y;

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Order order = new Order();

                order.setOrderNo(cursor.getString(0));
                order.setUserID(cursor.getInt(1));
                order.setTimestamp(cursor.getString(2));
                order.setDeliveryCharge(cursor.getDouble(3));

                order.setCustomerName(cursor.getString(4));
                order.setPhoneNo(cursor.getString(5));
                order.setLandmark(cursor.getString(6));
                order.setAddress(cursor.getString(7));
                order.setCity(cursor.getString(8));
                order.setState(cursor.getString(9));
                order.setCountry(cursor.getString(10));
                order.setPincode(cursor.getString(11));

                order.setReadStatus(cursor.getInt(12));
                order.setOrderStatus(cursor.getString(13));

                orderList.add(order);
            }

            while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return orderList;
    }


    public ArrayList<Order> getOrderItems(String order_no)
    {

        ArrayList<Order> orderList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ORDER_DETAILS + " WHERE " + KEY_ORDER_NO + "='" + order_no + "'";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Order order = new Order();

                order.setOrderNo(cursor.getString(0));
                order.setProductID(cursor.getInt(1));
                order.setProductName(cursor.getString(2));
                order.setProductImage(cursor.getString(3));
                order.setPrice(cursor.getDouble(4));
                order.setDiscountPrice(cursor.getDouble(5));
                order.setWeight(cursor.getInt(6));
                order.setUnit(cursor.getString(7));
                order.setQuantity(cursor.getInt(8));

                orderList.add(order);
            }

            while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return orderList;
    }


    public ArrayList<Advertisement> getAllAdvertisement(int x, int y)
    {

        ArrayList<Advertisement> advertisementList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ADVERTISEMENT + " ORDER BY " + KEY_ID + " DESC LIMIT "  + x + "," + y;


        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Advertisement advertisementObj = new Advertisement();

                advertisementObj.setAdvertisementId(cursor.getInt(0));
                advertisementObj.setMessage(cursor.getString(1));
                advertisementObj.setFileName(cursor.getString(2));
                advertisementObj.setTimestamp(cursor.getString(3));

                advertisementList.add(advertisementObj);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return advertisementList;
    }


    public void updateProduct(Product product, int sync_status)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_PRODUCT + " SET " + KEY_CATEGORY_ID + " = '" + product.category_id + "', " + KEY_SUB_CATEGORY_ID + " = '" + product.sub_category_id + "', "
                + KEY_PRODUCT_NAME + " = '" + product.product_name  + "', " + KEY_PRODUCT_DESCRIPTION + " = '" + product.description + "', " + KEY_PRODUCT_IMAGE + " = '" + product.product_thumbnail
                + "', " + KEY_PRICE + " = '" + product.price + "', " + KEY_DISCOUNT_PRICE + " = '" + product.discount_price + "', " + KEY_PRODUCT_WEIGHT + " = '" + product.weight + "', "
                + KEY_UNIT + " = '" + product.unit + "', " + KEY_STATUS + " = '" + product.status + "', " + KEY_SYNC_STATUS + " ='" + sync_status + "' WHERE " + KEY_PRODUCT_CODE + " = '" + product.product_code + "'";

        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateOrder(Order order)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_ORDERS + " SET " + KEY_ORDER_STATUS + " = '" + order.order_status + "', "
                + KEY_SYNC_STATUS + " ='0' WHERE " + KEY_ORDER_NO + " = '" + order.order_no + "'";

        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public int dbRowCount(String TABLE_NAME)
    {

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public int dbRowCount(String TABLE_NAME, String COLUMN_NAME, String value)
    {

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + "='" + value + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public int dbProductCount(Product product)
    {

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + KEY_CATEGORY_ID + "='" + product.category_id + "' AND " +
                KEY_SUB_CATEGORY_ID + "='" + product.sub_category_id + "' AND LOWER(" + KEY_PRODUCT_NAME + ") LIKE LOWER('" + product.product_name + "%') AND " +
                KEY_PRODUCT_WEIGHT + "='" + product.weight + "' AND LOWER(" + KEY_UNIT + ") LIKE LOWER('" + product.unit + "%')";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public void updateStoreProfile(Store store)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_STORE_PROFILE + " SET " + KEY_STORE_NAME + "='" + store.name + "'," + KEY_OWNER + "='" + store.owner_name + "',"
                + KEY_ALTERNATE_PHONE_NO + "='" + store.alternate_mobile_no + "'," + KEY_EMAIL + "='" + store.email + "'," + KEY_SYNC_STATUS + "='0' WHERE "
                + KEY_STORE_ID + " = '" + store.store_id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateDeliveryDetails(DeliveryDetails details)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_DELIVERY_DETAILS + " SET " + KEY_DELIVERY_CHARGE + "='" + details.delivery_charge + "'," + KEY_AMOUNT + "='" + details.amount + "',"
                + KEY_DELIVERY_STATUS + "='" + details.delivery_status + "'," + KEY_SYNC_STATUS + "='0' WHERE " + KEY_STORE_ID + " = '" + details.store_id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateStoreAddress(Address address)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_STORE_ADDRESS + " SET " + KEY_ADDRESS + "='" + address.address + "'," + KEY_CITY + "='" + address.city + "',"
                + KEY_STATE + "='" + address.state + "'," + KEY_PINCODE + "='" + address.pincode + "'," + KEY_LATITUDE + "='" + address.latitude + "',"
                + KEY_LONGITUDE + "='" + address.longitude + "'," + KEY_SYNC_STATUS + "='0' WHERE " + KEY_STORE_ID + " = '" + address.store_id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateCategory(Product product, int sync_status)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_PRODUCT_CATEGORY + " SET " + KEY_CATEGORY_NAME + "='" + product.category_name + "', "
                + KEY_CATEGORY_IMAGE + "='" + product.category_thumbnail + "', " + KEY_SYNC_STATUS + "='" + sync_status + "' WHERE " +
                KEY_CATEGORY_ID + " = '" + product.category_id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateCategoryStatus(int category_id, int status)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_PRODUCT_CATEGORY + " SET " + KEY_STATUS + "='" + status + "', "
                + KEY_SYNC_STATUS + "='0' WHERE " + KEY_CATEGORY_ID + " = '" + category_id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateSubCategory(Product product)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_PRODUCT_SUB_CATEGORY + " SET " + KEY_CATEGORY_ID + "='" + product.category_id + "', "
                + KEY_SUB_CATEGORY_NAME + "='" + product.sub_category_name + "', " + KEY_STATUS + "='" + product.status + "' WHERE "
                + KEY_SUB_CATEGORY_ID + " = '" + product.sub_category_id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateSyncStatus(String TABLE_NAME, String COLUMN_NAME, int id, int status)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_NAME + " SET " + KEY_SYNC_STATUS + " = '" + status + "' WHERE " + COLUMN_NAME + " = '" + id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateSyncStatus(String TABLE_NAME, String COLUMN_NAME, String id, int status)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_NAME + " SET " + KEY_SYNC_STATUS + " = '" + status + "' WHERE " + COLUMN_NAME + " = '" + id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateProduct(String product_code, int status)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_PRODUCT + " SET " + KEY_STATUS + " = '" + status + "'," + KEY_SYNC_STATUS +  "='0' WHERE " + KEY_PRODUCT_CODE + " = '" + product_code + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateChatUser(ChatMessage user)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_CHAT_USERS + " SET " + KEY_USER_NAME + " = '" + user.user_name + "'," + KEY_TIMESTAMP + " = '" + user.timestamp + "' WHERE " + KEY_USER_ID + " = '" + user.user_id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void deleteAllRow(String TABLE_NAME)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_NAME;
        Log.d("query", deleteQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(deleteQuery);
        database.close();
    }


    public void clearChatMessages(String user_id)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + TABLE_CHAT_MESSAGES + " WHERE " + KEY_USER_ID + "='" + user_id + "'";
        Log.d("query",updateQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(updateQuery);
        database.close();
    }


    public void clearChatUsers()
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + TABLE_CHAT_USERS;
        Log.d("query",updateQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(updateQuery);
        database.close();
    }


    public void clearChatUsers(String user_id)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + TABLE_CHAT_USERS + " WHERE " + KEY_USER_ID + "='" + user_id + "'";
        Log.d("query",updateQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(updateQuery);
        database.close();
    }


    public void clearAllAdvertisement()
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + TABLE_ADVERTISEMENT;
        Log.d("query",updateQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(updateQuery);
        database.close();
    }


    public void clearAdvertisement(int advertisement_id)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + TABLE_ADVERTISEMENT + " WHERE " + KEY_ID + "='" + advertisement_id + "'";
        Log.d("query",updateQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(updateQuery);
        database.close();
    }


    public int dbSyncCount(String TABLE_NAME)
    {

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_SYNC_STATUS + "='0'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public int setAsRead(int user_id)
    {

        String selectQuery = "UPDATE " + TABLE_CHAT_MESSAGES + " SET " + KEY_READ_STATUS + "='1' WHERE " + KEY_USER_ID + "='" + user_id + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public int setAsRead(String order_no)
    {

        String selectQuery = "UPDATE " + TABLE_ORDERS + " SET " + KEY_READ_STATUS + "='1' WHERE " + KEY_ORDER_NO + "='" + order_no + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public int setAsRead()
    {

        String selectQuery = "UPDATE " + TABLE_ADVERTISEMENT + " SET " + KEY_READ_STATUS + "='1'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public int unreadMessageCount()
    {

        String selectQuery = "SELECT * FROM " + TABLE_ADVERTISEMENT + " WHERE " + KEY_READ_STATUS + "='0'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public int countActiveCategory()
    {

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_CATEGORY + " WHERE " + KEY_STATUS + "='1'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public void deleteOrder(String order_no)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_ORDERS + " WHERE " + KEY_ORDER_NO + " ='" + order_no + "'";
        Log.d("query", deleteQuery);
        database.execSQL("PRAGMA foreign_keys=ON");
        database.execSQL(deleteQuery);
        database.close();
    }


    private Store getStoreDetails()
    {

        Store storeObj = new Store();

        if (session.checkLogin())
        {

            HashMap<String, String> store = session.getStoreDetails();

            storeObj.setStoreId(Integer.parseInt(store.get(SessionManager.KEY_STORE_ID)));
            storeObj.setStoreName(store.get(SessionManager.KEY_STORE_NAME));
            storeObj.setMobileNo(store.get(SessionManager.KEY_MOBILE_NO));
            storeObj.setPassword(store.get(SessionManager.KEY_PASSWORD));
        }

        return storeObj;
    }
}