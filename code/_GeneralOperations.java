/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author korisnik
 */
public class _GeneralOperations implements GeneralOperations {

    private Calendar cur = null;

    @Override
    public void setInitialTime(Calendar clndr) {
        boolean exists = true;
        cur = clndr;
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("update TimeTable set CurrTime = ?")) {
            stmt.setDate(1, new java.sql.Date(cur.getTimeInMillis()));

            if (stmt.executeUpdate() == 0) {
                exists = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!exists) {
            try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("insert into TimeTable(CurrTime) values(?)")) {
                stmt.setDate(1, new java.sql.Date(cur.getTimeInMillis()));
                stmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public Calendar time(int i) {
        Calendar c = Calendar.getInstance();
        try (Statement stmt = DB.getInstance().getConnection().createStatement();) {
            ResultSet rs = stmt.executeQuery("select CurrTime from TimeTable");
            rs.next();
            c.setTimeInMillis(rs.getDate(1).getTime());
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(i<=0)
            return c;
        
        ArrayList<Integer> orders = new ArrayList<>();
        try (Statement stmt = DB.getInstance().getConnection().createStatement();) {
            ResultSet rs = stmt.executeQuery("select IdO from Orders where Status = 'sent' ");
            while (rs.next()) {
                orders.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select D.LocPath, D.DaysLeft from Delivery D where D.IdO = ?");
                PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("select Distance from Line where (IdC1 = ? and IdC2=?) or (IdC1=? and IdC2=?)");) {
            for (int IdO : orders) {
                stmt1.setInt(1, IdO);
                ResultSet rs = stmt1.executeQuery();
                rs.next();
                ArrayList<String> cities = new ArrayList<>(Arrays.asList(rs.getString(1).split(",")));
                int time = rs.getInt(2) - i;
                while (time <= 0 && cities.size() > 2) {
                    cities.remove(0);
                    int idC1 = Integer.valueOf(cities.get(0));
                    int idC2 = Integer.valueOf(cities.get(1));
                    stmt2.setInt(1, idC1);
                    stmt2.setInt(2, idC2);
                    stmt2.setInt(3, idC2);
                    stmt2.setInt(4, idC1);
                    rs = stmt2.executeQuery();
                    rs.next();
                    time += rs.getInt(1);
                }

                if (time > 0) {
                    try (PreparedStatement stmt3 = DB.getInstance().getConnection().prepareStatement("update Delivery set DaysLeft=?, LocPath=? where IdO = ?")) {
                        stmt3.setInt(1, time);
                        stmt3.setString(2, String.join(",", cities));
                        stmt3.setInt(3, IdO);
                        stmt3.executeUpdate();
                    } catch (SQLException ex) {
                        Logger.getLogger(_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try (PreparedStatement stmt3 = DB.getInstance().getConnection().prepareStatement("delete from Delivery where IdO = ?");
                            PreparedStatement stmt4 = DB.getInstance().getConnection().prepareStatement("update orders set Status = 'recieved' where IdO = ?");
                            PreparedStatement stmt5 = DB.getInstance().getConnection().prepareStatement("update TimeTable set CurrTime = ?");) {
                        stmt3.setInt(1, IdO);
                        stmt3.executeUpdate();
                        
                        time = i + time;
                        c.add(Calendar.DATE, time);
                        stmt5.setDate(1, new java.sql.Date(c.getTimeInMillis()));
                        stmt5.executeUpdate();
                        c.add(Calendar.DATE, -time);
                        
                        stmt4.setInt(1, IdO);
                        stmt4.executeUpdate();
                    } catch (SQLException ex) {
                        Logger.getLogger(_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        c.add(Calendar.DATE, i);

        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("update TimeTable set CurrTime = ?")) {
            stmt.setDate(1, new java.sql.Date(c.getTimeInMillis()));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return c;
    }

    @Override
    public Calendar getCurrentTime() {
        Calendar c = Calendar.getInstance();
        try (Statement stmt = DB.getInstance().getConnection().createStatement();) {
            ResultSet rs = stmt.executeQuery("select CurrTime from TimeTable");
            rs.next();
            c.setTimeInMillis(rs.getDate(1).getTime());
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }

    @Override
    public void eraseAll() {
        try (Statement stmt = DB.getInstance().getConnection().createStatement()) {
            stmt.execute("delete from TransactionShop");
            stmt.execute("delete from TransactionBuyer");
            stmt.execute("delete from Transactions");
            stmt.execute("delete from Delivery");
            stmt.execute("delete from Item");
            stmt.execute("delete from Orders");
            stmt.execute("delete from HasArticles");
            stmt.execute("delete from Article");
            stmt.execute("delete from Shop");
            stmt.execute("delete from Buyer");
            stmt.execute("delete from Line");
            stmt.execute("delete from City");
        } catch (SQLException ex) {
            Logger.getLogger(_GeneralOperations.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        _GeneralOperations g = new _GeneralOperations();
        Calendar c = Calendar.getInstance();
        c.set(2022, 1, 25, 20, 15);
        //g.setInitialTime(c);
        g.eraseAll();
    }
}
