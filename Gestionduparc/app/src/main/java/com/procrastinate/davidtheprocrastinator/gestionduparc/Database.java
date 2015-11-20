package com.procrastinate.davidtheprocrastinator.gestionduparc;

/**
 * Created by David on 15/11/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/*
 * UNIQUEMENT POUR LA VERSION 1 (sans DAO)
 *
 * Dans cet exemple, cette classe joue 2 rôles :
 * 	- le "DataBaseHandler" avec onCreate et onUpgrade
 * 	- le DAO avec open, close, (getDB), add, delete,...
 * Par conséquent on peut extraire le code de DAO pour le mettre
 * dans la classe BD_DAO, d'où les commentaires dans cette classe (version 2)
 */

public class Database extends SQLiteOpenHelper {

    private Context ctx;
    private String TABLE_NAME = "issues";

    // version 1 : sans DAO
    private SQLiteDatabase database;

    // version 1 : sans DAO
    public Database(Context ctx) {
        super(ctx, "issues.bd", null, 1);
        this.ctx = ctx;
        database = getWritableDatabase();
    }

    // à garder pour les deux versions
    public Database(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // ces deux fonctions (onCreate et onUpgrade) sont à garder absolument ici
    // (héritage de SQLiteOpenHelper)
    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "; ");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "title TEXT NOT NULL DEFAULT 'title',"
                        + "type TEXT NOT NULL DEFAULT 'OTHER',"
                        + "address TEXT NOT NULL DEFAULT 'address',"
                        + "latitude REAL NOT NULL DEFAULT 0,"
                        + "longitude REAL NOT NULL DEFAULT 0,"
                        + "date TEXT NOT NULL DEFAULT 'JJ-MM-AAAA-HH-MM',"
                        + "description TEXT,"
                        + "image BLOB,"
                        + "resolved TEXT NOT NULL DEFAULT 'false'"
                        + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int ancienneVersion,
                          int nouvelleVersion) {
        db.execSQL("DROP TABLE " + TABLE_NAME+ ";");
        onCreate(db);
    }

    // Version 1 : sans DAO
    // Tout le code ci-dessous deviendra inutile quand on le déplacera dans BD_DAO (pour la
    // version 2)
    //

    public void fermeture() {
        database.close();
    }

    public long ajouter(Issue issue) {

        ContentValues valeurs = new ContentValues();

        valeurs.put("title", issue.getTitle());
        valeurs.put("type", issue.getType().name());
        valeurs.put("address",issue.getAddress());
        valeurs.put("latitude",issue.getLatitude());
        valeurs.put("longitude", issue.getLongitude());
        valeurs.put("resolved",issue.isResolved());
        valeurs.put("description", issue.getDescription());
        valeurs.put("date", issue.getDate());

        return database.insert(TABLE_NAME, null, valeurs);
    }

    public int miseAJour(Issue issue) {

        ContentValues valeurs = new ContentValues();

        valeurs.put("title", issue.getTitle());
        valeurs.put("type", issue.getType().name());
        valeurs.put("address",issue.getAddress());
        valeurs.put("latitude",issue.getLatitude());
        valeurs.put("longitude", issue.getLongitude());
        valeurs.put("resolved",issue.isResolved());
        valeurs.put("description", issue.getDescription());
        valeurs.put("date", issue.getDate());

        return database.update(TABLE_NAME, valeurs, "id = " + issue.getId(), null);
    }

    public int supprimer(int id) {
        return database.delete(TABLE_NAME, "id = " + id, null);
    }

    public Issue getIssue(int id) {

        Cursor curseur = database.query(TABLE_NAME, null, "id = " + id, null, null,	null, null);

        if (curseur.getCount() == 0)
            return null;

        // théroriquement, il n'y a qu'un seul enregistrement (au plus) qui
        // répond à la requête...
        // donc pas besoin de parcourir le résultat de la requête
        curseur.moveToFirst();

        return curseurToIssue(curseur);
    }

    public ArrayList<Issue> getIssues() {

        ArrayList<Issue> liste = new ArrayList<Issue>();

        // on renvoie toute la table "Issues", triée par id dans l'ordre décroissant
        Cursor curseur = database.query(TABLE_NAME, null, null, null, null, null, "id desc");

        if (curseur.getCount() == 0)
            return liste;

        // on parcourt le résultat de la requête et on le transforme en un
        // tableau qu'on renvoie à la ListeView
        curseur.moveToFirst();
        do {
            liste.add(curseurToIssue(curseur));
        } while (curseur.moveToNext());

        curseur.close();

        return liste;
    }

    private Issue curseurToIssue(Cursor curseur) {

        Issue issue = null;

        try{
            issue = new Issue(
                    curseur.getString(1),
                    IssueType.valueOf(curseur.getString(2)),
                    Double.parseDouble(curseur.getString(4)),
                    Double.parseDouble(curseur.getString(5))
            );

            issue.setId(curseur.getInt(0));
            issue.setAddress(curseur.getString(3));
            issue.setResolved((curseur.getString(5).equals("false") ? false : true));
            issue.setDescription(curseur.getString(6));
            issue.setDate(curseur.getString(7));
        }
        catch(Exception e){
            Log.d("Info: ", "couldn't retrieve issue.");
            Toast.makeText(this.ctx,"",Toast.LENGTH_SHORT).show();
        }

        return issue;
    }

}
