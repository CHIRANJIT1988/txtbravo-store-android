package educing.tech.store.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import educing.tech.store.R;
import educing.tech.store.adapter.ExpandableListAdapter;


public class FAQActivity extends AppCompatActivity {

	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faq);


		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		assert getSupportActionBar() != null;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setTitle("FAQs");

		// get the listview
		ExpandableListView expListView = (ExpandableListView) findViewById(R.id.lvExp);
		// preparing list data
		prepareListData();
		ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
		// setting list adapter
		expListView.setAdapter(listAdapter);

		for(int i=0; i<6; i++)
		{
			expListView.expandGroup(i);
		}

		// Listview Group click listener
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
										int groupPosition, long id) {
				// Toast.makeText(getApplicationContext(),
				// "Group Clicked " + listDataHeader.get(groupPosition),
				// Toast.LENGTH_SHORT).show();
				return false;
			}
		});


		// Listview Group expanded listener
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				/*Toast.makeText(getApplicationContext(),
						listDataHeader.get(groupPosition) + " Expanded",
						Toast.LENGTH_SHORT).show();*/
			}
		});


		// Listview Group collasped listener
		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				/*Toast.makeText(getApplicationContext(),
						listDataHeader.get(groupPosition) + " Collapsed",
						Toast.LENGTH_SHORT).show();*/

			}
		});


		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				/*Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
								listDataHeader.get(groupPosition)).get(
								childPosition), Toast.LENGTH_SHORT)
						.show();*/
				return false;
			}
		});
	}


	/*
	 * Preparing the list data
	 */
	private void prepareListData() {

		listDataHeader = new ArrayList<>();
		listDataChild = new HashMap<>();

		// Adding child data
		listDataHeader.add("How do i manage my account ?");
		listDataHeader.add("How to add products ?");
		listDataHeader.add("What is the charge of using txtBravo Biz app ?");
		listDataHeader.add("How to send offers to customers ?");
		listDataHeader.add("Do you help businesses in marketing ?");
		listDataHeader.add("In what time should i respond to customer's chat ?");


		// Adding child data
		List<String> faq1 = new ArrayList<>();
		faq1.add("In order to manage your account, go to the  \"Change Profile\" and fill up the details.");

		List<String> faq2 = new ArrayList<>();
		faq2.add("In order to add products, go to the \"Add Products\" section. Here you can add products, price, images and details.");

		List<String> faq3 = new ArrayList<>();
		faq3.add("Any business can use the app to chat and deal with customers completely free. Yes, you are hearing right. Its completely free for lifetime.");

		List<String> faq4 = new ArrayList<>();
		faq4.add("In order to send offers to customers as notifications you need to contact our customer care executive through chat.");

		List<String> faq5 = new ArrayList<>();
		faq5.add("Yes, we do. We have different marketing plans for businesses to drive customers. For more details chat with our customer care executive.");

		List<String> faq6 = new ArrayList<>();
		faq6.add("We suggest you to  respond quickly to customers' chat to drive sale. Quick respond will give you more customers.");

		listDataChild.put(listDataHeader.get(0), faq1); // Header, Child data
		listDataChild.put(listDataHeader.get(1), faq2);
		listDataChild.put(listDataHeader.get(2), faq3);
		listDataChild.put(listDataHeader.get(3), faq4);
		listDataChild.put(listDataHeader.get(4), faq5);
		listDataChild.put(listDataHeader.get(5), faq6);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{

			case android.R.id.home:

				finish();
				break;
		}

		return super.onOptionsItemSelected(item);
	}
}