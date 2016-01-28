package com.cqz.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initialWidgets();
    }

    private void initialWidgets() {
        Button btnListView= (Button) findViewById(R.id.btn_listview);
        btnListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        Button btnGridView= (Button) findViewById(R.id.btn_gridview);
        btnGridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,GridViewActivity.class);
                startActivity(intent);
            }
        });
        final Button btnRecyclerView= (Button) findViewById(R.id.btn_recyclerview);
        btnRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,RecyclerViewActivity.class);
                startActivity(intent);
            }
        });
    }
}
