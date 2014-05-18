package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.android.gms.internal.em;
import com.octo.android.robospice.persistence.exception.SpiceException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile2.R;

public class ContactAdderFragment extends HttpFragment implements
		OnAccountsUpdateListener {
	public static final String TAG = "ContactsAdder";
	public static final String ACCOUNT_NAME = "com.example.android.contactmanager.ContactsAdder.ACCOUNT_NAME";
	public static final String ACCOUNT_TYPE = "com.example.android.contactmanager.ContactsAdder.ACCOUNT_TYPE";

	private ArrayList<AccountData> mAccounts;
	private AccountAdapter mAccountAdapter;
	private Spinner mAccountSpinner;
	private EditText mContactEmailEditText;
	private ArrayList<Integer> mContactEmailTypes;
	private Spinner mContactEmailTypeSpinner;
	private EditText mContactNameEditText;
	private EditText mContactPhoneEditText;
	private EditText mContactAddressEditText;
	private ArrayList<Integer> mContactPhoneTypes;
	private Spinner mContactPhoneTypeSpinner;
	private Button mContactSaveButton;
	private AccountData mSelectedAccount;
	private String prenom;
	private String service;
	private String empl;
	private String titre;
	private String courriel;
	private String phone;
	private String nom;

	
	public static ContactAdderFragment newInstance(Bundle args){
		
		ContactAdderFragment fragment = new ContactAdderFragment();
		if(args != null) {
			
			fragment.setArguments(args);
		}
		return fragment;
		
	}
	
	
	/**
	 * Called when the activity is first created. Responsible for initializing
	 * the UI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments() != null) {

			Bundle bundle = getArguments();
			nom = bundle.getString(BottinDetailsFragment.NOM);
			prenom = bundle.getString(BottinDetailsFragment.PRENOM);
			titre = bundle.getString(BottinDetailsFragment.TITRE);
			service = bundle.getString(BottinDetailsFragment.SERVICE);
			empl = bundle.getString(BottinDetailsFragment.EMPLACEMENT);
			phone = bundle.getString(BottinDetailsFragment.TELBUREAU);
			courriel = bundle.getString(BottinDetailsFragment.COURRIEL);

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		

		View v = inflater.inflate(R.layout.contactadder, container, false);
		
		Log.v(TAG, "Activity State: onCreate()");

//		final Bundle extras = getArguments();
//
//		nom = BottinDetailsFragment.NOM;
//		prenom = BottinDetailsFragment.PRENOM;
//		service = BottinDetailsFragment.SERVICE;
//		// empl = extras.getString("empl");
//		titre = BottinDetailsFragment.TITRE;
//		courriel = BottinDetailsFragment.COURRIEL;
//		phone = BottinDetailsFragment.TELBUREAU;

		// Obtain handles to UI objects
		mAccountSpinner = (Spinner) v.findViewById(R.id.accountSpinner);

		mContactNameEditText = (EditText) v.findViewById(R.id.contactNameEditText);
		System.out.println(prenom);
		mContactNameEditText.setText(prenom + " " + nom);
		mContactPhoneEditText = (EditText) v.findViewById(R.id.contactPhoneEditText);
		mContactPhoneEditText.setText(phone);
		mContactEmailEditText = (EditText) v.findViewById(R.id.contactEmailEditText);
		mContactEmailEditText.setText(courriel);

		mContactPhoneTypeSpinner = (Spinner) v.findViewById(R.id.contactPhoneTypeSpinner);
		mContactEmailTypeSpinner = (Spinner) v.findViewById(R.id.contactEmailTypeSpinner);
		
		mContactAddressEditText = (EditText) v.findViewById(R.id.addressEditText);
		mContactAddressEditText.setText(empl);
		
		

		mContactSaveButton = (Button) v.findViewById(R.id.contactSaveButton);

		// Prepare list of supported account types
		// Note: Other types are available in ContactsContract.CommonDataKinds
		// Also, be aware that type IDs differ between Phone and Email, and MUST
		// be computed
		// separately.
		mContactPhoneTypes = new ArrayList<Integer>();
		mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
		mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
		mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
		mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);
		mContactEmailTypes = new ArrayList<Integer>();
		mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_HOME);
		mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_WORK);
		mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_MOBILE);
		mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_OTHER);

		// Prepare model for account spinner
		mAccounts = new ArrayList<AccountData>();
		mAccountAdapter = new AccountAdapter(getActivity(), mAccounts);
		mAccountSpinner.setAdapter(mAccountAdapter);

		// Populate list of account types for phone
		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Iterator<Integer> iter;
		iter = mContactPhoneTypes.iterator();
		while (iter.hasNext()) {
			adapter.add(ContactsContract.CommonDataKinds.Phone.getTypeLabel(
					this.getResources(), iter.next(),
					"undefined").toString());
		}
		mContactPhoneTypeSpinner.setAdapter(adapter);
		mContactPhoneTypeSpinner.setPrompt("select");

		// Populate list of account types for email
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		iter = mContactEmailTypes.iterator();
		while (iter.hasNext()) {
			adapter.add(ContactsContract.CommonDataKinds.Email.getTypeLabel(
					this.getResources(), iter.next(),
					"undefined").toString());
		}
		mContactEmailTypeSpinner.setAdapter(adapter);
		mContactEmailTypeSpinner.setPrompt("select");

		// Prepare the system account manager. On registering the listener
		// below, we also ask for
		// an initial callback to pre-populate the account list.
		AccountManager.get(getActivity()).addOnAccountsUpdatedListener(this, null, true);

		// Register handlers for UI elements
		mAccountSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long i) {
				updateAccountSelection();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// We don't need to worry about nothing being selected, since
				// Spinners don't allow
				// this.
			}
		});
		mContactSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSaveButtonClicked();
			}
		});
		
		
		return v;
	}

	/**
	 * Actions for when the Save button is clicked. Creates a contact entry and
	 * terminates the activity.
	 */
	private void onSaveButtonClicked() {
		Log.v(TAG, "Save button clicked");
		createContactEntry();
		getActivity().finish();
	}

	/**
	 * Creates a contact entry from the current UI values in the account named
	 * by mSelectedAccount.
	 */
	protected void createContactEntry() {
		// Get values from UI
		final String name = mContactNameEditText.getText().toString();
		final String phone = mContactPhoneEditText.getText().toString();
		final String email = mContactEmailEditText.getText().toString();
		final int phoneType = mContactPhoneTypes.get(mContactPhoneTypeSpinner
				.getSelectedItemPosition());
		final int emailType = mContactEmailTypes.get(mContactEmailTypeSpinner
				.getSelectedItemPosition());

		// Prepare contact creation request
		//
		// Note: We use RawContacts because this data must be associated with a
		// particular account.
		// The system will aggregate this with any other data for this contact
		// and create a
		// coresponding entry in the ContactsContract.Contacts provider for us.
		final ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,mSelectedAccount.getType())
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME,mSelectedAccount.getName()).build());
		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
//				.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,name)
//				.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,prenom)
				.build());
		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
				.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,phoneType).build());
		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
				.withValue(ContactsContract.CommonDataKinds.Email.TYPE,emailType).build());
		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Organization.COMPANY,"École de technologie supérieure")
				.withValue(ContactsContract.CommonDataKinds.Organization.DEPARTMENT,service)
				.withValue(ContactsContract.CommonDataKinds.Organization.TITLE,titre).build());
		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.StructuredPostal.DATA,empl).build());

		// Ask the Contact provider to create a new contact
		Log.i(TAG, "Selected account: " + mSelectedAccount.getName() + " (" + mSelectedAccount.getType() + ")");
		Log.i(TAG, "Creating contact: " + name);
		try {
			getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (final Exception e) {
			// Display warning
			final Context ctx = getActivity();
			final CharSequence txt = "Echec de la création du contact";
			final int duration = Toast.LENGTH_SHORT;
			final Toast toast = Toast.makeText(ctx, txt, duration);
			toast.show();

			// Log exception
			Log.e(TAG, "Exceptoin encoutered while inserting contact: " + e);
		}
	}

	/**
	 * Called when this activity is about to be destroyed by the system.
	 */
	@Override
	public void onDestroy() {
		// Remove AccountManager callback
		AccountManager.get(getActivity()).removeOnAccountsUpdatedListener(this);
		super.onDestroy();
	}

	/**
	 * Updates account list spinner when the list of Accounts on the system
	 * changes. Satisfies OnAccountsUpdateListener implementation.
	 */
	@Override
	public void onAccountsUpdated(Account[] a) {
		Log.i(TAG, "Account list update detected");
		// Clear out any old data to prevent duplicates
		mAccounts.clear();

		// Get account data from system
		final AuthenticatorDescription[] accountTypes = AccountManager.get(getActivity()).getAuthenticatorTypes();

		// Populate tables
		for (final Account element : a) {
			// The user may have multiple accounts with the same name, so we
			// need to construct a
			// meaningful display name for each.
			final String systemAccountType = element.type;
			final AuthenticatorDescription ad = getAuthenticatorDescription(systemAccountType, accountTypes);
			final AccountData data = new AccountData(element.name, ad);
			mAccounts.add(data);
		}

		// Update the account spinner
		mAccountAdapter.notifyDataSetChanged();
	}

	/**
	 * Obtain the AuthenticatorDescription for a given account type.
	 * 
	 * @param type
	 *            The account type to locate.
	 * @param dictionary
	 *            An array of AuthenticatorDescriptions, as returned by
	 *            AccountManager.
	 * @return The description for the specified account type.
	 */
	private static AuthenticatorDescription getAuthenticatorDescription(
			String type, AuthenticatorDescription[] dictionary) {
		for (final AuthenticatorDescription element : dictionary) {
			if (element.type.equals(type)) {
				return element;
			}
		}
		// No match found
		throw new RuntimeException("Unable to find matching authenticator");
	}

	/**
	 * Update account selection. If NO_ACCOUNT is selected, then we prohibit
	 * inserting new contacts.
	 */
	private void updateAccountSelection() {
		// Read current account selection
		mSelectedAccount = (AccountData) mAccountSpinner.getSelectedItem();
	}

	/**
	 * A container class used to repreresent all known information about an
	 * account.
	 */
	private class AccountData {
		private final String mName;
		private String mType;
		private CharSequence mTypeLabel;
		private Drawable mIcon;

		/**
		 * @param name
		 *            The name of the account. This is usually the user's email
		 *            address or username.
		 * @param description
		 *            The description for this account. This will be dictated by
		 *            the type of account returned, and can be obtained from the
		 *            system AccountManager.
		 */
		public AccountData(String name, AuthenticatorDescription description) {
			mName = name;
			if (description != null) {
				mType = description.type;

				// The type string is stored in a resource, so we need to
				// convert it into something
				// human readable.
				final String packageName = description.packageName;
				final PackageManager pm = getActivity().getPackageManager();

				if (description.labelId != 0) {
					mTypeLabel = pm.getText(packageName, description.labelId,
							null);
					if (mTypeLabel == null) {
						throw new IllegalArgumentException(
								"LabelID provided, but label not found");
					}
				} else {
					mTypeLabel = "";
				}

				if (description.iconId != 0) {
					mIcon = pm.getDrawable(packageName, description.iconId,
							null);
					if (mIcon == null) {
						throw new IllegalArgumentException(
								"IconID provided, but drawable not " + "found");
					}
				} else {
					mIcon = getResources().getDrawable(
							android.R.drawable.sym_def_app_icon);
				}
			}
		}

		public String getName() {
			return mName;
		}

		public String getType() {
			return mType;
		}

		public CharSequence getTypeLabel() {
			return mTypeLabel;
		}

		public Drawable getIcon() {
			return mIcon;
		}

		@Override
		public String toString() {
			return mName;
		}
	}

	/**
	 * Custom adapter used to display account icons and descriptions in the
	 * account spinner.
	 */
	private class AccountAdapter extends ArrayAdapter<AccountData> {
		public AccountAdapter(Context context,
				ArrayList<AccountData> accountData) {
			super(context, android.R.layout.simple_spinner_item, accountData);
			setDropDownViewResource(R.layout.account_entry);
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// Inflate a view template
			if (convertView == null) {
				final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
				convertView = layoutInflater.inflate(R.layout.account_entry,parent, false);
			}
			final TextView firstAccountLine = (TextView) convertView.findViewById(R.id.firstAccountLine);
			final TextView secondAccountLine = (TextView) convertView.findViewById(R.id.secondAccountLine);
			final ImageView accountIcon = (ImageView) convertView.findViewById(R.id.accountIcon);

			// Populate template
			final AccountData data = getItem(position);
			firstAccountLine.setText(data.getName());
			secondAccountLine.setText(data.getTypeLabel());
			Drawable icon = data.getIcon();
			if (icon == null) {
				icon = getResources().getDrawable(android.R.drawable.ic_menu_search);
			}
			accountIcon.setImageDrawable(icon);
			return convertView;
		}
	}

	@Override
	public void onRequestFailure(SpiceException arg0) {}

	@Override
	public void onRequestSuccess(Object arg0) {}

	@Override
	void updateUI() {}
}
