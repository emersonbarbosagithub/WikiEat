package com.pacote.wikieat.cardapio;



import java.util.ArrayList;

import com.pacote.wikieat.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterListViewCardapio extends BaseAdapter{
    
	private LayoutInflater mInflater;
    private ArrayList<ItemListViewCardapio> itens;

    public AdapterListViewCardapio(Context context, ArrayList<ItemListViewCardapio> itens)
    {
        //Itens que preencheram o listview
        this.itens = itens;
        //responsavel por pegar o Layout do item.
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Retorna a quantidade de itens
     *
     * @return
     */
    public int getCount()
    {
        return itens.size();
    }

    /**
     * Retorna o item de acordo com a posicao dele na tela.
     *
     * @param position
     * @return
     */
    public ItemListViewCardapio getItem(int position)
    {
        return itens.get(position);
    }

    /**
     * Sem implementação
     *
     * @param position
     * @return
     */
    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        //Pega o item de acordo com a posção.
    	ItemListViewCardapio item = itens.get(position);
        //infla o layout para podermos preencher os dados
        view = mInflater.inflate(R.layout.item_cardapio, null);

        //atravez do layout pego pelo LayoutInflater, pegamos cada id relacionado
        //ao item e definimos as informações.
        ((TextView) view.findViewById(R.id.textViewNomePrato)).setText(item.getNome());
        ((TextView) view.findViewById(R.id.textViewDescricaoPrato)).setText(item.getDescricao());
        ((TextView) view.findViewById(R.id.textViewPreco)).setText(item.getPreco());
        ((TextView) view.findViewById(R.id.textViewId)).setText(item.getId());
        ((ImageView) view.findViewById(R.id.imageViewFotoPrato)).setImageBitmap(getRoundedCornerBitmap(getResizedBitmap(
        		item.getUrlImagem(), 150, 150)));

        return view;
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
}