package mmconsultoria.co.mz.mbelamova.service;

import mmconsultoria.co.mz.mbelamova.Common.Common;
import mmconsultoria.co.mz.mbelamova.R;
import mmconsultoria.co.mz.mbelamova.model.BaseActivity;
import mmconsultoria.co.mz.mbelamova.model.Controller;
import mmconsultoria.co.mz.mbelamova.model.IGoogleAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

public class CustommerCall extends BaseActivity {

    private TextView textAdress;
    private TextView textDistance;
    private TextView textTime;
    private MediaPlayer mediaPlayer;
    private String TAG="CustommerCall";
    private IGoogleAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommer_call);

        textAdress= findViewById(R.id.textAddress);
        textDistance=findViewById(R.id.textDistance);
        textTime=findViewById(R.id.textTime);
        mService=Common.geIGoogleAPI();

       mediaPlayer= MediaPlayer.create(this,R.raw.s);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        if (getIntent()!=null){
            double lat=getIntent().getDoubleExtra("lat",-1.0);
            double lng=getIntent().getDoubleExtra("lng",-1.0);
            getDirection(lat,lng);
        }

    }

    private void getDirection(double lat, double lng) {
        {

            String requestApi = null;
            try {
                LatLng start = Controller.getInstance().getStart();
                String destination = Controller.getInstance().getDestinationName();

                Log.d(TAG, "Destination: " + destination + "Start: " + start);
                requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "mode=driving&" +
                        "transit_routing_preference=less_driving&" +
                        "origin=" + start.latitude + "," + start.longitude + "&" +
                        "destination=" + destination + "&" +
                        "key=" + getResources().getString(R.string.google_direction_api);
                Log.d(TAG, "getDirection: " + requestApi);// Print Url for Debug
                mService.getPath(requestApi).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());

                            JSONArray routes=jsonObject.getJSONArray("routes");

                            JSONObject object=routes.getJSONObject(0);

                            JSONArray legs=object.getJSONArray("legs");

                            JSONObject legsObject=legs.getJSONObject(0);

                            JSONObject distance=legsObject.getJSONObject("distance");

                            textDistance.setText(distance.getString("text"));
                            JSONObject time=legsObject.getJSONObject("duration");
                            textTime.setText(time.getString("text"));

                            String address=legsObject.getString("end_address");
                            textTime.setText(address);
//                        tripDistance = Double.parseDouble(legs_info.getJSONObject("distance").getString("text"));

//                      //ProgresDialog
//                        progressDialog = ProgressDialog.show(ClientMapActivity.this, "Por favor Aguarde.",
//                                "Processando a Rota.", true,true);
//                        requestApi.build();
//                        routing.execute();

//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject route = jsonArray.getJSONObject(i);
//                                JSONObject poly = route.getJSONObject("overview_polyline");
//                                String polyline = poly.getString("points");
//                                polyLineList = decodePoly(polyline);
//
//                                //Adjusting Bounds
//
//                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                                for (LatLng latLng : polyLineList)
//                                    builder.include(latLng);
//                                LatLngBounds bounds = builder.build();
//                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
//                                mMap.animateCamera(mCameraUpdate);
//
//                                polylineOptions = new PolylineOptions();
//                                polylineOptions.color(R.color.rotaTracandoFundo);
//                                polylineOptions.width(8);
//                                polylineOptions.startCap(new SquareCap());
//                                polylineOptions.endCap(new SquareCap());
//                                polylineOptions.jointType(JointType.ROUND);
//                                polylineOptions.addAll(polyLineList);
//                                greyPolyline = mMap.addPolyline(polylineOptions);
//
//                                blackPolylineOptions = new PolylineOptions();
//                                blackPolylineOptions.color(R.color.rotaTracandoSuperficie);
//                                blackPolylineOptions.width(8);
//                                blackPolylineOptions.startCap(new SquareCap());
//                                blackPolylineOptions.endCap(new SquareCap());
//                                blackPolylineOptions.jointType(JointType.ROUND);
//                                blackPolyline = mMap.addPolyline(blackPolylineOptions);
//
//                                mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Ponto de Busca").icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
//                                showNotification(ClientMapActivity.this, "Mbela Mova ", "Procurando motorista ");
//
//                                mMap.addMarker(new MarkerOptions()
//                                        .position(polyLineList.get(polyLineList.size() - 1))
//                                        .title("Destino: " + Controller.getInstance().getDestinationName() + " Duracao: " + duracao + " Distancia: " + distancia).icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker)));
//                                //pickup Location
//
//                                //Animacao
//                                ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
//                                polylineAnimator.setDuration(2000);
//                                polylineAnimator.setInterpolator(new LinearInterpolator());
//                                polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                    @Override
//                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                        List<LatLng> points = greyPolyline.getPoints();
//                                        int percentValue = (int) valueAnimator.getAnimatedValue();
//                                        int size = points.size();
//                                        int newPoits = (int) (size * (percentValue / 100.0f));
//                                        List<LatLng> p = points.subList(0, newPoits);
//                                        blackPolyline.setPoints(p);
//
//
//                                    }
//                                });
//                                polylineAnimator.start();
//
//
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Toast.makeText(CustommerCall.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {

            }


        }

    }

    @Override
    protected void onStop() {

        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mediaPlayer.start();
        super.onResume();
    }
}
