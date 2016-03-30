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

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.pacote.wikieat.R;

public class CardapioActivity extends Activity implements OnItemClickListener {
	String id;
	String idPrato;
	int i;
	String[] nome, descricao, fotoPratoUrl, preco, id_prato;
	String nomeString, descricaoString, fotoPratoUrlString, precoString;
	ProgressDialog progressDialog;
	private static InputStream inpStrin;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog mProgressDialog;
	ItemListViewCardapio[] item;
	private ListView mList;
	private AdapterListViewCardapio adapterListView;
	private ArrayList<ItemListViewCardapio> itens;

	String result = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cardapio);
		addActionBar();
		Bundle bundleEndereco = getIntent().getExtras();

		id = bundleEndereco.getString("id");

		mList = (ListView) findViewById(R.id.listaCardapio);
		mList.setOnItemClickListener(this);
		new CarregarDadosCardapio().execute(id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cardapio, menu);
		return true;
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
			CardapioActivity.this.finish();

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class CarregarDadosCardapio extends
			AsyncTask<String, Integer, String> {

		

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(CardapioActivity.this, "Aguarde...", "Fazendo o download do cardápio.");
			/*Toast.makeText(getApplicationContext(),
					"Aguarde, carregando pratos do cardápio...", Toast.LENGTH_SHORT)
					.show();*/
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			
			carregaRestaurante(id.trim());
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
		
		
		
		private void carregaRestaurante(String idRestaurante) {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			parame.add(new BasicNameValuePair("id", idRestaurante));

			try {
				Log.i("DANIEL", "TESTE TRY1 " + idRestaurante);
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/cardapioRestaurante.php");

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
				id_prato = new String[jArray.length()];
				fotoPratoUrl = new String[jArray.length()];
				itens = new ArrayList<ItemListViewCardapio>();
				item = new ItemListViewCardapio[jArray.length()];
				for (i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					nome[i] = json_data.getString("nome_prato");
					descricao[i] = json_data.getString("descricao_foto");
					preco[i] = json_data.getString("preco");
					fotoPratoUrl[i] = json_data.getString("caminho_foto");
					id_prato[i] = json_data.getString("id");

					nomeString = nome[i];
					descricaoString = descricao[i];
					precoString = preco[i];
					fotoPratoUrlString = fotoPratoUrl[i];
					idPrato = id_prato[i];
					// Criamos nossa lista que preenchera o ListView

					Log.e("DANIEL", nomeString + " " + descricaoString + " "
							+ precoString + " " + fotoPratoUrlString);
					item[i] = new ItemListViewCardapio(idPrato,nomeString,
							precoString, descricaoString,carregaBitmap(fotoPratoUrlString));
					itens.add(item[i]);
				}
				
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						// Cria o adapter
						adapterListView = new AdapterListViewCardapio(
								CardapioActivity.this, itens);
						Log.e("DANIEL", "TESTE 4");
						// Define o Adapter
						mList.setAdapter(adapterListView);
						Log.e("DANIEL", "TESTE 5");
						// Cor quando a lista é selecionada para ralagem.
						mList.setCacheColorHint(Color.TRANSPARENT);
						Log.e("DANIEL", "TESTE 6");
					}
				});

			} catch (Exception e) {
				Log.e("DANIEL",
						"Erro ao popular os dados DADOS CRECHE CARREGAR"
								+ e.toString());
			}

		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long idTeste) {
		
		ItemListViewCardapio item = (ItemListViewCardapio) mList.getAdapter().getItem(position);
		Intent intent = new Intent(
				CardapioActivity.this,
				PratoActivity.class);
		intent.putExtra("id", item.getId());
		
		startActivity(intent);

	}
	
	

}
