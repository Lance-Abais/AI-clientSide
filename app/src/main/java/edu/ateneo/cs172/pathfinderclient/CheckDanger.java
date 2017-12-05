package edu.ateneo.cs172.pathfinderclient;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.google.maps.android.PolyUtil.isLocationOnPath;

/**
 * Created by ljda0 on 29/11/2017.
 */

public class CheckDanger extends AsyncTask<List<LatLng>,Void, List<LatLng>> {

    private GoogleMap mMap;
    private List<LatLng> mnearDanger;

    public CheckDanger(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public List<LatLng> getMnearDanger() {
        return mnearDanger;
    }

    public void setMnearDanger(List<LatLng> mnearDanger) {
        this.mnearDanger = mnearDanger;
    }

    @Override
    protected List<LatLng> doInBackground(List<LatLng>... latlngs) {


        List<LatLng> dangerAreas = new ArrayList<LatLng>();
        dangerAreas = latLongParser();

        List<LatLng> nearUserDanger = new ArrayList<LatLng>();

        for(int i=0; i<dangerAreas.size(); i++) {

            if(isLocationOnPath(dangerAreas.get(i),latlngs[0],false,10.0d))
            {
                nearUserDanger.add(dangerAreas.get(i));
            }
        }
        Log.e("LatLng", Double.toString(nearUserDanger.get(0).latitude)+"|"+Double.toString(nearUserDanger.get(0).longitude));

        return nearUserDanger;
    }

    @Override
    protected void onPostExecute(List<LatLng> latLngs) {

       if(latLngs.size()>0) for(int i = 0; i<latLngs.size(); i++) drawCircle(latLngs.get(i));



        super.onPostExecute(latLngs);
    }

    private List<LatLng> latLongParser(){

        String url = "https://local.localtunnel.me/v1/directions/getDangerAreas";
        String response = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        List<LatLng> lstLatLng = new ArrayList<LatLng>();

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            response = EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONArray resultLatLng = new JSONArray(response);
            for(int i = 0; i<resultLatLng.length(); i++){
                JSONObject curr = resultLatLng.getJSONObject(i);
                double latD = Double.parseDouble(curr.getString("lat"));
                double lngD = Double.parseDouble(curr.getString("long"));
                lstLatLng.add(new LatLng(latD,lngD));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setMnearDanger(lstLatLng);
        return lstLatLng;
    };

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(20);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }


}
