package com.pacote.wikieat;

import java.security.Permissions;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Album;
import com.sromku.simple.fb.entities.Checkin;
import com.sromku.simple.fb.entities.Comment;
import com.sromku.simple.fb.entities.Event;
import com.sromku.simple.fb.entities.Event.EventDecision;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Group;
import com.sromku.simple.fb.entities.Like;
import com.sromku.simple.fb.entities.Page;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.entities.Post;
import com.sromku.simple.fb.entities.Privacy;
import com.sromku.simple.fb.entities.Privacy.PrivacySettings;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.entities.Video;
//import com.sromku.simple.fb.example.friends.FriendsActivity;
//import com.sromku.simple.fb.example.utils.Utils;
import com.sromku.simple.fb.listeners.OnAlbumsListener;
import com.sromku.simple.fb.listeners.OnCheckinsListener;
import com.sromku.simple.fb.listeners.OnCommentsListener;
import com.sromku.simple.fb.listeners.OnEventsListener;
import com.sromku.simple.fb.listeners.OnFriendsListener;
import com.sromku.simple.fb.listeners.OnGroupsListener;
import com.sromku.simple.fb.listeners.OnInviteListener;
import com.sromku.simple.fb.listeners.OnLikesListener;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnPageListener;
import com.sromku.simple.fb.listeners.OnPhotosListener;
import com.sromku.simple.fb.listeners.OnPostsListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.listeners.OnPublishListener;
import com.sromku.simple.fb.listeners.OnVideosListener;
import com.sromku.simple.fb.utils.Attributes;
import com.sromku.simple.fb.utils.PictureAttributes;
import com.sromku.simple.fb.utils.PictureAttributes.PictureType;
import com.sromku.simple.fb.utils.Utils;

public class MainActivity extends Activity {
	protected static final String TAG = MainActivity.class.getName();

	private SimpleFacebook mSimpleFacebook;

	private ProgressDialog mProgress;
	private ImageView mButtonLogin;

	private TextView mTextStatus;

	// Login listener
	private OnLoginListener mOnLoginListener = new OnLoginListener() {

		@Override
		public void onFail(String reason) {
			mTextStatus.setText(reason);
			Log.w(TAG, "Failed to login");
		}

		@Override
		public void onException(Throwable throwable) {
			mTextStatus.setText("Exception: " + throwable.getMessage());
			Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {
			// show progress bar or something to the user while login is
			// happening
			Toast.makeText(MainActivity.this, "Carregando...",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onLogin() {
			// change the state of the button or do whatever you want

			loggedInUIState();
			Toast.makeText(MainActivity.this, "Login Feito com sucesso!",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onNotAcceptingPermissions(Permission.Type type) {
			toast(String
					.format("You didn't accept %s permissions", type.name()));
		}
	};

	// Logout listener
	private OnLogoutListener mOnLogoutListener = new OnLogoutListener() {

		@Override
		public void onFail(String reason) {
			mTextStatus.setText(reason);
			Log.w(TAG, "Failed to login");
		}

		@Override
		public void onException(Throwable throwable) {
			mTextStatus.setText("Exception: " + throwable.getMessage());
			Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {
			// show progress bar or something to the user while login is
			// happening
			mTextStatus.setText("Thinking...");
		}

		@Override
		public void onLogout() {
			// change the state of the button or do whatever you want
			mTextStatus.setText("Logged out");
			loggedOutUIState();
			toast("You are logged out");
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// test local language
		// Utils.updateLanguage(getApplicationContext(), "en");
		Utils.getHashKey(getApplicationContext());

		setContentView(R.layout.activity_login);
		initUI();

		/*
		 * ---- Examples -----
		 */

		// 1. Login example
		loginFacebook();

		// 2. Logout example
		// logoutExample();

	}

	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		setUIState();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
	}

	/**
	 * Login example.
	 */
	private void loginFacebook() {
		mButtonLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mSimpleFacebook.login(mOnLoginListener);
				getProfileExample();
			}
		});
	}

	/**
	 * Logout example
	 */
	/*
	 * private void logoutExample() { mButtonLogout.setOnClickListener(new
	 * View.OnClickListener() {
	 * 
	 * @Override public void onClick(View arg0) {
	 * mSimpleFacebook.logout(mOnLogoutListener); } }); }
	 */

	private void getProfileExample() {
		// listener for profile request
		final OnProfileListener onProfileListener = new OnProfileListener() {

			@Override
			public void onFail(String reason) {
				hideDialog();
				// insure that you are logged in before getting the profile
				Log.w(TAG, reason);
			}

			@Override
			public void onException(Throwable throwable) {
				hideDialog();
				Log.e(TAG, "Bad thing happened", throwable);
			}

			@Override
			public void onThinking() {
				showDialog();
				// show progress bar or something to the user while fetching
				// profile
				Log.i(TAG, "Thinking...");
			}

			@Override
			public void onComplete(Profile profile) {
				hideDialog();
				Log.i(TAG, "PROFILE ID = " + profile.getId());
				String name = profile.getFirstName();
				String lastName = profile.getLastName();
				String email = profile.getEmail();
				String id = profile.getId();
				String biografia = profile.getBio();
				String aniversario = profile.getBirthday();
				String cidade = profile.getHometown();
				String urlImagem = profile.getPicture();

				Intent i = new Intent(MainActivity.this,
						PerfilLogadoActivity.class);
				i.putExtra("nomeUsuario", name);
				i.putExtra("sobrenomeUsuario", lastName);
				i.putExtra("EmailUsuario", String.valueOf(email));
				i.putExtra("id", String.valueOf(id));
				i.putExtra("biografia", String.valueOf(biografia));
				i.putExtra("aniversario", String.valueOf(aniversario));
				i.putExtra("cidade", String.valueOf(cidade));
				i.putExtra("url_imagem", String.valueOf(urlImagem));
				startActivity(i);
			}
		};
		PictureAttributes pictureAttributes = Attributes
				.createPictureAttributes();
		pictureAttributes.setHeight(500);
		pictureAttributes.setWidth(500);
		pictureAttributes.setType(PictureType.SQUARE);

		// prepare the properties that we need
		Profile.Properties properties = new Profile.Properties.Builder()
				.add(Profile.Properties.ID).add(Profile.Properties.FIRST_NAME)
				.add(Profile.Properties.LAST_NAME)
				.add(Profile.Properties.HOMETOWN)
				.add(Profile.Properties.EMAIL)
				.add(Profile.Properties.BIRTHDAY).add(Profile.Properties.COVER)
				.add(Profile.Properties.PICTURE, pictureAttributes).build();

		mSimpleFacebook.getProfile(properties, onProfileListener);
	}

	/**
	 * Get my profile with properties example
	 */
	/*
	 * private void getProfileWithPropertiesExample() { // listener for profile
	 * request final OnProfileListener onProfileListener = new
	 * OnProfileListener() {
	 * 
	 * @Override public void onFail(String reason) { hideDialog(); // insure
	 * that you are logged in before getting the profile Log.w(TAG, reason); }
	 * 
	 * @Override public void onException(Throwable throwable) { hideDialog();
	 * Log.e(TAG, "Bad thing happened", throwable); }
	 * 
	 * @Override public void onThinking() { showDialog(); // show progress bar
	 * or something to the user while fetching // profile Log.i(TAG,
	 * "Thinking..."); }
	 * 
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Override public void onComplete(Profile profile) { hideDialog(); String
	 * id = profile.getId(); String firstName = profile.getFirstName(); Photo
	 * photo = profile.getCover(); String pictureUrl = profile.getPicture();
	 * 
	 * // this is just to show the results AlertDialog dialog =
	 * com.pacote.wikieat
	 * .utils.Utils.buildProfileResultDialog(MainActivity.this, new Pair<String,
	 * String>(Profile.Properties.ID, id), new Pair<String,
	 * String>(Profile.Properties.FIRST_NAME, firstName), new Pair<String,
	 * String>(Profile.Properties.COVER, photo.getSource()), new Pair<String,
	 * String>(Profile.Properties.PICTURE, pictureUrl)); dialog.show(); } };
	 * 
	 * // set on click button mButtonGetProfileProperties.setOnClickListener(new
	 * View.OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // prepare specific picture that
	 * we need PictureAttributes pictureAttributes =
	 * Attributes.createPictureAttributes(); pictureAttributes.setHeight(500);
	 * pictureAttributes.setWidth(500);
	 * pictureAttributes.setType(PictureType.SQUARE);
	 * 
	 * // prepare the properties that we need Profile.Properties properties =
	 * new Profile.Properties.Builder().add(Profile.Properties.ID).add(Profile.
	 * Properties.FIRST_NAME).add(Profile.Properties.COVER)
	 * .add(Profile.Properties.PICTURE, pictureAttributes).build();
	 * 
	 * // do the get profile action mSimpleFacebook.getProfile(properties,
	 * onProfileListener); } });
	 * 
	 * }
	 */

	private void initUI() {
		mButtonLogin = (ImageView) findViewById(R.id.button_login);

		/*
		 * mButtonFragments.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent = new
		 * Intent(MainActivity.this, FriendsActivity.class);
		 * startActivity(intent); } });
		 */

	}

	private void setUIState() {
		if (mSimpleFacebook.isLogin()) {
			loggedInUIState();
		} else {
			loggedOutUIState();
		}
	}

	/**
	 * Show toast
	 * 
	 * @param message
	 */
	private void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	private void loggedInUIState() {

		getProfileExample();
	}

	private void loggedOutUIState() {

	}

	private void showDialog() {
		mProgress = ProgressDialog.show(this, "Um momento",
				"Aguardando o Facebook...", true);
	}

	private void hideDialog() {
		if (mProgress != null) {
			mProgress.hide();
		}
	}

}
