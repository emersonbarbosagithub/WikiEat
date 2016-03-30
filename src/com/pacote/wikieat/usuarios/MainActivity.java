package com.pacote.wikieat.usuarios;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.pacote.wikieat.R;
import com.pacote.wikieat.mapas.ActivityMapa;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.utils.Utils;

public class MainActivity extends Activity {
	protected static final String TAG = MainActivity.class.getName();

	private SimpleFacebook mSimpleFacebook;

	private ProgressDialog mProgress;
	private ImageView mButtonLogin;
	OnLoginListener mOnLoginListener;
	
	private class LoginComFacebook extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {
			
			mOnLoginListener = new OnLoginListener() {
				@Override
				public void onFail(String reason) {
					
					Toast.makeText(MainActivity.this, "Login com o facebook falhou por esse motivo: "+reason,
							Toast.LENGTH_SHORT).show();
					Log.w(TAG, "Failed to login");
				}

				@Override
				public void onException(Throwable throwable) {
					Log.e(TAG, "Algo errado aconteceu!", throwable);
				}

				@Override
				public void onThinking() {
					// show progress bar or something to the user while login is
					// happening
					//Toast.makeText(MainActivity.this, "Carregando...",Toast.LENGTH_SHORT).show();
					Log.w(TAG, "Teste frame 1");
					//showDialog();
				}

				@Override
				public void onLogin() {
					// change the state of the button or do whatever you want
					Log.w(TAG, "Teste frame 2");
					loggedInUIState();
					//hideDialog();
					
				}

				@Override
				public void onNotAcceptingPermissions(Permission.Type type) {
					toast(String
							.format("You didn't accept %s permissions", type.name()));
					Log.w(TAG, "Teste frame 3");
				}
			};
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			
			Log.w(TAG, "Teste frame 4");
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					mSimpleFacebook.login(mOnLoginListener);
					
				}
			});
			
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {

			Log.w(TAG, "Teste frame 5");
			super.onPreExecute();
		}		
	}

	class ThreadLogin extends Thread{
		public void run(){
			
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.w(TAG, "Teste frame 5");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Utils.getHashKey(getApplicationContext());
		setContentView(R.layout.activity_login);
		initUI();
		loginFacebook();
		Log.w(TAG, "Teste frame 6");
	}

	@Override
	protected void onResume() {
		super.onResume();
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSimpleFacebook = SimpleFacebook.getInstance(MainActivity.this);
				Log.w(TAG, "Teste frame 7");
				setUIState();
			}
		});
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
			
				mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
		
		
		Log.w(TAG, "Teste frame 8");
	}

	/**
	 * Login example.
	 */
	private void loginFacebook() {
		mButtonLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.w(TAG, "Teste frame 9");
				new LoginComFacebook().execute();
				Log.w(TAG, "Teste frame 10");
				//getProfileExample();
			}
		});
	}

	
	private void initUI() {
		mButtonLogin = (ImageView) findViewById(R.id.button_login);
	}

	private void setUIState() {
		if (mSimpleFacebook.isLogin()) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					loggedInUIState();
				}
			});
			
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
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this,
						PerfilLogadoActivity.class);
				startActivity(i);
			}
		});
		
		//getProfileExample();
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
