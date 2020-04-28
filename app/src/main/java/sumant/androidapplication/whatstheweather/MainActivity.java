package sumant.androidapplication.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    public static int LOGLEVEL = 2;
    public static boolean ERROR = LOGLEVEL > 0;
    public static boolean WARN = LOGLEVEL > 1;
    public static boolean INFO = LOGLEVEL > 2;

    EditText edittxt;
    TextView textView1;

    public void getWeather(View view) {
        if (INFO) Log.i("Clicked:", "Search for city weather condition started");
        try {
            Drawable task = new Drawable();
            String encodeCityName = URLEncoder.encode(edittxt.getText().toString(), "UTF-8");  // Get the city name from the edittext box in UTF-8 format
            //task.execute("https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=439d4b804bc8187953eb36d2a8c26a02");
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodeCityName +"&appid=439d4b804bc8187953eb36d2a8c26a02");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(edittxt.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Drawable extends AsyncTask<String, Void, String> {

        String result = "";
        URL url = null;
        HttpURLConnection urlConnection = null;

        @Override
        protected String doInBackground(String... urls) {
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Could not find weather", Toast.LENGTH_SHORT).show();
                return "Failed";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatehrInfo = null;
                weatehrInfo = jsonObject.getString("weather");
                if (INFO) Log.i("weather content:", weatehrInfo);
                JSONArray arr = new JSONArray(weatehrInfo);
                String message = "";
                for (int i=0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    if (INFO) Log.i("main:", jsonPart.getString("main"));
                    if (INFO) Log.i("decsription:", jsonPart.getString("description"));
                    if (!main.isEmpty() && !description.isEmpty()) {
                        message += main + ": " + description;
                    }
                }

                if (!message.isEmpty()) {
                    textView1.setText(message);
                } else {
                    Toast.makeText(MainActivity.this, "Could not find weather", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (INFO) Log.i("onCreate:", "Start of application");

        edittxt = findViewById(R.id.editText);
        textView1 = findViewById(R.id.textView1);
    }
}
