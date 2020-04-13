package com.example.carrentaltask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ProgressBar mainProgress, progressBar;
    LinearLayout mainLL;
    TextView itemName, itemDes, tvTotalPrice;
    Spinner carSpinner, carItemSpinner;
    ImageView ivItem;
    EditText etQty;
    CheckBox nav, sunRoof,camera;
    Button btnBuy;
    double totalPrice=0,itemPrice=0;
    //list
    List<String> foodCategories = new ArrayList<String>();
    List<String> foodName= new ArrayList<String>();
    List<MainModel> mainModels = new ArrayList<>();
    List<InnerModel> innerModels = new ArrayList<>();
    int qty=0,sr=0,bc=0,nvg=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getDataFromApi("https://api.jsonbin.io/b/5e940ea6b08d064dc025ee29");
        clicks();
    }


    private void init() {
        mainProgress = findViewById(R.id.mainProgress);
        progressBar = findViewById(R.id.progressBar);
        mainLL = findViewById(R.id.mainLL);
        itemName = findViewById(R.id.itemName);
        itemDes = findViewById(R.id.itemDes);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        carSpinner = findViewById(R.id.carSpinner);
        carItemSpinner = findViewById(R.id.carItemSpinner);
        ivItem = findViewById(R.id.ivItem);
        etQty = findViewById(R.id.etQty);
        camera = findViewById(R.id.camera);
        nav = findViewById(R.id.nav);
        sunRoof = findViewById(R.id.sunRoof);
        btnBuy = findViewById(R.id.btnBuy);
    }
    private void  getDataFromApi(String url){
        StringRequest request=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mainProgress.setVisibility(View.GONE);
                try {
                    foodCategories.add("Select Brand");
                    JSONObject object=new JSONObject(response);
                    JSONArray array=object.getJSONArray("data");
                    for(int i=0;i<array.length();i++){
                        JSONObject data=(JSONObject)array.get(i);
                        String brand=data.getString("brand");
                        foodCategories.add(brand);
                        MainModel mainModel=new MainModel(brand,data.getJSONArray("list"));
                        mainModels.add(mainModel);
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, foodCategories);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    carSpinner.setAdapter(dataAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(request);
    }
    private void clicks(){
        carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    showToast("Please Select Car Brand");
                }else{
                    try {
                        loadNewData(mainModels.get(i-1).getArray());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        carItemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    mainLL.setVisibility(View.GONE);
                }else{
                    mainLL.setVisibility(View.VISIBLE);
                    String text = carSpinner.getSelectedItem().toString();
                    itemName.setText(text +" "+innerModels.get(i-1).getName());
                    itemDes.setText("This "+itemName.getText().toString() +" traveled near "+innerModels.get(i-1).getKm()+"KM.\n" +
                            ""+"We charge $"+innerModels.get(i-1).getPrice()+" for 250KM.If you want travel more 250Km then $"+innerModels.get(i-1).getPerKm() +" Per 5 Km.");
                    Glide.with(MainActivity.this)
                            .load(innerModels.get(i-1).getImg())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(ivItem);
                    itemPrice=innerModels.get(i-1).getPrice();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etQty.getText().toString().equals("")){
                    qty=0;
                }else{
                    qty=Integer.parseInt(etQty.getText().toString().trim());
                    if(camera.isChecked()){
                        bc=1;
                    }else{
                        bc=0;
                    }
                    if(nav.isChecked()){
                        nvg=1;
                    }else{
                        nvg=0;
                    }
                    if(sunRoof.isChecked()){
                        sr=1;
                    }else {
                        sr=0;
                    }
                    filterData();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        camera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    bc=1;
                }else{
                    bc=0;
                }
                filterData();
            }
        });
        nav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    nvg=1;
                }else{
                    nvg=0;
                }
                filterData();
            }
        });
        sunRoof.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sr=1;
                }else{
                    sr=0;
                }
                filterData();
            }
        });
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(qty==0){
                    showToast("Enter valid time period");
                }else{
                    carItemSpinner.setSelection(0);
                    showToast("Booking successfully register");
                }
            }
        });
    }

    private void filterData() {
        totalPrice=0;
        totalPrice=itemPrice;
        if(bc==1){
            totalPrice=totalPrice+3;
        }
        if(nvg==1){
            totalPrice=totalPrice+8;
        }
        if(sr==1){
            totalPrice=totalPrice+18;
        }
        if(qty>2){
            tvTotalPrice.setText("Total Bill  $"+Math.round(totalPrice+200)+" include all specification.In Total amount we add $200 due to security amount. 200$ will refund after complete th ride.");
        }else{
            tvTotalPrice.setText("Total Bill  $"+Math.round(totalPrice)+" include all specification");
        }

    }

    private void loadNewData(JSONArray array) throws JSONException{
        foodName.clear();
        foodName.add("Select");
        innerModels.clear();
        for(int j=0;j<array.length();j++){
            JSONObject object=(JSONObject)array.get(j);
            String f= object.getString("name");
            foodName.add(f);
            InnerModel innerModel=new InnerModel();
            innerModel.setName(f);
            innerModel.setImg(object.getString("img"));
            innerModel.setKm(object.getString("km"));
            innerModel.setNoPlate(object.getString("img"));
            innerModel.setPerKm(object.getDouble("perKm"));
            innerModel.setPrice(object.getDouble("price"));
            innerModels.add(innerModel);

        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, foodName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carItemSpinner.setAdapter(dataAdapter);
    }

    private void showToast(String toast){
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
