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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
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
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Attributes;
import com.sromku.simple.fb.utils.PictureAttributes;
import com.sromku.simple.fb.utils.PictureAttributes.PictureType;

public class PerfilLogadoActivity extends FragmentActivity implements
		OnItemClickListener, PanelSlideListener {
	private SimpleFacebook mSimpleFacebook;
	private SlidingPaneLayout mSlidingLayout;
	private ListView mList;
	private AdapterListView adapterListView;
	private ArrayList<ItemListView> itens;
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
	String cidadeNaoJson, idFacebook;
	String[] nomeArray, emailArray, aniversarioArray, cidadeArray,
			cidadeNaoJsonArray, idFacebookArray, url_imagemArray;
	ProgressDialog progressDialog;
	private static InputStream inpStrin;
	String result = "";
	Typeface font;
	TextView cidadeText, aniversarioText, nomeText, nomeCompleto, emailText;

	private OnLogoutListener mOnLogoutListener = new OnLogoutListener() {

		@Override
		public void onFail(String reason) {
			// mTextStatus.setText(reason);
			Log.w("logout", "Failed to login");
		}

		@Override
		public void onException(Throwable throwable) {
			// mTextStatus.setText("Exception: " + throwable.getMessage());
			Log.e("logout", "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {
			// show progress bar or something to the user while login is
			// happening
			// mTextStatus.setText("Thinking...");
		}

		@Override
		public void onLogout() {
			// change the state of the button or do whatever you want
			Toast.makeText(getApplicationContext(),
					"Logout efetuado com sucesso!", Toast.LENGTH_SHORT).show();
			Intent iLogout = new Intent(PerfilLogadoActivity.this,
					MainActivity.class);
			startActivity(iLogout);
		}

	};

	// listener for profile request
	private OnProfileListener onProfileListener = new OnProfileListener() {

		@Override
		public void onFail(String reason) {

			// insure that you are logged in before getting the profile
			Log.w("PerfilLogado", reason);
		}

		@Override
		public void onException(Throwable throwable) {

			Log.e("PerfilLogado", "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {

			// show progress bar or something to the user while fetching
			// profile
			Log.i("PerfilLogado", "Thinking...");
		}

		@Override
		public void onComplete(Profile profile) {

			Log.i("PerfilLogado", "PROFILE ID = " + profile.getId());
			String name = profile.getFirstName();
			String lastName = profile.getLastName();
			String emailPerf = profile.getEmail();
			String id = profile.getId();
			String aniversarioPerf = profile.getBirthday();
			String cidadePerf = profile.getHometown();
			String urlImagem = profile.getPicture();

			nome = name;
			sobrenome = lastName;
			email = emailPerf;
			aniversario = aniversarioPerf;
			cidade = cidadePerf;
			url_imagem = urlImagem;
			idFacebook = id;
			Log.i("DANIEL", "TESTE TRY PROFILE " + nome);
			Log.i("DANIEL", "TESTE TRY PROFILE " + sobrenome);
			Log.i("DANIEL", "TESTE TRY PROFILE " + email);
			Log.i("DANIEL", "TESTE TRY PROFILE " + aniversario);
			Log.i("DANIEL", "TESTE TRY PROFILE " + cidade);
			Log.i("DANIEL", "TESTE TRY PROFILE " + url_imagem);
			Log.i("DANIEL", "TESTE TRY PROFILE " + idFacebook);
			addElementosDoPerfilUsuario();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logado);
		TextView txt = (TextView) findViewById(R.id.textViewDados);
		TextView txtNome = (TextView) findViewById(R.id.textView1);
		TextView txtCidade = (TextView) findViewById(R.id.textView2);
		TextView txtEmail = (TextView) findViewById(R.id.textView3);
		TextView txtNascimento = (TextView) findViewById(R.id.textView4);
		font = Typeface.createFromAsset(getAssets(), "MUSEO.TTF");
		txt.setTypeface(font);
		txtNome.setTypeface(font);
		txtCidade.setTypeface(font);
		txtEmail.setTypeface(font);
		txtNascimento.setTypeface(font);

		// adicionando cor a action bar
		addActionBar();
		// fim adicionando cor a action bar
		// declarando a lista lateral
		addSlidePannel();
		// fim declarando a lista lateral
		// pegando os valores do facebook pegos na outra página
		// setInterfaceComOsDadosDeUsuario();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				PerfilLogadoActivity.this.setInterfaceComOsDadosDeUsuario();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logado, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_restaurantes:
			Intent i = new Intent(PerfilLogadoActivity.this, ActivityMapa.class);
			startActivity(i);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setInterfaceComOsDadosDeUsuario() {
		if (mSimpleFacebook.isLogin()) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					getPerfilUsuarioFacebook();

				}
			});

		} else {
			this.finish();
		}
	}

	private void getPerfilUsuarioFacebook() {

		PictureAttributes pictureAttributes = Attributes
				.createPictureAttributes();
		pictureAttributes.setHeight(500);
		pictureAttributes.setWidth(500);
		pictureAttributes.setType(PictureType.SQUARE);

		// prepare the properties that we need
		Profile.Properties properties = new Profile.Properties.Builder()
				.add(Profile.Properties.ID).add(Profile.Properties.FIRST_NAME)
				.add(Profile.Properties.LAST_NAME)
				.add(Profile.Properties.HOMETOWN).add(Profile.Properties.EMAIL)
				.add(Profile.Properties.BIRTHDAY).add(Profile.Properties.COVER)
				.add(Profile.Properties.PICTURE, pictureAttributes).build();
		mSimpleFacebook.getProfile(properties, onProfileListener);

	}

	public void addElementosDoPerfilUsuario() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Bundle extras = getIntent().getExtras();
				if (extras == null) {
					Log.e("EMAIL", email);
					Log.e("NOME", nome);
					Log.e("SOBRENOME", sobrenome);
					Log.e("ID DO FACEBOOK", idFacebook);
					Log.e("ENTROU NO ADD ELEMENTOS", "3");
					try {
						jArray = new JSONObject(cidade);
						Log.e("DANIEL", "entrou aqui NOTIFICACAO");
						nome_cidade = new String[jArray.length()];

						nomeCidade = (String) jArray.get("name");

						cidadeText = (TextView) findViewById(R.id.textViewCidade);
						cidadeText.setText(nomeCidade);
						Log.e("ENTROU NO ADD ELEMENTOS", "4");
					} catch (JSONException e) {
						cidadeText = (TextView) findViewById(R.id.textViewCidade);
						cidadeText.setText(cidade);
						e.printStackTrace();
						Log.e("ENTROU NO ADD ELEMENTOS", "5");
					}

					if (aniversario.equals("null")) {
						aniversarioText = (TextView) findViewById(R.id.textViewNascimento);
						aniversarioText.setText("Editar perfil...");
						aniversarioText.setTextColor(Color.BLUE);
						aniversarioText
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										enviarDadosParaEdicao();

									}
								});

					} else {
						aniversarioText = (TextView) findViewById(R.id.textViewNascimento);
						aniversarioText.setText(aniversario);

					}

					if (nome.equals("null")) {
						nomeText = (TextView) findViewById(R.id.textViewNomeUsu);
						nomeText.setText("Editar perfil...");
						nomeText.setTextColor(Color.BLUE);
						nomeText.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								enviarDadosParaEdicao();

							}
						});

					} else {
						nomeText = (TextView) findViewById(R.id.textViewNomeUsu);
						nomeText.setText(nome);
						nomeText.setTextSize(35.f);

					}

					if ((nome.equals("null")) && (sobrenome.equals("null"))) {
						nomeCompleto = (TextView) findViewById(R.id.textViewNomeCompleto);
						nomeCompleto.setText("Editar perfil...");
						nomeCompleto.setTextColor(Color.BLUE);
						nomeCompleto.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								enviarDadosParaEdicao();

							}
						});

					} else {
						nomeCompleto = (TextView) findViewById(R.id.textViewNomeCompleto);
						nomeCompleto.setText(nome + " " + sobrenome);
					}

					if (email.equals("null")) {
						emailText = (TextView) findViewById(R.id.textViewEmail);
						emailText.setText("Editar perfil...");
						emailText.setTextColor(Color.BLUE);
						emailText.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								enviarDadosParaEdicao();
							}
						});
					} else {
						emailText = (TextView) findViewById(R.id.textViewEmail);
						emailText.setText(email);

					}

					imagem = (ImageView) findViewById(R.id.imageViewUsu);
					DownloadImage async = new DownloadImage();
					async.execute(url_imagem);

					new CarregarDados().execute();
				}
			}
		});

		Log.e("ENTROU NO ADD ELEMENTOS", "2");
		// get data via the key
		/*
		 * nome = extras.getString("nomeUsuario"); sobrenome =
		 * extras.getString("sobrenomeUsuario"); email =
		 * extras.getString("EmailUsuario");
		 */
		/*
		 * String id = extras.getString("id"); String biografia =
		 * extras.getString("biografia");
		 */
		/*
		 * aniversario = extras.getString("aniversario"); cidade =
		 * extras.getString("cidade"); cidadeNaoJson =
		 * extras.getString("cidade"); url_imagem =
		 * extras.getString("url_imagem"); idFacebook =
		 * extras.getString("idFacebook");
		 */

	}

	private class CarregarDados extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// progressDialog =
			// ProgressDialog.show(PerfilLogadoActivity.this,"Aguarde...",
			// "Carregando dados...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			carregaUsuario(idFacebook);

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaUsuario(String id_facebook) {

			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			parame.add(new BasicNameValuePair("id_facebook", id_facebook));

			try {
				Log.i("DANIEL", "TESTE TRY1 " + id_facebook);
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/loadUsuarioFacebookCadastrado.php");

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
				nomeArray = new String[jArray.length()];

				emailArray = new String[jArray.length()];
				aniversarioArray = new String[jArray.length()];
				cidadeArray = new String[jArray.length()];
				url_imagemArray = new String[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {
					Log.e("DANIEL", "entrou aqui CIDADE 1");
					JSONObject json_data = jArray.getJSONObject(i);
					Log.e("DANIEL", "entrou aqui CIDADE 2");
					nomeArray[i] = json_data.getString("nome");
					Log.e("DANIEL", "entrou aqui CIDADE 3");
					emailArray[i] = json_data.getString("email");
					Log.e("DANIEL", "entrou aqui CIDADE 4");
					aniversarioArray[i] = json_data
							.getString("data_nascimento");
					Log.e("DANIEL", "entrou aqui CIDADE 5");
					cidadeArray[i] = json_data.getString("cidade");
					Log.e("DANIEL", "entrou aqui CIDADE 6");
					url_imagemArray[i] = json_data.getString("foto_usuario");
					Log.e("DANIEL", "entrou aqui CIDADE 7");

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Log.e("DANIEL", "entrou aqui CIDADE 8");
							cidadeText.setText(cidadeArray[0]);
							cidadeText.setTextColor(Color.BLACK);
							cidadeText.setClickable(false);
							nomeText.setText(nomeArray[0]);

							nomeText.setClickable(false);
							nomeCompleto.setText(nomeArray[0]);
							nomeCompleto.setTextColor(Color.BLACK);
							nomeCompleto.setClickable(false);
							emailText.setText(emailArray[0]);
							emailText.setTextColor(Color.BLACK);
							emailText.setClickable(false);
							aniversarioText.setText(aniversarioArray[0]);
							aniversarioText.setTextColor(Color.BLACK);
							aniversarioText.setClickable(false);
							cidadeText.setTypeface(font);
							nomeText.setTypeface(font);
							nomeCompleto.setTypeface(font);
							emailText.setTypeface(font);
							aniversarioText.setTypeface(font);

							DownloadImage async = new DownloadImage();
							async.execute(url_imagemArray[0]);
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

	public void addActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.rgb(221, 10, 10)));
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		LayoutInflater inflator = LayoutInflater.from(this);
		View v = inflator.inflate(R.layout.titleview, null);

		// if you need to customize anything else about the text, do it here.
		// I'm using a custom TextView with a custom font in my layout xml so
		// all I need to do is set title
		((TextView) v.findViewById(R.id.title)).setText(this.getTitle());

		// assign the view to the actionbar
		actionBar.setCustomView(v);

	}

	public void addSlidePannel() {
		mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
		mSlidingLayout.setPanelSlideListener(this);
		mList = (ListView) findViewById(R.id.left_pane);
		mList.setOnItemClickListener(this);
		createListViewLateral();
	}

	private void createListViewLateral() {
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		displayView(position);
	}

	public void displayView(int position) {

		switch (position) {
		case 0:
			Intent i1 = new Intent(PerfilLogadoActivity.this,
					TimeLineActivity.class);
			startActivity(i1);
			break;
		case 1:
			Intent i2 = new Intent(PerfilLogadoActivity.this,
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

		case 6:

			break;

		default:
			break;
		}
	}

	// enviando dados para edição de perfil
	public void enviarDadosParaEdicao() {
		Intent i = new Intent(PerfilLogadoActivity.this,
				EditarPerfilActivity.class);
		if (nome.equals("null")) {
			i.putExtra("nomeUsuario", "");
		} else {
			i.putExtra("nomeUsuario", nome);
		}

		if (url_imagem.equals("null")) {
			i.putExtra("url_imagem", "");
		} else {
			i.putExtra("url_imagem", String.valueOf(url_imagem));
		}
		if (sobrenome.equals("null")) {
			i.putExtra("sobrenomeUsuario", "");
		} else {
			i.putExtra("sobrenomeUsuario", sobrenome);
		}

		if (nomeCidade.equals("null")) {

		} else {
			i.putExtra("cidade", String.valueOf(nomeCidade));
		}

		if (email.equals("null")) {
			i.putExtra("EmailUsuario", "");
		} else {
			i.putExtra("EmailUsuario", String.valueOf(email));
		}

		if (aniversario.equals("null")) {
			i.putExtra("aniversario", "");
		} else {
			i.putExtra("aniversario", String.valueOf(aniversario));
		}

		if (idFacebook.equals("null")) {
			i.putExtra("idFacebook", "");
		} else {
			i.putExtra("idFacebook", String.valueOf(idFacebook));
		}
		startActivity(i);
	}

	// fim enviando dados para edição de perfil

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
		final float roundPx = 100;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	// fim colocando borda arredondada na imagem do perfil do facebook

}
