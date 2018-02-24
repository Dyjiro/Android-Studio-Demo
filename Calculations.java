/**************************

 Calcutions.java

 Kory Staab

 Uses GPS speed to calculate time dilation

 ***************************/
package k_Staab.timedilation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Main5Activity extends AppCompatActivity implements LocationListener
{

    TextView timeD;     //Displays Time dialated object
    TextView speed;     //Displays speed of phone relative to GPS updates object
    LocationManager lc;
    BigDecimal timeDilated;
    Handler handler;
    double gpsSpeedVal;     //speed of phone relative to GPS

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        handler = new Handler();

        AdView adView = (AdView) this.findViewById(R.id.adView);    //Load ad on top of page
        AdRequest adRequest = new AdRequest.Builder().build();
        if (adView != null)
        {
            adView.loadAd(adRequest);
        }

        timeD = (TextView) findViewById(R.id.gpsSpeed);     //initialize text views to
        speed = (TextView) findViewById(R.id.speed);

        lc = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)     //check if permissions were granted for GPS locations
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);    //if they werent, request them
        }
        else
        {
            lc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);    //starts the GPS
        }

    }

    @Override
    public void onLocationChanged(Location location)
    {
        gpsSpeedVal = location.getSpeed();  //class call to get the speed of the phone
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {     //delay value updates for integration

                //calculates time dilated using formula
                double v = gpsSpeedVal / 1000;
                BigDecimal c = new BigDecimal(v/299792d);
                double changeInTime = .00000277778;
                BigDecimal s = c.multiply(c);
                BigDecimal e = new BigDecimal(1);
                BigDecimal a = e.subtract(s);

                BigDecimal sqrt = new BigDecimal(1);
                int scale = 25;
                sqrt = sqrt.setScale(scale + 3, RoundingMode.FLOOR);
                BigDecimal store = new BigDecimal(a.toString());
                boolean first = true;

                do  //checks if it is the first update and calculate square root of bigdecimal
                {
                    if (!first)
                    {
                        store = new BigDecimal(sqrt.toString());
                    }
                    else
                    {
                        first = false;
                    }
                    store = store.setScale(scale + 3, RoundingMode.FLOOR);
                    sqrt = a.divide(store, scale + 3, RoundingMode.FLOOR).add(store).divide(
                            BigDecimal.valueOf(2), scale + 3, RoundingMode.FLOOR);
                }while (!store.equals(sqrt));

                sqrt = sqrt.setScale(scale, RoundingMode.FLOOR);
                BigDecimal b = sqrt.multiply(new BigDecimal(changeInTime));
                timeDilated =  b.negate().add(new BigDecimal(changeInTime));

                //decide units for the seconds relative and sets the time dilation textview
                if(timeDilated.compareTo(BigDecimal.ZERO) > 0 && timeDilated.compareTo(new BigDecimal(.00000000000000001)) < .000000000000000001) {
                    BigDecimal timeDilate = timeDilated.multiply(new BigDecimal(3600000000000000000000d));
                    timeD.setText(getString(R.string.attosecond, timeDilate));
                }else if(timeDilated.compareTo(new BigDecimal(.000000000000000001)) > .000000000000000001 && timeDilated.compareTo(new BigDecimal(.00000000000001)) < .00000000000001){
                    BigDecimal timeDilate = timeDilated.multiply(new BigDecimal(3600000000000000000d));
                    timeD.setText(getString(R.string.femtosecond, timeDilate));
                }else if(timeDilated.compareTo(new BigDecimal(.0000000000000001)) > .0000000000000001 && timeDilated.compareTo(new BigDecimal(.0000000000001)) < .0000000000001){
                    BigDecimal timeDilate = timeDilated.multiply(new BigDecimal(3600000000000000d));
                    timeD.setText(getString(R.string.picosecond, timeDilate));
                }else if(timeDilated.compareTo(new BigDecimal(.0000000000001)) > .0000000000001 && timeDilated.compareTo(new BigDecimal(.0000000001)) < .0000000001){
                    BigDecimal timeDilate = timeDilated.multiply(new BigDecimal(36000000000000d));
                    timeD.setText(getString(R.string.nanosecond, timeDilate));
                }else if(timeDilated.compareTo(new BigDecimal(.0000000001)) > .0000000001){
                    BigDecimal timeDilate = timeDilated.multiply(new BigDecimal(3600000000d));
                    timeD.setText(getString(R.string.microsecond, timeDilate));
                }else if(timeDilated.compareTo(BigDecimal.ZERO) == 0){
                    timeD.setText(getString(R.string.attosecond, 0f));
                }
                speed.setText(getString(R.string.mag, gpsSpeedVal));    //updates speed textview
            }
        }, 10); //end the integration delay

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }
}
