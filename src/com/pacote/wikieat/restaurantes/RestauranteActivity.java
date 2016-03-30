package com.pacote.wikieat.restaurantes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pacote.wikieat.R;
import com.pacote.wikieat.cardapio.CardapioActivity;
import com.pacote.wikieat.mapas.ActivityMapa;
import com.pacote.wikieat.menulateral.AdapterListView;
import com.pacote.wikieat.menulateral.ItemListView;
import com.pacote.wikieat.timeline.TimeLineActivity;
import com.pacote.wikieat.usuarios.MainActivity;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLogoutListener;

public class RestauranteActivity extends FragmentActivity implements
		OnItemClickListener, PanelSlideListener {
	TextView nomeTxt, enderecoTxt, descricaoTxt, telefoneTxt,
			horarioAberturaTxt, horarioFechamentoTxt;
	ImageView fotoRestauranteImage, imageViewJaFui, imageViewJaFuiBranco, imageViewQueroIr, imageViewQueroIrBranco;
	String[] nome, endereco, descricao, telefone, horarioAbertura,
			horarioFechamento, fotoRestauranteUrl, jafoi,querir,nota;
	private static InputStream inpStrin;
	private SimpleFacebook mSimpleFacebook;
	String result = "";
	String id;
	private SlidingPaneLayout mSlidingLayout;
	private ListView mList;
	private AdapterListView adapterListView;
	private ArrayList<ItemListView> itens;
	String[] insert;
	ProgressDialog progressDialog;
	RatingBar ratingBarRestaurante, ratingBarRestaurantePreenchida;
	String notaCadastro;
	float notaInicial;
	
	private OnLogoutListener mOnLogoutListener = new OnLogoutListener() {

		@Override
		public void onFail(String reason) {
			//mTextStatus.setText(reason);
			Log.w("logout", "Failed to login");
		}

		@Override
		public void onException(Throwable throwable) {
			//mTextStatus.setText("Exception: " + throwable.getMessage());
			Log.e("logout", "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {
			// show progress bar or something to the user while login is
			// happening
			//mTextStatus.setText("Thinking...");
		}

		@Override
		public void onLogout() {
			// change the state of the button or do whatever you want
			Toast.makeText(getApplicationContext(),
					"Logout efetuado com sucesso!",
					Toast.LENGTH_SHORT).show();
			Intent iLogout = new Intent(RestauranteActivity.this,
					MainActivity.class);
			startActivity(iLogout);
			finish();
		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurante);
		addActionBar();
		addSlidePannel();
		Bundle bundleEndereco = getIntent().getExtras();

		id = bundleEndereco.getString("id");
		new CarregarDados().execute(id);
		new CarregaBtnJafui().execute();
		new CarregaBtnQueroIr().execute();
		imageViewJaFui = (ImageView) findViewById(R.id.imageViewJaFui);
		imageViewJaFuiBranco = (ImageView) findViewById(R.id.imageViewJaFuiBranco);
		imageViewQueroIr = (ImageView) findViewById(R.id.imageViewQueroIr);
		imageViewQueroIrBranco = (ImageView) findViewById(R.id.imageViewQueroIrBranco);
		ratingBarRestaurante = (RatingBar) findViewById(R.id.ratingBarRestaurante);
		ratingBarRestaurantePreenchida = (RatingBar)findViewById(R.id.ratingBarRestaurantePreenchida);
		
		new CarregaBarra().execute(id);
		
		ratingBarRestaurante.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				
				alertConfirmaAvaliacao(rating);
				notaCadastro = String.valueOf(rating);
				
			}
		});
		
		imageViewJaFui.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new InsereJaFui().execute();

			}
		});
		
		imageViewQueroIr.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new InsereQueroIr().execute();
				
			}
		});
	}

	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		setUIState();
	}
	
	private void setUIState() {
		if (mSimpleFacebook.isLogin()) {
			//loggedInUIState();
		} else {
			this.finish();
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public void alertConfirmaAvaliacao(float nota){
		AlertDialog alertDialog = new AlertDialog.Builder(RestauranteActivity.this).create();
		alertDialog.setTitle("Confirmação de nota");
		alertDialog.setMessage("Você confirma essa nota para o restaurante: " + nota);
		alertDialog.setButton2("Sim", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				new InsereAvaliacao().execute("");
			}
		});
		
		alertDialog.setButton("Não", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel(); 
			
			}
		});
		alertDialog.setIcon(R.drawable.logowikieatpequeno);
		alertDialog.show();
	}

	public void addActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.rgb(221, 10, 10)));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restaurante, menu);
		return true;
	}

	private class CarregarDados extends AsyncTask<String, Integer, String> {
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(RestauranteActivity.this, "Aguarde...", "Carregando restaurante...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			carregaRestaurante(id.trim());
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante(String idCreche) {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			parame.add(new BasicNameValuePair("id", idCreche));

			try {
				Log.i("DANIEL", "TESTE TRY1 " + idCreche);
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/perfilRestaurante.php");

				httppost.setEntity(new UrlEncodedFormEntity(parame));

				HttpResponse response = httpcliente.execute(httppost);

				HttpEntity entity = response.getEntity();

				inpStrin = entity.getContent();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Nao foi possivel conectar com a internet!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "erro ao conectar" + e.toString());
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inpStrin, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				inpStrin.close();
				result = sb.toString();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Ocorreu um erro ao converter o resultado do feed!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "Erro ao converter o resultado" + e.toString());
			}
			try {
				JSONArray jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				nome = new String[jArray.length()];
				descricao = new String[jArray.length()];
				telefone = new String[jArray.length()];
				endereco = new String[jArray.length()];
				horarioAbertura = new String[jArray.length()];
				horarioFechamento = new String[jArray.length()];
				fotoRestauranteUrl = new String[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					nome[i] = json_data.getString("nome");
					descricao[i] = json_data.getString("descricao");
					telefone[i] = json_data.getString("telefone");
					endereco[i] = json_data.getString("endereco");
					horarioAbertura[i] = json_data
							.getString("horario_abertura");
					horarioFechamento[i] = json_data
							.getString("horario_fechamento");
					fotoRestauranteUrl[i] = json_data
							.getString("foto_restaurante");

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							nomeTxt = (TextView) findViewById(R.id.textViewNome);
							enderecoTxt = (TextView) findViewById(R.id.textViewEndereco);
							telefoneTxt = (TextView) findViewById(R.id.textViewTelefone);
							descricaoTxt = (TextView) findViewById(R.id.textViewDescricao);
							horarioAberturaTxt = (TextView) findViewById(R.id.textViewHorarioAbertura);
							horarioFechamentoTxt = (TextView) findViewById(R.id.textViewHorarioFechamento);

							nomeTxt.setText(nome[0]);
							// Typeface font =
							// Typeface.createFromAsset(getAssets(),
							// "mailrays.ttf");
							// txt.setTypeface(font);
							enderecoTxt.setText(endereco[0]);
							telefoneTxt.setText(telefone[0]);
							descricaoTxt.setText(descricao[0]);
							horarioAberturaTxt.setText(horarioAbertura[0]);
							horarioFechamentoTxt.setText(horarioFechamento[0]);

							DownloadImage async = new DownloadImage();
							async.execute(fotoRestauranteUrl[0]);

						}
					});

				}

			} catch (Exception e) {
				Log.e("DANIEL",
						"Erro ao popular os dados DADOS CRECHE CARREGAR"
								+ e.toString());
			}

		}

	}

	public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Log.e("DANIEL", "ENTROU NO BITMAP" + params[0]);
			String url = params[0];
			Bitmap bitmap = null;

			if (url != null) {
				try {
					final URL u = new URL(url);
					final URLConnection conn = u.openConnection();
					conn.connect();

					InputStream is = conn.getInputStream();
					final BufferedInputStream bis = new BufferedInputStream(is);
					bitmap = BitmapFactory.decodeStream(bis);

					bis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				fotoRestauranteImage = (ImageView) findViewById(R.id.imageViewFoto);
				fotoRestauranteImage
						.setImageBitmap(getRoundedCornerBitmap(getResizedBitmap(
								result, 200, 200)));
			}
		}

	}

	public class InsereJaFui extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			adicionarJaFui();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		public void adicionarJaFui() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();
			// passando os parametros para o php
			try {
				AccountManager accountManager = AccountManager
						.get(RestauranteActivity.this);
				Account[] accounts = accountManager
						.getAccountsByType("com.google");
				if (accounts.length > 0) {
					Account account = accounts[0];
					parame.add(new BasicNameValuePair("id_usuario",
							account.name));

				}
			} catch (Exception e) {
				parame.add(new BasicNameValuePair("id_usuario", ""));
			}

			parame.add(new BasicNameValuePair("id_restaurante", id));

			Log.e("DANIEL", "teste ," + id);
			try {

				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/cadastraJafui.php");

				httppost.setEntity(new UrlEncodedFormEntity(parame));

				HttpResponse response = httpcliente.execute(httppost);

				HttpEntity entity = response.getEntity();

				inpStrin = entity.getContent();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Nao foi possivel conectar com a internet!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "erro ao conectar" + e.toString() + ", " + id);
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inpStrin, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				inpStrin.close();
				result = sb.toString();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Ocorreu um erro ao converter o resultado do feed!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "Erro ao converter o resultado" + e.toString());
			}
			try {
				Log.e("DANIEL", "entrou aqui NOTIFICACAO1");
				JSONArray jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO2");
				insert = new String[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					insert[i] = json_data.getString("insert");
					Log.e("DANIEL", "linhas afetadas " + insert[i]);
					if (insert[i].equals("SIM")) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {

								imageViewJaFui.setVisibility(View.INVISIBLE);

								imageViewJaFuiBranco
										.setVisibility(View.VISIBLE);
								imageViewQueroIr.setVisibility(View.VISIBLE);
								imageViewQueroIrBranco.setVisibility(View.INVISIBLE);

							}
						});

					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(RestauranteActivity.this,
										"Erro ao cadastrar o Já fui!",
										Toast.LENGTH_LONG).show();
							}
						});

					}

				}

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	
	public class InsereQueroIr extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			adicionarJaFui();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		public void adicionarJaFui() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();
			// passando os parametros para o php
			try {
				AccountManager accountManager = AccountManager
						.get(RestauranteActivity.this);
				Account[] accounts = accountManager
						.getAccountsByType("com.google");
				if (accounts.length > 0) {
					Account account = accounts[0];
					parame.add(new BasicNameValuePair("id_usuario",
							account.name));

				}
			} catch (Exception e) {
				parame.add(new BasicNameValuePair("id_usuario", ""));
			}

			parame.add(new BasicNameValuePair("id_restaurante", id));

			Log.e("DANIEL", "teste ," + id);
			try {

				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/cadastraQueroIr.php");

				httppost.setEntity(new UrlEncodedFormEntity(parame));

				HttpResponse response = httpcliente.execute(httppost);

				HttpEntity entity = response.getEntity();

				inpStrin = entity.getContent();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Nao foi possivel conectar com a internet!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "erro ao conectar" + e.toString() + ", " + id);
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inpStrin, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				inpStrin.close();
				result = sb.toString();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Ocorreu um erro ao converter o resultado do feed!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "Erro ao converter o resultado" + e.toString());
			}
			try {
				Log.e("DANIEL", "entrou aqui NOTIFICACAO1");
				JSONArray jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO2");
				insert = new String[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					insert[i] = json_data.getString("insert");
					Log.e("DANIEL", "linhas afetadas " + insert[i]);
					if (insert[i].equals("SIM")) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {

								imageViewQueroIr.setVisibility(View.INVISIBLE);
								imageViewQueroIrBranco
										.setVisibility(View.VISIBLE);
								
								imageViewJaFui.setVisibility(View.VISIBLE);
								imageViewJaFuiBranco.setVisibility(View.INVISIBLE);

							}
						});

					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(RestauranteActivity.this,
										"Erro ao cadastrar o Já fui!",
										Toast.LENGTH_LONG).show();
							}
						});

					}

				}

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}
	
	private class CarregaBtnJafui extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			carregaBtnJafui(id.trim());
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("DANIEL", "TEEEEEEEEEEEEEEEEESTEEEEEEEEEEE");

			super.onPostExecute(result);
		}

		private void carregaBtnJafui(String idRestaurante) {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();
			try {
				AccountManager accountManager = AccountManager
						.get(RestauranteActivity.this);
				Account[] accounts = accountManager
						.getAccountsByType("com.google");
				if (accounts.length > 0) {
					Account account = accounts[0];
					parame.add(new BasicNameValuePair("id_usuario",
							account.name));
					Log.i("DANIEL", "TESTE TRY1 " + account.name);
				}
			} catch (Exception e) {
				parame.add(new BasicNameValuePair("id_usuario", ""));
			}

			parame.add(new BasicNameValuePair("id_restaurante", idRestaurante));

			try {
				Log.i("DANIEL", "TESTE TRY1 " + idRestaurante);
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/carregaJafui.php");

				httppost.setEntity(new UrlEncodedFormEntity(parame));

				HttpResponse response = httpcliente.execute(httppost);

				HttpEntity entity = response.getEntity();

				inpStrin = entity.getContent();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Nao foi possivel conectar com a internet!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "erro ao conectar" + e.toString());
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inpStrin, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				inpStrin.close();
				result = sb.toString();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Ocorreu um erro ao converter o resultado do feed!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "Erro ao converter o resultado" + e.toString());
			}
			try {
				JSONArray jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");

				jafoi = new String[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);

					jafoi[i] = json_data.getString("jafoi");

				}
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (jafoi[0].equals("0")) {
							
							imageViewJaFui.setVisibility(View.VISIBLE);
							imageViewJaFuiBranco.setVisibility(View.GONE);
						} else {


							imageViewJaFui.setVisibility(View.INVISIBLE);
							imageViewJaFuiBranco.setVisibility(View.VISIBLE);
						}

					}
				});
				
				
			} catch (Exception e) {
				Log.e("DANIEL",
						"Erro ao popular os dados DADOS CRECHE CARREGAR BARRA"
								+ e.toString());
			}

		}

	}

	private class CarregaBtnQueroIr extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			carregaBtnJafui(id.trim());
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("DANIEL", "TEEEEEEEEEEEEEEEEESTEEEEEEEEEEE");

			super.onPostExecute(result);
		}

		private void carregaBtnJafui(String idRestaurante) {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();
			try {
				AccountManager accountManager = AccountManager
						.get(RestauranteActivity.this);
				Account[] accounts = accountManager
						.getAccountsByType("com.google");
				if (accounts.length > 0) {
					Account account = accounts[0];
					parame.add(new BasicNameValuePair("id_usuario",
							account.name));
					Log.i("DANIEL", "TESTE TRY1 " + account.name);
				}
			} catch (Exception e) {
				parame.add(new BasicNameValuePair("id_usuario", ""));
			}

			parame.add(new BasicNameValuePair("id_restaurante", idRestaurante));

			try {
				Log.i("DANIEL", "TESTE TRY1 " + idRestaurante);
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/carregaQueroIr.php");

				httppost.setEntity(new UrlEncodedFormEntity(parame));

				HttpResponse response = httpcliente.execute(httppost);

				HttpEntity entity = response.getEntity();

				inpStrin = entity.getContent();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Nao foi possivel conectar com a internet!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "erro ao conectar" + e.toString());
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inpStrin, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				inpStrin.close();
				result = sb.toString();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Ocorreu um erro ao converter o resultado do feed!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "Erro ao converter o resultado" + e.toString());
			}
			try {
				JSONArray jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");

				querir = new String[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);

					querir[i] = json_data.getString("querir");

				}
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (querir[0].equals("0")) {
							
							imageViewQueroIr.setVisibility(View.VISIBLE);
							imageViewQueroIrBranco.setVisibility(View.GONE);
						} else {


							imageViewQueroIr.setVisibility(View.INVISIBLE);
							imageViewQueroIrBranco.setVisibility(View.VISIBLE);
						}

					}
				});
				
				
			} catch (Exception e) {
				Log.e("DANIEL",
						"Erro ao popular os dados DADOS CRECHE CARREGAR BARRA"
								+ e.toString());
			}

		}

	}
	
	public class InsereAvaliacao extends AsyncTask<String, Integer, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			adicionarAvaliacao();
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
		public void adicionarAvaliacao(){
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();
            // passando os parametros para o php
			try {
				AccountManager accountManager = AccountManager.get(RestauranteActivity.this);
		        Account[] accounts = accountManager.getAccountsByType("com.google");
		        if ( accounts.length > 0 )
		        {
		            Account account = accounts[0];
		            parame.add(new BasicNameValuePair("id_android", account.name));
		            
		        }
			} catch (Exception e) {
				parame.add(new BasicNameValuePair("id_android", ""));
			}
			
            parame.add(new BasicNameValuePair("id_local", id));
            parame.add(new BasicNameValuePair("nota", notaCadastro));
            
            Log.e("DANIEL", "teste ,"
                    + id + " " + notaCadastro);
            try {
                Log.i("DANIEL", "TESTE TRY1 "+notaCadastro);
                HttpClient httpcliente = new DefaultHttpClient();
               
                HttpPost httppost = new HttpPost(
                        "http://wikieat.com.br/wikieat/insereAvaliacaoRestaurante.php");
                
                httppost.setEntity(new UrlEncodedFormEntity(parame));
                
                HttpResponse response = httpcliente.execute(httppost);
                
                HttpEntity entity = response.getEntity();
                
                inpStrin = entity.getContent();
                
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Nao foi possivel conectar com a internet!",Toast.LENGTH_SHORT).show();
                Log.e("DANIEL", "erro ao conectar" + e.toString() + ","
                        + notaCadastro);
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        inpStrin, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                inpStrin.close();
                result = sb.toString();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Ocorreu um erro ao converter o resultado do feed!",Toast.LENGTH_SHORT).show();
                Log.e("DANIEL", "Erro ao converter o resultado" + e.toString());
            }
            try {
            	Log.e("DANIEL", "entrou aqui NOTIFICACAO1");
                JSONArray jArray = new JSONArray(result);
                Log.e("DANIEL", "entrou aqui NOTIFICACAO2");
                insert = new String[jArray.length()];
                

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    insert[i] = json_data.getString("insert");
                    Log.e("DANIEL", "linhas afetadas "+insert[i]);
                    if(insert[i].equals("SIM")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RestauranteActivity.this,
                                        "Muito bem! Nota cadastrada com sucesso!",
                                        Toast.LENGTH_LONG).show();
                                new CarregaBarra().execute(id);
                                
                            }
                        });

                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RestauranteActivity.this,
                                        "Erro ao cadastrar nota!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }


                }
               
            } catch (Exception e) {
                Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
            }
           
		}
		
	}

	
	private class CarregaBarra extends AsyncTask<String, Integer, String>{

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			carregaBarra(id.trim());
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.e("DANIEL", "TEEEEEEEEEEEEEEEEESTEEEEEEEEEEE");
			
			super.onPostExecute(result);
		}
		
		private void carregaBarra(String idCreche){
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();
			
			
			parame.add(new BasicNameValuePair("id_restaurante", idCreche));

            try {
                Log.i("DANIEL", "TESTE TRY1 "+idCreche);
                HttpClient httpcliente = new DefaultHttpClient();
                
                HttpPost httppost = new HttpPost(
                        "http://wikieat.com.br/wikieat/carregaNotaRestaurante.php");
                
                httppost.setEntity(new UrlEncodedFormEntity(parame));
                
                HttpResponse response = httpcliente.execute(httppost);
                
                HttpEntity entity = response.getEntity();
                
                inpStrin = entity.getContent();
                
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Nao foi possivel conectar com a internet!",Toast.LENGTH_SHORT).show();
                Log.e("DANIEL", "erro ao conectar" + e.toString());
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        inpStrin, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                inpStrin.close();
                result = sb.toString();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Ocorreu um erro ao converter o resultado do feed!",Toast.LENGTH_SHORT).show();
                Log.e("DANIEL", "Erro ao converter o resultado" + e.toString());
            }
            try {
                JSONArray jArray = new JSONArray(result);
                Log.e("DANIEL", "entrou aqui NOTIFICACAO RATING BAR");
                
                nota = new String[jArray.length()];

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);

                    nota[i] = json_data.getString("nota");
                    
                    
                	runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if(nota[0].equals("null")){
								ratingBarRestaurante.setVisibility(View.VISIBLE);
								ratingBarRestaurantePreenchida.setVisibility(View.INVISIBLE);
							}
							else{
								ratingBarRestaurantePreenchida.setRating(Float.parseFloat(nota[0]));
								ratingBarRestaurantePreenchida.setIsIndicator(true);
								ratingBarRestaurantePreenchida.setVisibility(View.VISIBLE);
								ratingBarRestaurante.setVisibility(View.INVISIBLE);
								
							}
							
						}
					});
             
  
                }
 
                
            } catch (Exception e) {
                Log.e("DANIEL", "Erro ao popular os dados DADOS CRECHE CARREGAR BARRA" + e.toString());
            }
            
		}
		
		
		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_cardapio:
			Intent i = new Intent(RestauranteActivity.this,
					CardapioActivity.class);
			i.putExtra("id", id);

			startActivity(i);
			break;
		case android.R.id.home:
			RestauranteActivity.this.finish();

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addSlidePannel() {
		mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
		mSlidingLayout.setPanelSlideListener(this);
		mList = (ListView) findViewById(R.id.left_pane);
		mList.setOnItemClickListener(this);
		createListView();
	}

	private void createListView() {
		// Criamos nossa lista que preenchera o ListView
				itens = new ArrayList<ItemListView>();
				ItemListView item1 = new ItemListView("Timeline", R.drawable.timeline);
				ItemListView item2 = new ItemListView("Restaurantes",
						R.drawable.icon_restaurante);
				ItemListView item3 = new ItemListView("Favoritos", R.drawable.favorito);
				ItemListView item4 = new ItemListView("Amigos", R.drawable.amigo);
				ItemListView item5 = new ItemListView("Sair", R.drawable.logoff);

				itens.add(item1);
				itens.add(item2);
				itens.add(item3);
				itens.add(item4);
				itens.add(item5);

				// Cria o adapter
				adapterListView = new AdapterListView(this, itens);

				// Define o Adapter
				mList.setAdapter(adapterListView);
				// Cor quando a lista é selecionada para ralagem.
				mList.setCacheColorHint(Color.TRANSPARENT);
	}

	// redimencionando a foto do perfil do usuario
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// Criando a matriz para manipulação
		Matrix matrix = new Matrix();
		// Redimensionando a imagem
		matrix.postScale(scaleWidth, scaleHeight);
		// Recriando a imagem
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	// fim redimencionando a foto do perfil do usuario

	// colocando borda arredondada na imagem do perfil do facebook
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 20;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	@Override
	public void onPanelClosed(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPanelOpened(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPanelSlide(View arg0, float arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int posicao,
			long arg3) {
		displayView(posicao);

	}

	public void displayView(int position) {

		switch (position) {
		case 0:
			Intent i1 = new Intent(RestauranteActivity.this,
					TimeLineActivity.class);
			startActivity(i1);
			break;
		case 1:
			Intent i2 = new Intent(RestauranteActivity.this, ActivityMapa.class);
			startActivity(i2);
			break;
		case 2:

			break;
		case 3:

			break;
		case 4:
			mSimpleFacebook.logout(mOnLogoutListener);
			
			
			break;
		case 5:

			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
	}
}
