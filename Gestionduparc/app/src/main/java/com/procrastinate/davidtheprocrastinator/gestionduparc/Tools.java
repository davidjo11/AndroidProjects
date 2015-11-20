package com.procrastinate.davidtheprocrastinator.gestionduparc;

/**
 * Created by David on 16/11/2015.
 */
public class Tools {

    public static final String LOCATION_IN_PROGRESS = "Géolocalisation en cours...";

    public static final String ISSUE_LAYOUT_MODE_NEW = "new";

    public static final String ISSUE_LAYOUT_MODE_EDIT = "edit";

    public static final String LOCATION_NOT_FOUND = "Aucune adresse ne correspond à votre recherche.";

    public static String[] IssueTypeToString(){
        String[] res = new String[IssueType.values().length];
        for(IssueType it : IssueType.values())
            res[it.ordinal()] = it.name().replace('_', ' ');

        return res;
    }

    public static Issue i = new Issue();

    public static void init(){
        i.setTitle("Rien");
        i.setType(IssueType.AUTRE);
        i.setLatitude(45.0);
        i.setLongitude(90.0);
    }


}
