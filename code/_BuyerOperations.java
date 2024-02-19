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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.BuyerOperations;

/**
 *
 * @author Admin
 */
public class _BuyerOperations implements BuyerOperations{

    @Override
    public int createBuyer(String string, int i) {
        int idB = -1;
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("insert into Buyer(Name, IdC, Balance) values (?, ?, 0)", Statement.RETURN_GENERATED_KEYS);
                ){
            stmt1.setString(1, string);
            stmt1.setInt(2, i);
            if(stmt1.executeUpdate() == 0)
                return -1;
            ResultSet rs = stmt1.getGeneratedKeys();
            rs.next();
            idB = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return idB;
    }

    @Override
    public int setCity(int i, int i1) {
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("update Buyer set IdC = ? where IdB = ?");
                ){
            stmt1.setInt(1, i1);
            stmt1.setInt(2, i);
            if(stmt1.executeUpdate() == 0)
                return -1;
        } catch (SQLException ex) {
            Logger.getLogger(_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 1;
    }

    @Override
    public int getCity(int i) {
        int idC = -1;
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select IdC from Buyer where IdB = ?");
                ){
            stmt1.setInt(1, i);
            ResultSet rs = stmt1.executeQuery();
            if(!rs.next())
                return -1;
            
            idC = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return idC;
    }

    @Override
    public BigDecimal increaseCredit(int i, BigDecimal bd) {
         BigDecimal balance = null;
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select Balance from Buyer where IdB = ?");
            PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("update Buyer set Balance = Balance + ? where IdB = ?");
   ){
            stmt1.setInt(1, i);
            ResultSet rs = stmt1.executeQuery();
            if(!rs.next())
                return new BigDecimal(-1);
            balance = rs.getBigDecimal(1).setScale(3).add(bd);
            
            stmt2.setBigDecimal(1, bd);
            stmt2.setInt(2, i);
            stmt2.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new BigDecimal(-1);
        }
        return balance;
    }

    @Override
    public int createOrder(int i) {
        int idO = -1;
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("insert into Orders (Status, IdB) values ('created', ?)", Statement.RETURN_GENERATED_KEYS);
                ){
            stmt1.setInt(1, i);
            if(stmt1.executeUpdate() == 0)
                return -1;
            ResultSet rs = stmt1.getGeneratedKeys();
            rs.next();
            idO = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return idO;
    }

    @Override
    public List<Integer> getOrders(int i) {
        List<Integer> lista = new ArrayList<>();
        
        try(PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select IdO from Orders where IdB = ?");
                ){
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
                lista.add(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return lista;
    }

    @Override
    public BigDecimal getCredit(int i) {
        BigDecimal balance = new BigDecimal(-1);
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select Balance from Buyer where IdB = ?");
                ){
            stmt1.setInt(1, i);
            ResultSet rs = stmt1.executeQuery();
            if(!rs.next())
                return new BigDecimal(-1);
            balance = rs.getBigDecimal(1).setScale(3);
            
        } catch (SQLException ex) {
            Logger.getLogger(_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new BigDecimal(-1);
        }
        return balance;
    }
    
    public static void main(String[] args) {
        _BuyerOperations B = new _BuyerOperations();
        System.out.println(B.increaseCredit(1, new BigDecimal(100000)));
        //System.out.println(B.createBuyer("Zika", 1));
        //System.out.println(B.createOrder(1));
        //System.out.println(B.createOrder(10));
        //System.out.println(B.getCity(10));
        //System.out.println(B.getCredit(1));
        //System.out.println(B.increaseCredit(10, new BigDecimal(10000)));
        //System.out.println(B.setCity(1, 1));
    }
    
}
