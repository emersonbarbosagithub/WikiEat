package com.pacote.wikieat.cardapio;

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
import com.pacote.wikieat.mapas.ActivityMapa;
import com.pacote.wikieat.menulateral.AdapterListView;
import com.pacote.wikieat.menulateral.ItemListView;
import com.pacote.wikieat.timeline.TimeLineActivity;
import com.pacote.wikieat.usuarios.MainActivity;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLogoutListener;

public class PratoActivity extends FragmentActivity implements
		OnItemClickListener, PanelSlideListener {
	TextView nomeTxt, descricaoTxt, precoTxt;
	private SimpleFacebook mSimpleFacebook;
	ImageView fotoPratoImage;
	String[] nome, descricao, preco, fotoPratoUrl, insert, jaComeu, querComer,
			nota;
	String nomePrato;
	ImageView imageViewJaComi, imageViewJaComiBranco, imageViewQueroComer,
			imageViewQueroComerBranco;
	private static InputStream inpStrin;
	String result = "";
	String id;
	private SlidingPaneLayout mSlidingLayout;
	private ListView mList;
	private AdapterListView adapterListView;
	private ArrayList<ItemListView> itens;
	ProgressDialog progressDialog;
	RatingBar ratingBarPrato, ratingBarPratoPreenchida;
	String notaCadastro;
	
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
			Intent iLogout = new Intent(PratoActivity.this,
					MainActivity.class);
			startActivity(iLogout);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prato);
		addActionBar();
		addSlidePannel();
		Bundle bundleEndereco = getIntent().getExtras();

		id = bundleEndereco.getString("id");
		new CarregarDados().execute(id);
		new CarregaBtnJaComi().execute();
		new CarregaBtnQueroComer().execute();
		imageViewJaComi = (ImageView) findViewById(R.id.imageViewJaComi);
		imageViewJaComiBranco = (ImageView) findViewById(R.id.imageViewJaComiBranco);
		imageViewQueroComer = (ImageView) findViewById(R.id.imageViewQueroComer);
		imageViewQueroComerBranco = (ImageView) findViewById(R.id.imageViewQueroComerBranco);
		ratingBarPrato = (RatingBar) findViewById(R.id.ratingBarPrato);
		ratingBarPratoPreenchida = (RatingBar) findViewById(R.id.ratingBarPratoPreenchida);

		new CarregaBarra().execute(id);

		ratingBarPrato
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {

						alertConfirmaAvaliacao(rating);
						notaCadastro = String.valueOf(rating);

					}
				});

		imageViewJaComi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new InsereJaComi().execute();
			}
		});

		imageViewQueroComer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new InsereQueroComer().execute();
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
	public void alertConfirmaAvaliacao(float nota) {
		AlertDialog alertDialog = new AlertDialog.Builder(PratoActivity.this)
				.create();
		alertDialog.setTitle("Confirmação de nota");
		alertDialog.setMessage("Você confirma essa nota para o restaurante: "
				+ nota);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prato, menu);
		return true;
	}

	private class CarregarDados extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(PratoActivity.this,
					"Aguarde...", "Carregando Prato...");
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
						"http://wikieat.com.br/wikieat/perfilPrato.php");

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
				preco = new String[jArray.length()];

				fotoPratoUrl = new String[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					nome[i] = json_data.getString("nome_prato");
					descricao[i] = json_data.getString("descricao_foto");
					preco[i] = json_data.getString("preco");

					fotoPratoUrl[i] = json_data.getString("caminho_foto");

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							nomeTxt = (TextView) findViewById(R.id.textViewNome);

							descricaoTxt = (TextView) findViewById(R.id.textViewDescricao);
							precoTxt = (TextView) findViewById(R.id.textViewPreco);

							nomePrato = nome[0];

							nomeTxt.setText(nome[0]);
							// Typeface font =
							// Typeface.createFromAsset(getAssets(),
							// "mailrays.ttf");
							// txt.setTypeface(font);

							descricaoTxt.setText(descricao[0]);
							precoTxt.setText(preco[0]);

							DownloadImage async = new DownloadImage();
							async.execute(fotoPratoUrl[0]);

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
				fotoPratoImage = (ImageView) findViewById(R.id.imageViewFoto);
				fotoPratoImage
						.setImageBitmap(getRoundedCornerBitmap(getResizedBitmap(
								result, 200, 200)));
			}
		}

	}

	public class InsereJaComi extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			adicionarJaComi();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		public void adicionarJaComi() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();
			// passando os parametros para o php
			try {
				AccountManager accountManager = AccountManager
						.get(PratoActivity.this);
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

			parame.add(new BasicNameValuePair("id_prato", id));

			Log.e("DANIEL", "teste ," + id);
			try {

				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/cadastraJaComi.php");

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

								imageViewJaComi.setVisibility(View.INVISIBLE);

								imageViewJaComiBranco
										.setVisibility(View.VISIBLE);
								imageViewQueroComerBranco
										.setVisibility(View.INVISIBLE);
								imageViewQueroComer.setVisibility(View.VISIBLE);

							}
						});

					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(PratoActivity.this,
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

	public class InsereQueroComer extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			adicionarJaComi();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		public void adicionarJaComi() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();
			// passando os parametros para o php
			try {
				AccountManager accountManager = AccountManager
						.get(PratoActivity.this);
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

			parame.add(new BasicNameValuePair("id_prato", id));

			Log.e("DANIEL", "teste ," + id);
			try {

				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/cadastraQueroComer.php");

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

								imageViewQueroComer
										.setVisibility(View.INVISIBLE);

								imageViewQueroComerBranco
										.setVisibility(View.VISIBLE);
								imageViewJaComi.setVisibility(View.VISIBLE);
								imageViewJaComiBranco
										.setVisibility(View.INVISIBLE);

							}
						});

					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(PratoActivity.this,
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

	private class CarregaBtnJaComi extends AsyncTask<String, Integer, String> {

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
						.get(PratoActivity.this);
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

			parame.add(new BasicNameValuePair("id_prato", idRestaurante));

			try {
				Log.i("DANIEL", "TESTE TRY1 " + idRestaurante);
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/carregaJaComi.php");

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

				jaComeu = new String[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);

					jaComeu[i] = json_data.getString("jacomeu");

				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (jaComeu[0].equals("0")) {

							imageViewJaComi.setVisibility(View.VISIBLE);
							imageViewJaComiBranco.setVisibility(View.GONE);
						} else {

							imageViewJaComi.setVisibility(View.INVISIBLE);
							imageViewJaComiBranco.setVisibility(View.VISIBLE);
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

	private class CarregaBtnQueroComer extends
			AsyncTask<String, Integer, String> {

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
						.get(PratoActivity.this);
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

			parame.add(new BasicNameValuePair("id_prato", idRestaurante));

			try {
				Log.i("DANIEL", "TESTE TRY1 " + idRestaurante);
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/carregaQueroComer.php");

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

				querComer = new String[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);

					querComer[i] = json_data.getString("quercomer");

				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (querComer[0].equals("0")) {

							imageViewQueroComer.setVisibility(View.VISIBLE);
							imageViewQueroComerBranco.setVisibility(View.GONE);
						} else {

							imageViewQueroComer.setVisibility(View.INVISIBLE);
							imageViewQueroComerBranco
									.setVisibility(View.VISIBLE);
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

	public class InsereAvaliacao extends AsyncTask<String, Integer, String> {

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

		public void adicionarAvaliacao() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();
			// passando os parametros para o php
			try {
				AccountManager accountManager = AccountManager
						.get(PratoActivity.this);
				Account[] accounts = accountManager
						.getAccountsByType("com.google");
				if (accounts.length > 0) {
					Account account = accounts[0];
					parame.add(new BasicNameValuePair("id_android",
							account.name));

				}
			} catch (Exception e) {
				parame.add(new BasicNameValuePair("id_android", ""));
			}

			parame.add(new BasicNameValuePair("id_local", id));
			parame.add(new BasicNameValuePair("nota", notaCadastro));

			Log.e("DANIEL", "teste ," + id + " " + notaCadastro);
			try {
				Log.i("DANIEL", "TESTE TRY1 " + notaCadastro);
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/insereAvaliacaoPrato.php");

				httppost.setEntity(new UrlEncodedFormEntity(parame));

				HttpResponse response = httpcliente.execute(httppost);

				HttpEntity entity = response.getEntity();

				inpStrin = entity.getContent();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Nao foi possivel conectar com a internet!",
						Toast.LENGTH_SHORT).show();
				Log.e("DANIEL", "erro ao conectar" + e.toString() + ","
						+ notaCadastro);
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
								Toast.makeText(
										PratoActivity.this,
										"Muito bem! Nota cadastrada com sucesso!",
										Toast.LENGTH_LONG).show();
								new CarregaBarra().execute(id);

							}
						});

					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(PratoActivity.this,
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

	private class CarregaBarra extends AsyncTask<String, Integer, String> {

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

		private void carregaBarra(String idCreche) {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			parame.add(new BasicNameValuePair("id_restaurante", idCreche));

			try {
				Log.i("DANIEL", "TESTE TRY1 " + idCreche);
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/carregaNotaPrato.php");

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
				Log.e("DANIEL", "entrou aqui NOTIFICACAO RATING BAR");

				nota = new String[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);

					nota[i] = json_data.getString("nota");

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (nota[0].equals("null")) {
								ratingBarPrato.setVisibility(View.VISIBLE);
								ratingBarPratoPreenchida
										.setVisibility(View.INVISIBLE);
							} else {
								ratingBarPratoPreenchida.setRating(Float
										.parseFloat(nota[0]));
								ratingBarPratoPreenchida.setIsIndicator(true);
								ratingBarPratoPreenchida
										.setVisibility(View.VISIBLE);
								ratingBarPrato.setVisibility(View.INVISIBLE);

							}

						}
					});

				}

			} catch (Exception e) {
				Log.e("DANIEL",
						"Erro ao popular os dados DADOS CRECHE CARREGAR BARRA"
								+ e.toString());
			}

		}

	}

	public void addActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.rgb(221, 10, 10)));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case android.R.id.home:
			PratoActivity.this.finish();

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
			Intent i1 = new Intent(PratoActivity.this, TimeLineActivity.class);
			startActivity(i1);
			break;
		case 1:
			Intent i2 = new Intent(PratoActivity.this, ActivityMapa.class);
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
