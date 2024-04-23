package com.example.entregageral;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

public class Card extends LinearLayout {

    private ImageView imageViewEdit;
    private ImageView imageViewDelete;
    private boolean editClicked = false;
    private boolean deleteClicked = false;

    public Card(Context context) {
        super(context);
        init();
    }

    public Card(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Card(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.activity_card, this, true);
        imageViewEdit = findViewById(R.id.imageViewEdit);
        imageViewDelete = findViewById(R.id.imageViewDelete);

        // Adicionando funcionalidade de clique para imageViewEdit
        imageViewEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alterna a cor de fundo do imageViewEdit após o clique
                if (editClicked) {
                    imageViewEdit.setColorFilter(null); // Remove o filtro de cor
                } else {
                    imageViewEdit.setColorFilter(Color.MAGENTA); // Define a cor roxa
                }
                editClicked = !editClicked; // Inverte o estado do clique
            }
        });

        // Adicionando funcionalidade de clique para imageViewDelete
        imageViewDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alterna a cor de fundo do imageViewDelete após o clique
                if (deleteClicked) {
                    imageViewDelete.setColorFilter(null); // Remove o filtro de cor
                } else {
                    imageViewDelete.setColorFilter(Color.MAGENTA); // Define a cor roxa
                }
                deleteClicked = !deleteClicked; // Inverte o estado do clique
            }
        });
    }
}
