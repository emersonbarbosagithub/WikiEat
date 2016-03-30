package com.pacote.wikieat.restaurantes;

import java.util.ArrayList;

import com.pacote.wikieat.R;
import com.pacote.wikieat.menulateral.AdapterListView;
import com.pacote.wikieat.menulateral.ItemListView;

import android.os.Bundle;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityRestaurantesLista extends FragmentActivity implements
OnItemClickListener, PanelSlideListener {
	private SlidingPaneLayout mSlidingLayout;
	private ListView mList;
	private AdapterListView adapterListView;
	private ArrayList<ItemListView> itens;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurantes_lista);
		
		addActionBar();
		addSlidePannel();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_restaurantes_lista, menu);
		return true;
	}
	
	public void addActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.rgb(221, 10, 10)));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

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

		itens.add(item1);
		itens.add(item2);
		itens.add(item3);
		itens.add(item4);

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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}


}
