package com.example.entregageral;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class CardListAdapter extends ArrayAdapter<CardItem> {

    private Context context;
    private List<CardItem> cardItemList;

    public CardListAdapter(Context context, List<CardItem> cardItemList) {
        super(context, R.layout.activity_card, cardItemList);
        this.context = context;
        this.cardItemList = cardItemList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_card, null);

        TextView textViewId = view.findViewById(R.id.textViewId);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        TextView textViewStartDate = view.findViewById(R.id.textViewStartData);
        TextView textViewEndDate = view.findViewById(R.id.textViewEndData);

        final CardItem cardItem = cardItemList.get(position);

        textViewId.setText(cardItem.getId());
        textViewTitle.setText(cardItem.getTitle());
        textViewDescription.setText(cardItem.getDescription());
        textViewStartDate.setText(cardItem.getStartDate());
        textViewEndDate.setText(cardItem.getEndDate());

        // Adiciona um OnClickListener para o clique no item da lista
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria uma Intent para a atividade EditTodoList
                Intent editIntent = new Intent(context, EditTodoList.class);
                // Adiciona os dados do cartão como extras na Intent
                editIntent.putExtra("card_id", cardItem.getId());
                editIntent.putExtra("title", cardItem.getTitle());
                editIntent.putExtra("description", cardItem.getDescription());
                editIntent.putExtra("start_date", cardItem.getStartDate());
                editIntent.putExtra("end_date", cardItem.getEndDate());
                // Inicia a atividade
                context.startActivity(editIntent);
                Intent deleteIntent = new Intent(context, DeleteCard.class);
                editIntent.putExtra("card_id", cardItem.getId());
            }
        });

        return view;
    }
}
