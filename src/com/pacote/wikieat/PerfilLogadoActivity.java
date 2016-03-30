package com.pacote.wikieat;

import java.io.BufferedInputStream;
import android.widget.AdapterView.OnItemClickListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.pacote.wikieat.utils.Utils;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.entities.Profile.Properties;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Attributes;
import com.sromku.simple.fb.utils.PictureAttributes;
import com.sromku.simple.fb.utils.PictureAttributes.PictureType;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PerfilLogadoActivity extends FragmentActivity implements
		OnItemClickListener, PanelSlideListener {
	private SlidingPaneLayout mSlidingLayout;
	private ListView mList;
	private String url_imagem;
	ImageView imagem;
	String nome_cidade[];
	String nomeCidade;
	JSONObject jArray = null;
	String nome;
	String sobrenome;
	String email;
	String aniversario;
	String cidade;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_logado);

		// adicionando cor a action bar
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.rgb(221, 10, 10)));
		// fim adicionando cor a action bar

		// declarando a lista lateral
		mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
		mSlidingLayout.setPanelSlideListener(this);

		String[] opcoes = new String[] { "TIMELINE", "RESTAURANTES",
				"FAVORITOS", "AMIGOS"};
		
		mList = (ListView) findViewById(R.id.left_pane);
		mList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, opcoes));
		mList.setOnItemClickListener(this);
		// fim declarando a lista lateral

		// pegando os valores do facebook pegos na outra página
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}

		// get data via the key
		nome = extras.getString("nomeUsuario");
		sobrenome = extras.getString("sobrenomeUsuario");
		email = extras.getString("EmailUsuario");
		/*
		 * String id = extras.getString("id"); String biografia =
		 * extras.getString("biografia");
		 */
		aniversario = extras.getString("aniversario");
		cidade = extras.getString("cidade");
		url_imagem = extras.getString("url_imagem");
		Log.e("EMAIL", email);
		Log.e("NOME", nome);
		Log.e("SOBRENOME", sobrenome);
		
		try {
			jArray = new JSONObject(cidade);
			Log.e("DANIEL", "entrou aqui NOTIFICACAO");
			nome_cidade = new String[jArray.length()];

			nomeCidade = (String) jArray.get("name");

			TextView cidadeText = (TextView) findViewById(R.id.textViewCidade);
			cidadeText.setText(nomeCidade);
		} catch (JSONException e) {
			TextView cidadeText = (TextView) findViewById(R.id.textViewCidade);
			cidadeText.setText("Não foi");
			e.printStackTrace();
		}

		if (aniversario.equals("null")) {
			TextView aniversarioText = (TextView) findViewById(R.id.textViewNascimento);
			aniversarioText.setText("Editar perfil...");
			aniversarioText.setTextColor(Color.BLUE);
			aniversarioText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					enviarDadosParaEdicao();

				}
			});

		} else {
			TextView aniversarioText = (TextView) findViewById(R.id.textViewNascimento);
			aniversarioText.setText(aniversario);

		}
		
		if (nome.equals("null")) {
			TextView nomeText = (TextView) findViewById(R.id.textViewNomeUsu);
			nomeText.setText("Editar perfil...");
			nomeText.setTextColor(Color.BLUE);
			nomeText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					enviarDadosParaEdicao();

				}
			});

		} else {
			TextView nomeText = (TextView) findViewById(R.id.textViewNomeUsu);
			nomeText.setText(nome);

		}

		if ((nome.equals("null")) && (sobrenome.equals("null"))) {
			TextView nomeCompleto = (TextView) findViewById(R.id.textViewNomeCompleto);
			nomeCompleto.setText("Editar perfil...");
			nomeCompleto.setTextColor(Color.BLUE);
			nomeCompleto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					enviarDadosParaEdicao();

				}
			});

		} else {
			TextView nomeCompleto = (TextView) findViewById(R.id.textViewNomeCompleto);
			nomeCompleto.setText(nome + " " + sobrenome);
		}

		if (email.equals("null")) {
			TextView emailText = (TextView) findViewById(R.id.textViewEmail);
			emailText.setText("Editar perfil...");
			emailText.setTextColor(Color.BLUE);
			emailText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					enviarDadosParaEdicao();
				}
			});
		} else {
			TextView emailText = (TextView) findViewById(R.id.textViewEmail);
			emailText.setText(email);

		}

		imagem = (ImageView) findViewById(R.id.imageViewUsu);
		DownloadImage async = new DownloadImage();
		async.execute(url_imagem);

		

		// fim pegando os valores do facebook pegos na outra página

	}
	
	
	//enviando dados para edição de perfil
	public void enviarDadosParaEdicao(){
		Intent i = new Intent(PerfilLogadoActivity.this,
				EditarPerfilActivity.class);
		if(nome.equals("null")){
			
		}else{
			i.putExtra("nomeUsuario", nome);
		}
		
		if(url_imagem.equals("null")){
			
		}else{
			i.putExtra("url_imagem", String.valueOf(url_imagem));
		}
		if(sobrenome.equals("null")){
			
		}else{
			i.putExtra("sobrenomeUsuario", sobrenome);
		}
		
		if(nomeCidade.equals("null")){
			
		}else{
			i.putExtra("cidade", String.valueOf(nomeCidade));
		}
		
		if(email.equals("null")){
			
		}else{
			i.putExtra("EmailUsuario", String.valueOf(email));
		}
		
		if(aniversario.equals("null")){
			
		}
		else{
			i.putExtra("aniversario", String.valueOf(aniversario));
		}
		
		
		
		
		startActivity(i);
	}
	//fim enviando dados para edição de perfil

	// asyncTask para baixar a imagem de perfil do facebook
	public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

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

	// fim asyncTask para baixar a imagem de perfil do facebook

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
		final float roundPx = 12;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	// fim colocando borda arredondada na imagem do perfil do facebook

	// Evento de clique do botão PARA ABRIR O MENU LATERAL
	public void abrirMenu(View v) {
		// Se estive aberto, feche. Senão abra.
		if (mSlidingLayout.isOpen()) {
			mSlidingLayout.closePane();
		} else {
			mSlidingLayout.openPane();
		}
	}

	// FIM Evento de clique do botão PARA ABRIR O MENU LATERAL

	 

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
