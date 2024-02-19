/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.TransactionOperations;

/**
 *
 * @author korisnik
 */
public class _TransactionOperations implements TransactionOperations {

    @Override
    public BigDecimal getBuyerTransactionsAmmount(int i) {
        BigDecimal ret = null;
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select * from Buyer where IdB = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new BigDecimal(-1).setScale(3);
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select sum(T.Amount) from TransactionBuyer B join Transactions T on B.IdT = T.IdT where B.IdB = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            ret = rs.getBigDecimal(1) == null ? new BigDecimal(0).setScale(3) : rs.getBigDecimal(1).setScale(3);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public BigDecimal getShopTransactionsAmmount(int i) {
        BigDecimal ret = null;
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select * from Shop where IdS = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new BigDecimal(-1).setScale(3);
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select sum(T.Amount) from TransactionShop S join Transactions T on S.IdT = T.IdT where S.IdS = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            ret = rs.getBigDecimal(1) == null ? new BigDecimal(0).setScale(3) : rs.getBigDecimal(1).setScale(3);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public List<Integer> getTransationsForBuyer(int i) {
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select * from Buyer where IdB = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<Integer> transactions = new ArrayList<>();
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select IdT from TransactionBuyer where IdB = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(rs.getInt(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transactions.isEmpty() ? null : transactions;
    }

    @Override
    public int getTransactionForBuyersOrder(int i) {
        int IdT = -1;
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select B.IdT from TransactionBuyer B join Transactions T on T.IdT = B.IdT where T.IdO = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            IdT = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return IdT;
    }

    @Override
    public int getTransactionForShopAndOrder(int i, int i1) {
        int IdT = -1;
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select S.IdT from TransactionShop S join Transactions T on T.IdT = S.IdT where T.IdO = ? and S.IdS = ?");) {
            stmt.setInt(1, i);
            stmt.setInt(2, i1);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            IdT = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return IdT;
    }

    @Override
    public List<Integer> getTransationsForShop(int i) {
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select * from Shop where IdS = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<Integer> transactions = new ArrayList<>();
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select IdT from TransactionShop where IdS = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(rs.getInt(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transactions.isEmpty() ? null : transactions;
    }

    @Override
    public Calendar getTimeOfExecution(int i) {
        Calendar ret = null;
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select TimeSen from TransactionBuyer where IdT = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ret = Calendar.getInstance();
                ret.setTimeInMillis(rs.getDate(1).getTime());
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select TimeRec from TransactionShop where IdT = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ret = Calendar.getInstance();
                ret.setTimeInMillis(rs.getDate(1).getTime());
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public BigDecimal getAmmountThatBuyerPayedForOrder(int i) {
        BigDecimal ret = new BigDecimal(-1).setScale(3);
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select T.Amount from TransactionBuyer B join Transactions T on T.IdT = B.IdT where T.IdO = ?");) {
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
    public BigDecimal getAmmountThatShopRecievedForOrder(int i, int i1) {
        BigDecimal ret = new BigDecimal(-1).setScale(3);
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select T.Amount from TransactionShop S join Transactions T on T.IdT = S.IdT where T.IdO = ? and S.IdS=?");) {
            stmt.setInt(1, i1);
            stmt.setInt(2, i);
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
    public BigDecimal getTransactionAmount(int i) {
        BigDecimal ret = new BigDecimal(-1).setScale(3);
        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select T.Amount from TransactionBuyer B join Transactions T on T.IdT = B.IdT where B.IdT = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ret = rs.getBigDecimal(1).setScale(3);
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select T.Amount from TransactionShop S join Transactions T on T.IdT = S.IdT where S.IdT = ?");) {
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ret = rs.getBigDecimal(1).setScale(3);
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    @Override
    public BigDecimal getSystemProfit() {
        BigDecimal ret = new BigDecimal(0).setScale(3);

        try (Statement stmt = DB.getInstance().getConnection().createStatement();) {
            ResultSet rs = stmt.executeQuery("select sum(T.Amount) from TransactionBuyer B join Transactions T on T.IdT = B.IdT join Orders O on O.IdO = T.IdO where O.Status = 'recieved'");
            if (rs.next() && rs.getBigDecimal(1) != null) {
                ret = ret.add(rs.getBigDecimal(1).setScale(3));
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (Statement stmt = DB.getInstance().getConnection().createStatement();) {
            ResultSet rs = stmt.executeQuery("select sum(T.Amount) from TransactionShop S join Transactions T on T.IdT = S.IdT join Orders O on O.IdO = T.IdO where O.Status = 'recieved'");
            if (rs.next() && rs.getBigDecimal(1) != null) {
                ret = ret.subtract(rs.getBigDecimal(1).setScale(3));
            }

        } catch (SQLException ex) {
            Logger.getLogger(_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    public static void main(String[] args) {
        _TransactionOperations t = new _TransactionOperations();
        System.out.println(t.getBuyerTransactionsAmmount(1));
    }
}
