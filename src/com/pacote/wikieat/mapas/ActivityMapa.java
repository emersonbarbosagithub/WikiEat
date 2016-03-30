package com.pacote.wikieat.mapas;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pacote.wikieat.R;
import com.pacote.wikieat.restaurantes.RestauranteActivity;
import com.sromku.simple.fb.SimpleFacebook;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class ActivityMapa extends FragmentActivity implements LocationListener,
		OnNavigationListener {
	private SimpleFacebook mSimpleFacebook;
	GoogleMap googleMap;
	LocationManager locationManager;
	MarkerOptions options;
	String provider;
	LatLng latLng;
	double latitude;
	double longitude;
	private static InputStream inpStrin;
	String result = "";
	Double[] latit;
	Double[] longit;
	String[] nome;
	String[] endereco;
	String[] id;
	String ids;
	String nomes;
	String enderecos;
	LatLng local;
	ProgressDialog progressDialog;
	JSONArray jArray;
	int i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_mapa);

		addActionBar();

		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else {

			googleMap = ((SupportMapFragment) (getSupportFragmentManager()
					.findFragmentById(R.id.map))).getMap();

			googleMap.setMyLocationEnabled(true);
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, true);
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				onLocationChanged(location);
			}
			locationManager.requestLocationUpdates(provider, 40000, 0, this);
		}

		new CarregarRestaurantes().execute();
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

	public void addActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.rgb(221, 10, 10)));

		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.tag_1),
								getString(R.string.tag_2),
								getString(R.string.tag_3),
								getString(R.string.tag_4),
								getString(R.string.tag_5),
								getString(R.string.tag_6),
								getString(R.string.tag_7),
								getString(R.string.tag_8),
								getString(R.string.tag_9),
								getString(R.string.tag_10),
								getString(R.string.tag_11),
								getString(R.string.tag_12), }), this);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case android.R.id.home:
			ActivityMapa.this.finish();

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_mapa, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		LatLng latLng = new LatLng(latitude, longitude);

		// mostrar a localização atual no mapa
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom no mapa
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// TODO Auto-generated method stub
		/*
		 * if(position == 0){ googleMap.clear(); new
		 * CarregarRestaurantes().execute(); }
		 */
		if (position == 1) {
			googleMap.clear();
			new CarregarRestaurantesCarnes().execute();
		}
		if (position == 2) {
			googleMap.clear();
			new CarregarRestaurantesMassas().execute();
		}
		if (position == 3) {
			googleMap.clear();
			new CarregarRestaurantesPizza().execute();
		}

		if (position == 4) {
			googleMap.clear();
			new CarregarRestaurantesSushi().execute();
		}
		if (position == 5) {
			googleMap.clear();
			new CarregarRestaurantesFrutosDoMar().execute();
		}
		if (position == 6) {
			googleMap.clear();
			new CarregarRestaurantesBares().execute();
		}
		if (position == 7) {
			googleMap.clear();
			new CarregarRestaurantesCafe().execute();
		}
		if (position == 8) {
			googleMap.clear();
			new CarregarRestaurantesLanchoneteSorveteria().execute();
		}
		if (position == 9) {
			googleMap.clear();
			new CarregarRestaurantesVegetariano().execute();
		}
		if (position == 10) {
			googleMap.clear();
			new CarregarRestaurantesVinhos().execute();
		}
		if (position == 11) {
			googleMap.clear();
			new CarregarRestaurantesItalianos().execute();
		}
		return false;
	}

	private class CarregarRestaurantes extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesCarnes extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_carnes.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesMassas extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_massas.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesPizza extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_pizzarias.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesSushi extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_sushi.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesFrutosDoMar extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_frutosdomar.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);

							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesBares extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_bares.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesCafe extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_cafe.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);

							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesLanchoneteSorveteria extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_lanchonetesorveteria.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesVegetariano extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_vegetariano.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesVinhos extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_vinhos.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

	private class CarregarRestaurantesItalianos extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ActivityMapa.this,
					"Aguarde...", "Carregando os restaurantes...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("DANIEL", "TESTE TRY1 ");
			carregaRestaurante();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void carregaRestaurante() {
			ArrayList<NameValuePair> parame = new ArrayList<NameValuePair>();

			try {
				Log.i("DANIEL", "TESTE TRY1 ");
				HttpClient httpcliente = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://wikieat.com.br/wikieat/load_italianos.php");

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
				jArray = new JSONArray(result);
				Log.e("DANIEL", "entrou aqui NOTIFICACAO");
				latit = new Double[jArray.length()];
				longit = new Double[jArray.length()];
				nome = new String[jArray.length()];
				endereco = new String[jArray.length()];
				id = new String[jArray.length()];
				// final String ids2 = "";

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (i = 0; i < jArray.length(); i++) {

							try {
								JSONObject json_data = jArray.getJSONObject(i);
								latit[i] = Double.valueOf(json_data
										.getString("latitude"));
								longit[i] = Double.valueOf(json_data
										.getString("longitude"));
								nome[i] = json_data.getString("nome");
								endereco[i] = json_data.getString("endereco");
								id[i] = json_data.getString("id");
								nomes = nome[i];
								ids = id[i];
								enderecos = endereco[i];
								local = new LatLng(latit[i], longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ latit[i] + longit[i]);
								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ nome[i]);
								options = new MarkerOptions()
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.map_pin))
										.title(ids + " - " + nomes)
										.snippet(String.valueOf(enderecos))
										.position(local);

								Log.e("DANIEL", "PASSOU NO FOR ENTROU NO RUN "
										+ i);
								googleMap.addMarker(options);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e("DANIEL", "NumberFormatException " + e);
								e.printStackTrace();
							} catch (JSONException e) {
								Log.e("DANIEL", "JSONException " + e);
								e.printStackTrace();
							}

						}
					}
				});

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						googleMap
								.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

									@Override
									public void onInfoWindowClick(Marker marker) {
										String titulo = marker.getTitle();
										String split[] = titulo.split("-");

										Intent intent = new Intent(
												ActivityMapa.this,
												RestauranteActivity.class);
										intent.putExtra("id", split[0]);

										startActivity(intent);
									}
								});

					}
				});

			} catch (Exception e) {
				Log.e("DANIEL", "Erro ao popular os dados" + e.toString());
			}

		}

	}

}
