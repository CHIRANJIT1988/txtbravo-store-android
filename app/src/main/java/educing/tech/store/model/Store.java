package educing.tech.store.model;


import java.io.Serializable;


public class Store implements Serializable
{

	public String name, owner_name, password, mobileNo, alternate_mobile_no, confirmation_code, email, device_id;
	public int store_id, category, status;
	public boolean is_online;



	public Store()
	{
		
	}


	public Store(int store_id, String name, String mobileNo)
	{
		this.store_id = store_id;
		this.name = name;
		this.mobileNo = mobileNo;
	}


	public Store(int store_id, int category, int status)
	{

		this.store_id = store_id;
		this.category = category;
		this.status = status;
	}


	public Store(int store_id, boolean is_online)
	{

		this.store_id = store_id;
		this.is_online = is_online;
	}


	public void setStoreName(String name)
	{
		this.name = name;
	}

	public String getStoreName()
	{
		return this.name;
	}


	public void setOwnerName(String owner_name)
	{
		this.owner_name = owner_name;
	}

	/*public String getOwnerName()
	{
		return this.owner_name;
	}*/


	public void setStoreId(int store_id)
	{
		this.store_id = store_id;
	}
	
	public int getStoreId()
	{
		return this.store_id;
	}
	
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/*public String getPassword()
	{
		return this.password;
	}*/


	public void setMobileNo(String mobileNo)
	{
		this.mobileNo = mobileNo;
	}

	public String getMobileNo()
	{
		return this.mobileNo;
	}


	public void setAlternateMobileNo(String alternate_mobile_no)
	{
		this.alternate_mobile_no = alternate_mobile_no;
	}

	/*public String getAlternateMobileNo()
	{
		return this.alternate_mobile_no;
	}*/


	/*public String getEmail()
	{
		return this.email;
	}*/

	public void setEmail(String email)
	{
		this.email = email;
	}


	public void setCategory(int category)
	{
		this.category = category;
	}

	public int getCategory()
	{
		return this.category;
	}


	public void setStatus(int status)
	{
		this.status = status;
	}

	public int getStatus()
	{
		return this.status;
	}


	public void setDeviceId(String device_id)
	{
		this.device_id = device_id;
	}

	/*public String getDeviceId()
	{
		return this.device_id;
	}*/


	public void setIsOnline(boolean is_online)
	{
		this.is_online = is_online;
	}

	public boolean getIsOnline()
	{
		return this.is_online;
	}


	public void setConfirmationCode(String confirmation_code)
	{
		this.confirmation_code = confirmation_code;
	}

	public String getConfirmationCode()
	{
		return this.confirmation_code;
	}
}