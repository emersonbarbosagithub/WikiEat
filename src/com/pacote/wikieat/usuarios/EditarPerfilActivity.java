package com.pacote.wikieat.usuarios;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pacote.wikieat.R;
import com.pacote.wikieat.mapas.ActivityMapa;
import com.pacote.wikieat.menulateral.AdapterListView;
import com.pacote.wikieat.menulateral.ItemListView;
import com.pacote.wikieat.timeline.TimeLineActivity;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLogoutListener;


public class EditarPerfilActivity extends FragmentActivity implements
OnItemClickListener, PanelSlideListener {
	private SimpleFacebook mSimpleFacebook;
	private static InputStream inpStrin;
	String[] insert;
	String result = "";
	ImageView imagem;
	private String url_imagem;
	private SlidingPaneLayout mSlidingLayout;
	private ListView mList;
	private AdapterListView adapterListView;
	private ArrayList<ItemListView> itens;
	String nome;
	String sobrenome;
	String cidade;
	String senha;
	String email, idFacebook;
	String data_nascimento;
	EditText editTextSenha;
	EditText editTextEmail;
	EditText editTextDataNascimento;
	
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
			Intent iLogout = new Intent(EditarPerfilActivity.this,
					MainActivity.class);
			startActivity(iLogout);
		}

	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar_perfil);
		
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.rgb(221, 10, 10)));
		
		addSlidePannel();		
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}
		
		
		
		// get data via the key
		nome = extras.getString("nomeUsuario");
		url_imagem = extras.getString("url_imagem");
		sobrenome = extras.getString("sobrenomeUsuario");
		cidade = extras.getString("cidade");
		
		email= extras.getString("EmailUsuario");
		data_nascimento = extras.getString("aniversario");
		idFacebook= extras.getString("idFacebook");
		
		
		TextView textoNome = (TextView)findViewById(R.id.textViewNomeUsu);
		textoNome.setText(nome);
		EditText editTextNome = (EditText)findViewById(R.id.editTextNome);
		editTextNome.setText(nome+" "+sobrenome);
		EditText editTextCidade = (EditText)findViewById(R.id.editTextCidade);
		editTextCidade.setText(cidade);
		
		
		
		
		imagem = (ImageView) findViewById(R.id.imageViewUsu);

		DownloadImage async = new DownloadImage();
		async.execute(url_imagem);
		
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
	
	
	private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
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

				imagem.setImageBitmap(getRoundedCornerBitmap(getResizedBitmap(
						result, 150, 150)));
			}
		}

	}
	
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
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.editar_perfil, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub	
		switch (item.getItemId()) {
		case R.id.action_salvar:
			
			//pegando os textos
			editTextSenha = (EditText)findViewById(R.id.editTextSenha);
			senha = editTextSenha.getText().toString();			
			editTextEmail = (EditText)findViewById(R.id.editTextEmail);
			email = editTextEmail.getText().toString();
			editTextDataNascimento= (EditText)findViewById(R.id.editTextDataNascimento);
			data_nascimento = editTextDataNascimento.getText().toString();
			//log para testes
			Log.i("DANIEL PERFIL", "TESTE PERFIL"+nome);
			Log.i("DANIEL PERFIL", "TESTE PERFIL"+senha);
			Log.i("DANIEL PERFIL", "TESTE PERFIL"+email);
			Log.i("DANIEL PERFIL", "TESTE PERFIL"+data_nascimento);
			Log.i("DANIEL PERFIL", "TESTE PERFIL"+idFacebook);
			Log.i("DANIEL PERFIL", "TESTE PERFIL"+url_imagem);
			
			new EditarPerfil().execute(idFacebook,nome,senha,email,data_nascimento,cidade, url_imagem); 
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void enviarDadosParaPerfilLogado(){
		Intent i = new Intent(EditarPerfilActivity.this,
				PerfilLogadoActivity.class);
		if(nome.equals("null")){
			i.putExtra("nomeUsuario", "");
		}else{
			i.putExtra("nomeUsuario", nome);
		}
		
		if(url_imagem.equals("null")){
			i.putExtra("url_imagem", "");
		}else{
			i.putExtra("url_imagem", String.valueOf(url_imagem));
		}
		if(sobrenome.equals("null")){
			i.putExtra("sobrenomeUsuario", "");
		}else{
			i.putExtra("sobrenomeUsuario", sobrenome);
		}
		
		if(cidade.equals("null")){
			
		}else{
			i.putExtra("cidade", String.valueOf(cidade));
		}
		
		if(email.equals("null")){
			i.putExtra("EmailUsuario", "");
		}else{
			i.putExtra("EmailUsuario", String.valueOf(email));
		}
		
		if(data_nascimento.equals("null")){
			i.putExtra("aniversario", "");
		}
		else{
			i.putExtra("aniversario", String.valueOf(data_nascimento));
		}		
		
		if(idFacebook.equals("null")){
			i.putExtra("idFacebook", "");
		}
		else{
			i.putExtra("idFacebook", String.valueOf(idFacebook));
		}		
		
		startActivity(i);
	}
	
	
	private class EditarPerfil extends AsyncTask<String, Integer, String>{
	
		
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(getApplicationContext(),
                    "Aguarde, cadastrando perfil...",Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			cadastraPerfil(idFacebook,nome, senha, email, data_nascimento, cidade, url_imagem);
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(),
                    "Muito bem! Creche cadastrada com sucesso!",Toast.LENGTH_SHORT).show();	
			super.onPostExecute(result);
		}
		
		private String cadastraPerfil(String idFacebook,String nome, String senha, String email, String data_nascimento, String cidade,String url_imagem) {
            ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();
            // passando os parametros para o php
            parame.add(new BasicNameValuePair("nome", nome));
            parame.add(new BasicNameValuePair("cidade", cidade));
            parame.add(new BasicNameValuePair("email", email));
            parame.add(new BasicNameValuePair("senha", senha));
            parame.add(new BasicNameValuePair("dt_nascimento", data_nascimento));
            parame.add(new BasicNameValuePair("idFacebook", idFacebook));
            parame.add(new BasicNameValuePair("url_imagem", url_imagem));
            try {
				AccountManager accountManager = AccountManager
						.get(EditarPerfilActivity.this);
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
            
            Log.e("DANIEL", "teste ,"
                    + idFacebook);
            try {
                Log.i("DANIEL", "TESTE TRY1 "+nome);
                HttpClient httpcliente = new DefaultHttpClient();
               
                HttpPost httppost = new HttpPost(
                        "http://wikieat.com.br/wikieat/editar_perfil.php");
                
                httppost.setEntity(new UrlEncodedFormEntity(parame));
                
                HttpResponse response = httpcliente.execute(httppost);
                
                HttpEntity entity = response.getEntity();
                
                inpStrin = entity.getContent();
                
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Nao foi possivel conectar com a internet!",Toast.LENGTH_SHORT).show();
                Log.e("DANIEL", "erro ao conectar" + e.toString() + ","
                        + nome);
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
                            public void run(){
                                Toast.makeText(EditarPerfilActivity.this,
                                        "Muito bem! Perfil completado com sucesso!",
                                        Toast.LENGTH_LONG).show();
                                enviarDadosParaPerfilLogado();
                            }
                        });

                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditarPerfilActivity.this,
                                        "Erro ao cadastrar Creche!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }


                }
               
            } catch (Exception e) {
                Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
            }
            return null;
        }
		
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int posicao, long arg3) {
		// TODO Auto-generated method stub
		displayView(posicao);
	}
	
	public void displayView(int position) {

		switch (position) {
		case 0:
			Intent i1 = new Intent(EditarPerfilActivity.this,
					TimeLineActivity.class);
			startActivity(i1);
			break;
		case 1:
			Intent i2 = new Intent(EditarPerfilActivity.this,
					ActivityMapa.class);
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
