/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.OrderOperations;

/**
 *
 * @author korisnik
 */
public class _OrderOperations implements OrderOperations {

    int IdS = -1;

    @Override
    public int addArticle(int i, int i1, int i2) {
        int IdI = -1;
        try (PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select * from Orders where IdO = ? and Status = 'created'");
                PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("select IdS from HasArticles where IdA = ? and Amount >= ?");) {
            stmt1.setInt(1, i);
            ResultSet rs = stmt1.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            stmt2.setInt(1, i1);
            stmt2.setInt(2, i2);
            rs = stmt2.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            IdS = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select IdI from Item where IdO = ? and IdA = ?");
                PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("update HasArticles set Amount = Amount - ? where IdA = ?");) {
            stmt1.setInt(1, i);
            stmt1.setInt(2, i1);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next()) {
                IdI = rs.getInt(1);
            }

            stmt2.setInt(1, i2);
            stmt2.setInt(2, i1);
            stmt2.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (IdI != -1) {
            try (PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("update Item set Amount = Amount + ? where IdI = ?");) {
                stmt1.setInt(1, i2);
                stmt1.setInt(2, IdI);
                stmt1.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try (PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("insert into Item(IdO, IdA, IdS, FinalArticlePrice, Amount, ArticlePrice, ArticlePriceWithDiscount)"
                    + "values (?, ?, ?, 0, ?, 0, 0)", Statement.RETURN_GENERATED_KEYS);) {
                stmt1.setInt(1, i);
                stmt1.setInt(2, i1);
                stmt1.setInt(3, IdS);
                stmt1.setInt(4, i2);
                stmt1.executeUpdate();
                ResultSet rs = stmt1.getGeneratedKeys();
                rs.next();
                IdI = rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return IdI;
    }

    @Override
    public int removeArticle(int i, int i1) {
        int Amount = -1;

        try (PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select * from Orders where IdO = ? and Status = 'created'");) {
            stmt1.setInt(1, i);
            ResultSet rs = stmt1.executeQuery();
            if (!rs.next()) {
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select Amount from Item where IdA = ? and IdO = ?");) {
            stmt1.setInt(1, i1);
            stmt1.setInt(2, i);
            ResultSet rs = stmt1.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            Amount = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        try (PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("update HasArticles set Amount = Amount + ? where IdA = ?");) {
            stmt1.setInt(1, Amount);
            stmt1.setInt(2, i1);
            stmt1.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        try (PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("delete from Item where IdA = ? and IdO = ?");) {
            stmt1.setInt(1, i1);
            stmt1.setInt(2, i);
            if (stmt1.executeUpdate() == 0) {
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    @Override
    public List<Integer> getItems(int i) {
        ArrayList<Integer> lista = new ArrayList<>();
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select * from Orders where IdO = ?");
                PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("select IdI from Item where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            stmt2.setInt(1, i);
            rs = stmt2.executeQuery();
            while (rs.next()) {
                lista.add(rs.getInt(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    @Override
    public int completeOrder(int i) {
        String status = "";
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select Status from Orders where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            status = rs.getString(1);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select sum(Amount) from Item where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            if(rs.getInt(1) == 0)
                return -1;

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (!status.equals("created")) {
            return -1;
        }
        BigDecimal finalPrice = null;
        try (CallableStatement stmt = DB.getInstance().getConnection().prepareCall("{ call SP_FINAL_PRICE(?, ?) }")) {
            stmt.setInt(1, i);
            stmt.registerOutParameter(2, Types.DECIMAL);
            stmt.execute();
            finalPrice = stmt.getBigDecimal(2).setScale(3);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        BigDecimal balance = null;
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select B.Balance from Buyer B join Orders O on B.IdB = O.IdB where IdO = ?")) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            balance = rs.getBigDecimal(1).setScale(3);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (balance.compareTo(finalPrice) < 0) {
            return -1;
        }

        int IdB = -1, IdC=-1;
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select B.IdB, B.IdC from Orders O join Buyer B on B.IdB=O.IdB where O.IdO = ?")) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            IdB = rs.getInt(1);
            IdC = rs.getInt(2);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ArrayList<Integer> cities = new ArrayList<>();
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select distinct S.IdC from Shop S join Item I on S.IdS = I.IdS where I.IdO = ? ");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
                cities.add(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ArrayList<Integer> citiesWShop = new ArrayList<>();
        try (Statement stmt = DB.getInstance().getConnection().createStatement();) {
            ResultSet rs = stmt.executeQuery("select distinct S.IdC from Shop S");
            while(rs.next())
                citiesWShop.add(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Dijkstra dijkstra = Dijkstra.getInstance();
        dijkstra.empty();
        
        
        try (Statement stmt = DB.getInstance().getConnection().createStatement();) {
            ResultSet rs = stmt.executeQuery("select IdC1, IdC2, Distance from Line");
            while(rs.next())
                dijkstra.addBranch(rs.getInt(1), rs.getInt(2), rs.getInt(3));

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        dijkstra.compute(IdC);
        int maxTime = -1;
        
        int closestCity = citiesWShop.get(0);
        for(int city : citiesWShop)
            if(dijkstra.getOptimalDistance(city) < dijkstra.getOptimalDistance(closestCity))
                closestCity = city;
        String path = dijkstra.getOptimalPath(closestCity);
        path = path.split(",")[0] + ","+ path;
        dijkstra.compute(closestCity);
        for(int city : cities){
            if(dijkstra.getOptimalDistance(city) == Integer.MAX_VALUE)
                return -1;
            if(dijkstra.getOptimalDistance(city) > maxTime)
                maxTime = dijkstra.getOptimalDistance(city);
        }
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("insert into Delivery(IdO, DaysLeft, LocPath) values (?, ?, ?)");) {
            stmt.setInt(1, i);
            stmt.setInt(2, maxTime);
            stmt.setString(3, path);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("update Orders set Status = 'sent' where IdO = ?");) {
            stmt.setInt(1, i);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        Calendar c = Calendar.getInstance();
        try (Statement stmt = DB.getInstance().getConnection().createStatement();){
            ResultSet rs = stmt.executeQuery("select CurrTime from TimeTable");
            rs.next();
            c.setTimeInMillis(rs.getDate(1).getTime());
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("insert into Transactions(IdO, Amount) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
                PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("insert into TransactionBuyer(IdT, TimeSen, IdB) values (?, ?, ?)");
                
                ){
            stmt1.setInt(1, i);
            stmt1.setBigDecimal(2, finalPrice);
            stmt1.executeUpdate();
            ResultSet rs = stmt1.getGeneratedKeys();
            rs.next();
            int IdT = rs.getInt(1);
                    
            stmt2.setInt(1, IdT);
            stmt2.setDate(2, new java.sql.Date(c.getTimeInMillis()));
            stmt2.setInt(3, IdB);
            stmt2.executeUpdate();
                    
        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("update Buyer set Balance = Balance - ? where IdB=?")) {
            stmt.setBigDecimal(1, finalPrice);
            stmt.setInt(2, IdB);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }


        return 1;
    }

    @Override
    public BigDecimal getFinalPrice(int i) {
        String status = null;
        BigDecimal ret = new BigDecimal(-1).setScale(3);
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select Status from Orders where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new BigDecimal(-1).setScale(3);
            }
            status = rs.getString(1);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!status.equals("created")) {
            try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select sum(FinalArticlePrice) from Item where IdO = ?");) {
                stmt.setInt(1, i);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    return new BigDecimal(-1).setScale(3);
                }
                ret = rs.getBigDecimal(1).setScale(3);

            } catch (SQLException ex) {
                Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            return new BigDecimal(-1).setScale(3);
        }

        return ret;
    }

    @Override
    public BigDecimal getDiscountSum(int i) {
        BigDecimal ret = new BigDecimal(-1).setScale(3);

        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select Status from Orders where Ido = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new BigDecimal(-1).setScale(3);
            }
            String status = rs.getString(1);
            if (status.equals("created")) {
                return new BigDecimal(-1).setScale(3);
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select sum(ArticlePrice) - sum(FinalArticlePrice) from Item where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new BigDecimal(-1).setScale(3);
            }
            ret = rs.getBigDecimal(1).setScale(3);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public String getState(int i) {
        String status = "";
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select Status from Orders where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            status = rs.getString(1);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;

    }

    @Override
    public Calendar getSentTime(int i) {
        Calendar ret = null;
        String status = "";
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select Status from Orders where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            status = rs.getString(1);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (status.equals("created")) {
            return null;
        }

        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select TimeSen from Transactions T join TransactionBuyer B on T.IdT = B.IdT where T.IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }

            java.sql.Date date = rs.getDate(1);
            ret = Calendar.getInstance();
            ret.setTime(date);

            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;

    }

    @Override
    public Calendar getRecievedTime(int i) {
        Calendar ret = null;
        String status = "";
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select Status from Orders where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            status = rs.getString(1);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!status.equals("recieved")) {
            return null;
        }

        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select TimeRec from Transactions T join TransactionShop B on T.IdT = B.IdT where T.IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }

            java.sql.Date date = rs.getDate(1);
            ret = Calendar.getInstance();
            ret.setTime(date);

            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getBuyer(int i) {
        int IdB = -1;
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select IdB from Orders where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            IdB = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return IdB;
    }

    @Override
    public int getLocation(int i) {
        String status = "";
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select Status from Orders where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            status = rs.getString(1);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (status.equals("created")) {
            return -1;
        }

        if (status.equals("recieved")) {
            int IdC = -1;
            try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select B.IdC from Buyer B join Orders O on O.IdB = B.IdB where O.IdO = ?");) {
                stmt.setInt(1, i);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    return -1;
                }
                IdC = rs.getInt(1);
                return IdC;

            } catch (SQLException ex) {
                Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int IdC = -1;
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select LocPath from Delivery where IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return -1;
            }

            String path = rs.getString(1).split(",")[0];
            IdC = Integer.valueOf(path);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return IdC;
    }

    public static void main(String[] args) {
        _OrderOperations o = new _OrderOperations();
        //o.completeOrder(1);
        o.addArticle(1, 2, 5);
        /*o.addArticle(1, 3, 5);*/
 /*o.removeArticle(1, 1);
        o.removeArticle(1, 2);
        o.removeArticle(1, 3);*/
        //System.out.println(o.getFinalPrice(1));
        //System.out.println(o.getItems(1));
    }

}
