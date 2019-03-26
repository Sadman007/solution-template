package com.tigerit.exam;

import static com.tigerit.exam.IO.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Scanner;
import javafx.util.Pair;

/**
 * All of your application logic should be placed inside this class.
 * Remember we will load your application from our custom container.
 * You may add private method inside this class but, make sure your
 * application's execution points start from inside run method.
 */ 

public class Solution implements Runnable 
{
    static Scanner sc;

    static class table {

        Integer nC, nD;
        String tName;
        ArrayList<String> cName = new ArrayList<String>();
        ArrayList< ArrayList<Integer>> tInfo = new ArrayList< ArrayList<Integer>>();
        Hashtable<String, Integer> colMap = new Hashtable<String, Integer>();

        table() {
        }

        table(int _nC, int _nD, String _tName) {
            nC = _nC;
            nD = _nD;
            tName = _tName;
        }

        public void doSort() {
            Collections.sort(tInfo, new Comparator<ArrayList<Integer>>() {
                public int compare(ArrayList<Integer> list1, ArrayList<Integer> list2) {
                    int result = 0;
                    for (int i = 0; i <= list1.size() - 1 && result == 0; i++) {
                        result = list1.get(i).compareTo(list2.get(i));
                    }
                    return result;
                }
            });
        }
    }

    static ArrayList<table> vTable = new ArrayList<table>();
    static Hashtable<String, Integer> tID = new Hashtable<String, Integer>();

    static void inputAll(int noT) {
        int ID = 0;
        for (int xi = 0; xi < noT; xi++) {
            int val, nC, nD;
            String tmp, tName;
            ArrayList<String> cName = new ArrayList<String>();
            Hashtable<String, Integer> colMap = new Hashtable<String, Integer>();
            ArrayList< ArrayList<Integer>> tInfo = new ArrayList< ArrayList<Integer>>();

            tName = sc.next();
            tID.put(tName, xi);
            nC = sc.nextInt();
            nD = sc.nextInt();
            for (int i = 0; i < nC; i++) {
                tmp = sc.next();
                cName.add(tmp);
                colMap.put(tmp, i);
            }
            for (int i = 0; i < nD; i++) {
                ArrayList<Integer> rr = new ArrayList<Integer>();
                for (int j = 0; j < nC; j++) {
                    val = sc.nextInt();
                    rr.add(val);
                }
                tInfo.add(rr);
            }
            table cur = new table();
            cur.nC = nC;
            cur.nD = nD;
            cur.cName = cName;
            cur.colMap = colMap;
            cur.tInfo = tInfo;
            cur.tName = tName;
            vTable.add(cur);
        }
    }

   static void printQuery(table vT) {
        Integer modval = 0;
        for (int i = 0; i < vT.cName.size(); i++) {
            if(i>0) System.out.printf(" ");
            System.out.print(vT.cName.get(i));
        }
        System.out.println();
        for (int i = 0; i < vT.nD; i++) {
            for (int j = 0; j < vT.nC; j++) {
                if(j>0) System.out.printf(" ");
                System.out.print(vT.tInfo.get(i).get(j));
            }
            System.out.println();
        }
        System.out.println();
    }

    static int getTableID(String tName) {
        if (tID.containsKey(tName) == false) {
            return -1;
        }
        return tID.get(tName);
    }

    static table crossJoin(table v1, table v2, String tableName) {
        table vN = new table(v1.nC + v2.nC, v1.nD * v2.nD, tableName);
        vN.cName.addAll(v1.cName);
        vN.cName.addAll(v2.cName);
        for (int i = 0; i < v1.nD; i++) {
            for (int j = 0; j < v2.nD; j++) {
                ArrayList<Integer> r1 = new ArrayList<Integer>();
                r1.addAll(v1.tInfo.get(i));
                r1.addAll(v2.tInfo.get(j));
                vN.tInfo.add(r1);
            }
        }
        
        return vN;
    }

    static table OuterJoin(ArrayList<Pair<String, Integer>> pc, table v1, table v2, String cond1, String cond2) {
        String tableName = "newTable";
        table vN = crossJoin(v1, v2, tableName);
        table res = new table(vN.nC, vN.nD, vN.tName);
        res.cName = vN.cName;
        Integer a, b;
        a = v1.colMap.get(cond1);
        b = v2.colMap.get(cond2);

        for (int i = 0; i < vN.nD; i++) {
            if ((vN.tInfo.get(i).get(a)).equals(vN.tInfo.get(i).get(v1.nC+b))) {
                res.tInfo.add(vN.tInfo.get(i));
            }
        }
        res.nD = res.tInfo.size();
        ArrayList<Integer> ipc = new ArrayList<Integer>();

        for (int i = 0; i < pc.size(); i++) {
            Pair<String, Integer> it = pc.get(i);
            if (it.getValue().equals(0)) {
                ipc.add(v1.colMap.get(it.getKey()));
            } else {
                ipc.add(v1.nC + v2.colMap.get(it.getKey()));
            }
        }
        table fin = new table(ipc.size(), res.nD, res.tName);

        for (int i = 0; i < res.nD; i++) {
            ArrayList<Integer> rr = new ArrayList<Integer>();
            for (int j = 0; j < ipc.size(); j++) {
                rr.add(res.tInfo.get(i).get(ipc.get(j)));
            }
            fin.tInfo.add(rr);
        }
        for (int i = 0; i < ipc.size(); i++) {
            fin.cName.add(res.cName.get(ipc.get(i)));
        }
        //lexi sort
        fin.doSort();
        return fin;
    }

    static void queryProcess(int qNo) {
        ArrayList<String> ki_ki = new ArrayList<String>();
        ArrayList<String> from = new ArrayList<String>();
        ArrayList<String> with_whom = new ArrayList<String>();
        ArrayList<String> vcond = new ArrayList<String>();
        String str = "";
        String tmp = "";

        for (int i = 0; i < 4;) {
            str = sc.nextLine();
            Integer tt = str.length();
            if (tt.equals(0)) {
                continue;
            }

            if (i == 0) {
                String[] tokens = str.split("\\ ");
                for (String token : tokens) {
                    ki_ki.add(token);
                }
                ki_ki.remove(0);
            } else if (i == 1) {
                String[] tokens = str.split("\\ ");
                for (String token : tokens) {
                    from.add(token);
                }
                from.remove(0);
            } else if (i == 2) {
                String[] tokens = str.split("\\ ");
                for (String token : tokens) {
                    with_whom.add(token);
                }
                with_whom.remove(0);
            } else if (i == 3) {
                String[] tokens = str.split("\\ ");
                for (String token : tokens) {
                    vcond.add(token);
                }
                vcond.remove(0);
            }
            i++;
        }
        /// refine vcond
        for (int i = 0; i < vcond.size(); i++) {
            String string = vcond.get(i);
            if (string.compareTo("=") == 0) {
                vcond.remove(i);
            }
        }
        /// refine ki_ki
        for (int i = 0; i < ki_ki.size(); i++) {
            int last = ki_ki.get(i).length() - 1;
            if (ki_ki.get(i).charAt(last) == ',') {
                /// shondeho
                String string = ki_ki.get(i);
                ki_ki.remove(i);
                string = string.substring(0, string.length() - 1);
                ki_ki.add(i, string);
            }
        }

        /// refine names
        if (from.size() > 1) {
            int ID1 = getTableID(from.get(0));
            tID.put(from.get(1), ID1);
            from.remove(0);
        }

        /// refine names
        if (with_whom.size() > 1) {
            int ID1 = getTableID(with_whom.get(0));
            tID.put(with_whom.get(1), ID1);
            with_whom.remove(0);
        }

        table v1 = vTable.get(getTableID(from.get(0)));
        table v2 = vTable.get(getTableID(with_whom.get(0)));
        ArrayList< Pair<String, Integer>> p12 = new ArrayList< Pair<String, Integer>>();

        if (ki_ki.size() == 1 && ki_ki.get(0).compareTo("*") == 0) {
            for (String it : v1.cName) {
                Pair<String, Integer> e = new Pair(it, 0);
                p12.add(e);
            }
            for (String it : v2.cName) {
                Pair<String, Integer> e = new Pair(it, 1);
                p12.add(e);
            }
        } else {
            Hashtable<Integer, Integer> mp;
            mp = new Hashtable<Integer, Integer>();
            for (String it : ki_ki) {
                String nstr = "";
                ArrayList<String> vs;
                vs = new ArrayList<String>();
                String[] tokens = it.split("\\.");
                for (String token : tokens) {
                    vs.add(token);
                }
                if (vs.get(0).compareTo(from.get(0)) == 0) {
                    Pair<String, Integer> e = new Pair(vs.get(1), 0);
                    p12.add(e);
                } else {
                    Pair<String, Integer> e = new Pair(vs.get(1), 1);
                    p12.add(e);
                }
            }
        }
        String c1, c2;
        c1 = "";
        c2 = "";

        for (String it : vcond) {
            String nstr = "";
            ArrayList<String> vs;
            vs = new ArrayList<String>();
            String[] tokens = it.split("\\.");
            for (String token : tokens) {
                vs.add(token);
            }
            if (vs.get(0).compareTo(from.get(0)) == 0) {
                c1 = vs.get(1);
            } else {
                c2 = vs.get(1);
            }

        }
        table fin = OuterJoin(p12, v1, v2, c1, c2);
        printQuery(fin);
        return;
    }
     @Override
    public void run() 
    {
       // TODO code application logic here
        Integer testCase, numberOfTable, nQ, qIdx = 0;
        sc = new Scanner(System.in);
        
        testCase = sc.nextInt();
        for (int i = 1; i <= testCase; i++) {
            vTable.clear();
            tID.clear();
            numberOfTable = sc.nextInt();
            inputAll(numberOfTable);
            nQ = sc.nextInt();
            System.out.printf("Test: %d\n", i);
            while (nQ-- > 0) {
                queryProcess(qIdx++);
                System.gc();
            }
        }
    }
}