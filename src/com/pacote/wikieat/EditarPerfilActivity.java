package com.pacote.wikieat;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.pacote.wikieat.PerfilLogadoActivity.DownloadImage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class EditarPerfilActivity extends FragmentActivity implements
OnItemClickListener, PanelSlideListener {
	ImageView imagem;
	private String url_imagem;
	private SlidingPaneLayout mSlidingLayout;
	private ListView mList;
	String nome;
	String sobrenome;
	String cidade;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar_perfil);
		
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.rgb(221, 10, 10)));
		
		mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
		mSlidingLayout.setPanelSlideListener(this);

		String[] opcoes = new String[] { "TIMELINE", "RESTAURANTES",
				"FAVORITOS", "AMIGOS"};
		
		mList = (ListView) findViewById(R.id.left_pane);
		mList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, opcoes));
		mList.setOnItemClickListener(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}
		
		
		// get data via the key
		nome = extras.getString("nomeUsuario");
		url_imagem = extras.getString("url_imagem");
		sobrenome = extras.getString("sobrenomeUsuario");
		cidade = extras.getString("cidade");
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
