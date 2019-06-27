package br.com.senac.weightcontrol;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.senac.weightcontrol.modelo.WeightControlApp;
import br.com.senac.weightcontrol.webservice.Api;
import br.com.senac.weightcontrol.webservice.RequestHandler;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST=1024;
    private static final int CODE_POST_REQUEST=1025;

    EditText editTextId;
    EditText editTextPeso;
    EditText editTextData;
    EditText editTextCirc;
    Button buttonSalvar;
    ProgressBar progressBar;
    ListView listview;
    List<WeightControlApp> weightappList;
    Boolean isUpdating = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar= findViewById(R.id.BarradeProgresso);
        listview=findViewById(R.id.ListViewListinha);
        weightappList= new ArrayList<>();

        editTextPeso = findViewById(R.id.editTextPeso);
        editTextId = findViewById(R.id.editTextId);
        editTextData = findViewById(R.id.editTextData);
        editTextCirc = findViewById(R.id.editTextCircunferencia);
        buttonSalvar = findViewById(R.id.buttonSalvar);

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUpdating){
                    updateWeightApp();
                }else{
                    createWeightApp();
                }
            }
        });
        readWeightApp();
    }
    private void createWeightApp(){
        String peso= editTextPeso.getText().toString().trim();
        String data= editTextData.getText().toString().trim();
        String circunferencia= editTextCirc.getText().toString().trim();

        if(TextUtils.isEmpty(peso)){
            editTextPeso.setError("Digite seu Peso");
            editTextPeso.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(data)){
            editTextData.setError("Digite a data da medição");
            editTextData.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(circunferencia)){
            editTextCirc.setError("Digite a Circunferência da sua gordura");
            editTextCirc.requestFocus();
            return;
        }

        HashMap<String,String> params = new HashMap<>();
        params.put("peso",peso);
        params.put("data",data);
        params.put("circunferencia",circunferencia);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_WEIGHTAPP,params,CODE_POST_REQUEST);
        request.execute();
    }
    private void updateWeightApp(){
        String id = editTextId.getText().toString();
        String peso= editTextPeso.getText().toString().trim();
        String data= editTextData.getText().toString().trim();
        String circunferencia= editTextCirc.getText().toString().trim();

        if(TextUtils.isEmpty(peso)){
            editTextPeso.setError("Digite seu Peso");
            editTextPeso.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(data)){
            editTextData.setError("Digite a data da medição");
            editTextData.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(circunferencia)){
            editTextCirc.setError("Digite a Cincunferência");
            editTextCirc.requestFocus();
            return;
        }
        HashMap<String,String> params = new HashMap<>();
        params.put("id",id);
        params.put("peso",peso);
        params.put("data",data);
        params.put("circunferencia",circunferencia);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_WEIGHTAPP,params,CODE_POST_REQUEST);
        request.execute();

        buttonSalvar.setText("Salvar");
        editTextPeso.setText("");
        editTextData.setText("");
        editTextCirc.setText("");

        isUpdating = false;
    }
    private void readWeightApp() {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_WEIGHTAPP,null, CODE_GET_REQUEST);
        request.execute();
    }

    private void deleteWeightApp(int id){
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_WEIGHTAPP + id,null, CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshWeightAppList(JSONArray weightapp)throws JSONException {
        weightappList.clear();

        for(int i = 0; i < weightapp.length(); i++){
            JSONObject obj = weightapp.getJSONObject(i);

            weightappList.add(new WeightControlApp(
                    obj.getInt("id"),
                    obj.getString("peso"),
                    obj.getString("data"),
                    obj.getString("circunferência")
            ));
        }

        WeightAppAdapter adapter = new WeightAppAdapter(weightappList);
        listview.setAdapter(adapter);
    }
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(MainActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshWeightAppList(object.getJSONArray("weightapp"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
    class WeightAppAdapter extends ArrayAdapter<WeightControlApp> {
        List<WeightControlApp> weightappList;

        public WeightAppAdapter(List<WeightControlApp> weightappList){
            super(MainActivity.this,R.layout.layout_weightcontroleapp_list, weightappList);

            this.weightappList = weightappList;
        }

        public View getView(int position, View converView, ViewGroup parent){
            LayoutInflater inflater = getLayoutInflater();
            final View listViewItem = inflater.inflate(R.layout.layout_weightcontroleapp_list,null,true);

            TextView textViewPeso = listViewItem.findViewById(R.id.textViewPeso);

            TextView textViewDelete = listViewItem.findViewById(R.id.textViewDelete);

            TextView textViewAlterar = listViewItem.findViewById(R.id.textViewAlterar);

            final WeightControlApp weightapp = weightappList.get(position);
            textViewPeso.setText(weightapp.getPeso());
            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Delete " + weightapp.getPeso())
                            .setMessage("Você quer realmente deletar?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteWeightApp(weightapp.getId());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }
            });
            textViewAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isUpdating = true;
                    editTextId.setText(String.valueOf(weightapp.getId()));
                    editTextPeso.setText(weightapp.getPeso());
                    editTextData.setText(weightapp.getData());
                    editTextCirc.setText(weightapp.getCirc());
                    buttonSalvar.setText("Alterar");

                }
            });
            return listViewItem;
        }

    }
}


