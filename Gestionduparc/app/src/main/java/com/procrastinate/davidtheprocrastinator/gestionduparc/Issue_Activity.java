package com.procrastinate.davidtheprocrastinator.gestionduparc;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by David on 15/11/2015.
 */
public class Issue_Activity extends Activity {

    // version 1 : sans DAO
    private Database bd;

    // version 2 : avec DAO
//	 private BD_DAO bd;

    private String reqType;

    private EditText title, lat, lng, description;
    private TextView address;
    private CheckBox checkBox;
    private ImageView img;
    private Spinner type;
    private Issue issue;
    private Button save, delete;
    private LinearLayout latlng, linearMap;

    private LocationManager localisations;
    private LocationListener listenGPS, listenNet;
    private ProgressBar pb;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.an_issue);

        title = (EditText) findViewById(R.id.titleView);
        type = (Spinner) findViewById(R.id.type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Tools.IssueTypeToString());
        this.type.setAdapter(adapter);
        img = (ImageView) findViewById(R.id.image);
        description = (EditText) findViewById(R.id.desc);
        lat = (EditText) findViewById(R.id.lat);
        lng = (EditText) findViewById(R.id.lng);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setEnabled(true);
        address = (TextView) findViewById(R.id.address);
        address.setClickable(false);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);

        save = (Button) findViewById(R.id.enregistrer);
        delete = (Button) findViewById(R.id.supprimer);

        latlng = (LinearLayout) findViewById(R.id.latlng);
        linearMap = (LinearLayout) findViewById(R.id.map);
        linearMap.setClickable(false);


        // init localisation
        localisations = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listenGPS = new MyLocationListener();
        listenNet = new MyLocationListener();
        // création d'une nouvelle issue
        issue = null;

        // version 1 : sans DAO
        bd = new Database(this);

        // version 2 : avec DAO
//		bd = new BD_DAO();
//		bd.ouverture(this);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @TargetApi(Build.VERSION_CODES.M)
    private Location getLastKnownLocation() {
        this.localisations = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = this.localisations.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return null;
            }
//            localisations.requestLocationUpdates(provider, 5000, 1, listen);
            Location l = this.localisations.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onResume(){
        super.onResume();
        Toast.makeText(this,"Resume", Toast.LENGTH_SHORT).show();

        // s'il y a eu passage de paramètre, alors on peut modifier/supprimer
        Bundle donnees = getIntent().getExtras();

        if (donnees != null) {
            reqType = donnees.getString("type");

            if (reqType == null) {
                //A voir
                Log.d("Info:", "Type: null");
                finish();
            } else if (reqType.equals(Tools.ISSUE_LAYOUT_MODE_NEW)) {
                this.latlng.setVisibility(View.VISIBLE);
                this.delete.setEnabled(false);
                this.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Issue_Activity.this.enregistrer();
                    }
                });
                //Rien à faire à priori
            } else if (reqType.equals(Tools.ISSUE_LAYOUT_MODE_EDIT) && donnees.getString("issueId") != null) {
                int id = Integer.parseInt(donnees.getString("issueId"));
                this.issue = bd.getIssue(id);
                if (this.issue != null) {
                    this.latlng.setVisibility(View.GONE);

                    this.title.setText(this.issue.getTitle());
                    this.description.setText(this.issue.getDescription());

//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.issue_type, Tools.IssueTypeToString());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Tools.IssueTypeToString());
                    this.type.setAdapter(adapter);
                    this.type.setSelection(this.issue.getType().ordinal(), true);

                    this.lat.setText("" + this.issue.getLatitude());
                    this.lng.setText("" + this.issue.getLongitude());

                    this.address.setText(this.issue.getAddress());

                    this.checkBox.setChecked(this.issue.isResolved());

                    this.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Issue_Activity.this.modifier(Issue_Activity.this.issue.getId());
                        }
                    });

                    this.save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(Issue_Activity.this, Issue_Activity.this.issue.getTitle() + " a été enregistré.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.i("Info: ", "issue inconnu (n'est pas dans la base de données.");
                    finish();
                }

                try {
                    //http://www.rdcworld-android.blogspot.in/2012/01/get-current-location-coordinates-city.html

                    Location positionGPS = null, positionNet = null;

                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.

                        Toast.makeText(this, "Stupid",Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    localisations.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, listenGPS);
                    localisations.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, listenNet);

                    Toast.makeText(this, "2*Stupid",Toast.LENGTH_SHORT).show();
                    positionGPS = localisations.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    positionNet = localisations.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    listenGPS.onLocationChanged(positionGPS);
                    listenNet.onLocationChanged(positionNet);

                } catch (Exception SecurityException) {
                    Log.i("Info: ", "La position n'a pas pu être recupéré");
                    Toast.makeText(this, "Erreur connexion GPS", Toast.LENGTH_SHORT).show();
                }
            }
        }
        // sinon on peut ajouter une personne
        else {
            Log.d("Info:", "pas d'id pour modifier le problème ou cas inconnu.");
            Toast.makeText(this, "Rien n'est fait", Toast.LENGTH_SHORT).show();
        }
    }

    public void showMap(View view) {
        String s = "";
        if(!this.lat.getText().toString().trim().equals("")
                && !this.lng.getText().toString().trim().equals("")
                && !this.address.getText().toString().trim().equals(Tools.LOCATION_NOT_FOUND)
                && !this.address.getText().toString().trim().equals(Tools.LOCATION_IN_PROGRESS))
            s += this.lat.getText().toString().trim()+","+this.lng.getText().toString().trim();

//        if(!this.address.getText().toString().trim().equals(Tools.LOCATION_NOT_FOUND) && !this.address.toString().trim().equals(Tools.LOCATION_IN_PROGRESS))
//            s += this.address.getText().toString().trim();
        if(!s.trim().equals("")){
            Uri u = Uri.parse("geo:"+s+"?z:17");
            Intent intent = new Intent(Intent.ACTION_VIEW, u);
            startActivity(intent);
        }
    }

    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            Issue_Activity.this.address.setText(Tools.LOCATION_IN_PROGRESS);
            Issue_Activity.this.pb.setVisibility(View.VISIBLE);
//            Toast.makeText(
//                    getBaseContext(),
//                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
//                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
//            String longitude = "Longitude: " + loc.getLongitude();
//            Log.v("Info: ", longitude);
//            String latitude = "Latitude: " + loc.getLatitude();
//            Log.v("Info: ", latitude);

        /*------- To get city name from coordinates -------- */
            String addr = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
//                if (addresses.size() > 0)
//                    System.out.println(addresses.get(0).getLocality());
                addr = addresses.get(0).getAddressLine(0);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String lat = "" + loc.getLatitude(),
                    lng = "" + loc.getLongitude();
            Issue_Activity.this.lat.setText(lat);
            Issue_Activity.this.lng.setText(lng);
            if(addr == null || addr.trim().equals("")){
                Issue_Activity.this.address.setText(Tools.LOCATION_NOT_FOUND);
                Issue_Activity.this.address.setClickable(false);
                Issue_Activity.this.linearMap.setClickable(false);
            }
            else {
                Issue_Activity.this.linearMap.setClickable(true);
                Issue_Activity.this.address.setClickable(true);
                Issue_Activity.this.address.setText(addr);
            }
            Issue_Activity.this.pb.setVisibility(View.GONE);
        }

        @Override
        public void onProviderDisabled(String provider) {
//            Toast.makeText(Issue_Activity.this, "Votre réseau internet vient d'être éteint.", Toast.LENGTH_LONG).show();
            DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // cancel the dialog box
                    dialog.cancel();
                    finish();
                }
            },
                    positive = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // finish the current activity
                            // AlertBoxAdvance.this.finish();
                            Intent myIntent = new Intent(
                                    Settings.ACTION_SECURITY_SETTINGS);
                            Issue_Activity.this.getApplicationContext().startActivity(myIntent);
                            dialog.cancel();
                        }
                    };
            Issue_Activity.this.alertbox("Veuillez activer votre signal GPS.\nSi vous annulez, vous serez redirigés vers la page précédente.",
                    "** Statut GPS **",
                    "Activer le GPS",
                    "Annuler",
                    positive,
                    negative);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(Issue_Activity.this, provider+" activé.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(Issue_Activity.this, provider+": Status changed: "+status,Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean displayGpsStatus() {
        String provider = LocationManager.GPS_PROVIDER;
        return this.localisations.isProviderEnabled(provider);
    }


    public void enregistrer() {

        Issue i = null;
        try{
            i = new Issue(
                    this.title.getText().toString().trim(),
                    IssueType.values()[this.type.getSelectedItemPosition()] ,
                    Double.parseDouble(this.lat.getText().toString().trim()),
                    Double.parseDouble(this.lng.getText().toString().trim())
            );
            i.setAddress(this.address.getText().toString().trim());
            i.setDescription(this.description.getText().toString().trim());
            i.setResolved(this.checkBox.isChecked());

            bd.ajouter(i);
            Toast.makeText(this, "\"" + i.getTitle()+"\" a été enregistré.", Toast.LENGTH_LONG).show();
            finish();
        }
        catch(Exception e){
//            Log.d("Info: ", "saving failed.");
//            Toast.makeText(this, "L'un des champs suivants est vide: Intitulé, type ou adresse (latitude/longitude => GPS non activé).", Toast.LENGTH_LONG);
            DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            };
            this.alertbox("** Enregistrement du problème **",
                    "L'un des champs suivants est vide: Intitulé, type ou adresse (latitude/longitude => GPS non activé).",
                    "ok",
                    "",
                    positive, null);
        }
    }

    public void modifier(int id) {

        if(this.title.getText().toString().trim().equals(issue.getTitle())
                && this.description.getText().toString().trim().equals(issue.getDescription())
                && this.checkBox.isChecked() == issue.isResolved()){
            DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            };
            this.alertbox("** Enregistrement du problème **",
                    "Aucune modification n'a été effectuée.",
                    "ok",
                    "",
                    positive, null);
        }
        else{
            //Seule ces valeurs peuvent être modifées: titre, description, résolu.
            this.issue.setTitle(this.title.getText().toString().trim());
            this.issue.setDescription(this.description.getText().toString().trim());
            this.issue.setType(IssueType.values()[this.type.getSelectedItemPosition()]);
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm");
            Date date = new Date();
            this.issue.setDate(dateFormat.format(date));
            this.issue.setResolved(this.checkBox.isChecked());

            bd.miseAJour(this.issue);
            Toast.makeText(this, "\""+this.issue.getTitle()+"\" a été modifié.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void supprimer(int id) {
        // version 1 : sans DAO
        bd.supprimer(id);

        // version 2 : avec DAO
//		bd.supprimer(personne);

        Toast.makeText(Issue_Activity.this, Issue_Activity.this.issue.getTitle() + " a été supprimé.", Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    protected void onDestroy() {
        bd.fermeture();
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        bd.fermeture();
        finish();
    }

    public void check(View view) {
        CheckBox c = (CheckBox) view;
        if(!c.isChecked()){
            c.setChecked(true);
            c.setText("Résolu");
        }
        else {
            c.setChecked(false);
            c.setText("Non résolu");
        }
    }


    /*----------Method to create an AlertBox ------------- */
    public void alertbox(String title, String mymessage, String ok, String cancel, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mymessage)
                .setCancelable(false)
                .setTitle(title);

        if(positive != null)
            builder.setPositiveButton(ok, positive);
        if(negative != null)
            builder.setNegativeButton(cancel, negative);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
