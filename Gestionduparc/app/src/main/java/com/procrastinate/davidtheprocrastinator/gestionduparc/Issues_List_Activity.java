package com.procrastinate.davidtheprocrastinator.gestionduparc;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Issues_List_Activity extends ListActivity {

    private List<Issue> issuesList;
    private IssueAdapter ia;
    private ListView list;
    private Database issuesDB;
    private TextView aucun;
    private FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        add = (FloatingActionButton) findViewById(R.id.addIssue);

        issuesDB = new Database(this);

        Tools.init();

        issuesDB.ajouter(Tools.i);

        ia = new IssueAdapter(this, issuesDB.getIssues());
        setListAdapter(ia);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
        list = (ListView) findViewById(android.R.id.list);
        aucun = (TextView) findViewById(R.id.aucun);

        // on récupère la liste des Personnes dans la BDD, sous forme de ArrayList
        this.issuesList = issuesDB.getIssues();

        this.ia = new IssueAdapter(this, this.issuesList);
        // on affiche la liste dans une ListView
        setListAdapter(this.ia);
        if(this.issuesList.isEmpty()){
            list.setVisibility(View.GONE);
            aucun.setVisibility(View.VISIBLE);
        }
        else{
            list.setVisibility(View.VISIBLE);
            aucun.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();
        this.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), Issue_Activity.class);
                intent.putExtra("type", Tools.ISSUE_LAYOUT_MODE_NEW);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView liste, View vue, int position, long id) {
        Issue personne = issuesList.get(position);

        Intent intent = new Intent(this, Issue_Activity.class);
        intent.putExtra("type", Tools.ISSUE_LAYOUT_MODE_EDIT);
        intent.putExtra("issueId", personne.getId());

        startActivity(intent);
    }

    public void edition(View vue) {
        startActivity(new Intent(this, Issue_Activity.class));
    }

    @Override
    protected void onDestroy() {
        //Important!
        this.issuesDB.fermeture();
        super.onDestroy();
    }

    public void newIssue(View v){
        Intent intent = new Intent(getApplicationContext(), Issue_Activity.class);
        intent.putExtra("type", Tools.ISSUE_LAYOUT_MODE_NEW);
        this.startActivity(intent);
    }

}
