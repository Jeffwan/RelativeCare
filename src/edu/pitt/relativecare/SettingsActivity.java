package edu.pitt.relativecare;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener{
	
	private EditText et_setup_number;
	private EditText et_setup_email;
	private Button btn_select_contact;
	private Button btn_finish_setup;
	private String recipientNumber = "";
	private String recipientName = "";
    // Persistent storage for geofences
    private SimpleGeofenceStore mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPrefs = new SimpleGeofenceStore(this);
		
		if (mPrefs.isSetup()) {
			 Intent intent = new Intent(this,MainActivity.class);
			 startActivity(intent);
		} else {
			et_setup_number = (EditText) findViewById(R.id.et_setup_saftnumber);
			et_setup_email = (EditText) findViewById(R.id.et_setup_safeemail);
			btn_select_contact = (Button) findViewById(R.id.btn_select_contact);
			btn_finish_setup = (Button) findViewById(R.id.btn_finish_setup);
			
			btn_select_contact.setOnClickListener(this);
			btn_finish_setup.setOnClickListener(this);
		}
			
	}

    public void finishSetup() {
    	String number = et_setup_number.getText().toString().trim();
    	String email = et_setup_email.getText().toString().trim();
    	
    	if (TextUtils.isEmpty(number) || TextUtils.isEmpty(email)) {
			Toast.makeText(getApplicationContext(), "number and email cannot be null", 1).show();
			return;
		}
    	
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }



	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_select_contact:
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, 0);

			break;
			
		case R.id.btn_finish_setup:
			finishSetup();
			break;

		default:
			break;
		}
		
	}
	
	//This will be called when a contact is selected from the contacts list
		public void onActivityResult(int reqCode, int resultCode, Intent data) {
			  super.onActivityResult(reqCode, resultCode, data);

			 if (resultCode == Activity.RESULT_OK) {  
		            ContentResolver reContentResolverol = getContentResolver();  
		            Uri contactData = data.getData();  
		            @SuppressWarnings("deprecation")  
		            Cursor cursor = managedQuery(contactData, null, null, null, null);  
		            cursor.moveToFirst();  
		            recipientName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));  
		            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));  
		            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
		                     null,   
		                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,   
		                     null,   
		                     null);  
		             while (phone.moveToNext()) {  
		                 recipientNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));  
		                 et_setup_number.setText(recipientNumber);  
		             }  
			 }
		}
	
}
