package com.pacote.wikieat.timeline;

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
import org.json.JSONArray;
import org.json.JSONObject;

import com.pacote.wikieat.R;
import com.pacote.wikieat.menulateral.AdapterListView;
import com.pacote.wikieat.menulateral.ItemListView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TimeLineActivity extends FragmentActivity implements
		OnItemClickListener, PanelSlideListener {
	private ListView mList;
	private itemTimeLine adapterTimeLine;
	private ArrayList<ItemListView> itensListLat;
	private ArrayList<itemTimeLineList> itens;
	ProgressDialog progressDialog;
	private static InputStream inpStrin;
	private ListView timeList;
	String result = "";
	String[] nomeRestaurante, nomePrato,nomeUsuario,fotoPrato,fotoRestaurante,fotoUsuario,acaoUsuario;
	itemTimeLineList[] item;
	int i;
	AdapterListView adapterListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_line);
		addActionBar();
		timeList = (ListView) findViewById(R.id.timeline);
		new CarregarDadosTimeLine().execute();
	}

	
	private class CarregarDadosTimeLine extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(TimeLineActivity.this,
					"Aguarde...", "Carregando a Timeline.");
			/*
			 * Toast.makeText(getApplicationContext(),
			 * "Aguarde, carregando pratos do cardápio...", Toast.LENGTH_SHORT)
			 * .show();
			 */
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			carregaTimeLine();
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private Bitmap carregaBitmap(String fotoPratoUrl) {
			Log.e("DANIEL", "ENTROU NO BITMAP 1" + fotoPratoUrl);
			String url = fotoPratoUrl;
			Log.e("DANIEL", "ENTROU NO BITMAP 2" + fotoPratoUrl);
			Bitmap bitmap = null;
			Log.e("DANIEL", "ENTROU NO BITMAP 3" + fotoPratoUrl);

			if (url != null) {
				try {
					Log.e("DANIEL", "ENTROU NO BITMAP 4" + fotoPratoUrl);
					final URL u = new URL(url);
					Log.e("DANIEL", "ENTROU NO BITMAP 5" + fotoPratoUrl);
					final URLConnection conn = u.openConnection();
					Log.e("DANIEL", "ENTROU NO BITMAP 6" + fotoPratoUrl);
					conn.connect();
					Log.e("DANIEL", "ENTROU NO BITMAP 7" + fotoPratoUrl);
					InputStream is = conn.getInputStream();
					Log.e("DANIEL", "ENTROU NO BITMAP 8" + fotoPratoUrl);
					final BufferedInputStream bis = new BufferedInputStream(is);
					Log.e("DANIEL", "ENTROU NO BITMAP 9" + fotoPratoUrl);
					bitmap = BitmapFactory.decodeStream(bis);
					Log.e("DANIEL", "ENTROU NO BITMAP 10" + fotoPratoUrl);

					bis.close();
					Log.e("DANIEL", "ENTROU NO BITMAP 11" + fotoPratoUrl);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return bitmap;
		}

		private void carregaTimeLine() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();



			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/carregaTimeline.php");

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
				nomeRestaurante = new String[jArray.length()];
				nomePrato = new String[jArray.length()];
				nomeUsuario = new String[jArray.length()];
				fotoPrato = new String[jArray.length()];
				fotoRestaurante = new String[jArray.length()];
				fotoUsuario = new String[jArray.length()];
				acaoUsuario = new String[jArray.length()];
				itens = new ArrayList<itemTimeLineList>();
				item = new itemTimeLineList[jArray.length()];
				for (i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					nomeRestaurante[i] = json_data.getString("nome_restaurante");
					nomePrato[i] = json_data.getString("nome_prato");
					nomeUsuario[i] = json_data.getString("nome_usuario");
					fotoPrato[i] = json_data.getString("foto_prato");
					fotoRestaurante[i] = json_data.getString("foto_restaurante");
					fotoUsuario[i] = json_data.getString("foto_usuario");
					acaoUsuario[i] = json_data.getString("acao_do_usuario");
					
				
					// Criamos nossa lista que preenchera o ListView

					Log.e("DANIEL", nomeRestaurante[i] + " " + nomePrato[i] + " "
							+ nomeUsuario[i] + " " + fotoPrato[i]);
					item[i] = new itemTimeLineList(nomeRestaurante[i], nomePrato[i], nomeUsuario[i], carregaBitmap(fotoRestaurante[i]),carregaBitmap(fotoPrato[i]),carregaBitmap(fotoUsuario[i]), acaoUsuario[i]);
					itens.add(item[i]);
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// Cria o adapter
						adapterTimeLine = new itemTimeLine(TimeLineActivity.this, itens);

						// Define o Adapter
						timeList.setAdapter(adapterTimeLine);
						// Cor quando a lista é selecionada para ralagem.
						timeList.setCacheColorHint(Color.TRANSPARENT);
					}
				});

			} catch (Exception e) {
				Log.e("DANIEL",
						"Erro ao popular os dados DADOS CRECHE CARREGAR"
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_line, menu);
		return true;
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

}
