package ca.etsmtl.applets.etsmobile.util;

import java.util.ArrayList;
import java.util.Comparator;

import ca.etsmtl.applets.etsmobile.model.Programme;

/**
 * Created by gnut3ll4 on 10/08/15.
 */
public class ProgrammeComparator implements Comparator<Programme> {

    @Override
    public int compare(Programme p1, Programme p2) {
        String statut1 = p1.statut;
        String statut2 = p2.statut;

        if (statut1.equals(statut2)) {

            int year1 = Integer.parseInt(p1.sessionDebut.substring(1));
            int year2 = Integer.parseInt(p2.sessionDebut.substring(1));

            if ((year1 - year2) == 0) {
                ArrayList<Character> order = new ArrayList<Character>();
                order.add('H');
                order.add('É');
                order.add('A');

                Character session1 = p1.sessionDebut.charAt(0);
                Character session2 = p2.sessionDebut.charAt(0);

                if (session1 == session2)
                    return 0;
                return order.indexOf(session1) > order.indexOf(session2) ? 1 : -1;

            } else {
                return year1 > year2 ? 1 : -1;
            }
        } else {
            if (statut2.equals("actif") || statut2.equals("tutelle"))
                return 1;
            if (statut1.equals("actif") || statut2.equals("tutelle"))
                return -1;
            return 0;
        }
    }
}
